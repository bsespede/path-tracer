package ar.edu.itba.graphiccomp.group2.tree.base;

import java.util.Collection;
import java.util.function.Consumer;

import ar.edu.itba.graphiccomp.group2.math.Box;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;

public interface TreeStructure<T> {

	TreeStructure<T> addAll(Collection<? extends T> objects);

	void test(Line line, CollisionContext collision);

	void traverse(Consumer<T> consumer);

	Box bounds();

}
