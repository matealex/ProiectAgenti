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

class HotelSellerGui extends JFrame {
	private static final long serialVersionUID = 1L;

	private HotelSellerAgent myAgent;
	private JTextField NumarCamere, numarPersoaneInCamera, numarStele, numeHotel, priceField;
	private HotelTableModel hotelTableModel;

	HotelSellerGui(HotelSellerAgent a) {
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
		jPanel.add(new JLabel("Numar Camere:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		NumarCamere = new JTextField(15);
		jPanel.add(NumarCamere, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		jPanel.add(new JLabel("Numar Persoane In Camera:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		numarPersoaneInCamera = new JTextField(15);
		jPanel.add(numarPersoaneInCamera, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		jPanel.add(new JLabel("Numar Stele:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		numarStele = new JTextField(15);
		jPanel.add(numarStele, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		jPanel.add(new JLabel("Nume Hotel:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		numeHotel = new JTextField(15);
		jPanel.add(numeHotel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		jPanel.add(new JLabel("Price:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 4;
		priceField = new JTextField(15);
		jPanel.add(priceField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 10, 0);
		JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					String nume = numeHotel.getText().trim();
					int nrCamere = Integer.parseInt(NumarCamere.getText().trim());
					int nrPersoneCamera = Integer.parseInt(numarPersoaneInCamera.getText().trim());
					int nrStele = Integer.parseInt(numarStele.getText().trim());
					int pret = Integer.parseInt(priceField.getText().trim());

					NumarCamere.setText("");
					numarPersoaneInCamera.setText("");
					numarStele.setText("");

					Hotel h = new Hotel(nume, nrCamere, nrPersoneCamera, nrStele, pret);
					myAgent.addHotel(h);
					hotelTableModel.addHotel(h);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(HotelSellerGui.this, "Invalid values. " + e.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		jPanel.add(addButton, gbc);

		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 0);
		hotelTableModel = new HotelTableModel();
		JTable jTable = new JTable(hotelTableModel);
		jTable.getTableHeader().setReorderingAllowed(false);
		jTable.getTableHeader().setResizingAllowed(false);

		JScrollPane jScrollPane = new JScrollPane(jTable);
		Dimension preferredSize = new Dimension();
		preferredSize.setSize(900, 100);
		jScrollPane.setPreferredSize(preferredSize);
		jPanel.add(jScrollPane, gbc);

		getContentPane().add(jPanel, BorderLayout.CENTER);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.doDelete();
			}
		});

		setResizable(true);
	}

	@SuppressWarnings("deprecation")
	public void show() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int) screenSize.getWidth() / 2;
		int centerY = (int) screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.show();
	}

	public void buyRoomAtHotel(final String numeHotel) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				hotelTableModel.buyRoomAtHotel(numeHotel);
			}
		});
	}

	public void refuseRoomAtHotel(final String numeHotel) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				hotelTableModel.refuseRoomAtHotel(numeHotel);
			}
		});
	}

	private static class HotelTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;

		public static final String[] COLUMN_NAMES = { "No", "Nume Hotel", "Numar Camere", "Persoane In Camera", "Stele",
				"Price", "Sales" };
		public static final int COLUMN_COUNT = COLUMN_NAMES.length;
		public static final int NR_COLUMN = 0;
		public static final int NUME_COLUMN = 1;
		public static final int NRCAMERE_COLUMN = 2;
		public static final int NRPERSOANECAMERA_COLUMN = 3;
		public static final int STELE_COLUMN = 4;
		public static final int PRICE_COLUMN = 5;
		public static final int SALES_COLUMN = 6;

		private List<Hotel> hotels = new ArrayList();

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
			return hotels.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Hotel hotel = hotels.get(row);
			switch (col) {
			case NR_COLUMN:
				return row + 1;
			case NUME_COLUMN:
				return hotel.getNume();
			case NRCAMERE_COLUMN:
				return hotel.getNumarCamere();
			case NRPERSOANECAMERA_COLUMN:
				return hotel.getNumarPersoaneInCamera();
			case STELE_COLUMN:
				return hotel.getNumarStele();
			case PRICE_COLUMN:
				return hotel.getPret();
			case SALES_COLUMN:
				return hotel.getSales();
			}
			return null;
		}

		public void addHotel(Hotel flight) {
			hotels.add(flight);
			fireTableDataChanged();
		}

		public void buyRoomAtHotel(final String numeHotel) {
			Hotel hotel = getHotelByName(numeHotel);
			if (hotel != null) {
				hotel.buyRoom();
				fireTableDataChanged();
			}
		}

		public void refuseRoomAtHotel(final String numeHotel) {
			//TODO ????
			// Hotel hotel = getHotelByName(numeHotel);
			// if (hotel != null) { {
			// hotel.incrementRefusals();
			// fireTableDataChanged();
			// }
		}

		private Hotel getHotelByName(String name) {
			for (Hotel hotel : hotels) {
				if (hotel.getNume().equals(name)) {
					return hotel;
				}
			}
			return null;
		}
	}
}
