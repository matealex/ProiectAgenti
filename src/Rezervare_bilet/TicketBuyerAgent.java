package Rezervare_bilet;

import jade.core.AID;
import jade.core.Agent;
//import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketBuyerAgent extends Agent {

	private static final long serialVersionUID = 1L;
	private String targetDeparture;
	private String targetArrival;
	private String targetFromDate;
	private String targetToDate;
	private AID[] sellerAgents;
	private Map<String, Boolean> accepted;
	private boolean finished;
	private List<AID> proposals;
	private List<Integer> prices;
	private TicketBuyerGui myGui;

	protected void setup() {
		myGui = new TicketBuyerGui(this);
		myGui.show();
	}

	// finish = buton "buy" cu sau fara selectie de checkbox-buy
	public void finish(final Map<String, Boolean> acc) {
		// System.out.println("finish");
		addBehaviour(new OneShotBehaviour() {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				System.out.println("Finished");
				accepted = acc;
				finished = true;
			}
		});
	}

	// click on search => CFP = call for proposal - cererea de oferte
	public void startCFP(final String searchDeparture, final String searchArrival, final String searchFromDate, final String searchToDate) {
		addBehaviour(new OneShotBehaviour() {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				targetDeparture = searchDeparture;
				targetArrival = searchArrival;
				targetFromDate = searchFromDate;
				targetToDate = searchToDate;
				accepted = new HashMap();
				proposals = new ArrayList();
				prices = new ArrayList();
				finished = false;

				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("Ticket-selling");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					sellerAgents = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						sellerAgents[i] = result[i].getName();
					}
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}

				myAgent.addBehaviour(new RequestPerformer());
			}
		});
	}

	protected void takeDown() {
		myGui.setVisible(false);
		myGui.dispose();
	}

	//
	private class RequestPerformer extends SimpleBehaviour {
		private static final long serialVersionUID = 1L;
		private int repliesCnt = 0; // The counter of replies from seller agents
		private MessageTemplate mt; // The template to receive replies
		private int step = 0;
		private int accCount = 0;
		private int countResults = 0;

		public void action() {
			String searchAll = targetDeparture + ":" + targetArrival + ":" + targetFromDate + ":" + targetToDate;
			switch (step) {
			// cere ofertele de la toti seller agents (companii)
			case 0:
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < sellerAgents.length; ++i) {
					cfp.addReceiver(sellerAgents[i]);
				}
				cfp.setContent(searchAll);
				cfp.setConversationId("Ticket-trade");
				cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																		// value
				myAgent.send(cfp);
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Ticket-trade"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				step = 1;
				break;
			// se primesc ofertele companiilor (cele care propun oferte)
			case 1:
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						int price = Integer.parseInt(reply.getContent());
						proposals.add(reply.getSender());// lista de companii
						prices.add(price);
						myGui.addProposal(reply.getSender().getLocalName(), price);
					}
					repliesCnt++;
					if (repliesCnt >= sellerAgents.length) {
						step = 2;
						myGui.endProposals();
						if (proposals.size() == 0) {
							step = 4;
							return;
						}
					}
				} else {
					block();// se incheie executia pana cand apare un nou mesaj
				}
				break;
			// apasare buton "buy"
			case 2:
				if (!finished)
					return;// daca nu am luat inca o decizie (accept sau reject)

				boolean acc = false;
				boolean rej = false;
				ACLMessage order_accept = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				ACLMessage order_reject = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
				for (AID aid : proposals) {
					if (accepted.get(aid.getLocalName())) {
						order_accept.addReceiver(aid);
						acc = true;
						accCount++;
						System.out.println("Accepted Count: " + accCount);
					} else {
						order_reject.addReceiver(aid);
						rej = true;
					}
				}
				order_accept.setContent(searchAll);
				order_reject.setContent(searchAll);
				order_accept.setConversationId("Ticket-trade");
				order_reject.setConversationId("Ticket-trade");
				order_accept.setReplyWith("order" + System.currentTimeMillis());
				order_reject.setReplyWith("order" + System.currentTimeMillis());
				if (rej) {
					myAgent.send(order_reject);
				}
				if (acc)
					myAgent.send(order_accept);
				else {
					step = 4;
					return;
				}
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Ticket-trade"),
						MessageTemplate.MatchInReplyTo(order_accept.getReplyWith()));
				step = 3;
				break;
			// se primeste informatie de la seller daca s-a cumparat cu succes
			// sau nu
			case 3:
				reply = myAgent.receive(mt);
				if (reply != null) {
					if (reply.getPerformative() == ACLMessage.INFORM) {
						myGui.receiveResult(reply.getSender().getLocalName(), "SUCCES");
					} else {
						myGui.receiveResult(reply.getSender().getLocalName(), "FAILURE");
					}
					countResults++;
					if (countResults == accCount) {
						step = 4;
					}
				} else {
					block();
				}
				break;
			}
		}

		public boolean done() {
			if ((step == 2 && proposals.size() == 0 && finished) || (step == 4)) {
				myGui.endResults();
				return true;
			}
			return false;
		}
	}
}
