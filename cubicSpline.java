import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class cubicSpline {
	double x [];
	double y [];
	static double mag = 400;
	public cubicSpline(Pose2d p1, Pose2d p2) {
		this(p1,new Pose2d(p1.x + mag * Math.cos(p1.heading),p1.y + 100 * Math.sin(p1.heading)),p2,new Pose2d(p2.x + mag * Math.cos(p2.heading),p2.y + 100 * Math.sin(p2.heading)));
	}
	public cubicSpline(Pose2d p1, Pose2d t1, Pose2d p2, Pose2d t2) {
		x = new double[4];
		y = new double[4];

		x[0] = p1.x;
		x[1] = t1.x - p1.x;
		x[3] = t2.x - 3*p2.x + 2*x[0] + x[1];
		x[2] = p2.x - x[0] - x[1] - x[3];
		
		y[0] = p1.y;
		y[1] = t1.y - p1.y;
		y[3] = t2.y - 3*p2.y + 2*y[0] + y[1];
		y[2] = p2.y - y[0] - y[1] - y[3];
	}
	public double getRelX(double t1, double t2) {
		double fidelity = 1.0E-4;
		double sum = 0;
		double lastT = t1;
		for (double t = t1; t < t2; t += fidelity) {
			Pose2d start = getPose2d(t);
			Pose2d end = getPose2d(lastT);
			sum += Math.cos(start.heading)*(start.x-end.x) + Math.sin(start.heading)*(start.y-end.y);
			lastT = t;
		}
		Pose2d start = getPose2d(t2);
		Pose2d end = getPose2d(lastT);
		sum += Math.cos(start.heading)*(start.y-end.y) - Math.sin(start.heading)*(start.x-end.x);
		return sum;
	}
	public double getRelY(double t1, double t2) {
		double fidelity = 1.0E-4;
		double sum = 0;
		double lastT = t1;
		for (double t = t1; t < t2; t += fidelity) {
			Pose2d start = getPose2d(t);
			Pose2d end = getPose2d(lastT);
			sum += Math.cos(start.heading)*(start.y-end.y) - Math.sin(start.heading)*(start.x-end.x);
			lastT = t;
		}
		Pose2d start = getPose2d(t2);
		Pose2d end = getPose2d(lastT);
		sum += Math.cos(start.heading)*(start.y-end.y) - Math.sin(start.heading)*(start.x-end.x);
		return sum;
	}
	public Pose2d getPose2d(double t) {
		double p[] = getCoord(t);
		double v[] = getVel(t);
		return new Pose2d(p[0],p[1],Math.atan2(v[1],v[0]) + Math.toRadians(15) * Math.sin(Math.toRadians(180 * t)),v[0],v[1]);
	}
	public void drawSpline(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Stroke s = new BasicStroke(3.0f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,10.0f);
		g2.setStroke(s);
		g2.setColor(Color.LIGHT_GRAY);
		int size = 6;
		for (int i = 0; i < 100; i ++) {
			Pose2d p1 = getPose2d(i/100.0);
			Pose2d p2 = getPose2d((i+1)/100.0);
			g2.drawLine((int)p1.x,(int)p1.y,(int)p2.x,(int)p2.y);
		}
	}

	public void drawVectors(Graphics g) {
		for (int i = 0; i < 10; i ++) {
			Pose2d p = getPose2d(i/10.0);
			g.setColor(Color.BLUE);
			g.drawLine((int)p.x, (int)p.y, (int)(p.x+p.velX), (int)(p.y+p.velY));
		}
	}
	
	public double[] getCoord(double t) {
		double p [] = {0,0};
		for (int i = 0; i < 4; i ++) {
			p[0] += this.x[i] * Math.pow(t, i);
			p[1] += this.y[i] * Math.pow(t, i);
		}
		return p;
	}
	public double[] getVel(double t) {
		double v [] = {0,0};
		for (int i = 0; i < 3; i ++) {
			v[0] += this.x[i+1] * Math.pow(t, i) * (i + 1);
			v[1] += this.y[i+1] * Math.pow(t, i) * (i + 1);
		}
		if (v[0] == 0 && v[1] == 0) {
			double tE = 0.001;
			double p1 [] = getCoord(t);
			double p2 [] = getCoord(t+tE);
			v[0] = (p1[0]-p2[0])/tE;
			v[1] = (p1[1]-p2[1])/tE;
		}
		return v;
	}

	public double getHeadingVel(double t) {
		return (getPose2d(t+0.0001).heading - getPose2d(t-0.0001).heading)/0.0002;
	}
}
