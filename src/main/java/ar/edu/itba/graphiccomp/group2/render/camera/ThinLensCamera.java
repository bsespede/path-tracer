package ar.edu.itba.graphiccomp.group2.render.camera;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ar.edu.itba.graphiccomp.group2.image.ViewPort;
import ar.edu.itba.graphiccomp.group2.math.Frustum;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.render.RayTracer;
import ar.edu.itba.graphiccomp.group2.util.Preconditions;

public final class ThinLensCamera extends Camera {

	private final double _aperture;
	private final double _focalDistance;

	public ThinLensCamera(Vector3d position, Frustum frustum, ViewPort viewPort, double aperture, double focalDistance) {
		super(position, frustum, viewPort);
		_aperture = aperture;
		_focalDistance = focalDistance;
	}
	
	public ThinLensCamera(Camera camera, double aperture, double focalDistance) {
		super(camera);
		_aperture = aperture;
		_focalDistance = focalDistance;
	}

	private double aperture() {
		return _aperture;
	}

	private double focalDistance() {
		return _focalDistance;
	}
	
	public void pointRay(RayTracer rayTracer, Line ray, double row, double column) {
		pointRay(rayTracer, ray, row, column, null);
	}

	// formulas from ray tracing from the ground up
	public void pointRay(RayTracer rayTracer, Line ray, double row, double column, Vector3d worldPos) {
		Preconditions.checkIsTrue(inViewPort(row, column));
		double scaledCol = column - 0.5f;
		double scaledRow = (1 - row) - 0.5f;
		Vector3d f = front();
		Vector3d r = right();
		Vector3d u = up();
		double fd = focalDistance();
		double aperture = aperture();
		double near = frustum().near();
		double width = frustum().width();
		double height = frustum().height();
		
		Vector2d diskSample = rayTracer.sampler().sampleUnitDisk();
		
		double spX = diskSample.x * aperture;
		double spY = diskSample.y * aperture;
		
		double px = width * scaledCol * fd / near;
		double py = height * scaledRow * fd / near;
		
		double dx = r.x * (px - spX) + u.x * (py - spY) + f.x * fd;
		double dy = r.y * (px - spX) + u.y * (py - spY) + f.y * fd;
		double dz = r.z * (px - spX) + u.z * (py - spY) + f.z * fd;
		
		Vector3d dir = new Vector3d(dx, dy, dz);
		dir.normalize();
		ray.setD(dir);
		
		double eyeX = ray.p0().x + spY * u.x + spX * r.x;
		double eyeY = ray.p0().y + spY * u.y + spX * r.y;
		double eyeZ = ray.p0().z + spY * u.z + spX * r.z;
		
		ray.setP0(new Vector3d(eyeX, eyeY, eyeZ));
		
		if (worldPos != null) {
			worldPos.set(dx, dy, dz);
		}
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
