package ar.edu.itba.graphiccomp.group2.scene;

import ar.edu.itba.graphiccomp.group2.math.Line;

public interface RayTargeteable {

	boolean intersects(Line line, CollisionContext result);

}
