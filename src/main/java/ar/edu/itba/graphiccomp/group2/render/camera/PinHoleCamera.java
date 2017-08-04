package ar.edu.itba.graphiccomp.group2.render.camera;

import javax.vecmath.Vector3d;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ar.edu.itba.graphiccomp.group2.image.ViewPort;
import ar.edu.itba.graphiccomp.group2.math.Frustum;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.render.RayTracer;
import ar.edu.itba.graphiccomp.group2.util.Preconditions;

public final class PinHoleCamera extends Camera{

	public PinHoleCamera(Vector3d position, Frustum frustum, ViewPort viewPort) {
		super(position, frustum, viewPort);
	}

	public void pointRay(Line ray, double row, double column, Vector3d worldPos) {
		Preconditions.checkIsTrue(inViewPort(row, column));
		double scaledCol = column - 0.5f;
		double scaledRow = (1 - row) - 0.5f;
		Vector3d f = front();
		Vector3d r = right();
		Vector3d u = up();
		double near = frustum().near();
		double width = frustum().width();
		double height = frustum().height();		
		double dx = r.x * width * scaledCol + u.x * height * scaledRow + f.x * near;
		double dy = r.y * width * scaledCol + u.y * height * scaledRow + f.y * near;
		double dz = r.z * width * scaledCol + u.z * height * scaledRow + f.z * near;
		Vector3d dir = new Vector3d(dx, dy, dz);
		dir.normalize();		
		ray.setD(dir);
		if (worldPos != null) {
			worldPos.set(dx, dy, dz);
		}
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public void pointRay(RayTracer rayTracer, Line ray, double row, double column) {
		pointRay(ray, row, column, null);		
	}

}
