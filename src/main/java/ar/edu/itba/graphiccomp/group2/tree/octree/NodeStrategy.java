package ar.edu.itba.graphiccomp.group2.tree.octree;

import ar.edu.itba.graphiccomp.group2.math.Box;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;

public interface NodeStrategy<T> {

	boolean test(T object, Box bounds);

	boolean test(T object, Line line, CollisionContext collision);

}
