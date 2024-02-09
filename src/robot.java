import java.awt.*;
import java.util.ArrayList;

public class robot {
	point p;
	TrajectorySequence fwd;
	
	double deltaForward = 0,deltaHeading = 0;
	ArrayList<point> odoHistory, poseHistory;
	
	double lastT = 0;
	
	public void startTrajectory() {
		odoHistory = new ArrayList<>();
		poseHistory = new ArrayList<>();
		
		fwd = new TrajectorySequence(new point(100,200,-Math.toRadians(45)));
		fwd.addSpline(new point(400,200,Math.toRadians(0)));
		fwd.addSpline(new point(700,400,Math.toRadians(45)));
		fwd.addSpline(new point(600,600,Math.toRadians(180)));
		fwd.addSpline(new point(400,750,Math.toRadians(90)));
		/*
		fwd = new TrajectorySequence(new point(100,100,0));
		fwd.addSpline(new point(900,100,0));
		*/

		fwd.startTrajectory();
	}
	double loop = 0;
	double lastHeading;
	int traj = 0;
	double lastLoopT = 0;
	public void update(Graphics g) {
		//fwd.drawSpline(g);
		p = fwd.getPose();
		if (traj < fwd.traj.size()) {
			loop += fwd.t - lastT;
			if (odoHistory.size() == 0) {
				odoHistory.add(new point(p.x,p.y,p.heading));
				lastHeading = p.heading;
			}
			else if ((int)loop >= 99) {
				loop -= 100;
				traj ++;
				double f = fwd.getDist(fwd.t,lastT);
				double l = 59.0 * (Math.sin(fwd.t*Math.PI) - Math.sin(lastT*Math.PI));
				double avgHeading = (p.heading+lastHeading)/2;
				odoHistory.add(
						new point(
							odoHistory.get(odoHistory.size() - 1).x + f*Math.cos(avgHeading) - l*Math.sin(avgHeading),
							odoHistory.get(odoHistory.size() - 1).y + f*Math.sin(avgHeading) + l*Math.cos(avgHeading)
						)
					);
				lastT = fwd.t;
				lastHeading = p.heading;
			}
			double time = fwd.t;
			loop += time - lastLoopT;
			lastLoopT = time;

			System.out.println(loop);
			poseHistory.add(p);
		}
		drawPoseHistory(g);
		drawOdoHistory(g);
		drawRobot(g);
		
		
	}
	public void drawPoseHistory(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Stroke s = new BasicStroke(3.0f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,10.0f);
		g2.setStroke(s);
		g2.setColor(Color.LIGHT_GRAY);
		for (int i = 1; i < poseHistory.size(); i ++) {
			g2.drawLine((int)poseHistory.get(i-1).x,(int)poseHistory.get(i-1).y,(int)poseHistory.get(i).x,(int)poseHistory.get(i).y);
		}
	}
	public void drawOdoHistory(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Stroke s = new BasicStroke(3.0f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,10.0f);
		g2.setStroke(s);
		g2.setColor(Color.CYAN);
		for (int i = 1; i < odoHistory.size(); i ++) {
			g2.drawLine((int)odoHistory.get(i-1).x,(int)odoHistory.get(i-1).y,(int)odoHistory.get(i).x,(int)odoHistory.get(i).y);
		}
	}
	public void drawRobot(Graphics g) {

		double robotWidth = 100;
		
		Graphics2D g2 = (Graphics2D) g;
		Stroke s = new BasicStroke((float)(robotWidth/25.0),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f);
		g2.setStroke(s);
		g2.setColor(Color.DARK_GRAY);

		double wheelWidth = robotWidth/10;
		double wheelHeight = wheelWidth * 2;
		double wheelPosX = robotWidth - wheelHeight - wheelWidth * 0.5;
		double wheelPosY = robotWidth - wheelWidth * 1.5;
		drawRectangle(p,robotWidth,robotWidth,g2);
		double a = 1, b = 1;
		for (int i = 0; i < 4; i ++) {
			switch(i) {
			case(0): a = 1; b = 1; break;
			case(1): a = 1; b = -1; break;
			case(2): a = -1; b = -1; break;
			case(3): a = -1; b = 1; break;
			}
			drawRectangle(
				new point(
					p.x + a * wheelPosX/2 * Math.cos(p.heading) - b * wheelPosY/2 * Math.sin(p.heading),
					p.y + b * wheelPosY/2 * Math.cos(p.heading) + a * wheelPosX/2 * Math.sin(p.heading),
					p.heading
				),
				wheelWidth,
				wheelHeight,
				g2
			);
			drawOdo(g2,robotWidth,wheelWidth);
		}
	}
	public void drawOdo(Graphics g, double robotWidth, double wheelWidth) {
		double a = Math.cos(p.heading) * wheelWidth/2.0;
		double b = Math.sin(p.heading) * wheelWidth/2.0;
		
		double r = (robotWidth - wheelWidth * 1.5)/2.0;
		
		point leftOdo = new point(
				p.x + (0) * Math.cos(p.heading) - r * Math.sin(p.heading),
				p.y + r * Math.cos(p.heading) + (0) * Math.sin(p.heading)
			);
		g.drawLine((int)(leftOdo.x - a),(int)(leftOdo.y - b),(int)(leftOdo.x + a),(int)(leftOdo.y + b));
		
		point rightOdo = new point(
				p.x + (0) * Math.cos(p.heading) - -1 * r * Math.sin(p.heading),
				p.y + -1 * r * Math.cos(p.heading) + (0) * Math.sin(p.heading)
			);
		g.drawLine((int)(rightOdo.x - a),(int)(rightOdo.y - b),(int)(rightOdo.x + a),(int)(rightOdo.y + b));
		
		point backOdo = new point(
				p.x + -1 * r * Math.cos(p.heading) - (0) * Math.sin(p.heading),
				p.y + (0) * Math.cos(p.heading) + -1 * r * Math.sin(p.heading)
			);
		g.drawLine((int)(backOdo.x + b),(int)(backOdo.y - a),(int)(backOdo.x - b),(int)(backOdo.y + a));
	}
	public void drawRectangle(point p, double width, double heignt, Graphics g) {
		double a = 1, b = 1, c = 1, d = 1;
		for (int i = 0; i < 4; i ++) {
			switch(i) {
			case(0): a = 1; b = 1; c = -1; d = 1; break;
			case(1): a = 1; b = -1; c = 1; d = 1;  break;
			case(2): a = -1; b = -1; c = 1; d = -1; break;
			case(3): a = -1; b = 1; c = -1; d = -1; break;
			}
			int x1 = (int) (p.x + a * heignt/2 * Math.cos(p.heading) - b * width/2 * Math.sin(p.heading));
			int y1 = (int) (p.y + b * width/2 * Math.cos(p.heading) + a * heignt/2 * Math.sin(p.heading));
			int x2 = (int) (p.x + c * heignt/2 * Math.cos(p.heading) - d * width/2 * Math.sin(p.heading));
			int y2 = (int) (p.y + d * width/2 * Math.cos(p.heading) + c * heignt/2 * Math.sin(p.heading));
			g.drawLine(x1,y1,x2,y2);
		}
	}
}
