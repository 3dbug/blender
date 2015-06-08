package vrserver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import static java.lang.Math.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class HeadCamRot extends JFrame {

	static double DegToRad = (Math.PI / 180);
	static int Width = 1024;
	static int Height = 800;
	static int Radius = 200;
	static int CX = Width / 2;
	static int CY = Height / 2;

	private static final int HeadNaxRot = 70;
	static boolean Clamp = true;

	float bodyRot = 0;
	// don't touch this variable
	float camRot = 0;
	
	private DrawPanel drawPanel;

	public HeadCamRot() {
		setLayout(new BorderLayout());
		drawPanel = new DrawPanel();
		add(drawPanel, BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		HeadCamRot frame = new HeadCamRot();
		frame.setSize(Width, Height);
		frame.setTitle("HeadCamRot");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setFocusable(true);
		Listener listener = frame.new Listener();
		frame.addKeyListener(listener);
	}

	class Listener implements KeyListener {
		private static final float Step = 5;

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_KP_LEFT || key == KeyEvent.VK_LEFT) {
				camRot += Step;
				if (Clamp && camRot > 360) {
					camRot -= 360;
				}
			} else if (key == KeyEvent.VK_KP_RIGHT || key == KeyEvent.VK_RIGHT) {
				camRot -= Step;
				if (Clamp && camRot < 0) {
					camRot += 360;
				}
			} else if (key == KeyEvent.VK_ESCAPE) {
				System.exit(0);
			}
			repaint();
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
	}

	private double calcAngles() {

		System.out.println("------\npaint camRot=" + camRot);
		float delta = camRot- bodyRot;

		boolean locked = abs(delta) > HeadNaxRot;
		boolean flipped = abs(delta) > 180;

		if (locked &&  !flipped ) { // && !flipped
			System.out.println("locked=" + delta);
			if (delta > HeadNaxRot) {
				bodyRot = camRot - HeadNaxRot;
			}
			if (delta < HeadNaxRot) {
				bodyRot = camRot + HeadNaxRot;
			}
		}
		System.out.println("camRot=" + camRot + " body=" + bodyRot);

		if (flipped ) {
			System.out.println("flipped bodyRot=" + bodyRot);
			float orgRot = bodyRot;
			bodyRot = bodyRot - (360 - 2 * HeadNaxRot);
			delta = camRot - bodyRot;

			locked = abs(delta) > HeadNaxRot;
			if (locked) {
				System.out.println("flipped locked delta=" + delta);
				if (delta < HeadNaxRot) {
					bodyRot = camRot - 2 * HeadNaxRot;
				}
				if (delta > HeadNaxRot) {
					bodyRot = camRot + 2 * HeadNaxRot;
				}
			} else {
				System.out.println("flipped unlocked");
				//bodyRot = orgRot;
			}
			System.out.println("flipped bodyRot=" + bodyRot);
			// bodyRot %= 360;
			if ( bodyRot > 360  ) {
				System.out.println("recursion");
				return calcAngles();
			}
		}
		return delta;
	}

	class DrawPanel extends JPanel {

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.black);
			g.fillRect(0, 0, Width, Height);
			g.setColor(Color.white);

			double delta = calcAngles();
			double camangle = camRot * DegToRad;

			int x = (int) (CX + (cos(camangle) * Radius));
			int y = (int) (CY + (sin(camangle) * Radius));

			g.setColor(Color.gray);
			g.fillArc(CX - Radius / 2, CY - Radius / 2, Radius, Radius,
					(int) (bodyRot - HeadNaxRot), (int) HeadNaxRot * 2);
			g.setColor(Color.white);
			g.drawLine(CX, CY, x, Height - y);
			g.drawString("cam=" + (int) camRot, x, Height - y);
			g.drawString("delta=" + (int) delta, 10, CY);
			g.drawString("camRot=" + (int) camRot, 10, CY + 20);

			double bodyangle = bodyRot * DegToRad;

			int xb = (int) (CX + (cos(bodyangle) * Radius));
			int yb = (int) (CY + (sin(bodyangle) * Radius));

			g.setColor(Color.orange);
			g.drawLine(CX, CY, xb, Height - yb);
			g.drawString("body=" + (int) bodyRot, xb, Height - yb);
		}
	}
}
