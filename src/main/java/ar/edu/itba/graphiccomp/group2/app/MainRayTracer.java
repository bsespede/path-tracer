package ar.edu.itba.graphiccomp.group2.app;

import static ar.edu.itba.graphiccomp.group2.util.OptionUtil.required;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import ar.edu.itba.graphiccomp.group2.imagecorrect.CapRGBImage;
import ar.edu.itba.graphiccomp.group2.imagecorrect.DynamicRangeCompression;
import ar.edu.itba.graphiccomp.group2.imagecorrect.ImageCorrector;
import ar.edu.itba.graphiccomp.group2.lux.LuxParser;
import ar.edu.itba.graphiccomp.group2.math.MathUtil;
import ar.edu.itba.graphiccomp.group2.render.ConvertViewPortToImage;
import ar.edu.itba.graphiccomp.group2.render.RayTracer;
import ar.edu.itba.graphiccomp.group2.render.Renderer.RenderType;
import ar.edu.itba.graphiccomp.group2.render.camera.FishEyeCamera;
import ar.edu.itba.graphiccomp.group2.render.camera.ThinLensCamera;
import ar.edu.itba.graphiccomp.group2.render.sampler.MultiJitteredSampler;
import ar.edu.itba.graphiccomp.group2.render.sampler.RegularSampler;
import ar.edu.itba.graphiccomp.group2.util.Statistics;

public class MainRayTracer {

	private static final Options options = options();

	private final static Options options() {
		Options options = new Options();
		options.addOption("help", false, "(Opcional) Mostrar menu de ayuda");
		options.addOption("gamma", false, "(Opcional) Correccion gamma");
		options.addOption("o", true, "(Opcional) Nombre del archivo de salida");
		options.addOption(required(new Option("i", true, "Nombre del archivo de entrada (Escena)")));
		options.addOption("time", false, "(Opcional) tiempo empleado en el render");
		options.addOption(required(new Option("aa", true, "Cantidad de muestras de antialiasing")));
		options.addOption("benchmark", true, "(Opcional) Realizar el render completo n veces consecutivas");
		options.addOption("d", true, "(Opcional) Define el ray depth de reflejos y refracciones");
		options.addOption("r", true, "(Opcional) Define el tipo de render a usar");
		options.addOption("correction", true, "(Opcional) Tipo de correcion de la imagen");
		options.addOption("gui", false, "Interfaz grafica (Warning: In alpha stage!)");
		// Pathtracer exclusive
		options.addOption("pathtracer", false, "Utilizar metodo de path tracing");
		options.addOption("tr", true, "Define el la cantidad de hops que un camino puede tener (­pathtracer)");
		options.addOption("s", true, "Define la cantidad de samples por pixel (­pathtracer)");
		options.addOption("debug", false, "Modo debug");
		options.addOption("fd", true, "Distancia al plano focal");
		options.addOption("lr", true, "Radio de la lente");
		options.addOption("phi", true, "Angulo maximo para camara esferica (en grados)");
		return options;
	}

	public static void main(String[] args) throws IOException, ParseException {
		CommandLine cmd;
		try {
			cmd = new BasicParser().parse(options, args);
		} catch (ParseException e) {
			System.err.println("Invalid argument. Reason: " + e.getMessage());
			new HelpFormatter().printHelp("ant", options);
			return;
		}
		new MainRayTracer(cmd).run();
	}

	private CommandLine cmd;
	private ConvertViewPortToImage pngBuilder;
	private boolean frameExported;
	private boolean debugMode;
	private File outputFile;

	public MainRayTracer(CommandLine cmd) {
		this.cmd = cmd;
	}

	public void run() throws IOException {
		if (cmd.hasOption("help")) {
			new HelpFormatter().printHelp("", options);
			return;
		}
		debugMode = cmd.hasOption("debug");
		File inputFile = new File(cmd.getOptionValue("i"));
		RayTracer rayTracer = new LuxParser().parse(inputFile);
		if (cmd.hasOption("aa")) {
			int samplesNumber = Integer.parseInt(cmd.getOptionValue("aa"));
			if (samplesNumber > 1) {
				rayTracer.setSampler(new MultiJitteredSampler(samplesNumber));
			} else {
				rayTracer.setSampler(new RegularSampler(1));
			}
		}
		if (cmd.hasOption("fd") && cmd.hasOption("lr")) {
			double fd = Double.parseDouble(cmd.getOptionValue("fd"));
			double lr = Double.parseDouble(cmd.getOptionValue("lr"));
			rayTracer.setCamera(new ThinLensCamera(rayTracer.camera(), lr, fd));
		}
		if (cmd.hasOption("phi")) {
			double phi = Double.valueOf(cmd.getOptionValue("phi"));
			if (phi > 360 || phi < 0) {
				System.out.println("Angulos validos: rango 0-360");
				return;
			}
			rayTracer.setCamera(new FishEyeCamera(rayTracer.camera(), phi));
		}
		if (cmd.hasOption("d")) {
			int rayDepth = Integer.valueOf(cmd.getOptionValue("d"));
			rayTracer.setRayDepth(rayDepth, rayDepth + 2);
		}
		if (cmd.hasOption("r")) {
			String renderType = cmd.getOptionValue("r");
			RenderType type = renderType.equals("normal") ? RenderType.NORMALS : renderType.equals("distance") ? RenderType.DISTANCE : RenderType.LIGHT;
			rayTracer.setRenderType(type);
		}
		if (cmd.hasOption("pathtracer")) {
			if (!cmd.hasOption("tr") || !MathUtil.isInt(cmd.getOptionValue("tr"))) {
				System.out.println("Required option tr when -pathtracer enabled");
				return;
			}
			if (!cmd.hasOption("s") || !MathUtil.isInt(cmd.getOptionValue("s"))) {
				System.out.println("Required option s when -pathtracer enabled");
				return;
			}
			rayTracer.configurePathTracer(Integer.valueOf(cmd.getOptionValue("tr")), Integer.valueOf(cmd.getOptionValue("s")));
		}
		if (cmd.hasOption("gui")) {
			new GraphicalMainRayTracer(rayTracer).run();
			return;
		}
		pngBuilder = new ConvertViewPortToImage(rayTracer.camera().viewPort()).applyGammaIf(cmd.hasOption("gamma")).correctWith(
				corrector(cmd.getOptionValue("correction")));
		outputFile = new File(outputFile(cmd));
		int samples = Integer.parseInt(cmd.hasOption("benchmark") ? cmd.getOptionValue("benchmark") : "1");
		Statistics stats = new Statistics();
		Thread exportEveryNSeconds = new Thread(new ExportImageTask());
		exportEveryNSeconds.start();
		frameExported = false;
		IntStream.rangeClosed(1, samples).forEach(i -> {
			System.out.println("[INFO] Building frame " + i);
			long startTime = System.currentTimeMillis();
			rayTracer.buildFrame();
			long delta = System.currentTimeMillis() - startTime;
			System.out.println(String.format("[INFO] Finished frame %d building. Elapsed: %.2f [sec]", i, delta / 1000f));
			stats.addData(delta);
			if (!frameExported) {
				frameExported = true;
				pngBuilder.export(outputFile);
			}
		});
		if (cmd.hasOption("time")) {
			String str = "Measures: %d / Mean Time: %.2f [s] / stdDev: %.2f [s] / Total time: %.2f [s]";
			System.out.println(String.format(str, stats.size(), stats.mean() / 1000, stats.stdDev() / 1000, stats.sum() / 1000));
		} else {
			String str = String.format("Total time: %.2f. Avg: %.2f", stats.sum() / 1000, stats.mean() / 1000);
			System.out.println(str);
		}
	}

	private static String outputFile(CommandLine cmd) {
		if (cmd.hasOption("o")) {
			return cmd.getOptionValue("o");
		}
		File file = new File(cmd.getOptionValue("i"));
		String fileName = file.getName();
		int dotIndex = fileName.indexOf(".");
		if (dotIndex != -1) {
			fileName = file.getName().substring(0, dotIndex);
		}
		return fileName + ".png";
	}

	private static ImageCorrector corrector(String value) {
		value = value == null ? "" : value;
		return value.equals("d") ? new DynamicRangeCompression() : new CapRGBImage();
	}

	private class ExportImageTask implements Runnable {

		@Override
		public void run() {
			while (debugMode && !frameExported) {
				try {
					Thread.sleep(TimeUnit.SECONDS.toMillis(60));
					String path = outputFile.getAbsolutePath();
					pngBuilder.export(new File(path.replace(".png", "-preview.png")));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
