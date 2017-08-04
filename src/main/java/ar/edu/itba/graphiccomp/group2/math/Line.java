package ar.edu.itba.graphiccomp.group2.math;

import javax.vecmath.Vector3d;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class Line {

	private final Vector3d _p0 = new Vector3d();
	private final Vector3d _d = new Vector3d();

	public Line() {
	}

	public Line(Vector3d p0, Vector3d d) {
		_p0.set(p0);
		_d.set(d);
		_d.normalize();
	}

	public Vector3d p0() {
		return _p0;
	}

	public Line setP0(Vector3d p0) {
		_p0.set(p0);
		return this;
	}

	public Line setD(Vector3d d) {
		_d.set(d);
		return this;
	}

	public Vector3d d() {
		return _d;
	}

	public Line lookAt(Vector3d position) {
		d().set(position);
		d().sub(p0());
		d().normalize();
		return this;
	}

	public Vector3d pointAt(double t, Vector3d result) {
		result.set(_d);
		result.scale(t);
		result.add(_p0);
		return result;
	}

	public Line translateP0(double amount) {
		_p0.x += _d.x * amount;
		_p0.y += _d.y * amount;
		_p0.z += _d.z * amount;
		return this;
	}

	public Line copy() {
		Line copy = new Line();
		copy.p0().set(p0());
		copy.d().set(d());
		return copy;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
