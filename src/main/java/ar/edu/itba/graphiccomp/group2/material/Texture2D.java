package ar.edu.itba.graphiccomp.group2.material;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.math.MathUtil;

public final class Texture2D {

	public static Texture2D solid(Vector3d v) {
		return new Texture2D(new Vector3d[][] { { v } });
	}

	public static Texture2D fromIS(InputStream is) {
		try {
			BufferedImage image = ImageIO.read(is);
			Vector3d[][] colors = new Vector3d[image.getHeight()][image.getWidth()];
			for (int x = 0; x < colors.length; x++) {
				for (int y = 0; y < colors[0].length; y++) {
					Color c = new Color(image.getRGB(y, x));
					Vector3d vector = new Vector3d(c.getRed(), c.getGreen(), c.getBlue());
					vector.scale(1 / 255f);
					colors[x][y] = vector;
				}
			}
			return new Texture2D(colors);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private Vector3d[][] _colors;
	private Vector2d _size;

	public Texture2D(Vector2d size) {
		_size = size;
		_colors = new Vector3d[(int) size.x][(int) size.y];
	}

	public Texture2D(Vector3d[][] colors) {
		_colors = colors;
		_size = new Vector2d(_colors.length, _colors[0].length);
	}

	public Vector3d colorAt(int row, int column) {
		return _colors[row][column];
	}

	public Vector3d colorAt(Vector2d uv) {
		checkOfBounds(uv);
		final double v = uv.y;
		final double u = uv.x;
		final double rows = _size.x;
		int row = Math.min((int) (v * rows), (int) (rows - 1));
		final double columns = _size.y;
		int column = Math.min((int) (u * columns), (int) (columns - 1));
		return _colors[row][column];
	}

	private void checkOfBounds(Vector2d uv) {
		if (uv.x < 0 || uv.x > 1 || uv.y < 0 || uv.y > 1) {
			System.out.println("[ERROR] Texture out of bounds: " + uv);
			uv.x = MathUtil.clamp(uv.x, 0, 1);
			uv.y = MathUtil.clamp(uv.y, 0, 1);
		}
	}

	public Vector2d size() {
		return _size;
	}

	public int rowsCount() {
		return (int) _size.x;
	}

	public int columnsCount() {
		return (int) _size.y;
	}

	public Vector3d[][] colors() {
		return _colors;
	}

}
