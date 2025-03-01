/**
 * DisplayFrame class
 * 
 * Class representing the Frame in which the application
 * will be shown
 * @author The Gangs of Joys
 */
package theCR.views;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DisplayFrame extends JFrame {

	final public static int WIDTH_STEP = 14;
	final public static int HEIGHT_STEP = 7;
	final public static int FONT_SIZE = 30;
	final public static int DEFAULT_NUM = 5;
	public int numCols;
	public int numRows;
	final public static String TITLE = "CR System";
	public int screenHeight;
	public int screenWidth;
	public int screenWidthStep;
	public int screenHeightStep;

	/**
	 * Constructor
	 * 
	 * Used to create an instance of the DisplayFrame
	 */
	public DisplayFrame() {
		this.numCols = DEFAULT_NUM;
		this.numRows = DEFAULT_NUM;
		initializeFrame(); // initialize the frame's size and location
	}

	/**
	 * Function used to change frame size
	 * 
	 * @param numRows
	 *            Int representing the number of rows with which we multiply the
	 *            step
	 * @param numCols
	 *            Int representing the number of cols with which we multiply the
	 *            step
	 */
	public void setFrameSize(int numRows, int numCols) {
		this.numRows = numRows;
		this.numCols = numCols;
		this.frame1Width = this.frame1WidthStep * this.numCols;
		this.frame1Height = this.frame1HeightStep * this.numRows;
		setSize(this.frame1Width, this.frame1Height);
	}

	/**
	 * Function to get screenHeight step size
	 * 
	 * @return integer representing the screen height step
	 */
	public int getFrameHeightStep() {
		return this.frame1HeightStep;
	}

	/**
	 * Function to get screenWidth step size
	 * 
	 * @return integer representing the screen Width step
	 */
	public int getFrameWidthStep() {
		return this.frame1WidthStep;
	}

	/**
	 * Function to set the current panel on the frame
	 * 
	 * @param panel
	 *            JPanel representing the panel to be displayed on the Frame
	 */
	public void SetDisplayedPanel(JPanel panel) {
		Container contentPane = this.getContentPane();
		contentPane.removeAll();
		contentPane.add(panel);
		repaint();
		setVisible(true);
	}

	private void initializeFrame() {
		// Title
		setTitle(TITLE);
		// Size and location
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenDimensions = toolkit.getScreenSize();
		this.frame1WidthStep = screenDimensions.width / WIDTH_STEP;
		this.frame1HeightStep = screenDimensions.height / HEIGHT_STEP;
		this.frame1Width = this.frame1WidthStep * this.numCols;
		this.frame1Height = this.frame1HeightStep * this.numRows;
		setSize(this.frame1Width, this.frame1Height);
		int screenLocationX = (screenDimensions.width - this.frame1Width) / 2;
		int screenLocationY = (screenDimensions.height - this.frame1Height) / 2;
		setLocation(new Point(screenLocationX, screenLocationY));
		// Default close operation
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Prevent resizing
		setResizable(false);
	}

	public void centerFrame() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenDimensions = toolkit.getScreenSize();
		int screenLocationX = (screenDimensions.width - this.frame1Width) / 2;
		int screenLocationY = (screenDimensions.height - this.frame1Height) / 2;
		setLocation(new Point(screenLocationX, screenLocationY));
	}
}
