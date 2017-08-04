package ar.edu.itba.graphiccomp.group2.render;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import ar.edu.itba.graphiccomp.group2.render.camera.Camera;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.Scene;

public class Renderer {

	public static enum RenderType {
		NORMALS, LIGHT, DISTANCE;
	}

	private RenderType _renderType;
	private Vector3d _backgroundColor;
	@SuppressWarnings("unused")
	private Camera _camera;

	public Renderer(Camera camera) {
		setRenderType(RenderType.LIGHT);
		_camera = camera;
	}

	public RenderType renderType() {
		return _renderType;
	}

	public boolean renderLights() {
		return renderType().equals(RenderType.LIGHT);
	}

	public void setRenderType(RenderType renderType) {
		_renderType = renderType;
		_backgroundColor = renderLights() ? new Vector3d(0.28, 0.23, 0.45) : new Vector3d();
	}

	public void draw(CollisionContext collision, Vector3f pixel, Scene scene) {
		pixel.set(_backgroundColor);
		if (collision.intersects()) {
			switch (_renderType) {
			case NORMALS:
				renderNormal(collision, pixel, scene);
				break;
			case LIGHT:
				renderLights(collision, pixel, scene);
				break;
			case DISTANCE:
				renderDistance(collision, pixel, scene);
			}
		}
	}

	private void renderNormal(CollisionContext collision, Vector3f pixel, Scene scene) {
		pixel.set(collision.normal());
		pixel.x += 1;
		pixel.y += 1;
		pixel.z += 1;
		pixel.scale(0.5f);
	}

	private void renderLights(CollisionContext collision, Vector3f pixel, Scene scene) {
		pixel.set(collision.shade());
	}

	private void renderDistance(CollisionContext collision, Vector3f pixel, Scene scene) {
		float dist = (float) Math.sqrt(collision.collisionDistanceSq());
		pixel.set(dist, dist, dist);
	}

}
