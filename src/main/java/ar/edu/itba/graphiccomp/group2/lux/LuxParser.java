package ar.edu.itba.graphiccomp.group2.lux;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.image.ViewPort;
import ar.edu.itba.graphiccomp.group2.lux.parse.GlobalAttributesParser;
import ar.edu.itba.graphiccomp.group2.lux.parse.WorldAttributeParser;
import ar.edu.itba.graphiccomp.group2.material.Material;
import ar.edu.itba.graphiccomp.group2.material.Texture2D;
import ar.edu.itba.graphiccomp.group2.math.Frustum;
import ar.edu.itba.graphiccomp.group2.render.RayTracer;
import ar.edu.itba.graphiccomp.group2.render.camera.Camera;
import ar.edu.itba.graphiccomp.group2.render.camera.PinHoleCamera;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;
import ar.edu.itba.graphiccomp.group2.scene.Scene;
import ar.edu.itba.graphiccomp.group2.tree.kd.SpatialKDTreeBuilder;
import ar.edu.itba.graphiccomp.group2.util.PeekableScanner;

public class LuxParser {

	public RayTracer parse(File inputFile) throws FileNotFoundException {
		Scanner s = new Scanner(inputFile);		
		System.out.println("[TRACE] Parser: started...");
		PeekableScanner scanner = new PeekableScanner(s);
		Scene scene = buildScene(scanner);
		Camera camera = buildCamera(scanner);
		RayTracer rayTracer = new RayTracer(scene, camera);
		System.out.println("[TRACE] Parser: Global attrbutes");
		GlobalAttributesParser globalParser = new GlobalAttributesParser();
		globalParser.parse(rayTracer, scanner);		
		scanner.consume();
		System.out.println("[TRACE] Parser: World attrbutes");
		WorldAttributeParser attributeParser = new WorldAttributeParser();
		Map<String, Material> materials = new HashMap<>();
		Map<String, Texture2D> textures = new HashMap<>();
		List<Geometry> geometries = new LinkedList<>();
		attributeParser.parse(scene, scanner, materials, geometries, textures, inputFile);
		scene.setTree(new SpatialKDTreeBuilder(geometries).get());
//		SpatialOctreeBuilder treeBuilder = new SpatialOctreeBuilder(new Box(new Vector3d(), new Vector3d(20, 20, 20)));
//		geometries.stream().forEach(g -> treeBuilder.add(g));
//		scene.setTree(treeBuilder.get());
		System.out.println("[TRACE] Parser: Done!");
		return rayTracer;
	}

	private Scene buildScene(PeekableScanner scanner) {
		return new Scene();
	}

	private Camera buildCamera(PeekableScanner scanner) {
		Vector3d position = new Vector3d();
		Frustum frustum = new Frustum(1, 10000, 2, 1);
		ViewPort viewPort = new ViewPort(frustum, 80);
		return new PinHoleCamera(position, frustum, viewPort);
	}
}
