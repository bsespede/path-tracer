package ar.edu.itba.graphiccomp.group2.scene;

import javax.vecmath.Vector3d;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.util.Preconditions;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public final class CollisionContext {

	private boolean _intersects;
	private Geometry _geometry;
	private final Vector3d _worldPosition = new Vector3d();
	private final Vector3d _localPosition = new Vector3d();
	private double _collisionDistanceSq;
	private final Vector3d _normal = new Vector3d();
	private final Vector3d _shade = new Vector3d();
	private int _index;
	private double _t;

	public CollisionContext() {
		_intersects = false;
	}

	public CollisionContext(boolean intersects, Vector3d position) {
		_intersects = intersects;
		_worldPosition.set(position);
	}

	public CollisionContext restart() {
		_geometry = null;
		_intersects = false;
		_index = -1;
		_worldPosition.set(0, 0, 0);
		_normal.set(0, 0, 0);
		_shade.set(0, 0, 0);
		return this;
	}

	public CollisionContext setPositionAt(Line line, double t) {
		Preconditions.checkIsTrue(!intersects());
		setIntersects(true);
		_t = t;
		line.pointAt(t, localPosition());
		_collisionDistanceSq = Vector3ds.distanceSq(line.p0(), localPosition());
		return this;
	}

	public CollisionContext updateIfCollisionIsCloser(Line line, CollisionContext other) {
		return updateIfCollisionIsCloser(line, other, other.index());
	}

	public CollisionContext updateIfCollisionIsCloser(Line line, CollisionContext other, int index) {
		Preconditions.checkIsTrue(other.intersects());
		if (!intersects() || other.collisionDistanceSq() < collisionDistanceSq()) {
			_geometry = other.geometry();
			_index = index;
			_intersects = other.intersects();
			_worldPosition.set(other.worldPosition());
			_localPosition.set(other.localPosition());
			_collisionDistanceSq = other.collisionDistanceSq();
			_normal.set(other.normal());
			_shade.set(other.shade());
			_t = other.t();
		}
		return this;
	}

	public Geometry geometry() {
		return _geometry;
	}

	public void setGeometry(Geometry geometry) {
		this._geometry = geometry;
	}

	public Vector3d normal() {
		return _normal;
	}

	public Vector3d worldPosition() {
		return _worldPosition;
	}

	public Vector3d localPosition() {
		return _localPosition;
	}

	public Vector3d shade() {
		return _shade;
	}

	public void setIntersects(boolean intersects) {
		_intersects = intersects;
	}

	public boolean intersects() {
		return _intersects;
	}

	public double collisionDistanceSq() {
		return _collisionDistanceSq;
	}

	public void setCollisionDistanceSq(double collisionDistanceSq) {
		this._collisionDistanceSq = collisionDistanceSq;
	}

	public void setIndex(int index) {
		_index = index;
	}

	public int index() {
		return _index;
	}

	public double t() {
		return _t;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
