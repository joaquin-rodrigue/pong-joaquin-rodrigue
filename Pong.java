import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Game of Pong using edited code from first two authors
 * Also learned about MouseInfo from a stack overflow article
 * @author CS121 Instructors
 * @author mvail
 * @author Joaquin Rodriguez
 */
@SuppressWarnings("serial")
public class Pong extends JPanel {
	private final int INIT_WIDTH = 600;
	private final int INIT_HEIGHT = 400;
	private final int DELAY = 60;       // milliseconds between Timer events

	private Random random;              // random number generator
	private Color color;                // initial ball color

	private int x, y;                   // ball anchor point coordinates
	private int xDelta, yDelta;         // change in x and y from one step to the next
	private int paddleDelta;			// change in y for the paddle from one step to the next
	private int vectorConstant = 12;		// constant to determine the velocity of the ball.

	private int radius = 10;  			// ball radius
	private int paddleRadius; 			// paddle width
	private int paddleY;				// y position of opposing paddle
	
	private int playerPoints = 0;		// Points for the player
	private int aiPoints = 0;			// Points for the AI

	/**
	 * Draws the game of Pong
	 *
	 * @param g Graphics context
	 */
	public void paintComponent(Graphics g)
	{
		int width = getWidth();
		int height = getHeight();

		// Clear canvas
		g.setColor(getBackground());
		g.fillRect(0, 0, width, height);
		
		//Calculate paddle width
		paddleRadius = height / 10;

		// Calculate new x anchor point value
		x += xDelta;
		
		// Determine if points are needed
		if (x >= width) {
			playerPoints++;
			x = width / 2;
			y = height / 2;
			yDelta = random.nextInt(10) - 5;
			xDelta = (int) Math.sqrt(Math.pow(vectorConstant, 2) - Math.pow(yDelta, 2));
			xDelta *= -1;
		} else if (x <= 0) {
			aiPoints++;
			x = width / 2;
			y = height / 2;
			yDelta = random.nextInt(10) - 5;
			xDelta = (int) Math.sqrt(Math.pow(vectorConstant, 2) - Math.pow(yDelta, 2));
			xDelta *= -1;
		}

		// Calculate new y anchor point value
		y += yDelta;
		if (y >= height || y <= 0) {
			yDelta *= -1;
		}
		
		//Calculate player paddle position
		Point mousePos = MouseInfo.getPointerInfo().getLocation();
		
		//Calculate enemy paddle position
		paddleY += paddleDelta;
		
		if (paddleY - paddleRadius <= 0 || paddleY + paddleRadius >= height) {
			paddleDelta *= -1;
		}
		
		// Paddle interactions
		if (x < 60 && (y - mousePos.y < paddleRadius && y - mousePos.y > -paddleRadius) && xDelta < 0) {
			xDelta *= -1;
		} else if (x > width - 60 && (y - paddleY < paddleRadius && y - paddleY > -paddleRadius) && xDelta > 0) {
			xDelta *= -1;
		}
		
		// Paint the ball at the calculated anchor point
		g.setColor(color);
		g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
		
		// Paint the paddles
		g.setColor(Color.white);
		g.fillRect(20, mousePos.y - paddleRadius, 20, height / 5);
		g.fillRect(width - 40, paddleY - paddleRadius, 20, height / 5);
		
		// Paint the score
		String scores = playerPoints + " - " + aiPoints;
		
		g.setFont(new Font("Serif", Font.BOLD, 10 * (height / 100)));
		FontMetrics metrics = g.getFontMetrics();
		int textX = (width - metrics.stringWidth(scores)) / 2;
		int textY = (height + metrics.getHeight()) / 10;
		
		g.drawString(scores, textX, textY);
		
		// Makes the animation smoother
		Toolkit.getDefaultToolkit().sync();
	}

	/**
	 * Constructor for the display panel initializes necessary variables.
	 * Only called once, when the program first begins.
	 * This method also sets up a Timer that will call paintComponent() 
	 * with frequency specified by the DELAY constant.
	 */
	public Pong()
	{
		setPreferredSize(new Dimension(INIT_WIDTH, INIT_HEIGHT));
		setDoubleBuffered(true);
		setBackground(Color.black);

		 // Instantiate instance variable for reuse in paintComponent()
		random = new Random();

		// Initialize ball color
		int red = random.nextInt(256);
		int green = random.nextInt(256);
		int blue = random.nextInt(256);
		
		color = new Color(red, green, blue);

		// Initialize ball anchor point within panel bounds
		x = INIT_WIDTH / 2;
		y = INIT_HEIGHT / 2;
		paddleY = INIT_HEIGHT / 2;

		// Initialize deltas for x and y
		xDelta = 5;
		yDelta = 5;
		paddleDelta = 8;

		//Start the animation
		startAnimation();
	}

	/**
	 * Create an animation thread that runs periodically.
	 */
	private void startAnimation()
	{
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				repaint();
			}
		};
		new Timer(DELAY, taskPerformer).start();
	}

	/**
	 * Starting point for the Pong program.
	 * @param args unused
	 */
	public static void main (String[] args)
	{
		JFrame frame = new JFrame ("Pong");
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new Pong());
		frame.pack();
		frame.setVisible(true);
	}
}
