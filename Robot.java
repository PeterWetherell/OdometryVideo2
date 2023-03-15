import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class Robot {
	public Pose2d p;
	public double scale = 1;
	
	public Robot(Pose2d Start) {
		p = Start;
	}
	
	public void update(Pose2d p, Graphics g) {
		this.p = p;
		drawRobot(g);
	}
	
	public void drawRobot(Graphics g) {

		double robotWidth = 100;
		
		Graphics2D g2 = (Graphics2D) g;
		Stroke s = new BasicStroke((float)(scale*robotWidth/25.0),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f);
		g2.setStroke(s);
		g2.setColor(Color.DARK_GRAY);

		double wheelWidth = scale*robotWidth/10;
		double wheelHeight = wheelWidth * 2;
		double wheelPosX = scale * robotWidth - wheelHeight - wheelWidth * 0.5;
		double wheelPosY = scale * robotWidth - wheelWidth * 1.5;
		drawRectangle(p,scale * robotWidth,scale * robotWidth,g2);
		double a = 1, b = 1;
		for (int i = 0; i < 4; i ++) {
			switch(i) {
			case(0): a = 1; b = 1; break;
			case(1): a = 1; b = -1; break;
			case(2): a = -1; b = -1; break;
			case(3): a = -1; b = 1; break;
			}
			drawRectangle(
				new Pose2d(
					p.x + a * wheelPosX/2 * Math.cos(p.heading) - b * wheelPosY/2 * Math.sin(p.heading),
					p.y + b * wheelPosY/2 * Math.cos(p.heading) + a * wheelPosX/2 * Math.sin(p.heading),
					p.heading
				),
				wheelWidth,
				wheelHeight,
				g2
			);
			drawOdo(g2,scale * robotWidth,wheelWidth);
		}
	}
	public void drawOdo(Graphics g, double robotWidth, double wheelWidth) {
		double a = Math.cos(p.heading) * wheelWidth/2.0;
		double b = Math.sin(p.heading) * wheelWidth/2.0;
		
		double r = (robotWidth - wheelWidth * 1.5)/2.0;
		
		Pose2d leftOdo = new Pose2d(
				p.x + (0) * Math.cos(p.heading) - r * Math.sin(p.heading),
				p.y + r * Math.cos(p.heading) + (0) * Math.sin(p.heading)
			);
		g.drawLine((int)(leftOdo.x - a),(int)(leftOdo.y - b),(int)(leftOdo.x + a),(int)(leftOdo.y + b));
		
		Pose2d rightOdo = new Pose2d(
				p.x + (0) * Math.cos(p.heading) - -1 * r * Math.sin(p.heading),
				p.y + -1 * r * Math.cos(p.heading) + (0) * Math.sin(p.heading)
			);
		g.drawLine((int)(rightOdo.x - a),(int)(rightOdo.y - b),(int)(rightOdo.x + a),(int)(rightOdo.y + b));
		
		Pose2d backOdo = new Pose2d(
				p.x + -1 * r * Math.cos(p.heading) - (0) * Math.sin(p.heading),
				p.y + (0) * Math.cos(p.heading) + -1 * r * Math.sin(p.heading)
			);
		g.drawLine((int)(backOdo.x + b),(int)(backOdo.y - a),(int)(backOdo.x - b),(int)(backOdo.y + a));
	}
	public void drawRectangle(Pose2d p, double width, double heignt, Graphics g) {
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
