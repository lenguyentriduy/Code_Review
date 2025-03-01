/**
 * PaymentsDueView class
 * 
 * Class that represents a view that is used to see a patient's due payments
 * @author The Gangs of Joys
 */
package theCR.views;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import theCR.models.Appointment;
import theCR.models.DBQueries;
import theCR.models.HealthPlan;
import theCR.models.Patient;
import theCR.utils.Database;

public class PaymentsDueView {

	/** Constant to define health plan transaction **/
	static public int healthPlan = 0;

	/** Constant to define appointment transaction **/
	final static public int APPOINTMENT = 1;

	// instance variables
	private JPanel frame1;
	private DisplayFrame frame;
	private Patient patient;
	private ArrayList<Object[]> paymentsList;
	private DefaultTableModel appointmentPaymentsTableModel;
	private JTable appointmentPaymentsTable;
	private DefaultTableModel healthPlanPaymentsTableModel;
	private JTable healthPlanPaymentsTable;

	/**
	 * Constructor used to instantiate instance of this view
	 * 
	 * @param frame
	 *            DisplayFrame in which it is to be shown
	 * @param patient
	 *            Patient who is paying or seeing payments due
	 */
	public PaymentsDueView(DisplayFrame frame, Patient patient) {
		this.frame = frame;
		this.patient = patient;
		initialize();
	}

	/**
	 * Function used to initialize components and add action listeners
	 */
	@SuppressWarnings("serial")
	private void initialize() {
		this.frame1 = new JPanel();
		this.frame1.setLayout(new BorderLayout());
		try {
			// title
			JLabel title = new JLabel("Payments Due", SwingConstants.CENTER);
			title.setFont(new Font("Sans Serif", Font.BOLD, DisplayFrame.FONT_SIZE));
			this.frame1.add(title, BorderLayout.NORTH);
			// We view all of this patient's transactions
			this.paymentsList = getAllDuePayments();
			ArrayList<Object[]> appointmentsList = new ArrayList<Object[]>();
			ArrayList<Object[]> healthPlansList = new ArrayList<Object[]>();
			for (int i = 0; i < this.paymentsList.size(); i++) {
				if ((int) this.paymentsList.get(i)[0] == this.healthPlan) { // Add to health plan payments table
					Object[] record = new Object[4];
					record[0] = this.patient.getPatientID();
					record[1] = String.valueOf((Double) this.paymentsList.get(i)[1]);
					record[2] = "Health Plan monthly invoice";
					record[3] = "Pay Subscription";
					healthPlansList.add(record);
				} else { // Add to appointment payments table
					// AmountDue, AppointmentStartTime, AppointmentUsername, AppointmentType, Pay
					// Appointment
					Object[] record = new Object[7];
					record[0] = this.patient.getPatientID();
					record[1] = String.valueOf((Double) this.paymentsList.get(i)[1]);
					record[2] = ((Appointment) this.paymentsList.get(i)[2]).getStartTime().toString();
					record[3] = ((Appointment) this.paymentsList.get(i)[2]).getDoctor().getFirstName();
					record[4] = ((Appointment) this.paymentsList.get(i)[2]).getUsername();
					record[5] = ((Appointment) this.paymentsList.get(i)[2]).getAppointmentType();
					record[6] = "Pay Appointment";
					appointmentsList.add(record);
				}
			}
			// First table
			Object[][] convertedAppointments = convertList(appointmentsList, APPOINTMENT);
			String[] columnTitlesAppointments = { "Patient ID", "Amount Due", "Start time", "Doctor Name",
					"Doctor Username", "Appointment Type", "Actions" };
			// initialize table and add to the views
			this.appointmentPaymentsTableModel = new DefaultTableModel(convertedAppointments,
					columnTitlesAppointments) {
				@Override
				public boolean isCellEditable(int row, int col) {
					// only the last column
					return col == 6;
				}
			};
			this.appointmentPaymentsTable = new JTable(this.appointmentPaymentsTableModel);
			this.appointmentPaymentsTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
			this.appointmentPaymentsTable.getColumn("Actions")
					.setCellEditor(new ButtonEditor(new JCheckBox(), this.frame));
			JScrollPane appointmentsTableScrollPane = new JScrollPane(this.appointmentPaymentsTable);
			this.appointmentPaymentsTable.setFillsViewportHeight(true);
			// Second table
			Object[][] convertedHealthPlans = convertList(healthPlansList, this.healthPlan);
			String[] columnTitlesHealthPlans = { "Patient ID", "Amount Due", "Description", "Actions" };
			// initialize table and add to the view
			this.healthPlanPaymentsTableModel = new DefaultTableModel(convertedHealthPlans, columnTitlesHealthPlans) {
				@Override
				public boolean isCellEditable(int row, int col) {
					return col = 3;
				}
			};
			this.healthPlanPaymentsTable = new JTable(this.healthPlanPaymentsTableModel);
			this.healthPlanPaymentsTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
			this.healthPlanPaymentsTable.getColumn("Actions")
					.setCellEditor(new ButtonEditor(new JCheckBox(), this.frame));
			JScrollPane healthPlansTableScrollPane = new JScrollPane(this.healthPlanPaymentsTable);
			this.healthPlanPaymentsTable.setFillsViewportHeight(true);
			JPanel tables = new JPanel();
			tables.setLayout(new GridLayout(0, 1));
			tables.add(appointmentsTableScrollPane);
			tables.add(healthPlansTableScrollPane);
			screen.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			appointmentsTableScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			healthPlansTableScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			screen.add(tables, BorderLayout.CENTER);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Function given an array list and type of list converts to a JTable usable
	 * array
	 * 
	 * @param oldList
	 *            ArrayList to be converted
	 * @param type
	 *            Type of list (Appointments or health plans)
	 * @return 2D object array with all details
	 */
	private Object[][] ConvertList(ArrayList<Object[]> oldList, int type) {
		int rows = oldList.size();
		Object[][] newArr = new Object[rows][];
		for (int i = 0; i < rows; i++) {
			if (type == this.healthPlan) {
				newArr[i] = new Object[4];
				newArr[i][0] = oldList.get(i)[0];
				newArr[i][1] = oldList.get(i)[1];
				newArr[i][2] = oldList.get(i)[2];
				newArr[i][3] = oldList.get(i)[3];
			} else {
				newArr[i] = new Object[7];
				newArr[i][0] = oldList.get(i)[0];
				newArr[i][1] = oldList.get(i)[1];
				newArr[i][2] = oldList.get(i)[2];
				newArr[i][3] = oldList.get(i)[3];
				newArr[i][4] = oldList.get(i)[4];
				newArr[i][5] = oldList.get(i)[5];
				newArr[i][6] = oldList.get(i)[6];
			}
		}
		return newArr;
	}

	/**
	 * Function that returns all due payments.
	 * 
	 * Return is an ArrayList of object arrays that are in one of the following two
	 * formats Format 1: Type, AmountDue, HealthPlan Format 2: Type, AmountDue,
	 * Appointment
	 * 
	 * @return
	 * @throws SQLException
	 */
	private ArrayList<Object[]> getAllDuePayments() throws SQLException {
		ArrayList<Object[]> list = new ArrayList<Object[]>();
		Connection conn = Database.getConnection();
		try {
			ResultSet rs = DBQueries
					.execQuery("SELECT * FROM Payments WHERE PatientID = " + this.patient.getPatientID(), conn);
			while (rs.next()) {
				Object[] row = new Object[3];
				// AmountDue
				row[1] = rs.getDouble("AmountDue");
				// Appointment
				if (rs.getString("StartDate") == null) {
					row[0] = PaymentsDueView.healthPlan;
					row[2] = new HealthPlan(rs.getString("PatientID"));
				} else {
					row[0] = PaymentsDueView.APPOINTMENT;
					row[2] = new Appointment(rs.getTimestamp("StartDate"), rs.getString("Username"));
				}
				list.add(row);
			}
		} finally {
			conn.close();
		}
		return list;
	}

	public JPanel getPanel() {
		return this.frame1;
	}

}
