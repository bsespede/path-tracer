package ar.edu.itba.graphiccomp.group2.mesh;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.bounding.BoundingVolume;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.math.Transform;
import ar.edu.itba.graphiccomp.group2.meshsample.MeshSampler;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;

public interface Mesh {

	boolean intersection(Line line, CollisionContext result);

	Vector2d uvmapping(Vector3d position, Vector2d bounds, int collisionIndex, Vector2d result);

	BoundingVolume boundingVolume(Transform transform);

	MeshSampler sampler(Geometry geometry);

}
