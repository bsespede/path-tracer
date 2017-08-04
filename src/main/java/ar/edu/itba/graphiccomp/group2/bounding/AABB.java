package ar.edu.itba.graphiccomp.group2.bounding;

import javax.vecmath.Vector3d;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ar.edu.itba.graphiccomp.group2.math.Box;
import ar.edu.itba.graphiccomp.group2.math.Line;

public final class AABB implements BoundingVolume {

	private Box _box;

	public AABB(Vector3d center, Vector3d halfExtents) {
		this(new Box(center, halfExtents));
	}

	public AABB(Box box) {
		_box = box;
	}

	@Override
	public boolean intersects(Box box) {
		Vector3d center1 = box.center();
		Vector3d halfExtents1 = box.halfExtents();
		Vector3d center2 = _box.center();
		Vector3d halfExtents2 = _box.halfExtents();
		if (Math.abs(center1.x - center2.x) > halfExtents1.x + halfExtents2.x) {
			return false;
		}
		if (Math.abs(center1.y - center2.y) > halfExtents1.y + halfExtents2.y) {
			return false;
		}
		if (Math.abs(center1.z - center2.z) > halfExtents1.z + halfExtents2.z) {
			return false;
		}
		return true;
	}

	@Override
	public boolean intersects(Line line) {
		return _box.intersects(line);
	}

	public Box box() {
		return _box;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public Vector3d lbb() {
		return box().lbb();
	}

	@Override
	public Vector3d rtf() {
		return box().rtf();
	}

	@Override
	public Vector3d center() {
		return box().center();
	}
}
