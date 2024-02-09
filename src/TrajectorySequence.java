import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;

public class TrajectorySequence {
	ArrayList<cubicSpline> traj;
	point lastP1, lastT1;
	double vel = 300;
	long start;
	public double t;
	public TrajectorySequence(point p1, point t1) {
		traj = new ArrayList<>();
		lastP1 = p1;
		lastT1 = t1;
	}
	public double getDist(double t1, double t2) {
		double sum = 0;
		double q = 0.0001;
		for (double t = t2; t < t1-q; t += q) {
			double velX = getVel(t);
			double velY = 59.0 * Math.PI * Math.cos(t * Math.PI);
			double totalVel = Math.sqrt(Math.pow(velX, 2) + Math.pow(velY, 2));
			sum += getDist(getPose(t), getPose(t + q)) * velX/totalVel;
		}
		return sum;
	}
	public double getDist(point p1, point p2) {
		return Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2));
	}
	public TrajectorySequence(point p1) {
		traj = new ArrayList<>();
		lastP1 = p1;
		lastT1 = new point(p1.x + vel*Math.cos(p1.heading),p1.y + vel*Math.sin(p1.heading));
	}
	public void addSpline(point p1) {
		vel = Math.sqrt(Math.pow(p1.x-lastP1.x,2) + Math.pow(p1.y-lastP1.y,2));
		point t1 = new point(p1.x + vel * Math.cos(p1.heading),p1.y + vel * Math.sin(p1.heading));
		traj.add(new cubicSpline(lastP1, lastT1, p1, t1));
		lastP1 = p1;
		lastT1 = t1;
	}
	public void addSpline(point p1, point t1) {
		traj.add(new cubicSpline(lastP1, lastT1, p1, t1));
		lastP1 = p1;
		lastT1 = t1;
	}
	public void startTrajectory() {
		start = System.nanoTime();
	}
	public void drawSpline(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Stroke s = new BasicStroke(3.0f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,10.0f);
		g2.setStroke(s);
		g2.setColor(Color.LIGHT_GRAY);
		int size = 6;
		for (int j = 0; j < traj.size(); j ++) {
			for (int i = 0; i < 100; i ++) {
				point p1 = traj.get(j).getPoint(i/100.0);
				point p2 = traj.get(j).getPoint((i+1)/100.0);
				g2.drawLine((int)p1.x,(int)p1.y,(int)p2.x,(int)p2.y);
			}
		}
	}
	public point getPose(double t) {
		int a = (int)t;
		if (a >= traj.size()) {
			t = traj.size() - 0.001;
			a = traj.size() - 1;
		}
		point b = traj.get(a).getPoint(t-a);
		b.y += 59.0 * Math.sin(t*Math.PI) * Math.cos(b.heading);
		b.x -= 59.0 * Math.sin(t*Math.PI) * Math.sin(b.heading);
		return b;
	}
	public double getVel(double t) {
		int a = (int)t;
		if (a >= traj.size()) {
			t = traj.size() - 0.001;
			a = traj.size() - 1;
		}
		double[] b = traj.get(a).getVel(t-a);
		return Math.sqrt(Math.pow(b[0],2) + Math.pow(b[1],2));
	}
	public point getPose() {
		double currentTime = ((System.nanoTime() - start)/(double)1.0E9)/3.0;
		t = Math.min(currentTime, traj.size() - 0.001);
		point b = getPose(currentTime);
		return b;
	}
}
