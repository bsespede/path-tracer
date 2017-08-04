package ar.edu.itba.graphiccomp.group2.bounding;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.math.Box;
import ar.edu.itba.graphiccomp.group2.math.Line;

public interface BoundingVolume {

	boolean intersects(Box box);

	boolean intersects(Line line);

	Vector3d lbb();

	Vector3d rtf();

	Vector3d center();

}
