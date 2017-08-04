package ar.edu.itba.graphiccomp.group2.meshsample;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.scene.Geometry;

public interface MeshSampler {

	Geometry geometry();

	Vector3d sample();

}
