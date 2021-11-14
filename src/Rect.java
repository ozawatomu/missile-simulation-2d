import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;

public class Rect {
	XYPair location;
	XYPair dimensions;
	double rotation;
	Color color;
	
	public Rect(XYPair location, XYPair dimensions, double rotation) {
		this.location = location;
		this.dimensions = dimensions;
		this.rotation = rotation;
		color = new Color(255, 255, 255);
	}
	
	public XYPair getCollisionPoint(Rect rect) {
		for(Line lineA: getCollisionLines()) {
			for(Line lineB: rect.getCollisionLines()) {
				XYPair colA = lineA.getCollisionPoint(lineB);
				if(colA != null) {
					return colA;
				}
			}
		}
		for(XYPair corner: getCorners()) {
			if(rect.isInside(corner)) {
				return corner;
			}
		}
		for(XYPair corner: rect.getCorners()) {
			if(isInside(corner)) {
				return corner;
			}
		}
		return null;
	}
	
	public int getCollisionCount(Line line) {
		int collisionCount = 0;
		for(Line rectLine: getCollisionLines()) {
			XYPair colA = line.getCollisionPoint(rectLine);
			if(colA != null) {
				collisionCount++;
			}
		}
		return collisionCount;
	}
	
	public boolean isInside(XYPair point) {
		Line line = new Line(point, new XYPair(0, 999999));
		int collisionCount = getCollisionCount(line);
		if(collisionCount%2 == 0) {
			return false;
		}else {
			return true;
		}
	}
	
	public ArrayList<Line> getCollisionLines(){
		ArrayList<Line> collisionLines = new ArrayList<Line>();
		double hyp = Math.sqrt(Math.pow(dimensions.x/2, 2) + Math.pow(dimensions.y/2, 2));
		double ang = Math.atan((dimensions.x/2)/(dimensions.y/2));
		double angOpp = (Math.PI/2) - ang;
		double alpha = Math.atan((dimensions.y/2)/(dimensions.x/2)) - rotation;
		XYPair topLeft = new XYPair(location.x + Math.cos((Math.PI/2) - rotation + ang)*hyp, location.y - Math.sin((Math.PI/2) - rotation + ang)*hyp);
		XYPair topRight = new XYPair(location.x + Math.sin(rotation + ang)*hyp, location.y - Math.cos(rotation + ang)*hyp);
		XYPair bottomLeft = new XYPair(location.x - Math.sin(rotation + ang)*hyp, location.y + Math.cos(rotation + ang)*hyp);
		XYPair bottomRight = new XYPair(location.x - Math.cos((Math.PI/2) - rotation + ang)*hyp, location.y + Math.sin((Math.PI/2) - rotation + ang)*hyp);
		collisionLines.add(new Line(topLeft, topRight));
		collisionLines.add(new Line(topRight, bottomRight));
		collisionLines.add(new Line(bottomRight, bottomLeft));
		collisionLines.add(new Line(bottomLeft, topLeft));
		return collisionLines;
	}
	
	public ArrayList<XYPair> getCorners(){
		ArrayList<XYPair> corners = new ArrayList<XYPair>();
		double hyp = Math.sqrt(Math.pow(dimensions.x/2, 2) + Math.pow(dimensions.y/2, 2));
		double ang = Math.atan((dimensions.x/2)/(dimensions.y/2));
		double angOpp = (Math.PI/2) - ang;
		double alpha = Math.atan((dimensions.y/2)/(dimensions.x/2)) - rotation;
		XYPair topLeft = new XYPair(location.x + Math.cos((Math.PI/2) - rotation + ang)*hyp, location.y - Math.sin((Math.PI/2) - rotation + ang)*hyp);
		XYPair topRight = new XYPair(location.x + Math.sin(rotation + ang)*hyp, location.y - Math.cos(rotation + ang)*hyp);
		XYPair bottomLeft = new XYPair(location.x - Math.sin(rotation + ang)*hyp, location.y + Math.cos(rotation + ang)*hyp);
		XYPair bottomRight = new XYPair(location.x - Math.cos((Math.PI/2) - rotation + ang)*hyp, location.y + Math.sin((Math.PI/2) - rotation + ang)*hyp);
		corners.add(topLeft);
		corners.add(topRight);
		corners.add(bottomLeft);
		corners.add(bottomRight);
		return corners;
	}
	
	public void draw(Graphics2D g) {
		drawCenterRect(
				g,
				location,
				(int) Math.ceil(dimensions.x),
				(int) Math.ceil(dimensions.y),
				rotation
				);
		/*for(Line line: getCollisionLines()) {
			line.draw(g);
		}*/
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void drawCenterRect(Graphics2D g2d, XYPair location, int width, int length, double rotation) {
		Rectangle rectangle = new Rectangle(-width/2, -length/2, width, length);
		Color originalColor = g2d.getColor();
		g2d.setColor(color);
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
		g2d.setColor(originalColor);
	}
}
