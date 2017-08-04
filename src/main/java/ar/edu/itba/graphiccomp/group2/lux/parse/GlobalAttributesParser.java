package ar.edu.itba.graphiccomp.group2.lux.parse;

import java.util.Optional;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.lux.LuxCommand;
import ar.edu.itba.graphiccomp.group2.render.RayTracer;
import ar.edu.itba.graphiccomp.group2.render.camera.Camera;
import ar.edu.itba.graphiccomp.group2.util.PeekableScanner;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public class GlobalAttributesParser {

	public void parse(RayTracer rayTracer, PeekableScanner scanner) {
		LuxCommand cmd = new LuxCommand();
		while (scanner.hasNext()) {
			String line = scanner.peek().trim();
			if (line.equals("WorldBegin")) {
				break;
			}
			if (!line.isEmpty() && !line.startsWith("#")) {
				LuxCommand auxCommand = cmd.parse(line);
				if (cmd.executeCommand()) {
					parse(rayTracer, cmd);
					cmd = auxCommand;
				}				
			}
			scanner.consume();
		}
		parse(rayTracer, cmd);
	}

	private void parse(RayTracer rayTracer, LuxCommand cmd) {
		switch (cmd.name()) {
		case "LookAt": {
			Vector3d camera = Vector3ds.parse(cmd.paramsList());
			Vector3d lookAt = Vector3ds.parse(cmd.paramsList(), 3);
			Vector3d up = Vector3ds.parse(cmd.paramsList(), 6);
			lookAt.sub(camera);
			lookAt.normalize();
			rayTracer.camera().position().set(camera);
			rayTracer.camera().setFront(lookAt, up);
			break;
		}
		case "Camera": {
			String fov = Optional.ofNullable(cmd.param("float fov", 0)).orElse("90");
			rayTracer.camera().frustum().setFOV(1, 10000, Float.parseFloat(fov));
			break;
		}
		case "Film": {
			Camera camera = rayTracer.camera();
			String xres = Optional.ofNullable(cmd.param("integer xresolution", 0)).orElse("800");
			String yres = Optional.ofNullable(cmd.param("integer yresolution", 0)).orElse("600");
			String gamma = Optional.ofNullable(cmd.param("float gamma", 0)).orElse("2.2");
			camera.viewPort().setDimentions(Integer.parseInt(xres), Integer.parseInt(yres));
			camera.viewPort().setGamma(Float.parseFloat(gamma));
			break;
		}
		case "Renderer":
		case "Sampler":
		case "Accelerator":
		case "SurfaceIntegrator":
		case "VolumeIntegrator":
		case "PixelFilter":
//			System.out.println("[WARN] Ignoring: " + cmd.name() + " " + cmd.description());
			break;
		}
	}
}
