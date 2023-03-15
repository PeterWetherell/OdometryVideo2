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

public class BadAssumption1 extends JPanel implements MouseListener, ActionListener, KeyListener{

	Timer t;
	Font big = new Font("Courier New", 1, 50);
	Font small = new Font("Courier New", 1, 30);
	Font biggest = new Font("Courier New", 1, 90);
	JFrame frame;

	enum robotCase{
		waitAtStart,
		followTraj;
	}
	
	int i = 0;
	Robot r,rLoc;
	Pose2d currentPose;
	ArrayList<Pose2d> poseHistory;
	public double relDeltaX,relDeltaY;
	cubicSpline[] path = new cubicSpline[5];
	long start = System.currentTimeMillis();
	robotCase rc;
	int fidelity = 1;
	boolean startPlay = false;
	
	public static void main(String[] args) {
		BadAssumption1 drive = new BadAssumption1();
	}
	
	public BadAssumption1() {
		frame = new JFrame("VideoExplanation");
		frame.setSize(1600, 934);
		frame.add(this);
		
		path[0] = new cubicSpline(new Pose2d(100,175,Math.toRadians(0)),new Pose2d(300,225,Math.toRadians(0)));
		path[1] = new cubicSpline(new Pose2d(300,225,Math.toRadians(0)), new Pose2d(450,425,Math.toRadians(75)));
		path[2] = new cubicSpline(new Pose2d(450,425,Math.toRadians(75)), new Pose2d(650,525,Math.toRadians(65)));
		path[3] = new cubicSpline(new Pose2d(650,525,Math.toRadians(65)),new Pose2d(800,675,Math.toRadians(90)));
		path[4] = new cubicSpline(new Pose2d(800,675,Math.toRadians(90)),new Pose2d(600,825,Math.toRadians(135)));
		
		r = new Robot(path[0].getPose2d(0));
		rLoc = new Robot(path[0].getPose2d(0));
		
		rc = robotCase.waitAtStart;
		
		start = System.currentTimeMillis();
		
		poseHistory = new ArrayList<>();
		currentPose = path[0].getPose2d(0);
		
		t = new Timer(5,this);
		t.start();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	double x = 0,y = 0;
	
	public void paint(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		Stroke s = new BasicStroke((float)(4),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f);
		g2.setStroke(s);
		
		drawAxis(g2);
		
		g2.setColor(Color.LIGHT_GRAY);
		drawLines(poseHistory,g2);

		double t = (System.currentTimeMillis() - start)/(1000.0);
		
		if (rc == robotCase.followTraj) {
			if (i == 0) {
				currentPose.heading = 0;
				rLoc.update(currentPose,g2);
			}
			else if (i == path.length) {
				rLoc.update(new Pose2d(x,y,currentPose.heading),g2);
			}
			else {
				rLoc.update(new Pose2d(x + path[i].getRelX(0,Math.min(t/3.0,1)), y + path[i].getRelY(0,Math.min(t/3.0,1)),currentPose.heading),g2);
			}
		}
		g2.setColor(Color.blue);
		g2.drawLine((int)rLoc.p.x,(int)rLoc.p.y,(int)(rLoc.p.x),(int)(175));
		g2.setColor(Color.red);
		g2.drawLine((int)rLoc.p.x,(int)(175),(int)(100),(int)(175));
		if (i != path.length) {
			switch (rc){
				case waitAtStart:
					if (t > 10) {
						start = System.currentTimeMillis();
						rc = robotCase.followTraj;
					}
					break;
				case followTraj:
					currentPose = path[i].getPose2d(t/3.0);
					if (i == 0) {
						currentPose.heading = 0;
					}
					if (t > 3) {
						if (i == 0) {
							x = path[i].getPose2d(1).x;
							y = path[i].getPose2d(1).y;
						}
						else {
							x += path[i].getRelX(0, 1);
							y += path[i].getRelY(0, 1);
						}
						i ++;
						rc = robotCase.followTraj;
						start = System.currentTimeMillis();
					}
					poseHistory.add(currentPose);
					break;
			}
		}
		
		g2.setColor(Color.black);
		r.update(currentPose,g);
	}

	public void drawAxis(Graphics2D g2) {
		double yAxisLength = 500;
		double xAxisLength = 1300;
		Pose2d center = path[0].getPose2d(0);
		double size = 6;
		g2.setColor(Color.GRAY);
		g2.drawLine((int)(center.x), (int)(center.y), (int)(center.x), (int)(center.y + yAxisLength));
		Polygon p2 = new Polygon();
		p2.addPoint((int)(center.x - size), (int)(center.y + yAxisLength));
		p2.addPoint((int)(center.x + size), (int)(center.y + yAxisLength));
		p2.addPoint((int)(center.x), (int)(center.y + yAxisLength + 2 * size));
		g2.fillPolygon(p2);
		
		g2.drawLine((int)(center.x), (int)(center.y), (int)(center.x + xAxisLength), (int)(center.y));
		Polygon p1 = new Polygon();
		p1.addPoint((int)(center.x + xAxisLength), (int)(center.y - size));
		p1.addPoint((int)(center.x + xAxisLength), (int)(center.y + size));
		p1.addPoint((int)(center.x + xAxisLength + 2 * size), (int)(center.y));
		g2.fillPolygon(p1);
	}
	
	public void drawLines(ArrayList<Pose2d> p, Graphics2D g2) {
		if (p.size() >= 2) {
			for (int i = 1; i < p.size(); i ++) {
				g2.drawLine((int)p.get(i-1).x,(int)p.get(i-1).y,(int)p.get(i).x,(int)p.get(i).y);
			}
		}
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
		System.out.println(e.getKeyChar());
		if (e.getKeyChar() == ' ') {
			startPlay = true;
		}
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
