
public class Pose2d {
	public double x, y, heading, velX, velY;
	
	public Pose2d (double x, double y, double heading, double velX, double velY) {
		this.x = x;
		this.y = y;
		this.heading = heading;
		this.velX = velX;
		this.velY = velY;
	}
	public Pose2d (double x, double y, double heading) {
		this(x,y,heading,0,0);
	}
	public Pose2d(double x, double y) {
		this(x,y,0);
	}
}
