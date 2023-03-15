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

public class ArcAccelMath extends JPanel implements MouseListener, ActionListener, KeyListener{

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
		ArcAccelMath drive = new ArcAccelMath();
	}
	
	public ArcAccelMath() {
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
		
		lastVelX = currentPose.velX;
		lastVelY = currentPose.velY;
		lastDeltaHeading = path[0].getHeadingVel(0);
		
		
		t = new Timer(15,this);
		t.start();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	

	double lastVelX = 0, lastVelY = 0, lastDeltaHeading = 0;
	
	public void paint(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		Stroke s = new BasicStroke((float)(4),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f);
		g2.setStroke(s);

		drawAxis(g2);
		
		g2.setColor(Color.LIGHT_GRAY);
		drawLines(poseHistory,g2);
		
		double c, d;

		double t = (System.currentTimeMillis() - start)/(1000.0);
		if (i != path.length) {
			double deltaHeading;
			switch (rc){
				case waitAtStart:
					if (t > 5) {
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
					deltaHeading = currentPose.heading - lastPos.heading;
					while (Math.abs(deltaHeading) > Math.PI) {
						deltaHeading -= Math.PI * 2.0 * Math.signum(deltaHeading);
					}
					
					double dRLT = 2;

					if (t > dRLT) {
						rc = robotCase.drawLocalization;
						start = System.currentTimeMillis();
						break;
					}

					d = lastDeltaHeading;
					c = 2.0*(deltaHeading - d);
					
					g2.setColor(Color.RED);
					drawAccelArc(g2,new Pose2d(lastOdo.x,lastOdo.y,0),2.0*(relDeltaX-lastVelX),lastVelX,0.0,0.0,c,d,t/dRLT);
					g2.setColor(Color.BLUE);
					drawAccelArc(g2,new Pose2d(lastOdo.x,lastOdo.y,0),0.0,0.0,2.0*(relDeltaY-lastVelY),lastVelY,c,d,t/dRLT);
					g2.setColor(Color.magenta);
					drawAccelArc(g2,new Pose2d(lastOdo.x,lastOdo.y,0),2.0*(relDeltaX-lastVelX),lastVelX,2.0*(relDeltaY-lastVelY),lastVelY,c,d,t/dRLT);

					
					break;
				case drawLocalization:
					deltaHeading = currentPose.heading - lastPos.heading;
					while (Math.abs(deltaHeading) > Math.PI) {
						deltaHeading -= Math.PI * 2.0 * Math.signum(deltaHeading);
					}
					
					double dLT = Math.abs(Math.toDegrees(currentPose.heading/70.0));
					
					Pose2d a = new Pose2d(lastOdo.x,lastOdo.y,lastOdo.heading + deltaHeading/2.0);

					if (t > dLT) {
						d = lastDeltaHeading;
						c = 2.0*(deltaHeading - d);
						lastOdo = accelArc(a,2.0*(relDeltaX-lastVelX),lastVelX,2.0*(relDeltaY-lastVelY),lastVelY,c,d,1.0);
						lastOdo.heading = currentPose.heading;
						odoHistory.add(lastOdo);
						lastVelX = relDeltaX;
						lastVelY = relDeltaY;
						lastDeltaHeading = deltaHeading;
						i ++;
						rc = robotCase.followTraj;
						start = System.currentTimeMillis();
						break;
					}
					
					d = lastDeltaHeading;
					c = 2.0*(deltaHeading - d);

					g2.setColor(Color.RED);
					drawAccelArcRotating(g2,a,2.0*(relDeltaX-lastVelX),lastVelX,0.0,0.0,c,d,t/dLT);
					g2.setColor(Color.BLUE);
					drawAccelArcRotating(g2,a,0.0,0.0,2.0*(relDeltaY-lastVelY),lastVelY,c,d,t/dLT);
					g2.setColor(Color.magenta);
					drawAccelArcRotating(g2,a,2.0*(relDeltaX-lastVelX),lastVelX,2.0*(relDeltaY-lastVelY),lastVelY,c,d,t/dLT);
					
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
				lastVelX = lastOdo.velX;
				lastVelY = lastOdo.velY;
				lastDeltaHeading = path[0].getHeadingVel(0);
				System.out.println(lastOdo.heading);
				for (int i = 0; i < path.length; i ++) {
					for (int j = 0; j < fidelity; j ++) {
						double t1 = (j)/(double)fidelity;
						double t2 = (j + 1.0)/(double)fidelity;
						
						double deltaHeading = path[i].getPose2d(t2).heading - path[i].getPose2d(t1).heading;
						while (Math.abs(deltaHeading) > Math.PI) {
							deltaHeading -= Math.PI * 2.0 * Math.signum(deltaHeading);
						}

						relDeltaX = path[i].getRelX(t1,t2);
						relDeltaY = path[i].getRelY(t1,t2);

						d = lastDeltaHeading;
						c = 2.0*(deltaHeading - d);
						
						Pose2d a = new Pose2d(lastOdo.x,lastOdo.y,lastOdo.heading + deltaHeading/2.0);
						
						lastOdo = accelArc(a,2.0*(relDeltaX-lastVelX),lastVelX,2.0*(relDeltaY-lastVelY),lastVelY,c,d,1.0);
						lastOdo.heading = path[i].getPose2d(t2).heading;
						odoHistory.add(lastOdo);
						
						lastVelX = relDeltaX;
						lastVelY = relDeltaY;
						lastDeltaHeading = deltaHeading;
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
	
	public void drawAccelArc(Graphics2D g, Pose2d lastOdo, double a1, double b1, double a2, double b2, double c, double d, double time) {
		Pose2d curr = lastOdo;
		double fidelity = 0.01;
		for (double t = fidelity; t < time; t += fidelity) {
			double lastX = curr.x;
			double lastY = curr.y;
			curr = accelArc(new Pose2d(lastOdo.x,lastOdo.y,0), a1, b1, a2, b2, c, d, t);
			g.drawLine(
					(int)(lastX),
					(int)(lastY),
					(int)(curr.x),
					(int)(curr.y)
				);
		}
	}
	
	public void drawAccelArcRotating(Graphics2D g, Pose2d lastOdo, double a1, double b1, double a2, double b2, double c, double d, double time) {
		Pose2d curr = lastOdo;
		double fidelity = 0.01;
		for (double t = fidelity; t < 1.0; t += fidelity) {
			double lastX = curr.x;
			double lastY = curr.y;
			curr = accelArc(new Pose2d(lastOdo.x,lastOdo.y,lastOdo.heading*time), a1, b1, a2, b2, c, d, t);
			g.drawLine(
					(int)(lastX),
					(int)(lastY),
					(int)(curr.x),
					(int)(curr.y)
				);
		}
	}

	double factorial(int n) {
		double sum = 1;
		for (int i = 0; i < n; i ++) {
			sum *= i + 1;
		}
		return sum;
	}
	
	public Pose2d accelArc(Pose2d lastOdo, double a1, double b1, double a2, double b2, double c, double d, double time) {
		Pose2d curr = new Pose2d(0,0);
		double currFwdX = 0,currFwdY = 0,currStrX = 0,currStrY = 0;
		for (int n = 0; n <= 3; n ++) {
			double cosVal = 0;
			double val2 = Math.pow(-1, n)/factorial((int)(2*n));
			int size = (int)(2*n);
			for (int k = 0; k <= size; k ++) {
				double val1 = factorial(size)/(factorial((int)k)*factorial((int)(size-k)));
				cosVal = val1*(Math.pow(c/2.0,k)*Math.pow(d,size-k)*Math.pow(time,size+k+1.0))/((size+k+1.0)*(size+k+2.0));
				currFwdX += cosVal*((size+k+2.0)*(a1*time+b1)-time*a1)*val2;
				currStrY += cosVal*((size+k+2.0)*(a2*time+b2)-time*a2)*val2;
			}
			double sinVal = 0;
			val2 = Math.pow(-1, n)/factorial((int)(2*n)+1);
			size = (int)(2*n)+1;
			for (int k = 0; k <= size; k ++) {
				double val1 = factorial(size)/(factorial((int)k)*factorial((int)(size-k)));
				sinVal = val1*(Math.pow(c/2.0,k)*Math.pow(d,size-k)*Math.pow(time,size+k+1.0))/((size+k+1.0)*(size+k+2.0));
				currFwdY += sinVal*((size+k+2.0)*(a1*time+b1)-time*a1)*val2;
				currStrX -= sinVal*((size+k+2.0)*(a2*time+b2)-time*a2)*val2;
			}
		}
		curr.x = currFwdX + currStrX;
		curr.y = currFwdY + currStrY;
		return new Pose2d(
					lastOdo.x + curr.x*Math.cos(lastOdo.heading) - curr.y*Math.sin(lastOdo.heading),
					lastOdo.y + curr.y*Math.cos(lastOdo.heading) + curr.x*Math.sin(lastOdo.heading)
				);
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
