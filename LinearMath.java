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

public class LinearMath extends JPanel implements MouseListener, ActionListener, KeyListener{

	Timer t;
	Font big = new Font("Courier New", 1, 50);
	Font small = new Font("Courier New", 1, 30);
	Font biggest = new Font("Courier New", 1, 90);
	JFrame frame;

	enum robotCase{
		waitAtStart,
		increaseFidelity,
		followTraj,
		drawRelativeLocalization,
		drawLocalization;
	}
	
	int i = 0;
	Robot r;
	Pose2d currentPose,lastPos,lastOdo;
	ArrayList<Pose2d> poseHistory, odoHistory;
	public double relDeltaX,relDeltaY;
	cubicSpline[] path = new cubicSpline[5];
	long start = System.currentTimeMillis();
	robotCase rc;
	int fidelity = 1;
	boolean startPlay = false;
	
	public static void main(String[] args) {
		LinearMath drive = new LinearMath();
	}
	
	public LinearMath() {
		frame = new JFrame("VideoExplanation");
		frame.setSize(1600, 934);
		frame.add(this);

		path[0] = new cubicSpline(new Pose2d(100,155,Math.toRadians(0)),new Pose2d(300,205,Math.toRadians(0)));
		path[1] = new cubicSpline(new Pose2d(300,205,Math.toRadians(0)), new Pose2d(450,405,Math.toRadians(75)));
		path[2] = new cubicSpline(new Pose2d(450,405,Math.toRadians(75)), new Pose2d(650,505,Math.toRadians(65)));
		path[3] = new cubicSpline(new Pose2d(650,505,Math.toRadians(65)),new Pose2d(800,655,Math.toRadians(90)));
		path[4] = new cubicSpline(new Pose2d(800,655,Math.toRadians(90)),new Pose2d(600,805,Math.toRadians(135)));
		
		r = new Robot(path[0].getPose2d(0));
		
		rc = robotCase.waitAtStart;
		
		start = System.currentTimeMillis();
		
		poseHistory = new ArrayList<>();
		odoHistory = new ArrayList<>();
		currentPose = path[0].getPose2d(0);
		lastOdo = path[0].getPose2d(0);
		odoHistory.add(lastOdo);
		
		t = new Timer(15,this);
		t.start();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	
	public void paint(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		Stroke s = new BasicStroke((float)(4),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f);
		g2.setStroke(s);
		
		drawAxis(g2);
		
		g2.setColor(Color.LIGHT_GRAY);
		drawLines(poseHistory,g2);

		double t = (System.currentTimeMillis() - start)/(1000.0);
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
					if (t > 3) {
						rc = robotCase.drawRelativeLocalization;
						start = System.currentTimeMillis();
						relDeltaX = path[i].getRelX(0,1);
						relDeltaY = path[i].getRelY(0,1);
						lastPos = path[i].getPose2d(0);
						currentPose = path[i].getPose2d(1);
					}
					poseHistory.add(currentPose);
					break;
				case drawRelativeLocalization:
					double dRLT = 2;

					g2.setColor(Color.RED);
					g2.drawLine((int)lastOdo.x,(int)lastOdo.y,(int)(lastOdo.x + relDeltaX * t/dRLT),(int)lastOdo.y);
					g2.setColor(Color.BLUE);
					g2.drawLine((int)lastOdo.x,(int)lastOdo.y,(int)lastOdo.x,(int)(lastOdo.y + relDeltaY * t/dRLT));

					g2.setColor(Color.magenta);
					g2.drawLine((int)lastOdo.x,(int)lastOdo.y,(int)(lastOdo.x + relDeltaX * t/dRLT),(int)(lastOdo.y + relDeltaY * t/dRLT));
					if (t > dRLT) {
						rc = robotCase.drawLocalization;
						start = System.currentTimeMillis();
					}
					break;
				case drawLocalization:
					double dLT = Math.toDegrees((lastPos.heading+currentPose.heading)/2.0)/35.0;
					double theta = (lastPos.heading+currentPose.heading)/2.0 * t/dLT;

					g2.setColor(Color.RED);
					g2.drawLine((int)lastOdo.x,(int)lastOdo.y,(int)(lastOdo.x + relDeltaX * Math.cos(theta)),(int)(lastOdo.y + relDeltaX * Math.sin(theta)));
					g2.setColor(Color.BLUE);
					g2.drawLine((int)lastOdo.x,(int)lastOdo.y,(int)(lastOdo.x - relDeltaY * Math.sin(theta)),(int)(lastOdo.y + relDeltaY * Math.cos(theta)));
					
					g2.setColor(Color.magenta);
					g2.drawLine((int)lastOdo.x,(int)lastOdo.y,(int)(lastOdo.x + relDeltaX * Math.cos(theta) - relDeltaY * Math.sin(theta)),(int)(lastOdo.y + relDeltaX * Math.sin(theta) + relDeltaY * Math.cos(theta)));
					if (t > dLT) {
						i ++;
						rc = robotCase.followTraj;
						start = System.currentTimeMillis();
						theta = (lastPos.heading+currentPose.heading)/2.0;
						lastOdo = new Pose2d(
								lastOdo.x + relDeltaX * Math.cos(theta) - relDeltaY * Math.sin(theta),
								lastOdo.y + relDeltaX * Math.sin(theta) + relDeltaY * Math.cos(theta)
							);
						odoHistory.add(lastOdo);
					}
					break;
			}
		}
		else {
			if (fidelity <= 64 && t > 0.5) {
				start = System.currentTimeMillis();
				fidelity *= 2;
				odoHistory.clear();
				lastOdo = path[0].getPose2d(0);
				odoHistory.add(lastOdo);
				for (int i = 0; i < path.length; i ++) {
					for (int j = 0; j < fidelity; j ++) {
						double t1 = (j)/(double)fidelity;
						double t2 = (j + 1)/(double)fidelity;

						relDeltaX = path[i].getRelX(t1,t2);
						relDeltaY = path[i].getRelY(t1,t2);

						double theta = (path[i].getPose2d(t1).heading+path[i].getPose2d(t2).heading)/2.0;
						lastOdo = new Pose2d(
								lastOdo.x + relDeltaX * Math.cos(theta) - relDeltaY * Math.sin(theta),
								lastOdo.y + relDeltaX * Math.sin(theta) + relDeltaY * Math.cos(theta)
							);
						odoHistory.add(lastOdo);
					}
				}
			}
		}
		g2.setColor(Color.magenta);
		drawLines(odoHistory,g2);
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
