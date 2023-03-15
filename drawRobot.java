import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

public class drawRobot extends JPanel implements MouseListener, ActionListener, KeyListener{

	Timer t;
	Font big = new Font("Courier New", 1, 50);
	Font small = new Font("Courier New", 1, 30);
	Font biggest = new Font("Courier New", 1, 90);
	JFrame frame;

	enum robotCase{
		waitAtStart,
		expandRobot,
		drawRobot,
		forward,
		strafe,
		turn,
		idle;
	}
	
	Robot r;
	long start = System.currentTimeMillis();
	Pose2d center;
	double scale = 4.0;
	robotCase rc;
	
	public static void main(String[] args) {
		drawRobot drive = new drawRobot();
	}
	
	public drawRobot() {
		frame = new JFrame("VideoExplanation");
		frame.setSize(1600, 934);
		center = new Pose2d(1600/2, 934/2-17, Math.toRadians(-90));
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
	
	
	public void paint(Graphics g) {
		super.paintComponent(g);
		
		double robotWidth = 100;
		double wheelWidth = scale*robotWidth/10;
		double wheelHeight = wheelWidth * 2;
		double wheelPosX = (scale * robotWidth - wheelHeight - wheelWidth * 0.5)/2;
		double wheelPosY = (scale * robotWidth - wheelWidth * 1.5)/2;

		Graphics2D g2 = (Graphics2D) g;
		Stroke s = new BasicStroke((float)(scale*4),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f);
		g2.setStroke(s);
		g2.setColor(Color.DARK_GRAY);

		double t = (System.currentTimeMillis() - start)/(1000.0);
		switch (rc){
			case waitAtStart:
				if (t > 7) {
					start = System.currentTimeMillis();
					rc = robotCase.expandRobot;
				}
				break;
			case expandRobot:
				if (t < 1) {
					g2.drawLine((int)(center.x - r.scale * 50), (int)(center.y - r.scale * 50), (int)(center.x - r.scale * 50), (int)(center.y - r.scale * 50 + r.scale * 100 * t));
					g2.drawLine((int)(center.x - r.scale * 50), (int)(center.y - r.scale * 50), (int)(center.x - r.scale * 50 + r.scale * 100 * t), (int)(center.y - r.scale * 50));
				}
				else if (t < 2) {
					g2.drawLine((int)(center.x - r.scale * 50), (int)(center.y - r.scale * 50), (int)(center.x - r.scale * 50), (int)(center.y + r.scale * 50));//-- to -+
					g2.drawLine((int)(center.x - r.scale * 50), (int)(center.y + r.scale * 50), (int)(center.x - r.scale * 50 + r.scale * 100 * (t-1)), (int)(center.y + r.scale * 50));//-+ to ++
					g2.drawLine((int)(center.x - r.scale * 50), (int)(center.y - r.scale * 50), (int)(center.x + r.scale * 50), (int)(center.y - r.scale * 50));//-- to +-
					g2.drawLine((int)(center.x + r.scale * 50), (int)(center.y - r.scale * 50), (int)(center.x + r.scale * 50), (int)(center.y - r.scale * 50 + r.scale * 100 * (t-1)));//-+ to ++
				}
				else if (t < 2.5) {
					g2.drawLine((int)(center.x - r.scale * 50), (int)(center.y - r.scale * 50), (int)(center.x - r.scale * 50), (int)(center.y + r.scale * 50));//-- to -+
					g2.drawLine((int)(center.x - r.scale * 50), (int)(center.y + r.scale * 50), (int)(center.x + r.scale * 50), (int)(center.y + r.scale * 50));//-+ to ++
					g2.drawLine((int)(center.x - r.scale * 50), (int)(center.y - r.scale * 50), (int)(center.x + r.scale * 50), (int)(center.y - r.scale * 50));//-- to +-
					g2.drawLine((int)(center.x + r.scale * 50), (int)(center.y - r.scale * 50), (int)(center.x + r.scale * 50), (int)(center.y + r.scale * 50));//-+ to ++
					double T = (t - 2)/0.5;
					double a = 1, b = 1;
					for (int i = 0; i < 4; i ++) {
						switch(i) {
						case(0): a = 1; b = 1; break;
						case(1): a = 1; b = -1; break;
						case(2): a = -1; b = -1; break;
						case(3): a = -1; b = 1; break;
						}
						g2.drawLine((int)(center.x - (wheelPosY + wheelWidth/2) * a), (int)(center.y - (wheelPosX + wheelHeight/2) * b), (int)(center.x - (wheelPosY + wheelWidth/2 - wheelWidth * T) * a), (int)(center.y - (wheelPosX + wheelHeight/2) * b));
						g2.drawLine((int)(center.x - (wheelPosY + wheelWidth/2) * a), (int)(center.y - (wheelPosX + wheelHeight/2) * b), (int)(center.x - (wheelPosY + wheelWidth/2) * a), (int)(center.y - (wheelPosX + wheelHeight/2 - wheelHeight*T) * b));
					}
				}
				else {
					double T = (t - 2.5)/0.5;
					g2.drawLine((int)(center.x - r.scale * 50), (int)(center.y - r.scale * 50), (int)(center.x - r.scale * 50), (int)(center.y + r.scale * 50));//-- to -+
					g2.drawLine((int)(center.x - r.scale * 50), (int)(center.y + r.scale * 50), (int)(center.x + r.scale * 50), (int)(center.y + r.scale * 50));//-+ to ++
					g2.drawLine((int)(center.x - r.scale * 50), (int)(center.y - r.scale * 50), (int)(center.x + r.scale * 50), (int)(center.y - r.scale * 50));//-- to +-
					g2.drawLine((int)(center.x + r.scale * 50), (int)(center.y - r.scale * 50), (int)(center.x + r.scale * 50), (int)(center.y + r.scale * 50));//-+ to ++
					double a = 1, b = 1;
					for (int i = 0; i < 4; i ++) {
						switch(i) {
						case(0): a = 1; b = 1; break;
						case(1): a = 1; b = -1; break;
						case(2): a = -1; b = -1; break;
						case(3): a = -1; b = 1; break;
						}
						g2.drawLine((int)(center.x - (wheelPosY + wheelWidth/2) * a), (int)(center.y - (wheelPosX + wheelHeight/2) * b), (int)(center.x - (wheelPosY - wheelWidth/2) * a), (int)(center.y - (wheelPosX + wheelHeight/2) * b));
						g2.drawLine((int)(center.x - (wheelPosY + wheelWidth/2) * a), (int)(center.y - (wheelPosX + wheelHeight/2) * b), (int)(center.x - (wheelPosY + wheelWidth/2) * a), (int)(center.y - (wheelPosX - wheelHeight/2) * b));

						g2.drawLine((int)(center.x - (wheelPosY - wheelWidth/2) * a), (int)(center.y - (wheelPosX + wheelHeight/2) * b),(int)(center.x - (wheelPosY - wheelWidth/2) * a), (int)(center.y - (wheelPosX + wheelHeight/2 - wheelHeight * T) * b));
						g2.drawLine((int)(center.x - (wheelPosY + wheelWidth/2) * a), (int)(center.y - (wheelPosX - wheelHeight/2) * b),(int)(center.x - (wheelPosY + wheelWidth/2 - wheelWidth * T) * a), (int)(center.y - (wheelPosX - wheelHeight/2) * b));
					}
				}
				if (t > 3) {
					start = System.currentTimeMillis();
					rc = robotCase.drawRobot;
				}
				break;
			case drawRobot:
				r.drawRobot(g2);
				if (t > 0.5) {
					scale = 4 - (t-0.5);
					r.scale = scale;
				}
				if (scale < 1) {
					scale = 1;
					r.scale = scale;
				}
				if (t > 4.5) {
					start = System.currentTimeMillis();
					rc = robotCase.forward;
				}
				break;
			case forward:
				r.p = new Pose2d(center.x,center.y - (Math.sqrt(t*1.5 + 1) - 1) * (300),Math.toRadians(-90));
				if (t > 2) {
					start = System.currentTimeMillis();
					rc = robotCase.strafe;
				}
				g2.setColor(Color.BLUE);
				g2.drawLine((int)center.x, (int)center.y, (int)center.x,(int)(center.y - (Math.sqrt(t*1.5 + 1) - 1) * (300)));
				r.drawRobot(g2);
				break;
			case strafe:
				r.p = new Pose2d(center.x + (Math.sqrt(t*1.5 + 1) - 1) * (300),center.y - 300,Math.toRadians(-90));
				if (t > 2) {
					start = System.currentTimeMillis();
					rc = robotCase.turn;
				}
				g2.setColor(Color.BLUE);
				g2.drawLine((int)(center.x + (Math.sqrt(t*1.5 + 1) - 1) * (300)), (int)center.y, (int)(center.x + (Math.sqrt(t*1.5 + 1) - 1) * (300)),(int)(center.y - 300));
				g2.setColor(Color.RED);
				g2.drawLine((int)center.x, (int)center.y, (int)(center.x + (Math.sqrt(t*1.5 + 1) - 1) * (300)),(int)(center.y));
				r.drawRobot(g2);
				break;
			case turn:
				r.p = new Pose2d(center.x + 300,center.y - 300,Math.toRadians(-90 + 60 * Math.sin(Math.PI * (2*t/3))));
				if (t > 3) {
					start = System.currentTimeMillis();
					rc = robotCase.idle;
				}
				g2.setColor(Color.BLUE);
				g2.drawLine((int)(center.x + 300), (int)center.y, (int)(center.x + 300),(int)(center.y - 300));
				g2.setColor(Color.RED);
				g2.drawLine((int)center.x, (int)center.y, (int)(center.x + 300),(int)(center.y));
				r.drawRobot(g2);
				break;
			case idle:
				r.p = new Pose2d(center.x + 300,center.y - 300,Math.toRadians(-90));
				g2.setColor(Color.BLUE);
				g2.drawLine((int)(center.x + 300), (int)center.y, (int)(center.x + 300),(int)(center.y - 300));
				g2.setColor(Color.RED);
				g2.drawLine((int)center.x, (int)center.y, (int)(center.x + 300),(int)(center.y));
				r.drawRobot(g2);
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
