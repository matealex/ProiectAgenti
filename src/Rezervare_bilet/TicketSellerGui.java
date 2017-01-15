package Rezervare_bilet;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

class TicketSellerGui extends JFrame {	
	private static final long serialVersionUID = 1L;
	
	private TicketSellerAgent myAgent;	
	private JTextField departureField, arrivalField, dateField, timeDepartureField, timeArrivalField, seatsField, priceField;
	private FlightTableModel flightTableModel;
	
	TicketSellerGui(TicketSellerAgent a) {
		super(a.getLocalName());
		
		myAgent = a;
		
		JPanel jPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.5;
		gbc.weighty = 0.5;
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		jPanel.add(new JLabel("Departure:"), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		departureField = new JTextField(15);
		jPanel.add(departureField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		jPanel.add(new JLabel("Arrival:"), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		arrivalField = new JTextField(15);
		jPanel.add(arrivalField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		jPanel.add(new JLabel("Date:"), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		dateField = new JTextField(15);
		jPanel.add(dateField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		jPanel.add(new JLabel("Time departure:"), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 3;
		timeDepartureField = new JTextField(15);
		jPanel.add(timeDepartureField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		jPanel.add(new JLabel("Time arrival:"), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 4;
		timeArrivalField = new JTextField(15);
		jPanel.add(timeArrivalField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 5;
		jPanel.add(new JLabel("Seats:"), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 5;
		seatsField = new JTextField(15);
		jPanel.add(seatsField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 6;
		jPanel.add(new JLabel("Price:"), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 6;
		priceField = new JTextField(15);
		jPanel.add(priceField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 10, 0);
		JButton addButton = new JButton("Add");
		addButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					String departure = departureField.getText().trim();
					String arrival = arrivalField.getText().trim();
					String date = dateField.getText().trim();
					String timeDeparture = timeDepartureField.getText().trim();
					String timeArrival = timeArrivalField.getText().trim();
					String seats = seatsField.getText().trim();
					String price = priceField.getText().trim();
					
					departureField.setText("");
					arrivalField.setText("");
					dateField.setText("");
					timeDepartureField.setText("");
					timeArrivalField.setText("");
					seatsField.setText("");
					priceField.setText("");
					
					myAgent.addFlight(new Flight(departure,arrival,date,timeDeparture, timeArrival, Integer.parseInt(price), Integer.parseInt(seats)));
					flightTableModel.addFlight(new Flight(departure,arrival,date, timeDeparture, timeArrival,Integer.parseInt(price), Integer.parseInt(seats)));
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(TicketSellerGui.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
				}
			}
		} );
		jPanel.add(addButton, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 0);
		flightTableModel = new FlightTableModel();
		JTable jTable = new JTable(flightTableModel);
		jTable.getTableHeader().setReorderingAllowed(false);
		jTable.getTableHeader().setResizingAllowed(false);		

		JScrollPane jScrollPane = new JScrollPane(jTable);
		Dimension preferredSize = new Dimension();
		preferredSize.setSize(900, 100);
		jScrollPane.setPreferredSize(preferredSize);
		jPanel.add(jScrollPane, gbc);
		
		getContentPane().add(jPanel, BorderLayout.CENTER);
		
		addWindowListener(new	WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.doDelete();
			}
		} );
		
		setResizable(true);
	}
	
	@SuppressWarnings("deprecation")
	public void show() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.show();
	}	
	
	public void addFlight(final Flight flight) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				flightTableModel.addFlight(flight);
			}
		});
	}
	
	public void incrementSalesAndDecreaseSeatsForFlight(final String departure, final String arrival, final String date) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				flightTableModel.incrementSalesAndDecreaseSeatsForFlight(departure,arrival,date);
			}
		});
	}
	
	public void incrementRefusalsForFlight(final String departure, final String arrival, final String date) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				flightTableModel.incrementRefusalsForFlight(departure,arrival,date);
			}
		});
	}
	
	private static class FlightTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		public static final String[] COLUMN_NAMES = {"No", "Departure", 
			"Arrival", "Date", "Time departure", "Time arrival", "Seats", "Price", "Sales", "Refusal"};
		public static final int COLUMN_COUNT = COLUMN_NAMES.length;
		public static final int NR_COLUMN = 0;
		public static final int DEPARTURE_COLUMN = 1;
		public static final int ARRIVAL_COLUMN = 2;
		public static final int DATE_COLUMN = 3;
		public static final int TIMED_COLUMN = 4;
		public static final int TIMEA_COLUMN = 5;
		public static final int SEATS_COLUMN = 6;
		public static final int PRICE_COLUMN = 7;
		public static final int SALES_COLUMN = 8;
		public static final int REFUSALS_COLUMN = 9;
		
		private List<Flight> flights = new ArrayList();
		
		@Override
		public String getColumnName(int col) {
			return COLUMN_NAMES[col];
		}

		@Override
		public int getColumnCount() {
			return COLUMN_COUNT;
		}

		@Override
		public int getRowCount() {
			return flights.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Flight flight = flights.get(row);
			switch (col) {
			case NR_COLUMN:
				return row + 1;
			case DEPARTURE_COLUMN:
				return flight.getDeparture();
			case ARRIVAL_COLUMN:
				return flight.getArrival();	
			case DATE_COLUMN:
				return flight.getDate();	
			case TIMED_COLUMN:
				return flight.getTimeDeparture();
			case TIMEA_COLUMN:
				return flight.getTimeArrival();
			case SEATS_COLUMN:
				return flight.getNrSeats();
			case PRICE_COLUMN:
				return flight.getPrice();			
			case SALES_COLUMN:
				return flight.getSales();
			case REFUSALS_COLUMN:
				return flight.getRefusals();
			}
			return null;
		}
		
		public void addFlight(Flight flight) {
			flights.add(flight);
			fireTableDataChanged();
		}
		
		public void incrementSalesAndDecreaseSeatsForFlight(final String departure, final String arrival, final String date) {
			Flight flight = Flight.findFlight(flights, departure, arrival, date);
			if (flight != null) {
				flight.incrementSalesAndDecreaseSeats();
				fireTableDataChanged();
			}
		}
		
		public void incrementRefusalsForFlight(final String departure, final String arrival, final String date) {
			Flight flight = Flight.findFlight(flights, departure, arrival, date);
			if (flight != null) {
				flight.incrementRefusals();
				fireTableDataChanged();
			}
		}
	}
}

