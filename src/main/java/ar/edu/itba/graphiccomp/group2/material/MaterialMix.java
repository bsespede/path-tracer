package ar.edu.itba.graphiccomp.group2.material;

import static ar.edu.itba.graphiccomp.group2.util.Vector3ds.lerp;

import javax.vecmath.Vector3d;

public class MaterialMix {

	public Material build(Material m1, Material m2, double t) {
		Material mix = new Material(String.format("mix(%s,%s)", m1.name(), m2.name()));
		mix.setTexture(mixTexture(m1.texture(), m2.texture(), t));
		mix.setShader(m1.shader());
		mix.setType(m1.type());
		mix.setRefractionIndex((1 - t) * m1.refractionIndex() + t * m2.refractionIndex());
		mix.ka().set(lerp(m1.ka(), m2.ka(), t));
		mix.kd().set(lerp(m1.kd(), m2.kd(), t));
		mix.kr().set(lerp(m1.kr(), m2.kr(), t));
		mix.ks().set(lerp(m1.ks(), m2.ks(), t));
		mix.kt().set(lerp(m1.kt(), m2.kt(), t));
		return mix;
	}

	private Texture2D mixTexture(Texture2D t1, Texture2D t2, double t) {
		if (!t1.size().equals(t2.size())) {
			String errror = String.format("Incompatible texture size: %s != %s", t1.size(), t2.size());
			throw new IllegalStateException(errror);
		}
		Texture2D texture = new Texture2D(t1.size());
		for (int row = 0; row < texture.rowsCount(); row++) {
			for (int column = 0; column < texture.columnsCount(); column++) {
				Vector3d c1 = t1.colorAt(row, column);
				Vector3d c2 = t1.colorAt(row, column);
				texture.colors()[row][column] = lerp(c1, c2, t);
			}
		}
		return texture;
	}
}
