package ar.edu.itba.graphiccomp.group2.render;

import java.util.Objects;

import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.render.Renderer.RenderType;
import ar.edu.itba.graphiccomp.group2.render.camera.Camera;
import ar.edu.itba.graphiccomp.group2.render.processor.FrameBuilder;
import ar.edu.itba.graphiccomp.group2.render.processor.MultithreadedFrameBuilder;
import ar.edu.itba.graphiccomp.group2.render.processor.PathTracerRayResolver;
import ar.edu.itba.graphiccomp.group2.render.processor.RayResolver;
import ar.edu.itba.graphiccomp.group2.render.processor.RayTracerRayResolver;
import ar.edu.itba.graphiccomp.group2.render.sampler.RegularSampler;
import ar.edu.itba.graphiccomp.group2.render.sampler.Sampler;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.Scene;
import ar.edu.itba.graphiccomp.group2.util.Preconditions;

public class RayTracer {

	private final Scene _scene;
	private final Renderer _renderer;
	private final FrameBuilder _frameResolver;
	private Camera _camera;
	private RayResolver _rayResolver = new RayTracerRayResolver();

	private int _reflectionsMaxDepth = 3;
	private int _refractionsMaxDepth = 5;
	private Sampler _sampler = new RegularSampler(1);

	public RayTracer(Scene scene, Camera camera) {
		_scene = Objects.requireNonNull(scene);
		_camera = Objects.requireNonNull(camera);
		_renderer = new Renderer(camera);
		_frameResolver = new MultithreadedFrameBuilder();
	}

	public Scene scene() {
		return _scene;
	}

	public Camera camera() {
		return _camera;
	}

	public Renderer renderer() {
		return _renderer;
	}

	public void setRenderType(RenderType type) {
		_renderer.setRenderType(type);
	}

	public int reflectionsMaxDepth() {
		return _reflectionsMaxDepth;
	}

	public int refractionsMaxDepth() {
		return _refractionsMaxDepth;
	}

	public void setRayDepth(int reflectionsMaxDepth, int refractionsMaxDepth) {
		_reflectionsMaxDepth = reflectionsMaxDepth;
		_refractionsMaxDepth = refractionsMaxDepth;
		Preconditions.checkIsTrue(reflectionsMaxDepth() >= 0);
		Preconditions.checkIsTrue(refractionsMaxDepth() >= 0);
	}

	public void buildFrame() {
		System.out.println(String.format("Frame size: %dx%d", camera().viewPort().height(), camera().viewPort().height()));
		scene().generateGeometryLightsSamples();
		_frameResolver.build(this);
	}

	public Sampler sampler() {
		return _sampler;
	}

	public void configurePathTracer(int maxDepth, int samplesPerPixel) {
		setRayResolver(new PathTracerRayResolver().setMaxDepth(maxDepth).setSamplesPerPixel(samplesPerPixel));
	}

	public void setSampler(Sampler sampler) {
		_sampler = sampler;
	}

	public boolean trace(Line line, CollisionContext collision) {
		return scene().trace(line, collision);
	}

	public RayResolver rayResolver() {
		return _rayResolver;
	}

	public void setRayResolver(RayResolver rayResolver) {
		_rayResolver = rayResolver;
	}

	public void setCamera(Camera camera) {
		_camera = camera;
	}

}
