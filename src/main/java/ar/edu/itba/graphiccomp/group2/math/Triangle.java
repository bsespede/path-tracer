package ar.edu.itba.graphiccomp.group2.math;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;

public final class Triangle {

	public static final boolean intersects(Vector3d va, Vector3d vb, Vector3d vc, Line line, CollisionContext result) {
		double a = va.x - vb.x;
		double b = va.x - vc.x;
		double c = line.d().x;
		double d = va.x - line.p0().x;
		double e = va.y - vb.y;
		double f = va.y - vc.y;
		double g = line.d().y;
		double h = va.y - line.p0().y;
		double i = va.z - vb.z;
		double j = va.z - vc.z;
		double k = line.d().z;
		double l = va.z - line.p0().z;
		double m = f * k - g * j;
		double n = h * k - g * l;
		double p = f * l - h * j;
		double q = g * i - e * k;
		double s = e * j - f * i;
		double inv = 1 / (a * m + b * q + c * s);
		double aux1 = d * m - b * n - c * p;
		double beta = aux1 * inv;
		if (beta < 0) {
			return false;
		}
		double r = e * l - h * i;
		double aux2 = a * n + d * q + c * r;
		double gamma = aux2 * inv;
		if (gamma < 0) {
			return false;
		}
		if (beta + gamma > 1) {
			return false;
		}
		double aux3 = a * p - b * r + d * s;
		double t = aux3 * inv;
		if (t < 0) {
			return false;
		}
		result.setPositionAt(line, t);
		return true;
	}

}
