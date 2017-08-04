package ar.edu.itba.graphiccomp.group2.math;

import java.util.List;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.mesh.TriangleMesh;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public class Triangles {

	public static Box boundingBox(List<Vector3d> vertices) {
		Vector3d min = Vector3ds.constant(Double.MAX_VALUE);
		Vector3d max = Vector3ds.constant(-Double.MAX_VALUE);
		for (Vector3d v : vertices) {
			ensureBoundings(v, min, max);
		}
		return box(min, max);
	}

	public static void ensureBoundings(Vector3d v, Vector3d min, Vector3d max) {
		max.x = Math.max(max.x, v.x);
		max.y = Math.max(max.y, v.y);
		max.z = Math.max(max.z, v.z);

		min.x = Math.min(min.x, v.x);
		min.y = Math.min(min.y, v.y);
		min.z = Math.min(min.z, v.z);
	}

	public static Box box(Vector3d min, Vector3d max) {
		Vector3d center = new Vector3d(max);
		center.add(min);
		center.scale(0.5);
		Vector3d halfExtents = new Vector3d(max);
		halfExtents.sub(min);
		halfExtents.scale(0.5);
		// XXX: extend a bit the bounding box to ensure all triangles are inside
		// the enclosing bounds
		halfExtents.x += 0.001;
		halfExtents.y += 0.001;
		halfExtents.z += 0.001;
		return new Box(center, halfExtents, false);
	}

	public static double area(Vector3d a, Vector3d b, Vector3d c) {
		double x1 = b.x - a.x;
		double x2 = b.y - a.y;
		double x3 = b.z - a.z;

		double y1 = c.x - a.x;
		double y2 = c.y - a.y;
		double y3 = c.z - a.z;

		double d1 = x2 * y3 - x3 * y2;
		double d2 = x3 * y1 - x1 * y3;
		double d3 = x1 * y2 - x2 * y1;
		return 0.5 * Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
	}

	public static Vector3d pointAt(TriangleMesh mesh, int index, double u, double v, Vector3d result) {
		return pointAt(mesh.a(index), mesh.b(index), mesh.c(index), u, v, result);
	}

	public static Vector3d pointAt(Vector3d a, Vector3d b, Vector3d c, double u, double v, Vector3d result) {
		double dABx = b.x - a.x;
		double dABy = b.y - a.y;
		double dABz = b.z - a.z;

		double dACx = c.x - a.x;
		double dACy = c.y - a.y;
		double dACz = c.z - a.z;

		result.x = a.x + dABx * u + dACx * v;
		result.y = a.y + dABy * u + dACy * v;
		result.z = a.z + dABz * u + dACz * v;
		return result;
	}

	public static boolean test(Vector3d a, Vector3d b, Vector3d c, Box box) {
		return test1(a, b, c, box) && test2(a, b, c, box);
	}

	private static boolean test1(Vector3d a, Vector3d b, Vector3d c, Box bounds) {
		// testear que alguno de los puntos esten dentro del AABB entonces
		// seguro lo toca
		return bounds.contains(a) || bounds.contains(b) || bounds.contains(c);
	}

	private static boolean test2(Vector3d a, Vector3d b, Vector3d c, Box bounds) {
		// testear que alguna de las lineas del triangulo intersecten a alguna
		// de las caras del AABB entonces seguro tiene un cacho adentro a pesar
		// de que tenga todos los vertices afuera del AABB
		Vector3d d;
		d = new Vector3d(b);
		d.sub(a);
		Line ab = new Line(a, d);
		if (bounds.intersects(ab, d.length())) {
			return true;
		}
		d = new Vector3d(c);
		d.sub(b);
		Line bc = new Line(b, d);
		if (bounds.intersects(bc, d.length())) {
			return true;
		}
		d = new Vector3d(a);
		d.sub(c);
		Line ca = new Line(c, d);
		if (bounds.intersects(ca, d.length())) {
			return true;
		}
		return false;
	}
}
