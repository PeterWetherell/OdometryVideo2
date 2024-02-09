
public class point {
	public double x, y, heading, velX, velY;
	
	public point (double x, double y, double heading, double velX, double velY) {
		this.x = x;
		this.y = y;
		this.heading = heading;
		this.velX = velX;
		this.velY = velY;
	}
	public point (double x, double y, double heading) {
		this(x,y,heading,0,0);
	}
	public point(double x, double y) {
		this(x,y,0);
	}
}
