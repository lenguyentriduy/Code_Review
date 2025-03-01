/**
 * PayView class
 * 
 * Class that represents a view that is used to pay for an appointment
 * @author The Gangs of Joys
 */
package theCR.views;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import theCR.models.Appointment;
import theCR.models.DBQueries;
import theCR.models.HealthPlan;
import theCR.models.Usage;

public class PayView {

	// instance variables
	private DisplayFrame frame;
	private JPanel frame1;
	private Appointment appointment;
	private Usage usage;
	private HealthPlan healthplan;

	/**
	 * Constructor used to initialize PayView instance
	 * 
	 * @param frame
	 *            DisplayFrame in which it is to be shown
	 * @param appointment
	 *            Appointment which we are paying for
	 * @throws SQLException
	 *             In case of db error
	 */
	public PayView(DisplayFrame frame, Appointment appointment) throws SQLException {
		this.frame = frame;
		this.appointment = appointment;
		this.usage = appointment.getPatient().getUsage();
		if (this.usage != null)
			this.healthplan = appointment.getPatient().getUsage().getHealthPlan();
		initialize();
	}

	/**
	 * Initializes components and adds action listeners
	 * 
	 * @throws CommunicationsException
	 *             In case of no internet
	 * @throws SQLException
	 *             In case of db error
	 */
	private void initialize() throws CommunicationsException, SQLException {
		this.frame1 = new JPanel();
		this.frame1.setLayout(new BorderLayout());
		this.frame1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// title
		JLabel title = new JLabel("Payment breakdown", SwingConstants.CENTER);
		title.setFont(new Font("Sans Serif", Font.BOLD, DisplayFrame.FONT_SIZE));
		title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.frame1.add(title, BorderLayout.NORTH);
		// Add breakdown to JEditorPane
		HashMap<String, Double> allTreatments = DBQueries.getTreatments();
		ArrayList<String> selectedTreatments = this.appointment.getTreatments();
		JEditorPane breakdown = new JEditorPane();
		breakdown.setContentType("text/html");
		String text = "<html>";
		text += "<h2>Appointment type: " + this.appointment.getAppointmentType() + "</h2>";
		for (int i = 0; i < selectedTreatments.size(); i++) {
			// find it's entry
			Double price = allTreatments.get(selectedTreatments.get(i));
			String toAdd = "<h3><strong>" + selectedTreatments.get(i) + "</strong> | �" + String.valueOf(price)
					+ "</h3><br>";
			text += toAdd;
		}
		text += "<h2><strong>Total price for appointment:</strong> �" + String.valueOf(this.appointment.calculateCost())
				+ "</h2>";
		if (this.usage != null) { // has health plan
			// based on this appointment's type
			String type = this.appointment.getAppointmentType();
			// We compare what we consumed with what we're allowed to have
			if (type.equals("Cleaning")) {
				if (this.usage.getHygieneUsed() < this.healthplan.getHygieneLevel()) { // Discounted from healthplan
					text += "<h2><strong>Health Plan deduction:</strong> -�"
							+ String.valueOf(this.appointment.calculateCost()) + "</h2><br>";
					text += "<h2><strong>Final price:</strong> �0</h2><br>";
				}
			} else if (type.equals("Remedial")) {
				if (this.usage.getRepairUsed() < this.healthplan.getRepairLevel()) { // Discounted from healthplan
					text += "<h2><strong>Health Plan deduction:</strong> -�"
							+ String.valueOf(this.appointment.calculateCost()) + "</h2><br>";
					text += "<h2><strong>Final price:</strong> �0</h2><br>";
				}
			} else { // Checkup
				if (this.usage.getCheckUpUsed() < this.healthplan.getCheckUpLevel()) { // Discounted from healthplan
					text += "<h2><strong>Health Plan deduction:</strong> -�"
							+ String.valueOf(this.appointment.calculateCost()) + "</h2><br>";
					text += "<h2><strong>Final price:</strong> �0</h2><br>";
				}
			}
		}
		breakdown.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		text += "</html>";
		breakdown.setText(text);
		breakdown.setEditable(false);
		JScrollPane breakdownScrollPane = new JScrollPane(breakdown);
		this.frame1.add(breakdownScrollPane, BorderLayout.CENTER);
		// button
		JButton payButton = new JButton("Pay");
		payButton.setFont(new Font("Sans Serif", Font.PLAIN, DisplayFrame.FONT_SIZE / 2));
		payButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// Cost of appointment
					int selectedOption = JOptionPane.showConfirmDialog(null, "Pay for the Appointment?", "Confirm",
							JOptionPane.YES_NO_OPTION);
					if (selectedOption == JOptionPane.YES_OPTION) {
						appointment.pay();
						if (usage != null) { // we need to deduct from health plan as well if possible
							String appointmentType = appointment.getAppointmentType();
							switch (appointmentType.toLowerCase()) {
							case "checkup":
								if (usage.getCheckUpUsed() < usage.getHealthPlan().getCheckUpLevel())
									usage.incrementCheckUp();
								break;
							case "cleaning":
								if (usage.getHygieneUsed() < usage.getHealthPlan().getHygieneLevel())
									usage.incrementHygiene();
								break;
							case "remedial":
								if (usage.getRepairUsed() < usage.getHealthPlan().getRepairLevel())
									usage.incrementRepair();
								break;
							}
						}
						JOptionPane.showMessageDialog(null, "Successfully done transaction", "Success!",
								JOptionPane.INFORMATION_MESSAGE);
						frame.dispose();
					}
				} catch (CommunicationsException e1) {
					JOptionPane.showMessageDialog(frame, "Not connected to internet", "Error",
							JOptionPane.ERROR_MESSAGE);
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(frame, e1.getMessage(), "Error communicating with db",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		this.frame1.add(payButton, BorderLayout.SOUTH);
	}

	public JPanel getPanel() {
		return this.frame1;
	}
}
