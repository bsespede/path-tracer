package ar.edu.itba.graphiccomp.group2.app;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import ar.edu.itba.graphiccomp.group2.image.ViewPort;
import ar.edu.itba.graphiccomp.group2.imagecorrect.DynamicRangeCompression;
import ar.edu.itba.graphiccomp.group2.lux.DummyLuxParser;
import ar.edu.itba.graphiccomp.group2.render.RayTracer;
import ar.edu.itba.graphiccomp.group2.render.camera.Camera;

@SuppressWarnings("serial")
public final class GraphicalMainRayTracer implements Runnable {

	public static void main(String[] args) throws IOException {
		RayTracer rayTracer = new DummyLuxParser().parse(null);
		new GraphicalMainRayTracer(rayTracer).run();
	}

	private RayTracer _rayTracer;
	private Camera _camera;

	private final Vector3d _detaPos = new Vector3d();
	private final Vector3d _detaFront = new Vector3d();

	private ViewPort _buffer;
	private ViewPort _backBuffer;

	public GraphicalMainRayTracer(RayTracer rayTracer) throws FileNotFoundException {
		_rayTracer = rayTracer;
		_camera = _rayTracer.camera();
		_buffer = new ViewPort(_camera.viewPort());
		_backBuffer = new ViewPort(_camera.viewPort());
	}

	@Override
	public void run() {
		JFrame frame = new JFrame();
		frame.setSize(_camera.viewPort().width(), _camera.viewPort().height());
		frame.setVisible(true);
		frame.getContentPane().add(new FrameDrawer());
		frame.addKeyListener(new CameraKeyListener());
		DynamicRangeCompression corrector = new DynamicRangeCompression();
		while (true) {
			long starttime = System.currentTimeMillis();
			_camera.position().add(_detaPos);
			_detaPos.set(0, 0, 0);
			_camera.front().add(_detaFront);
			_camera.front().normalize();
			_camera.recalculateRight();
			_detaFront.set(0, 0, 0);
			_rayTracer.buildFrame();
			float elapsedtime = (System.currentTimeMillis() - starttime) / 1000f;
			System.out.println("[RayTracer] ended. Time: " + elapsedtime + " [s]");
			_backBuffer = corrector.correct(_camera.viewPort());
			frame.repaint();
		}
	}

	private class FrameDrawer extends Component {

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			_buffer = _backBuffer;
			for (int y = 0; y < _buffer.height(); y++) {
				for (int x = 0; x < _buffer.width(); x++) {
					Vector3f color = _buffer.pixel(x, y);
					g.setColor(new Color(color.x, color.y, color.z));
					_buffer.pixel(x, y);
					g.drawLine(x, y, x, y);
				}
			}
			g.setColor(Color.white);
			Vector3d camPos = _camera.position();
			g.drawString(String.format("Cam: [%.2f, %.2f, %.2f]", camPos.x, camPos.y, camPos.z), 10, _buffer.height() - 50);
			Vector3d lookDir = _camera.front();
			g.drawString(String.format("Dir: [%.2f, %.2f, %.2f]", lookDir.x, lookDir.y, lookDir.z), 10, _buffer.height() - 30);
			g.drawString("Keys: [W, A, S, D, R, F + ARROWS ]", 10, _buffer.height() - 10);
		}
	}

	private class CameraKeyListener implements KeyListener {

		private final Vector3d delta = new Vector3d();

		@Override
		public void keyTyped(KeyEvent e) {

		}

		@Override
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			double stepSize = 0.1;
			delta.set(0, 0, 0);
			switch (keyCode) {
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
				break;
			case KeyEvent.VK_W:
				delta.add(_camera.front());
				delta.scale(stepSize);
				_detaPos.add(delta);
				break;
			case KeyEvent.VK_S:
				delta.add(_camera.front());
				delta.scale(-stepSize);
				_detaPos.add(delta);
				break;
			case KeyEvent.VK_A:
				delta.add(_camera.right());
				delta.scale(-stepSize);
				_detaPos.add(delta);
				break;
			case KeyEvent.VK_D:
				delta.add(_camera.right());
				delta.scale(stepSize);
				_detaPos.add(delta);
				break;
			case KeyEvent.VK_F:
				delta.add(_camera.up());
				delta.scale(-stepSize * 0.5f);
				_detaPos.add(delta);
				break;
			case KeyEvent.VK_R:
				delta.add(_camera.up());
				delta.scale(stepSize * 0.5f);
				_detaPos.add(delta);
				break;

			case KeyEvent.VK_RIGHT:
				delta.add(_camera.right());
				delta.scale(stepSize);
				_detaFront.add(delta);
//				_camera.front().add(delta);
				break;
			case KeyEvent.VK_LEFT:
				delta.add(_camera.right());
				delta.scale(-stepSize * 0.5f);
				_detaFront.add(delta);
//				_camera.front().add(delta);
				break;
//			case KeyEvent.VK_DOWN:
//				delta.add(_camera.up());
//				delta.scale(-stepSize * 0.5f);
//				_detaFront.add(delta);
////				_camera.front().add(delta);
//				break;
//			case KeyEvent.VK_UP:
//				delta.add(_camera.up());
//				delta.scale(stepSize * 0.5f);
//				_detaFront.add(delta);
////				_camera.front().add(delta);
//				break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {

		}

	}

}
