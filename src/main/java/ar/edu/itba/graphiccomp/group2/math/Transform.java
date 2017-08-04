package ar.edu.itba.graphiccomp.group2.math;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

public final class Transform {

	private Matrix4d _scale;
	private Matrix3d _rx;
	private Matrix3d _ry;
	private Matrix3d _rz;
	private Matrix4d _translation;

	private final Matrix4d _matrix = new Matrix4d();
	private Transform _inverse;

	private boolean _dirty;
	private boolean _ignoreSTR;

	public Transform() {
		_scale = new Matrix4d();
		_rx = new Matrix3d();
		_ry = new Matrix3d();
		_rz = new Matrix3d();
		_translation = new Matrix4d();

		_scale.setIdentity();
		_rx.setIdentity();
		_ry.setIdentity();
		_rz.setIdentity();
		_translation.setIdentity();
		_ignoreSTR = false;
		setDirty();
		_inverse = new Transform(false);
	}

	private Transform(boolean flag) {
		_scale = null;
		_rx = null;
		_ry = null;
		_rz = null;
		_translation = null;
		_inverse = null;
		_ignoreSTR = true;
	}

	public synchronized Transform update() {
		if (_dirty) {
			_dirty = false;
			if (!_ignoreSTR) {
				_matrix.setIdentity();
				_matrix.mul(_translation);
				_matrix.mul(calculateRotation());
				_matrix.mul(_scale);
			}
			calculateInverse();
		}
		return this;
	}

	private Matrix4d calculateRotation() {
		Matrix3d tmp = new Matrix3d();
		tmp.set(_rx);
		tmp.mul(_ry);
		tmp.mul(_rz);
		Matrix4d rotation = new Matrix4d();
		rotation.setIdentity();
		rotation.setRotation(tmp);
		return rotation;
	}

	private void calculateInverse() {
		_inverse._matrix.invert(_matrix);
	}

	private void setDirty() {
		_dirty = true;
	}

	public Transform setM(Matrix4d matrix) {
		_matrix.set(matrix);
		_ignoreSTR = true;
		_scale = null;
		_rx = null;
		_ry = null;
		_rz = null;
		_translation = null;
		setDirty();
		return this;
	}

	public Vector3d scale() {
		if (_dirty) {
			update();
		}
		return new Vector3d(_scale.m00, _scale.m11, _scale.m22);
	}

	public Transform setScale(Vector3d scale) {
		_scale.setIdentity();
		_scale.m00 = scale.x;
		_scale.m11 = scale.y;
		_scale.m22 = scale.z;
		setDirty();
		return this;
	}

	public Vector3d rotation() {
		if (_dirty) {
			update();
		}
		double rx = Math.atan2(_rx.m21, _rx.m11);
		double ry = Math.atan2(_ry.m02, _ry.m00);
		double rz = Math.atan2(_rz.m10, _rz.m00);
		return new Vector3d(rx, ry, rz);
	}

	public Transform setRotation(Vector3d rotation) {
		_rx.rotX(rotation.x);
		_ry.rotY(rotation.y);
		_rz.rotZ(rotation.z);
		setDirty();
		return this;
	}

	public Vector3d translation() {
		if (_dirty) {
			update();
		}
		return new Vector3d(_matrix.m03, _matrix.m13, _matrix.m23);
	}

	public Transform setTranslation(Vector3d v) {
		_translation.set(v);
		setDirty();
		return this;
	}

	public Vector3d point(Vector3d v) {
		return point(v, v);
	}

	public Vector3d point(Vector3d v, Vector3d result) {
		return apply(v, 1, result);
	}

	public Vector3d direction(Vector3d v) {
		return direction(v, v);
	}

	public Vector3d direction(Vector3d v, Vector3d result) {
		return apply(v, 0, result);
	}

	private Vector3d apply(Vector3d v, double k, Vector3d result) {
		if (_dirty) {
			update();
		}
		final Matrix4d m = _matrix;
		double x = m.m00 * v.x + m.m01 * v.y + m.m02 * v.z + m.m03 * k;
		double y = m.m10 * v.x + m.m11 * v.y + m.m12 * v.z + m.m13 * k;
		double z = m.m20 * v.x + m.m21 * v.y + m.m22 * v.z + m.m23 * k;
		result.set(x, y, z);
		return result;
	}

	public Transform inverse() {
		if (_dirty) {
			update();
		}
		return _inverse;
	}

	@Override
	public String toString() {
		if (_dirty) {
			update();
		}
		return String.format(
			"%.014f %.014f %.014f %.014f\n" + 
			"%.014f %.014f %.014f %.014f\n" + 
			"%.014f %.014f %.014f %.014f\n" + 
			"%.014f %.014f %.014f %.014f\n", 
			_matrix.m00, _matrix.m01, _matrix.m02, _matrix.m03,
			_matrix.m10, _matrix.m11, _matrix.m12, _matrix.m13,
			_matrix.m20, _matrix.m21, _matrix.m22, _matrix.m23,
			_matrix.m30, _matrix.m31, _matrix.m32, _matrix.m33
		);
	}

}
