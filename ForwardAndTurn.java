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

public class ForwardAndTurn extends JPanel implements MouseListener, ActionListener, KeyListener{

	Timer t;
	Font big = new Font("Courier New", 1, 50);
	Font small = new Font("Courier New", 1, 30);
	Font biggest = new Font("Courier New", 1, 90);
	JFrame frame;

	enum robotCase{
		waitAtStart,
		turningLeft,
		turningRight,
		strafeLeft,
		strafeRight;
	}
	
	Robot r;
	long start = System.currentTimeMillis();
	Pose2d center;
	double scale = 4.0;
	robotCase rc;
	
	public static void main(String[] args) {
		ForwardAndTurn drive = new ForwardAndTurn();
	}
	
	public ForwardAndTurn() {
		frame = new JFrame("VideoExplanation");
		frame.setSize(1600, 934);
		center = new Pose2d(400, 600, 0);
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
				r.drawRobot(g2);
				if (t > 4) {
					start = System.currentTimeMillis();
					rc = robotCase.turningLeft;
				}
				break;
			case turningLeft:
				g2.setColor(Color.blue);
				g2.drawArc((int)(center.x-170), (int)(center.y-170), 340, 340, 180, (int)-Math.min(45*t,45));
				r.update(new Pose2d(center.x,center.y,Math.toRadians(Math.min(45*t,45))), g2);
				if (t > 1.5) {
					start = System.currentTimeMillis();
					rc = robotCase.turningRight;
				}
				break;
			case turningRight:
				g2.setColor(Color.blue);
				g2.drawArc((int)(center.x-170), (int)(center.y-170), 340, 340, 180, (int)-Math.max(45-45*t,0));
				r.update(new Pose2d(center.x,center.y,Math.toRadians(Math.max(45-45*t,0))), g2);
				if (t > 1.5) {
					start = System.currentTimeMillis();
					rc = robotCase.strafeLeft;
				}
				break;
			case strafeLeft:
				//g2.setColor(Color.blue);
				//g2.drawLine((int)(center.x-170), (int)(center.y),(int)(center.x-170), (int)(center.y-300*Math.min(t, 1.25)));
				r.update(new Pose2d(center.x,center.y-300*Math.min(t, 1.25),0), g2);
				if (t > 1.75) {
					start = System.currentTimeMillis();
					rc = robotCase.strafeRight;
				}
				break;
			case strafeRight:
				//g2.setColor(Color.blue);
				//g2.drawLine((int)(center.x-170), (int)(center.y),(int)(center.x-170), (int)(center.y-300*Math.max(1.25-t, 0)));
				r.update(new Pose2d(center.x,center.y-300*Math.max(1.25-t, 0),0), g2);
				break;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
