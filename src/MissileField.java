import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MissileField extends JPanel implements ActionListener, KeyListener{
	int framesPerSecond = 90;
	static int screenSizeWidth = 1620;
	static int screenSizeHeight = 1620;
	static double metersPerPixel = 0.1;

	static ArrayList<Missile> missiles;
	Timer timer = new Timer(1000/framesPerSecond, this);
	boolean isStart = false;

	public static void main(String[] args) {
		missiles = new ArrayList<Missile>();
		missiles.add(new Missile(
				new XYPair(screenSizeWidth/2, screenSizeHeight*3/4),
				new Color(255, 255, 255),
				metersPerPixel
				));
		
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
	
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(new Color(0, 0, 0));
		g2d.fillRect(0, 0, screenSizeWidth, screenSizeHeight);
		for(Missile missile: missiles) {
			missile.draw(g2d);
		}
		/*g2d.setColor(new Color(255, 255, 255));
		g2d.fillRect(0, 600, 1200, 20);
		g2d.fillOval(300, 300, 50, 50);*/
		
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
