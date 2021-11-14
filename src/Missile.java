import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Random;

import tnn.NeuralNetwork;

public class Missile {
	XYPair location;
	XYPair velocity;
	XYPair dimensions;
	double angularVelocity;
	double rotation;
	double mass;
	double maxThrust;
	boolean isThrust;
	boolean isLeftRotate;
	boolean isRightRotate;
	double gravityForce;
	Color color;
	double dragCoefficient;
	double angularCorrectionCoefficient;
	double metersPerPixel;
	double lastUpdateTime;
	double timeSinceUpdate;
	XYPair collisionLocation;
	
	public Missile(XYPair location, Color color, double metersPerPixel) {
		this.location = location;
		this.color = color;
		this.metersPerPixel = metersPerPixel;
		
		//https://en.wikipedia.org/wiki/Type_81_(missile)
		mass = 45;
		gravityForce = mass*9.81;
		maxThrust = 5000; //3500
		isThrust = false;
		dimensions = new XYPair(0.16, 2.7); //0.16, 2.7
		velocity = new XYPair(0, 0);
		rotation = 0;
		dragCoefficient = 0.002; //0.002
		angularCorrectionCoefficient = 0.000001; //0.000001
		lastUpdateTime = System.currentTimeMillis();
	}
	
	public void draw(Graphics2D g) {
		calculateUpdateTime();
		calculateVelocity();
		calculateAngularVelocity();
		move();
		if(isThrust) {
			double xOff = 2*-1*Math.sin(rotation)*(dimensions.y/2);
			double yOff = 2*Math.cos(rotation)*(dimensions.y/2);
			XYPair thrustLoc = new XYPair(location.x + xOff/metersPerPixel, location.y + yOff/metersPerPixel);
			Random r = new Random();
			g.setColor(new Color(255, 151, 0, r.nextInt(256)));
			drawCenterRect(
					g,
					thrustLoc,
					(int) Math.ceil(0.1/metersPerPixel),
					(int) Math.ceil(1.5/metersPerPixel),
					rotation
					);
		}
		if(isLeftRotate) {
			double angle = Math.atan((dimensions.y/3)/(dimensions.x*4));
			angle -= rotation;
			double hyp = Math.sqrt(Math.pow(dimensions.y/3, 2) + Math.pow(dimensions.x*4, 2));
			double xOff = Math.cos(angle)*hyp;
			double yOff = -1*Math.sin(angle)*hyp;
			XYPair thrustLoc = new XYPair(location.x + xOff/metersPerPixel, location.y + yOff/metersPerPixel);
			
			Random r = new Random();
			g.setColor(new Color(255, 151, 0, r.nextInt(256)));
			drawCenterRect(
					g,
					thrustLoc,
					(int) Math.ceil(dimensions.x*4/metersPerPixel),
					(int) Math.ceil(0.05/metersPerPixel),
					rotation
					);
		}
		if(isRightRotate) {
			double angle = Math.atan((dimensions.y/3)/(dimensions.x*4));
			angle += rotation;
			double hyp = Math.sqrt(Math.pow(dimensions.y/3, 2) + Math.pow(dimensions.x*4, 2));
			double xOff = -1*Math.cos(angle)*hyp;
			double yOff = -1*Math.sin(angle)*hyp;
			XYPair thrustLoc = new XYPair(location.x + xOff/metersPerPixel, location.y + yOff/metersPerPixel);
			
			Random r = new Random();
			g.setColor(new Color(255, 151, 0, r.nextInt(256)));
			drawCenterRect(
					g,
					thrustLoc,
					(int) Math.ceil(dimensions.x*4/metersPerPixel),
					(int) Math.ceil(0.05/metersPerPixel),
					rotation
					);
		}
		//g.fillRect((int) location.x, (int) location.y, (int) Math.ceil(dimensions.x/metersPerPixel), (int) Math.ceil(dimensions.y/metersPerPixel));
		g.setColor(color);
		drawCenterRect(
				g,
				location,
				(int) Math.ceil(dimensions.x/metersPerPixel),
				(int) Math.ceil(dimensions.y/metersPerPixel),
				rotation
				);
	}

	public void calculateUpdateTime() {
		double currentTime = System.currentTimeMillis();
		timeSinceUpdate = currentTime - lastUpdateTime;
		lastUpdateTime = currentTime;
	}
	
	public void calculateVelocity() {
		XYPair gravAcc = new XYPair(0, 1);
		gravAcc.setMagnitude(gravityForce/mass);
		gravAcc.divide(metersPerPixel);
		gravAcc.multiply(timeSinceUpdate/1000);
		
		velocity.add(gravAcc);
		if(isThrust) {
			XYPair thrustAcc = new XYPair(Math.sin(rotation)*10000, -1*Math.cos(rotation)*10000);
			thrustAcc.setMagnitude(maxThrust/mass);			
			thrustAcc.divide(metersPerPixel);
			thrustAcc.multiply(timeSinceUpdate/1000);
			velocity.add(thrustAcc);
		}
		
		//https://www.real-world-physics-problems.com/drag-force.html
		XYPair dragForce = velocity.copy();
		dragForce.reverse();
		dragForce.setMagnitude(dragCoefficient*1.225*(getCrossSectionalArea()*dimensions.x)*Math.pow(velocity.magnitude(), 2));
		XYPair dragAcc = dragForce.copy();
		dragAcc.divide(mass);
		dragAcc.divide(metersPerPixel);
		dragAcc.multiply(timeSinceUpdate/1000);
		velocity.add(dragAcc);
		//System.out.println("MACH: " + velocity.magnitude()*metersPerPixel/994.7); //velocity.magnitude()*metersPerPixel/994.7
	}
	
	private void calculateAngularVelocity() {
		if(isLeftRotate) {
			double leftRotate = 4*(timeSinceUpdate/1000);
			angularVelocity -= leftRotate;
		}
		if(isRightRotate) {
			double rightRotate = 4*(timeSinceUpdate/1000);
			angularVelocity += rightRotate;
		}
		rotation += Math.PI/4;
		double vel1 = getCrossSectionalArea();
		rotation -= Math.PI/4;

		rotation -= Math.PI/4;
		double vel2 = getCrossSectionalArea();
		rotation += Math.PI/4;
		
		if(vel1 > vel2) {
			angularVelocity -= (getCrossSectionalArea()/dimensions.x)*velocity.magnitude()*angularCorrectionCoefficient;
		}else if(vel2 > vel1){
			angularVelocity += (getCrossSectionalArea()/dimensions.x)*velocity.magnitude()*angularCorrectionCoefficient;
		}
		
		double angularVelocityDrag = (dragCoefficient/10)*Math.pow(angularVelocity, 2);
		if(angularVelocity > 0) {
			angularVelocity -= angularVelocityDrag;
		}else if(angularVelocity < 0) {
			angularVelocity += angularVelocityDrag;
		}
		//System.out.println(angularVelocity);
		
	}
	
	public void move() {
		//System.out.println(location.y);
		location.x += velocity.x*(timeSinceUpdate/1000);
		location.y += velocity.y*(timeSinceUpdate/1000);
		rotation += angularVelocity*(timeSinceUpdate/1000);
	}
	
	public double getCrossSectionalArea() {
		XYPair perpVel = velocity.copy();
		perpVel.rotate(Math.PI/2);
		XYPair lengthVector = new XYPair(0, dimensions.y);
		lengthVector.rotate(rotation);
		XYPair widthVector = new XYPair(dimensions.x, 0);
		widthVector.rotate(rotation);
		lengthVector.project(perpVel);
		widthVector.project(perpVel);
		return widthVector.magnitude() + lengthVector.magnitude();
		//System.out.println(widthVector.magnitude() + lengthVector.magnitude());
	}
	
	public boolean isCollide(Rect rect, Graphics2D g) {
		Rect missRect = new Rect(
				location.copy(),
				new XYPair((int) Math.ceil(dimensions.x/metersPerPixel), (int) Math.ceil(dimensions.y/metersPerPixel)),
				rotation
				);
		missRect.draw(g);
		XYPair collisionPoint = missRect.getCollisionPoint(rect);
		if(collisionPoint != null) {
			collisionLocation = collisionPoint;
			return true;
		}
		return false;
	}
	
	public void drawCenterRect(Graphics2D g2d, XYPair location, int width, int length, double rotation) {
		Rectangle rectangle = new Rectangle(-width/2, -length/2, width, length);
		//g2d.setColor(color);
		AffineTransform aT = new AffineTransform();
		aT.translate(location.x, location.y);
		aT.rotate(rotation);
		g2d.transform(aT);
		g2d.fill(rectangle);
		try {
			g2d.transform(aT.createInverse());
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
	}
}
