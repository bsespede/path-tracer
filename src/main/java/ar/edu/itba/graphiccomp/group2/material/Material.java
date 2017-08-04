package ar.edu.itba.graphiccomp.group2.material;

import java.io.InputStream;
import java.util.Optional;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.light.AreaLight;
import ar.edu.itba.graphiccomp.group2.materialsampler.MaterialSampler;
import ar.edu.itba.graphiccomp.group2.materialsampler.MatteMaterialSampler;
import ar.edu.itba.graphiccomp.group2.materialsampler.MetalMaterialSampler;
import ar.edu.itba.graphiccomp.group2.shader.CookTorranceShader;
import ar.edu.itba.graphiccomp.group2.shader.LambertianShader;
import ar.edu.itba.graphiccomp.group2.shader.PhongShader;
import ar.edu.itba.graphiccomp.group2.shader.Shader;

public final class Material {

	public static final double AIR_REFRACTION = 1;

	public static enum MaterialType {
		GLASS, MATTE, METAL, MIRROR, NONE;

		public boolean isMetal() {
			return equals(METAL);
		}

		public boolean isMirror() {
			return equals(MIRROR);
		}

		public boolean isGlass() {
			return equals(GLASS);
		}

		public boolean isMatte() {
			return equals(MATTE);
		}
	};

	private String _name;

	/**
	 * Specular reflection constant, the ratio of reflection of the specular
	 * term of incoming light.
	 */
	private final Vector3d _ks = new Vector3d(1, 1, 1);

	/**
	 * Diffuse reflection constant, the ratio of reflection of the diffuse term
	 * of incoming light (Lambertian reflectance).
	 */
	private final Vector3d _kd = new Vector3d(1, 1, 1);

	/**
	 * Ambient reflection constant, the ratio of reflection of the ambient term
	 * present in all points in the scene rendered.
	 */
	private final Vector3d _ka = new Vector3d(1, 1, 1);

	/**
	 * Fraction of light transmitted through the surface
	 */
	private final Vector3d _kt = new Vector3d(1, 1, 1);

	/**
	 * The reflectivity color of the material
	 */
	private final Vector3d _kr = new Vector3d(1, 1, 1);

	private Shader _shader = new LambertianShader();

	private Texture2D _texture;

	private double _refractionIndex = 1;

	private MaterialType _type = MaterialType.MATTE;

	private Optional<AreaLight> _light = Optional.empty();

	private MaterialSampler _sampler = new MatteMaterialSampler();

	public Material() {
	}

	public Material(String name) {
		_name = name;
	}

	public Material(Texture2D texture) {
		setTexture(texture);
	}

	public String name() {
		return _name;
	}

	public Vector3d ka() {
		return _ka;
	}

	public Vector3d kd() {
		return _kd;
	}

	public Vector3d ks() {
		return _ks;
	}

	public Vector3d kt() {
		return _kt;
	}

	public Vector3d kr() {
		return _kr;
	}

	public Material setProperties(Vector3d ka, Vector3d kd, Vector3d ks) {
		_ka.set(ka);
		_kd.set(kd);
		_ks.set(ks);
		return this;
	}

	public Material setType(MaterialType type) {
		_type = type;
		return this;
	}

	public MaterialType type() {
		return _type;
	}

	public Material setRefractionIndex(double refractionIndex) {
		_refractionIndex = refractionIndex;
		return this;
	}

	public double refractionIndex() {
		return _refractionIndex;
	}

	public Shader shader() {
		return _shader;
	}

	public Vector3d colorAt(Vector2d uv) {
		return _texture.colorAt(uv);
	}

	public Texture2D texture() {
		return _texture;
	}

	public void setKd(double rReflect, double gReflect, double bReflect) {
		_kd.set(rReflect, gReflect, bReflect);
	}

	public void setKs(double rReflect, double gReflect, double bReflect) {
		_ks.set(rReflect, gReflect, bReflect);
	}

	public Material setTexture(InputStream is) {
		return setTexture(Texture2D.fromIS(is));
	}

	public Material setTexture(Texture2D texture) {
		_texture = texture;
		return this;
	}

	public boolean hasTexture() {
		return _texture != null;
	}

	public Material setShader(Shader shader) {
		_shader = shader;
		return this;
	}

	public Material setMatte(Vector3d kd) {
		setType(MaterialType.MATTE);
		setShader(new LambertianShader());
		setProperties(kd, kd, kd);
		return this;
	}

	public Material setMetal(Vector3d ks, double shininess) {
		setType(MaterialType.METAL);
		setShader(new CookTorranceShader(shininess));
		setSampler(new MetalMaterialSampler());
		setProperties(ks, ks, ks);
		kr().set(new Vector3d(1, 1, 1));
		return this;
	}

	public Material setGlass(Vector3d kr, Vector3d kt, double refractionIndex) {
		return setGlass(kr, kt, refractionIndex, Optional.empty());
	}

	public Material setGlass(Vector3d kr, Vector3d kt, double refractionIndex, Optional<Integer> shiniess) {
		setType(MaterialType.GLASS);
		setRefractionIndex(refractionIndex);
		kr().set(kr);
		kt().set(kt);
		return this;
	}

	public Material setMirror(Vector3d kr) {
		setType(MaterialType.MIRROR);
		kr().set(kr);
		return this;
	}

	public boolean isEmissive() {
		return _light.isPresent();
	}

	public Optional<AreaLight> light() {
		return _light;
	}

	public void setEmissive(AreaLight light) {
		this._light = Optional.of(light);
	}

	public MaterialSampler sampler() {
		return _sampler;
	}

	public void setSampler(MaterialSampler sampler) {
		_sampler = sampler;
	}
}
