package ar.edu.itba.graphiccomp.group2.math;

import static ar.edu.itba.graphiccomp.group2.util.Vector3ds.dot;
import static ar.edu.itba.graphiccomp.group2.util.Vector3ds.lenghtSq;

import java.util.Objects;

import javax.vecmath.Vector3d;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.RayTargeteable;
import ar.edu.itba.graphiccomp.group2.util.Preconditions;

public final class Sphere implements RayTargeteable {

	private Vector3d _center;
	private double _r;
	private double _rSq;

	public Sphere(Vector3d center, double r) {
		_center = Objects.requireNonNull(center);
		_r = r;
		Preconditions.checkIsTrue(r() > 0);
		_rSq = r() * r();
	}

	public Vector3d center() {
		return _center;
	}

	public double r() {
		return _r;
	}

	@Override
	public boolean intersects(Line line, CollisionContext result) {
		Vector3d p0 = line.p0();
		Vector3d d = line.d();
		double b = b(p0, d);
		double c = c(p0);
		double kSq = (b * b) - (4 * c);
		if (kSq < 0) {
			return false;
		}
		double halfK = Math.sqrt(kSq);
		double t1 = (-b + halfK) / 2;
		double t2 = (-b - halfK) / 2;
		if (t1 < 0 && t2 < 0) {
			return false;
		}
		result.setPositionAt(line, t1 < 0 ? t2 : (t2 < 0 ? t1 : Math.min(t1, t2)));
		Vector3d normal = result.normal();
		normal.set(result.localPosition());
		normal.sub(center());
		normal.scale(1 / r());
		return true;
	}

	private double b(Vector3d p0, Vector3d d) {
		double dx = (p0.x - _center.x) * 2;
		double dy = (p0.y - _center.y) * 2;
		double dz = (p0.z - _center.z) * 2;
		return dot(d, dx, dy, dz);
	}

	private double c(Vector3d p0) {
		double dx = p0.x - _center.x;
		double dy = p0.y - _center.y;
		double dz = p0.z - _center.z;
		return lenghtSq(dx, dy, dz) - _rSq;
	}

	public void add(Transform transform) {
		transform.point(_center);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
