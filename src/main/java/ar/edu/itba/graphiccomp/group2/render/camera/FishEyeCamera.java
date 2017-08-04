package ar.edu.itba.graphiccomp.group2.render.camera;

import javax.vecmath.Vector3d;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ar.edu.itba.graphiccomp.group2.image.ViewPort;
import ar.edu.itba.graphiccomp.group2.math.Frustum;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.render.RayTracer;
import ar.edu.itba.graphiccomp.group2.util.Preconditions;

public final class FishEyeCamera extends Camera {

	private final double _phiMax;

	public FishEyeCamera(Vector3d position, Frustum frustum, ViewPort viewPort, double phiMax) {
		super(position, frustum, viewPort);
		_phiMax = phiMax;
	}
	
	public FishEyeCamera(Camera camera, double phiMax) {
		super(camera);
		_phiMax = phiMax;
	}
	
	public double phiMax() {
		return _phiMax;
	}

	public boolean inViewPort(double row, double column) {
		return 0 <= column && column <= 1 && 0 <= row && row <= 1;
	}

	public void pointRay(RayTracer rayTracer, Line ray, double row, double column) {
		pointRay(ray, row, column, null);
	}

	// formulas from http://paulbourke.net/dome/fisheye/	
	public void pointRay(Line ray, double row, double column, Vector3d worldPos) {
		Preconditions.checkIsTrue(inViewPort(row, column));
		double scaledCol = column - 0.5f;
		double scaledRow = (1 - row) - 0.5f;
		Vector3d f = front();
		Vector3d r = right();
		Vector3d u = up();
		double width = frustum().width();
		double height = frustum().height();
		double phiMax = phiMax() * Math.PI / 180;
		
		double ppx = width * scaledCol;
		double ppy = height * scaledRow;
		
		double rx = 2.0 * ppx / width;
		double ry = 2.0 * ppy / height;
		double rSquared = Math.sqrt(rx * rx + ry * ry);
		
		if (rSquared <= 1.0) {			
			double psi = calcPhi(rx, ry, rSquared);
			double sinPhi = (float) Math.sin(psi);
			double cosPhi = (float) Math.cos(psi);
			double thetha = rSquared * phiMax / 2;
			double sinTheta = (float) Math.sin(thetha);
			double cosTheta = (float) Math.cos(thetha);
			double dx = r.x * sinTheta * cosPhi + u.x * sinTheta * sinPhi + f.x * cosTheta;
			double dy = r.y * sinTheta * cosPhi + u.y * sinTheta * sinPhi + f.y * cosTheta;
			double dz = r.z * sinTheta * cosPhi + u.z * sinTheta * sinPhi + f.z * cosTheta;
			ray.setD(new Vector3d(dx, dy, dz));
		} else {
			ray.setD(new Vector3d(0, 0, 0));
		}
	}

	private double calcPhi(double x, double y, double r) {
		if (r == 0) {
			return 0;
		} else if (x < 0) {
			return Math.PI - Math.asin(y / r);
		} else {
			return Math.asin(y / r);
		}
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
