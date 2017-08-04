package ar.edu.itba.graphiccomp.group2.lux;

import java.io.File;
import java.io.FileNotFoundException;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.image.ViewPort;
import ar.edu.itba.graphiccomp.group2.math.Frustum;
import ar.edu.itba.graphiccomp.group2.render.RayTracer;
import ar.edu.itba.graphiccomp.group2.render.camera.PinHoleCamera;
import ar.edu.itba.graphiccomp.group2.scene.Scene;
import ar.edu.itba.graphiccomp.group2.scene.demo.DummyScene2;

public class DummyLuxParser extends LuxParser {

	@Override
	public RayTracer parse(File inputFile) throws FileNotFoundException {
		return newRayTracer(new DummyScene2().get());
	}

	private RayTracer newRayTracer(Scene scene) {
		Frustum frustum = new Frustum(1, 100, 1, 0.5f);
		ViewPort viewPort = new ViewPort(frustum, 400).setGamma(2.2f);
		PinHoleCamera camera = new PinHoleCamera(DummyScene2.cameraPos, frustum, viewPort);
		Vector3d upDirection = new Vector3d(0, 1, 0);
		Vector3d lookDirection = DummyScene2.cameraFront;
		camera.setFront(lookDirection, upDirection);
		return new RayTracer(scene, camera);
	}

}
