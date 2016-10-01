package daviscook447.animator.core;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.IIOException;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JFrame;
import javax.swing.JPanel;

import daviscook447.animator.gif.GifSequenceWriter;

public class Main {

	public static final int WIDTH = 600, HEIGHT = 600;
	public static final float SPEED = 4.0f;
	
	private static abstract class Params {
		public abstract float f0(double theta);
		public abstract float speed();
		public abstract int msPerFrame();
	}
	
	public static final Params limacon = new Params() {
		public float f0(double theta) {
			return (float) (80-160*Math.sin(theta));
		}
		
		public float speed() {
			// 4.0f norm
			return 2.0f;
		}
		
		public int msPerFrame() {
			// 25 norm
			return 25;
		}
	};
	
	public static final Params limacon_wide = new Params() {
		public float f0(double theta) {
			return (float) (160-80*Math.sin(theta));
		}
		
		public float speed() {
			// 4.0f norm
			return 2.0f;
		}
		
		public int msPerFrame() {
			// 25 norm
			return 25;
		}
	};
	
	public static final Params bizarre = new Params() {
		public float f0(double theta) {
			return (float) (160*Math.sin(theta)-80*Math.cos(2*theta));
		}
		
		public float speed() {
			// 4.0f norm
			return 2.0f;
		}
		
		public int msPerFrame() {
			// 25 norm
			return 25;
		}
	};
	
	public static final Params squarish = new Params() {
		public float f0(double theta) {
			return (float) (480*Math.sin(theta)*Math.cos(theta));
		}
		
		public float speed() {
			// 4.0f norm
			return 1.0f;
		}
		
		public int msPerFrame() {
			// 25 norm
			return 25;
		}
	};
	
	public static final Params tangents = new Params() {
		public float f0(double theta) {
			return (float) (120*Math.sin(theta)/Math.cos(theta));
		}
		
		public float speed() {
			// 4.0f norm
			return 2.0f;
		}
		
		public int msPerFrame() {
			// 25 norm
			return 25;
		}
	};
	
	public static final Params arctangents = new Params() {
		public float f0(double theta) {
			return (float) (420*Math.cos(theta)*Math.sin(theta));
		}
		
		public float speed() {
			// 4.0f norm
			return 1.0f;
		}
		
		public int msPerFrame() {
			// 25 norm
			return 25;
		}
	};
	
	public static float xof(double r, double theta) {
		return (float) (WIDTH / 2 + Math.cos(theta)*r);
	}
	
	public static float yof(double r, double theta) {
		return (float) (HEIGHT / 2 + Math.sin(theta)*r);
	}

	public static final float DEGREES = 360f;
	public static final int DOTS = 72;
	
	
	public static JPanel buildJPanel() {
		return new JPanel() {
			private int j = 0;
			
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2D = (Graphics2D) g;
				j++;
				for (int i = 0; i < DOTS; i++) {
					float theta = i*(DEGREES/DOTS);
					float thetaMod =  (theta + (j%(DEGREES*(1.0f/arctangents.speed())))*arctangents.speed());
					float thetaRad = (float) (theta*Math.PI/180);
					float thetaModRad = (float) (thetaMod*Math.PI/180);
					int x = (int) xof(arctangents.f0(thetaModRad), thetaRad);
					int y = (int) yof(arctangents.f0(thetaModRad), thetaRad);
					g2D.setColor(Color.getHSBColor((i/9.0f), 1.0f, 1.0f));
					g2D.fillOval(x-10, y-10, 20, 20);
					int x2 = (int) xof(arctangents.f0(thetaModRad+Math.PI), thetaRad+Math.PI);
					int y2 = (int) yof(arctangents.f0(thetaModRad+Math.PI), thetaRad+Math.PI);
					//g2D.drawLine(x, y, x2, y2);
					int x3 = (int) xof(arctangents.f0(thetaModRad+Math.PI*0.5f), thetaRad+Math.PI*0.5f);
					int y3 = (int) yof(arctangents.f0(thetaModRad+Math.PI*0.5f), thetaRad+Math.PI*0.5f);
					g2D.drawLine(x, y, x3, y3);
//					g2D.drawLine(0, 0, x, y);
//					g2D.drawLine(800, 0, x, y);
//					g2D.drawLine(800, 800, x, y);
//					g2D.drawLine(0, 800, x, y);
				}
			}
		};
	}
	
	private static class MyThread extends Thread {
		private JPanel thePanel;
		
		public MyThread(JPanel thePanel) {
			this.thePanel = thePanel;
		}
		
		@Override
		public void run() {
			while (true) {
				thePanel.repaint();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) throws IIOException, IOException {
		JFrame jframe = new JFrame();
		jframe.setSize(new Dimension(WIDTH, HEIGHT));
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel jpanel = buildJPanel();
		jpanel.setBackground(new Color(30,30,30));
		jframe.add(jpanel);
		MyThread myThread = new MyThread(jpanel);
		myThread.start();
		
		jframe.setVisible(true);
		makeGif(WIDTH, HEIGHT, new Color(30, 30, 30), "mygifisawesomesmallxhalfrose.gif");
	}
	
	public static void makeGif(int width, int height, Color backgroundColor, String filepath) throws IIOException, IOException {
		ImageOutputStream output = new FileImageOutputStream(new File(filepath));
		GifSequenceWriter giffy = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, arctangents.msPerFrame(), true);
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D ig2 = bi.createGraphics();
		System.out.println("Drawing " + (DEGREES/arctangents.speed()) + " frames");
		for (int j = 0; j < (DEGREES/arctangents.speed()); j++) {
			// clear canvas
			ig2.setColor(backgroundColor);
			ig2.fillRect(0, 0, width, height);
			// perform draw
			for (int i = 0; i < DOTS; i++) {
				float theta = i*(DEGREES/DOTS);
				float thetaMod =  (theta + (j%(DEGREES*(1.0f/arctangents.speed())))*arctangents.speed());
				float thetaRad = (float) (theta*Math.PI/180);
				float thetaModRad = (float) (thetaMod*Math.PI/180);
				int x = (int) xof(arctangents.f0(thetaModRad), thetaRad);
				int y = (int) yof(arctangents.f0(thetaModRad), thetaRad);
				ig2.setColor(Color.getHSBColor((i/9.0f), 1.0f, 1.0f));
				ig2.fillOval(x-10, y-10, 20, 20);
				int x2 = (int) xof(arctangents.f0(thetaModRad+Math.PI), thetaRad+Math.PI);
				int y2 = (int) yof(arctangents.f0(thetaModRad+Math.PI), thetaRad+Math.PI);
				//ig2.drawLine(x, y, x2, y2);
				int x3 = (int) xof(arctangents.f0(thetaModRad+Math.PI*0.5f), thetaRad+Math.PI*0.5f);
				int y3 = (int) yof(arctangents.f0(thetaModRad+Math.PI*0.5f), thetaRad+Math.PI*0.5f);
				ig2.drawLine(x, y, x3, y3);
			}
			// write to gif
			giffy.writeToSequence(bi);
			System.out.println("Frame " + j + " of " + (DEGREES/arctangents.speed()) + " completed");
		}
		// close resources
		giffy.close();
		output.close();
	}
	
}
