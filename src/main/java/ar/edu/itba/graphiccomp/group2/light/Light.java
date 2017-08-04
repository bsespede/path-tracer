package ar.edu.itba.graphiccomp.group2.light;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.Scene;

public abstract class Light {

	/**
	 * Specular components of the light source
	 */
	private final Vector3d _specular = new Vector3d(1, 1, 1);

	/**
	 * Diffuse components of the light source
	 */
	private final Vector3d _diffuse = new Vector3d(1, 1, 1);

	public Vector3d diffuse() {
		return _diffuse;
	}

	public Light setDiffuse(Vector3d diffuse) {
		_diffuse.set(diffuse);
		return this;
	}

	public Vector3d specular() {
		return _specular;
	}

	public Light setSpecular(Vector3d specular) {
		_specular.set(specular);
		return this;
	}

	public abstract Vector3d applyShaders(Scene scene, CollisionContext collision, Vector3d eye);

}
