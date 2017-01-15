package Rezervare_bilet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TicketBuyerGui extends JFrame {
	private static final long serialVersionUID = 1L;

	private TicketBuyerAgent myAgent;
	private List<String> sellerNames = new ArrayList();
	private List<Integer> sellerPrices = new ArrayList();
	private List<JCheckBox> sellerJCheckBoxes = new ArrayList();
	private List<JLabel> sellerJLabels = new ArrayList();

	private JPanel jPanelProposals;
	private JButton jButtonSearch;
	private JButton jButtonBuy;

	TicketBuyerGui(TicketBuyerAgent agent) {
		super(agent.getLocalName());

		myAgent = agent;

		JPanel jPanel = new JPanel(new GridBagLayout());
		final JTextField departureField = new JTextField(20);
		final JTextField arrivalField = new JTextField(20);
		final JTextField dateField = new JTextField(20);

		jPanel.add(new JLabel("Departure:"));
		jPanel.add(departureField);

		jPanel.add(new JLabel("Arrival:"));
		jPanel.add(arrivalField);

		jPanel.add(new JLabel("Date:"));
		jPanel.add(dateField);

		/*
		 * gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill =
		 * GridBagConstraints.NONE; gbc.insets = new Insets(0, 0, 10, 0);
		 */
		jButtonSearch = new JButton("Search");
		jButtonSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButtonSearch.setEnabled(false);
				jPanelProposals.removeAll();
				initProposals();
				sellerNames.clear();
				sellerPrices.clear();
				sellerJCheckBoxes.clear();
				sellerJLabels.clear();
				pack();
				myAgent.startCFP(departureField.getText(), arrivalField.getText(), dateField.getText());
			}
		});
		jPanel.add(jButtonSearch);

		/*
		 * gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill =
		 * GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(0, 0, 0, 0);
		 */
		jButtonBuy = new JButton("Buy");
		jButtonBuy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButtonBuy.setEnabled(false);
				Map<String, Boolean> accepted = new HashMap();
				for (int i = 0; i < sellerNames.size(); ++i) {
					accepted.put(sellerNames.get(i), sellerJCheckBoxes.get(i).isSelected());
				}
				myAgent.finish(accepted);
			}
		});
		jButtonBuy.setEnabled(false);
		jPanel.add(jButtonBuy);
		getContentPane().add(jPanel, BorderLayout.NORTH);

		jPanelProposals = new JPanel(new GridLayout(0, 4));
		initProposals();
		getContentPane().add(jPanelProposals, BorderLayout.CENTER);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.doDelete();
			}
		});

		setResizable(false);
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

	private void initProposals() {
		jPanelProposals.add(new JLabel("Seller Name"));
		jPanelProposals.add(new JLabel("Seller Price"));
		jPanelProposals.add(new JLabel("Buy?"));
		jPanelProposals.add(new JLabel("Result Info"));
	}

	public void addProposal(final String name, final int price) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				sellerNames.add(name);
				sellerPrices.add(price);
				jPanelProposals.add(new JLabel(name));
				jPanelProposals.add(new JLabel(Integer.toString(price)));
				JCheckBox jCheckBox = new JCheckBox();
				sellerJCheckBoxes.add(jCheckBox);
				jPanelProposals.add(jCheckBox);
				JLabel jLabel = new JLabel();
				sellerJLabels.add(jLabel);
				jPanelProposals.add(jLabel);
				pack();
			}
		});
	}

	public void endProposals() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				jButtonBuy.setEnabled(true);
			}
		});
	}

	public void endResults() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				jButtonSearch.setEnabled(true);
				jButtonBuy.setEnabled(false);
			}
		});
	}

	public void receiveResult(final String name, final String info) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < sellerNames.size(); ++i)
					if (sellerNames.get(i).equals(name)) {
						sellerJLabels.get(i).setText(info);
					}
			}
		});
	}
}
