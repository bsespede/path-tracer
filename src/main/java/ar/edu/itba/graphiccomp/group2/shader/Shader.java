package ar.edu.itba.graphiccomp.group2.shader;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.light.Light;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;

public interface Shader {

	default Vector3d apply(Light light, CollisionContext collision, Vector3d I, Vector3d eye) {
		return apply(light.diffuse(), light.specular(), collision, I, eye);
	}

	Vector3d apply(Vector3d diffuse, Vector3d specular, CollisionContext collision, Vector3d I, Vector3d eye);

}
