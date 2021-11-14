import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import tnn.NeuralNetwork;

public class MissileField extends JPanel implements ActionListener, KeyListener{
	int framesPerSecond = 120;
	static int screenSizeWidth = 2120;
	static int screenSizeHeight = 1620;
	static double metersPerPixel = 0.1;

	static ArrayList<Missile> missiles;
	static ArrayList<Rect> rectangles;
	static ArrayList<Explosion> explosions;
	Timer timer = new Timer(1000/framesPerSecond, this);
	boolean isStart = false;

	public static void main(String[] args) {
		missiles = new ArrayList<Missile>();
		missiles.add(new Missile(
				new XYPair(screenSizeWidth/2, screenSizeHeight*3/4),
				new Color(255, 255, 255),
				metersPerPixel
				));
//		for(int i = 0; i < 50; i++) {
//			missiles.add(new Missile(
//					new XYPair(screenSizeWidth/2 + (i + 1)*20, screenSizeHeight*3/4),
//					new Color(255, 255, 255),
//					metersPerPixel
//					));
//		}
		
		rectangles = new ArrayList<Rect>();
		rectangles.add(new Rect(new XYPair(screenSizeWidth/2, 0), new XYPair(screenSizeWidth, 10), 0));
		rectangles.add(new Rect(new XYPair(screenSizeWidth/2, screenSizeHeight), new XYPair(screenSizeWidth, 10), 0));
		rectangles.add(new Rect(new XYPair(0, screenSizeHeight/2), new XYPair(10, screenSizeHeight), 0));
		rectangles.add(new Rect(new XYPair(screenSizeWidth, screenSizeHeight/2), new XYPair(10, screenSizeHeight), 0));
		rectangles.add(new Rect(new XYPair(800, 400), new XYPair(1600, 100), 0.1));
		rectangles.add(new Rect(new XYPair(1400, 850), new XYPair(1600, 100), -0.1));
		for(Rect rect: rectangles) {
			rect.setColor(new Color(160, 160, 160));
		}
		
		explosions = new ArrayList<Explosion>();
		
		JFrame jFrame = new JFrame();
		jFrame.setTitle("Missile");
		jFrame.setSize(screenSizeWidth, screenSizeHeight);
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		MissileField missileField = new MissileField();
		missileField.setPreferredSize(new Dimension(screenSizeWidth, screenSizeHeight));
		jFrame.addKeyListener(missileField);
		jFrame.add(missileField);
		jFrame.pack();
	}
	int x = 1;
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(new Color(0, 0, 0));
		g2d.fillRect(0, 0, screenSizeWidth, screenSizeHeight);
		if(!missiles.isEmpty()) {
			for(Missile missile: missiles) {
				missile.draw(g2d);
			}
		}
		for(Rect rect: rectangles) {
			rect.draw(g2d);
			if(!missiles.isEmpty()) {
				for(int i = missiles.size() - 1; i >= 0; i--) {
					if(missiles.get(i).isCollide(rect, g2d)) {
						Random r = new Random();
						XYPair collisionLoc = missiles.get(i).collisionLocation;
						for(int j = 0; j < 10; j++) {
							XYPair expLoc = collisionLoc.copy();
							expLoc.add(new XYPair((r.nextDouble() - 0.5)*5/metersPerPixel, (r.nextDouble() - 0.5)*5/metersPerPixel));
							explosions.add(new Explosion(expLoc, metersPerPixel));
						}
						missiles.remove(i);

						missiles.add(new Missile(
								new XYPair(screenSizeWidth/2, screenSizeHeight*3/4),
								new Color(255, 255, 255),
								metersPerPixel
								));
					}
				}
			}
		}
		if(!explosions.isEmpty()) {
			for(int i = explosions.size() - 1; i >= 0; i--) {
				if(explosions.get(i).draw(g2d)) {
					explosions.remove(i);
				}
			}
		}
		
		if(isStart) {
			timer.start();
		}
	}
	
	public void actionPerformed(ActionEvent arg0) {
		repaint();
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			for(Missile missile: missiles) {
				missile.isThrust = true;
			}
			if(!isStart) {
				isStart = true;
				timer.start();
				for(Missile missile: missiles) {
					missile.timeSinceUpdate = 0;
					missile.lastUpdateTime = System.currentTimeMillis();
				}
			}
			break;
		case KeyEvent.VK_DOWN:
			break;
		case KeyEvent.VK_LEFT:
			for(Missile missile: missiles) {
				missile.isLeftRotate = true;
			}
			//missiles.get(0).isLeftRotate = true;
			break;
		case KeyEvent.VK_RIGHT:
			for(Missile missile: missiles) {
				missile.isRightRotate = true;
			}
			//missiles.get(0).isRightRotate = true;
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			for(Missile missile: missiles) {
				missile.isThrust = false;
			}
			break;
		case KeyEvent.VK_DOWN:
			break;
		case KeyEvent.VK_LEFT:
			for(Missile missile: missiles) {
				missile.isLeftRotate = false;
			}
			//missiles.get(0).isLeftRotate = false;
			break;
		case KeyEvent.VK_RIGHT:
			for(Missile missile: missiles) {
				missile.isRightRotate = false;
			}
			//missiles.get(0).isRightRotate = false;
			break;
		}
	}

	public void keyTyped(KeyEvent e) {
		
	}
}
