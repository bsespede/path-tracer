package ar.edu.itba.graphiccomp.group2.lux.parse;

import ar.edu.itba.graphiccomp.group2.render.RayTracer;
import ar.edu.itba.graphiccomp.group2.util.PeekableScanner;

public interface LuxAttributeParser {

	void parse(RayTracer rayTracer, PeekableScanner scanner);

}
