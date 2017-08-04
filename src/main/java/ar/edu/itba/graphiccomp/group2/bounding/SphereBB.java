package ar.edu.itba.graphiccomp.group2.bounding;

import static ar.edu.itba.graphiccomp.group2.math.MathUtil.sq;

import java.util.Objects;

import javax.vecmath.Vector3d;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ar.edu.itba.graphiccomp.group2.math.Box;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.math.Sphere;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public final class SphereBB implements BoundingVolume {

	private Sphere _sphere;

	public SphereBB(Vector3d center, double r) {
		_sphere = new Sphere(center, r);
	}

	public SphereBB(Sphere sphere) {
		_sphere = Objects.requireNonNull(sphere);
	}

	@Override
	public boolean intersects(Box box) {
		return testIntersection(box.lbb(), box.rtf(), _sphere.center(), _sphere.r());
	}

	boolean testIntersection(Vector3d C1, Vector3d C2, Vector3d S, double R) {
		double dist_squared = R * R;
		/* assume C1 and C2 are element-wise sorted, if not, do that now */
		if (S.x < C1.x)
			dist_squared -= sq(S.x - C1.x);
		else if (S.x > C2.x)
			dist_squared -= sq(S.x - C2.x);
		if (S.y < C1.y)
			dist_squared -= sq(S.y - C1.y);
		else if (S.y > C2.y)
			dist_squared -= sq(S.y - C2.y);
		if (S.z < C1.z)
			dist_squared -= sq(S.z - C1.z);
		else if (S.z > C2.z)
			dist_squared -= sq(S.z - C2.z);
		return dist_squared > 0;
	}

	@Override
	public boolean intersects(Line line) {
		return true;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public Vector3d lbb() {
		double r = -_sphere.r();
		return Vector3ds.add(new Vector3d(_sphere.center()), r, r, r);
	}

	@Override
	public Vector3d rtf() {
		double r = _sphere.r();
		return Vector3ds.add(new Vector3d(_sphere.center()), r, r, r);
	}

	@Override
	public Vector3d center() {
		return _sphere.center();
	}

}
