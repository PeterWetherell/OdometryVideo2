import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class drawAxis extends JPanel implements MouseListener, ActionListener, KeyListener{

	Timer t;
	Font big = new Font("Courier New", 1, 50);
	Font small = new Font("Courier New", 1, 30);
	Font biggest = new Font("Courier New", 1, 90);
	JFrame frame;

	enum robotCase{
		waitAtStart,
		drawAxis,
		drawRobot,
		switchAxis,
		drawVector,
		turnVector;
	}
	
	Robot r;
	long start = System.currentTimeMillis();
	Pose2d center;
	double scale = 4.0;
	robotCase rc;
	
	public static void main(String[] args) {
		drawAxis drive = new drawAxis();
	}
	
	public drawAxis() {
		frame = new JFrame("VideoExplanation");
		frame.setSize(1600, 934);
		center = new Pose2d(400, 600, Math.toRadians(-90));
		frame.add(this);
		
		r = new Robot(center);
		r.scale = scale;
		
		rc = robotCase.waitAtStart;
		
		start = System.currentTimeMillis();
		
		t = new Timer(15,this);
		t.start();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	int size = 25;
	
	public void paint(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		Stroke s = new BasicStroke((float)(16),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f);
		g2.setStroke(s);
		g2.setColor(Color.DARK_GRAY);

		double t = (System.currentTimeMillis() - start)/(1000.0);
		switch (rc){
			case waitAtStart:
				if (t > 2) {
					start = System.currentTimeMillis();
					rc = robotCase.drawAxis;
				}
				break;
			case drawAxis:
				drawAxis(g2,Math.min(t, 1));
				if (t > 1) {
					start = System.currentTimeMillis();
					rc = robotCase.drawRobot;
				}
				break;
			case drawRobot:
				drawAxis(g2,1);
				scale = Math.min(2*t, 4);
				r.scale = scale;
				r.drawRobot(g2);
				
				if (t > 3) {
					start = System.currentTimeMillis();
					rc = robotCase.switchAxis;
				}
				break;
			case switchAxis:
				drawAxis(g2,1);
				r.update(new Pose2d(center.x,center.y,Math.toRadians(Math.min(-90 + 90*t,0))), g2);
				
				if (t > 2) {
					start = System.currentTimeMillis();
					rc = robotCase.drawVector;
				}
				break;
			case drawVector:
				drawAxis(g2,1);
				scale = Math.max(4-2*t, 0);
				r.scale = scale;
				drawVector(g2,0,t/3,Color.cyan);
				if (scale != 0) {
					r.drawRobot(g2);
				}
				
				if (t > 3) {
					start = System.currentTimeMillis();
					rc = robotCase.turnVector;
				}
				break;
			case turnVector:
				drawAxis(g2,1);
				g2.setColor(Color.pink);
				g2.drawArc((int)(center.x-80), (int)(center.y-80), 160, 160, 0, (int)Math.min(45*t, 135));
				drawVector(g2,Math.toRadians(Math.min(45*t, 135)),1,Color.cyan);
				break;
		}
	}
	public void drawVector(Graphics2D g2, double heading, double t, Color c) {
		Stroke s = new BasicStroke((float)(16),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,10.0f);
		g2.setColor(c);
		g2.drawLine((int)(center.x), (int)(center.y), (int)(center.x + 400 * t * Math.cos(heading)), (int)(center.y - 400 * t * Math.sin(heading)));
		Polygon p1 = new Polygon();
		p1.addPoint((int)(center.x + 400 * t * Math.cos(heading) + size * Math.sin(heading)), (int)(center.y - 400 * t * Math.sin(heading) + size * Math.cos(heading)));
		p1.addPoint((int)(center.x + 400 * t * Math.cos(heading) - size * Math.sin(heading)), (int)(center.y - 400 * t * Math.sin(heading) - size * Math.cos(heading)));
		p1.addPoint((int)(center.x + (400 * t+2*size) * Math.cos(heading)), (int)(center.y - (400 * t+2*size) * Math.sin(heading)));
		g2.fillPolygon(p1);
	}
	
	public void drawAxis(Graphics2D g2, double t) {
		g2.setColor(Color.BLUE);
		g2.drawLine((int)(center.x), (int)(center.y), (int)(center.x), (int)(center.y - 337.5 * 1.55 * t));
		Polygon p2 = new Polygon();
		p2.addPoint((int)(center.x - size), (int)(center.y - 337.5 * 1.55 * t));
		p2.addPoint((int)(center.x + size), (int)(center.y - 337.5 * 1.55 * t));
		p2.addPoint((int)(center.x), (int)(center.y - 337.5 * 1.55 * t - 2 * size));
		g2.fillPolygon(p2);
		
		g2.setColor(Color.RED);
		g2.drawLine((int)(center.x), (int)(center.y), (int)(center.x + 600 * 1.55 * t), (int)(center.y));
		Polygon p1 = new Polygon();
		p1.addPoint((int)(center.x + 600 * 1.55 * t), (int)(center.y - size));
		p1.addPoint((int)(center.x + 600 * 1.55 * t), (int)(center.y + size));
		p1.addPoint((int)(center.x + 600 * 1.55 * t + 2 * size), (int)(center.y));
		g2.fillPolygon(p1);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		repaint();
	
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
