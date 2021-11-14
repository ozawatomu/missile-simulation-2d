import java.awt.Color;
import java.awt.Graphics2D;

public class Line {
	XYPair startPoint;
	XYPair endPoint;
	
	public Line(XYPair pointA, XYPair pointB) {
		startPoint = pointA;
		endPoint = pointB;
	}
	
	public XYPair getCollisionPoint(Line line) {
		double determinant = (startPoint.x - endPoint.x)*(line.startPoint.y - line.endPoint.y) - (startPoint.y - endPoint.y)*(line.startPoint.x - line.endPoint.x);
		if(determinant == 0) {
			return null;
		}else {
			double t = ((startPoint.x - line.startPoint.x)*(line.startPoint.y - line.endPoint.y) - (startPoint.y - line.startPoint.y)*(line.startPoint.x - line.endPoint.x))/determinant;
			double u = -1*(((startPoint.x - endPoint.x)*(startPoint.y - line.startPoint.y) - (startPoint.y - endPoint.y)*(startPoint.x - line.startPoint.x))/determinant);
			if(t >= 0 && t <= 1 && u >= 0 && u <= 1) {
				double px = ((startPoint.x*endPoint.y - startPoint.y*endPoint.x)*(line.startPoint.x - line.endPoint.x) - (startPoint.x - endPoint.x)*(line.startPoint.x*line.endPoint.y - line.startPoint.y*line.endPoint.x))/determinant;
				double py = ((startPoint.x*endPoint.y - startPoint.y*endPoint.x)*(line.startPoint.y - line.endPoint.y) - (startPoint.y - endPoint.y)*(line.startPoint.x*line.endPoint.y - line.startPoint.y*line.endPoint.x))/determinant;
				return new XYPair(px, py);
			}
			return null;
		}
	}
	
	public void draw(Graphics2D g) {
		g.setColor(new Color(255, 0, 0));
		g.drawLine((int) startPoint.x, (int) startPoint.y, (int) endPoint.x, (int) endPoint.y);
	}
}
