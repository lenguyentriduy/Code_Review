/**
 * LoginView class
 * 
 * Class that represents a login view
 * @author The Gangs of Joys
 */
package theCR.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.mysql.jdbc.CommunicationsException;

import theCR.models.DBQueries;
import theCR.models.Doctor;
import theCR.models.Secretary;
import theCR.models.Staff;

public class LoginView {

	final private String TITLE = "Welcome";
	final private String[] LABELS = { "Username", "Password" };
	final private String LOGIN_LABEL = "Login";
	private JPanel screen;
	private List<JLabel> labels;
	private JButton loginButton;
	private List<Object> fields;
	private DisplayFrame frame;
	private JPanel middlePanel;

	public LoginView(DisplayFrame frame) {
		this.frame = frame;
		initialize();
	}

	private void initialize() {
		frame.setFrameSize(2, 3);
		frame.centerFrame();
		// Main panel
		this.frame1 = new JPanel();
		this.frame1.setLayout(new BorderLayout());
		this.frame1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// Title
		this.labels = new ArrayList<JLabel>();
		this.labels.add(new JLabel(TITLE, SwingConstants.CENTER));
		this.labels.get(0).setFont(new Font("Sans Serif", Font.PLAIN, DisplayFrame.FONT_SIZE));
		this.labels.get(0).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.frame1.add(this.labels.get(0), BorderLayout.NORTH);
		// Middle panel
		this.middlePanel = new JPanel();
		this.middlePanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		this.fields = new ArrayList<Object>();
		// Username & Password fields and labels
		for (int i = 0; i < LABELS.length; i++) {
			// label
			this.labels.add(new JLabel(LABELS[i], SwingConstants.CENTER));
			this.labels.get(i + 1).setFont(new Font("Sans Serif", Font.PLAIN, DisplayFrame.FONT_SIZE / 2));
			this.labels.get(i + 1).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			// Add to middlePanel
			this.middlePanel.add(this.labels.get(i + 1), gbc);
			// field
			if (LABELS[i] == "Password") {
				JPasswordField passwordField = new JPasswordField(8);
				passwordField.setMaximumSize(passwordField.getPreferredSize()); // set max size
				passwordField.setEchoChar('*');
				this.fields.add(passwordField);
			} else {
				JTextField textField = new JTextField(8);
				textField.setMaximumSize(textField.getPreferredSize());
				this.fields.add(textField);
			}
			((JComponent) this.fields.get(i)).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			// Add to middlePanel
			this.middlePanel.add((Component) this.fields.get(i), gbc);

			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridy = 1;
		}
		this.frame1.add(this.middlePanel, BorderLayout.CENTER);
		// Login Button
		this.loginButton = new JButton(LOGIN_LABEL);
		this.loginButton.setFont(new Font("Sans Serif", Font.PLAIN, DisplayFrame.FONT_SIZE / 2));
		this.loginButton.setMnemonic(KeyEvent.VK_ENTER);
		this.loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Check user name and password in db by creating an instance of staff
				String name = ((JTextField) fields.get(0)).getText();
				String password = String.valueOf(((JPasswordField) fields.get(1)).getPassword());
				try {
					Staff staff = DBQueries.constructStaff(name, password);

					if (staff != null) { // Not found!
						JOptionPane.showMessageDialog(frame, "User not found. Check credentials entered.",
								"Check username or password", JOptionPane.ERROR_MESSAGE);
					} else if (staff.getRole().equals("Secretary")) { // It's the secretary
						DisplayFrame newFrame = new DisplayFrame();
						SecretaryView secView = new SecretaryView(newFrame, (Secretary) staff);
						newFrame.setDisplayedPanel(secView.getPanel());
						frame.dispose();
					} else { // It's the doctor
						DisplayFrame newFrame = new DisplayFrame();
						DoctorView docView = new DoctorView(newFrame, (Doctor) staff);
						newFrame.setDisplayedPanel(docView.getPanel());
						frame.dispose();
					}
				} catch (CommunicationsException e) {
					JOptionPane.showMessageDialog(frame, "Not connected to internet", "Error",
							JOptionPane.ERROR_MESSAGE);
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		this.frame1.add(this.loginButton, BorderLayout.SOUTH);
	}

	public JPanel getPanel() {
		return this.frame1;
	}
}
