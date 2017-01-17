package Rezervare_bilet;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
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
import java.util.concurrent.atomic.AtomicInteger;

public class TicketSellerAgent extends Agent {
	private static final long serialVersionUID = 1L;
	
	private List<Flight> flights;
	private Map<AID, AtomicInteger> clients;
	private TicketSellerGui myGui;

	protected void setup() {
		flights = new ArrayList();
		clients = new HashMap();

		myGui = new TicketSellerGui(this);
		myGui.show();

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Ticket-selling");
		sd.setName("JADE-ticket-trading");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		addBehaviour(new OfferRequestsServer());
		addBehaviour(new OfferRefusalsServer());
		addBehaviour(new PurchaseOrdersServer());
	}

	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		myGui.dispose();
	}

	// adaugare zboruri
	public void addFlight(final Flight flight) {
		addBehaviour(new OneShotBehaviour() {
			private static final long serialVersionUID = 1L;

			public void action() {
				flights.add(flight);
			}
		});
	}

	// oferta de zboruri in functie de cautarea clientului => propunere sau
	// refuzare
	private class OfferRequestsServer extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				String message = msg.getContent();
				String parts[] = message.split(":");
				String departure = parts[0];
				String arrival = parts[1];
				String fromDate = parts[2];
				String toDate = parts[3];
				Flight flight = Flight.findFlight(flights, departure, arrival, fromDate, toDate);
				ACLMessage reply = msg.createReply();

				if (flight != null) {
					reply.setPerformative(ACLMessage.PROPOSE);
					int price = flight.getPrice();
					reply.setContent(String.valueOf(price));
				} else {
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			} else {
				block();
			}
		}
	}

	// in cazul in care clientul a refuzat oferta propusa de seller
	private class OfferRefusalsServer extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				String message = msg.getContent();
				String parts[] = message.split(":");
				String departure = parts[0];
				String arrival = parts[1];
				String fromDate = parts[2];
				String toDate = parts[3];
				Flight flight = Flight.findFlight(flights, departure, arrival, fromDate, toDate);
				if (flight != null) {
					flight.incrementRefusals();
					myGui.incrementRefusalsForFlight(flight.getDeparture(), flight.getArrival(),
							flight.getFromDate(), flight.getToDate());
				}
			} else {
				block();
			}
		}
	}

	// in cazul in care clientul accepta oferta propusa de seller
	private class PurchaseOrdersServer extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				String message = msg.getContent();
				String parts[] = message.split(":");
				String departure = parts[0];
				String arrival = parts[1];
				String fromDate = parts[2];
				String toDate = parts[3];
				Flight flight = Flight.findFlight(flights, departure, arrival, fromDate, toDate);
				ACLMessage reply = msg.createReply();

				if (flight != null) {
					AID aid = msg.getSender();
					if (!clients.containsKey(aid))
						clients.put(aid, new AtomicInteger(1));
					else
						clients.get(aid).incrementAndGet();
					flight.incrementSalesAndDecreaseSeats();
					myGui.incrementSalesAndDecreaseSeatsForFlight(flight.getDeparture(), flight.getArrival(),
							flight.getFromDate(), flight.getToDate());
					reply.setPerformative(ACLMessage.INFORM);
				} else {
					reply.setPerformative(ACLMessage.FAILURE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			} else {
				block();
			}
		}
	}
}
