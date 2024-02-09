import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class main extends JPanel implements MouseListener, ActionListener {
	Timer t;
	Long lastLoopTime = System.nanoTime();
	Font big = new Font("Courier New", 1, 50);
	Font small = new Font("Courier New", 1, 30);
	Font biggest = new Font("Courier New", 1, 90);
	JFrame frame;

	TrajectorySequence test;
	
	robot r = new robot();
	
	
	public main() {
		frame = new JFrame("VideoExplanation");
		frame.setSize(1000, 1000);
		frame.add(this);
		
		r.startTrajectory();
		
		t = new Timer(15,this);
		t.start();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	public void paint(Graphics g) {
		super.paintComponent(g);
		//double loopTime = (double)(System.nanoTime() - lastLoopTime)/1E9;
		//System.out.println(loopTime);
		//System.out.print((MouseInfo.getPointerInfo().getLocation().getX() - frame.getLocation().getX() - 500) + " " + (MouseInfo.getPointerInfo().getLocation().getY() - frame.getLocation().getY()-500));
		lastLoopTime = System.nanoTime();
		r.update(g);
	}
	public static void main(String[] args) {
		main drive = new main();
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		repaint();
	
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
