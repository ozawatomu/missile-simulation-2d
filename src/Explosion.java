import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public class Explosion {
	double explosionDuration;
	double explosionRadius;
	double startTime;
	double mPP;
	double opacityCurveIntensity;
	double radiusCurveIntensity;
	Color color;
	XYPair location;
	
	public Explosion(XYPair location, double metersPerPixel) {
		this.location = location;
		startTime = System.currentTimeMillis();
		Random r = new Random();
		explosionDuration = 1000 + r.nextInt(2000);
		explosionRadius = 4 + r.nextDouble()*2;
		mPP = metersPerPixel;
		color = new Color((int) (255 - 20*r.nextDouble()), (int) (101.0 + 80*(2*(r.nextDouble() - 0.5))), (int) (5.0*r.nextDouble()));
		opacityCurveIntensity = 1 + 7*r.nextDouble();
		radiusCurveIntensity = 100*r.nextDouble();
	}
	
	public boolean draw(Graphics2D g) {
		Random r = new Random();
		double lifeLeft = explosionDuration - (System.currentTimeMillis() - startTime);
		if(lifeLeft < 0) {
			return true;
		}
		double lifeNorm = (double) (System.currentTimeMillis() - startTime)/explosionDuration;
		g.setColor(new Color((int) (255.0 - (255.0 - color.getRed())*lifeNorm), (int) (255.0 - (255.0 - color.getGreen())*lifeNorm), (int) (255.0 - (255.0 - color.getBlue())*lifeNorm), (int) Math.ceil(255 - (255*(1/(1 + opacityCurveIntensity*(1 - lifeNorm)))))));
		drawCenterCircle(g, (int) Math.round(location.x), (int) Math.round(location.y), (int) Math.round((explosionRadius - (1.0/(1.0 + radiusCurveIntensity*lifeNorm))*explosionRadius)/mPP));
		return false;
	}
	
	public void drawCenterCircle(Graphics2D g, int x, int y, int radius) {
		g.fillOval(x - radius, y - radius, 2*radius, 2*radius);
	}
}
