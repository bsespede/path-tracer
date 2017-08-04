package ar.edu.itba.graphiccomp.group2.mesh;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.bounding.AABB;
import ar.edu.itba.graphiccomp.group2.bounding.BoundingVolume;
import ar.edu.itba.graphiccomp.group2.material.UVMappingType;
import ar.edu.itba.graphiccomp.group2.math.Box;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.math.Transform;
import ar.edu.itba.graphiccomp.group2.meshsample.BoxSampler;
import ar.edu.itba.graphiccomp.group2.meshsample.MeshSampler;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;

public class BoxMesh implements Mesh {

	private Box _box;
	private UVMappingType _uvMappingTypee = UVMappingType.REPEAT;

	public BoxMesh(Vector3d halfExtents) {
		this(new Vector3d(), halfExtents);
	}

	public BoxMesh(Vector3d center, Vector3d halfExtents) {
		_box = new Box(center, halfExtents);
	}

	@Override
	public boolean intersection(Line line, CollisionContext result) {
		return _box.intersects(line, result);
	}

	public BoxMesh setUvMappingTypee(UVMappingType uvMappingTypee) {
		this._uvMappingTypee = uvMappingTypee;
		return this;
	}

	@Override
	public Vector2d uvmapping(Vector3d position, Vector2d bounds, int collisionIndex, Vector2d result) {
		final Vector3d halfExtents = _box.halfExtents();
		final double dx = (position.x - _box.center().x) / halfExtents.x;
		final double dy = (position.y - _box.center().y) / halfExtents.y;
		final double dz = (position.z - _box.center().z) / halfExtents.z;
		final double px = (dx + 1) / 2;
		final double py = (dy + 1) / 2;
		final double pz = (dz + 1) / 2;
		if (_uvMappingTypee.equals(UVMappingType.STRETCH)) {
			switch (collisionIndex) {
			case Box.BOTTOM:
				result.x = 0.25 + px / 4;
				result.y = 0.66 + (1 - pz) / 3;
				break;
			case Box.LEFT:
				result.x = pz / 4;
				result.y = 0.33 + (1 - py) / 3;
				break;
			case Box.FRONT:
				result.x = 0.25 + px / 4;
				result.y = 0.33 + (1 - py) / 3;
				break;
			case Box.RIGHT:
				result.x = 0.5 + (1 - pz) / 4;
				result.y = 0.33 + (1 - py) / 3;
				break;
			case Box.BACK:
				result.x = 0.75 + (1 - px) / 4;
				result.y = 0.33 + (1 - py) / 3;
				break;
			case Box.TOP:
				result.x = 0.25 + px / 4;
				result.y = (pz / 3);
				break;
			}
		} else {
			switch (collisionIndex) {
			case Box.BOTTOM:
				result.x = px;
				result.y = 1 - pz;
				break;
			case Box.LEFT:
				result.x = pz;
				result.y = 1 - py;
				break;
			case Box.FRONT:
				result.x = px;
				result.y = 1 - py;
				break;
			case Box.RIGHT:
				result.x = 1 - pz;
				result.y = 1 - py;
				break;
			case Box.BACK:
				result.x = 1 - px;
				result.y = 1 - py;
				break;
			case Box.TOP:
				result.x = px;
				result.y = pz;
				break;
			}
		}
		return result;
	}

	@Override
	public BoundingVolume boundingVolume(Transform tranform) {
		Vector3d center = new Vector3d(_box.center());
		center.add(tranform.translation());
		return new AABB(center, _box.halfExtents());
	}

	public Box box() {
		return _box;
	}

	@Override
	public MeshSampler sampler(Geometry geometry) {
		return new BoxSampler(geometry);
	}
}
