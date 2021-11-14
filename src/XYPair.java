
public class XYPair {
	double x;
	double y;
	
	public XYPair(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public XYPair(XYPair pointA, XYPair pointB) {
		x = pointB.x - pointA.x;
		y = pointB.y - pointA.y;
	}

	public double distanceTo(XYPair otherPair) {
		return Math.sqrt(Math.pow(x - otherPair.x, 2) + Math.pow(y - otherPair.y, 2));
	}
	
	public void multiply(double number) {
		x *= number;
		y *= number;
	}
	
	public void divide(double number) {
		x /= number;
		y /= number;
	}
	
	public void add(XYPair otherPair) {
		x += otherPair.x;
		y += otherPair.y;
	}
	
	public void rotate(double angleRadians) {
		double xNew = Math.cos(angleRadians)*x - Math.sin(angleRadians)*y;
		double yNew = Math.sin(angleRadians)*x + Math.cos(angleRadians)*y;
		x = xNew;
		y = yNew;
	}
	
	public XYPair copy() {
		return new XYPair(x, y);
	}
	
	public void project(XYPair otherPair) {
		double scale = dotProduct(otherPair)/Math.pow(otherPair.magnitude(), 2);
		x = otherPair.x*scale;
		y = otherPair.y*scale;
	}
	
	public double dotProduct(XYPair otherPair) {
		return x*otherPair.x + y*otherPair.y;
	}
	
	public double magnitude() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	
	public void setMagnitude(double scale) {
		x = x*scale/magnitude();
		y = y*scale/magnitude();
	}
	
	public void reverse() {
		x = -x;
		y = -y;
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
