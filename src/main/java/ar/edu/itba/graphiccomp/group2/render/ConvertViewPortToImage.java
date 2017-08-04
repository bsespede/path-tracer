package ar.edu.itba.graphiccomp.group2.render;

import java.io.File;

import ar.edu.itba.graphiccomp.group2.image.PNGImage;
import ar.edu.itba.graphiccomp.group2.image.ViewPort;
import ar.edu.itba.graphiccomp.group2.imagecorrect.GammaCorrection;
import ar.edu.itba.graphiccomp.group2.imagecorrect.ImageCorrector;

public class ConvertViewPortToImage {

	private ViewPort _viewPort;
	private ImageCorrector _corector;
	private boolean _applyGamma;

	public ConvertViewPortToImage(ViewPort viewPort) {
		_viewPort = viewPort;
	}

	public ConvertViewPortToImage applyGammaIf(boolean applyGamma) {
		_applyGamma = applyGamma;
		return this;
	}

	public ConvertViewPortToImage correctWith(ImageCorrector corrector) {
		_corector = corrector;
		return this;
	}

	public void export(File out) {
		System.out.println("[INFO] Exporting frame to: " + out);
		ViewPort image = _corector.correct(_viewPort);
		new PNGImage().build(image, out);
		if (_applyGamma) {
			File gammaCorrectedFile = gammaOutFile(out);
			System.out.println("[INFO] Exporting frame to (gamma): " + gammaCorrectedFile);
			new PNGImage().build(new GammaCorrection().correct(image), gammaCorrectedFile);
		}
	}

	private File gammaOutFile(File out) {
		String path = out.getAbsolutePath();
		int lastDot = path.lastIndexOf('.');
		String name;
		if (lastDot != -1) {
			name = path.substring(0, lastDot) + "-gamma" + path.substring(lastDot);
		} else {
			name = path + "-gamma";
		}
		return new File(name);
	}

}
