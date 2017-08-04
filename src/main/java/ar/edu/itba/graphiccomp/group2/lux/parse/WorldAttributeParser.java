package ar.edu.itba.graphiccomp.group2.lux.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.light.AmbientLight;
import ar.edu.itba.graphiccomp.group2.light.AreaLight;
import ar.edu.itba.graphiccomp.group2.light.DirectionalLight;
import ar.edu.itba.graphiccomp.group2.light.Light;
import ar.edu.itba.graphiccomp.group2.light.PointLight;
import ar.edu.itba.graphiccomp.group2.light.SpotLight;
import ar.edu.itba.graphiccomp.group2.lux.LuxCommand;
import ar.edu.itba.graphiccomp.group2.material.Material;
import ar.edu.itba.graphiccomp.group2.material.MaterialMix;
import ar.edu.itba.graphiccomp.group2.material.Texture2D;
import ar.edu.itba.graphiccomp.group2.math.Box;
import ar.edu.itba.graphiccomp.group2.math.Transform;
import ar.edu.itba.graphiccomp.group2.math.Triangles;
import ar.edu.itba.graphiccomp.group2.mesh.BoxMesh;
import ar.edu.itba.graphiccomp.group2.mesh.DiscMesh;
import ar.edu.itba.graphiccomp.group2.mesh.SphereMesh;
import ar.edu.itba.graphiccomp.group2.mesh.TriangleMesh;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;
import ar.edu.itba.graphiccomp.group2.scene.Scene;
import ar.edu.itba.graphiccomp.group2.util.PeekableScanner;
import ar.edu.itba.graphiccomp.group2.util.Preconditions;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public class WorldAttributeParser {

	private Transform curTransf;
	private Material curMaterial;
	private boolean parsingAttribute = true;

	public void parse(Scene scene, PeekableScanner scanner, Map<String, Material> materials, List<Geometry> geometries, Map<String, Texture2D> textures, File inputFile)
			throws FileNotFoundException {
		LuxCommand cmd = new LuxCommand();
		while (scanner.hasNext()) {
			String line = scanner.peek().trim();
			if (!line.isEmpty() && !line.startsWith("#")) {
				if (line.equals("WorldEnd")) {
					break;
				} else {
					LuxCommand auxCommand = cmd.parse(line);
					switch (auxCommand.name()) {
					default: {
						if (cmd.executeCommand()) {
							if (parsingAttribute || cmd.name().equals("Include") || cmd.name().equals("MakeNamedMaterial")) {
								parse(scene, cmd, materials, geometries, textures, inputFile);
								cmd = auxCommand;
							} else {
								throw new IllegalStateException();
							}
						}
					}
					}
				}
			}
			scanner.consume();
		}
		parse(scene, cmd, materials, geometries, textures, inputFile);
	}

	private void parse(Scene scene, LuxCommand cmd, Map<String, Material> materials, List<Geometry> geometries, Map<String, Texture2D> textures, File inputFile)
			throws FileNotFoundException {
		switch (cmd.name()) {
		case "LightSource": {
			switch (cmd.description()) {
			case "point": {
				Vector3d color = Vector3ds.parse(cmd.param("color L"));
				Light light = new PointLight(curTransf.translation());
				light.setDiffuse(color);
				scene.addLight(light);
				break;
			}
			case "distant": {
				Vector3d color = Vector3ds.parse(cmd.param("color L"));
				Vector3d from = Vector3ds.parse(cmd.param("point from"));
				Vector3d to = Vector3ds.parse(cmd.param("point to"));
				to.sub(from);
				Light light = new DirectionalLight(to);
				light.setDiffuse(color);
				scene.addLight(light);
				break;
			}
			case "infinite": {
				Vector3d color = Vector3ds.parse(cmd.param("color L"));
				scene.addLight(new AmbientLight(color));
				break;
			}
			case "spot": {
				Vector3d color = Vector3ds.parse(cmd.param("color L"));
				Vector3d from = Vector3ds.parse(cmd.param("point from"));
				Vector3d to = Vector3ds.parse(cmd.param("point to"));
				double exp = Double.parseDouble(cmd.param("float conedeltaangle").get(0));
				to.sub(from);
				Light light = new SpotLight(curTransf.translation(), to, exp, Math.toRadians(30));
				light.setDiffuse(color);
				scene.addLight(light);
				break;
			}
			default: {
//				System.out.println("[WARN] Ignoring: " + cmd.name() + " " + cmd.description());
				break;
			}
			}
			break;
		}
		case "Include": {
			Scanner s = new Scanner(new File(inputFile.getParentFile().getPath()+"/"+cmd.description()));
			PeekableScanner scanner = new PeekableScanner(s);
			parse(scene, scanner, materials, geometries, textures, inputFile);
			break;
		}
		case "Transform": {
			double[] matrify = new double[16];
			for (int i = 0; i < cmd.paramsList().size(); i++) {
				matrify[i] = Double.parseDouble(cmd.paramsList().get(i));
			}
			curTransf = new Transform();
			Matrix4d matrix = new Matrix4d(matrify);
			matrix.transpose();
			curTransf.setM(matrix);
			break;
		}
		case "Shape": {
			switch (cmd.description()) {
			case "plane": {
				Geometry geometry = new Geometry(cmd.param("string name", 0) + " plane");
				geometry.setLocalTranslation(curTransf.translation());
				Vector3d normal = Vector3ds.parse(cmd.param("normal N"));
				// FIXME: Hacer un PlaneMesh!!!
				geometry.setMesh(new DiscMesh(normal, 1000));
				geometry.setMaterial(curMaterial);
				geometry.buildBoundingVolume();
				System.out.println("[WARN] Configurar mas eficientemente Bounding Volume para la caja!");
				geometries.add(geometry);
				break;
			}
			case "trianglemesh": {
				String name = cmd.param("string name", 0);
				System.out.println("[DEBUG] Parser: loading trianglemesh " + name);
				List<Integer> indexes = integerList(cmd.param("integer indices"));
				List<Vector3d> vertices = vertexList(cmd.param("point P"));
				List<Vector3d> normals = generateNormals(indexes, vertices);
				List<Vector2d> uvs = Collections.emptyList();
				Geometry geometry = new Geometry(name);
				geometry.setTransform(curTransf);
				Box meshBox = Triangles.boundingBox(vertices);
				geometry.setMesh(new TriangleMesh(indexes, vertices, normals, uvs, meshBox));
				geometry.setMaterial(curMaterial);
				geometry.buildBoundingVolume();
				geometries.add(geometry);
				System.out.println("[DEBUG] Parser: trianglemesh " + geometry.name() + " OK!");
				break;
			}
			case "mesh": {
				String name = cmd.param("string name", 0);
				System.out.println("[DEBUG] Parser: loading mesh " + name);
				List<Integer> indexes = integerList(cmd.param("integer triindices"));
				List<Vector3d> vertices = vertexList(cmd.param("point P"));
				List<Vector3d> normals = vertexList(cmd.param("normal N"));
				List<Vector2d> uvs = vertex2DList(cmd.param("float uv"));
				Geometry geometry = new Geometry(name);
				geometry.setTransform(curTransf);
				Box meshBox = Triangles.boundingBox(vertices);
				geometry.setMesh(new TriangleMesh(indexes, vertices, normals, uvs, meshBox));
				geometry.setMaterial(curMaterial);
				geometry.buildBoundingVolume();
				geometries.add(geometry);
				System.out.println("[DEBUG] Parser: mesh " + geometry.name() + " OK!");
				break;
			}
			case "sphere": {
				Geometry geometry = new Geometry(cmd.param("string name", 0) + " sphere");
				geometry.setTransform(curTransf);
				float r = Float.parseFloat(cmd.param("float radius", 0));
				geometry.setMesh(new SphereMesh(r));
				geometry.setMaterial(curMaterial);
				geometry.buildBoundingVolume();
				geometries.add(geometry);
				break;
			}
			case "box": {
				String name = cmd.param("string name", 0);
				System.out.println("[DEBUG] Parser: loading box " + name);
				Geometry geometry = new Geometry(name);
				Vector3d halfExtents = new Vector3d(
					Double.parseDouble(cmd.param("float width", 0)), 
					Double.parseDouble(cmd.param("float height", 0)),
					Double.parseDouble(cmd.param("float depth", 0))
				);
				geometry.setTransform(curTransf);
				geometry.setMesh(new BoxMesh(halfExtents));
				geometry.setMaterial(curMaterial);
				geometry.buildBoundingVolume();
				geometries.add(geometry);
				System.out.println("[DEBUG] Parser: mesh " + geometry.name() + " OK!");
				break;
			}
			}
			break;
		}
		case "Texture": {
			File file = new File(inputFile.getParentFile().getParent() + "/" + cmd.param("string filename", 0));
			textures.put(cmd.description(), Texture2D.fromIS(new FileInputStream(file)));
		}
		case "NamedMaterial": {
			curMaterial = materials.get(cmd.description());
			break;
		}
		case "MakeNamedMaterial": {
			Material mat = null;
			switch (cmd.param("string type").get(0)) {
			case "matte": {
				mat = new Material(cmd.description());
				if (cmd.params().containsKey("color Kd")) {
					Vector3d kd = Vector3ds.parse(cmd.param("color Kd"));
					mat.setMatte(kd);
				}
				if (cmd.params().containsKey("texture Kd")) {
					mat.setTexture(textures.get(cmd.param("texture Kd", 0)));
					if (!cmd.params().containsKey("color Kd")) {
						mat.setMatte(new Vector3d(1, 1, 1));
					}
				}
				break;
			}
			case "metal2": {
				mat = new Material(cmd.description());
				String uroughness = null;
				if (cmd.param("float uroughness").isEmpty()) {
					uroughness = "0.075";
				} else {
					uroughness = cmd.param("float uroughness").get(0);
				}
				double shininess = Double.parseDouble(uroughness);
				if (cmd.params().containsKey("color Kr")) {
					Vector3d kr = Vector3ds.parse(cmd.param("color Kr"));
					mat.setMetal(kr, shininess);
				}
				if (cmd.params().containsKey("texture Kr")) {
					mat.setTexture(textures.get(cmd.param("texture Kr", 0)));
					if (!cmd.params().containsKey("color Kr")) {
						mat.setMetal(new Vector3d(1, 1, 1), shininess);
					}
				}
				break;
			}
			case "mirror": {
				mat = new Material(cmd.description());
				if (cmd.params().containsKey("color Kr")) {
					Vector3d kr = Vector3ds.parse(cmd.param("color Kr"));
					mat.setMirror(kr);
				}
				if (cmd.params().containsKey("texture Kr")) {
					mat.setTexture(textures.get(cmd.param("texture Kr", 0)));
					if (!cmd.params().containsKey("color Kr")) {
						mat.setMirror(Vector3ds.constant(1));
					}
				}
				break;
			}
			case "glass": {
				Vector3d kt = Vector3ds.parse(cmd.param("color Kt"));
				mat = new Material(cmd.description());
				double refraction = Double.parseDouble(cmd.param("float index").get(0));
				if (cmd.params().containsKey("color Kr")) {
					Vector3d kr = Vector3ds.parse(cmd.param("color Kr"));
					mat.setGlass(kt, kr, refraction);
				}
				if (cmd.params().containsKey("texture Kr")) {
					mat.setTexture(textures.get(cmd.param("texture Kr", 0)));
					if (!cmd.params().containsKey("color Kr")) {
						mat.setGlass(kt, new Vector3d(1, 1, 1), refraction);
					}
				}
				break;
			}
			case "mix": {
				String material1Name = cmd.param("string namedmaterial1", 0);
				String material2Name = cmd.param("string namedmaterial2", 0);
				String amount = Optional.ofNullable(cmd.param("float texture ", 0)).orElse("0.5");
				Material m1 = materials.get(material1Name);
				Preconditions.checkIsTrue(m1 != null, "Material " + material1Name + " is required");
				Material m2 = materials.get(material2Name);
				Preconditions.checkIsTrue(m2 != null, "Material " + material2Name + " is required");
				mat = new MaterialMix().build(m1, m2, Double.parseDouble(amount));
				break;
			}
			case "null": {
				mat = new Material(cmd.description());
				break;
			}
			default: {
				System.out.println("[WARN] Ignoring " + cmd.name() + " " + cmd.param("string type", 0));
				break;
			}
			}
			materials.put(cmd.description(), mat);
			break;
		}
		case "AreaLightSource":
			String name = cmd.description();
			System.out.println("[DEBUG] Parser: loading area light " + name);
			Vector3d color = Vector3ds.parse(cmd.param("color L"));
			AreaLight light = new AreaLight();
			light.setDiffuse(color).setSpecular(color);
			curMaterial.setEmissive(light);
			break;
		case "LightGroup":
		case "PortalShape":
		case "MakeNamedVolume":
		case "Interior":
		case "Exterior":
		case "AttributeEnd":
			curMaterial = new Material().setMatte(new Vector3d(.5, .5, .5));
//			curTransf = new Transform();
			break;
		default:
//			System.out.println("[WARN] Ignoring: " + cmd.name() + " " + cmd.description());
			break;
		}
	}

	private List<Integer> integerList(List<String> list) {
		List<Integer> ints = new ArrayList<>();
		for (String s : list) {
			ints.add(Integer.valueOf(s));
		}
		return ints;
	}

	private List<Vector3d> vertexList(List<String> list) {
		List<Vector3d> vertices = new ArrayList<>();
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			vertices.add(new Vector3d(Float.valueOf(it.next()), Float.valueOf(it.next()), Float.valueOf(it.next())));
		}
		return vertices;
	}

	private List<Vector2d> vertex2DList(List<String> list) {
		List<Vector2d> vertices = new ArrayList<>();
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			vertices.add(new Vector2d(Float.valueOf(it.next()), Float.valueOf(it.next())));
		}
		return vertices;
	}

	private List<Vector3d> generateNormals(List<Integer> indexes, List<Vector3d> vertices) {
		int trisCount = indexes.size() / 3;
		final Vector3d t1 = new Vector3d();
		final Vector3d t2 = new Vector3d();
		Vector3d[] ns = new Vector3d[vertices.size()];
		for (int i = 0; i < trisCount; i ++) {
			int j = i * 3;
			final int index1 = indexes.get(j);
			final int index2 = indexes.get(j + 1);
			final int index3 = indexes.get(j + 2);
			Vector3d a = vertices.get(index1);
			Vector3d b = vertices.get(index2);
			Vector3d c = vertices.get(index3);
			t1.set(b);
			t1.sub(a);
			t2.set(c);
			t2.sub(a);
			Vector3d n = new Vector3d();
			n.cross(t1, t2);
			n.normalize();
			ns[index1] = n;
			ns[index2] = n;
			ns[index3] = n;
		}
		return Arrays.asList(ns);
	}
}
