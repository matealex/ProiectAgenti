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

public class HotelSellerAgent extends Agent {
	private static final long serialVersionUID = 1L;

	private List<Hotel> hotels;
	private Map<AID, AtomicInteger> clients;
	private HotelSellerGui myGui;

	protected void setup() {
		hotels = new ArrayList();
		clients = new HashMap();

		myGui = new HotelSellerGui(this);
		myGui.show();

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Hotel-selling");
		sd.setName("JADE-hotel-trading");
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
	public void addHotel(final Hotel hotel) {
		addBehaviour(new OneShotBehaviour() {
			private static final long serialVersionUID = 1L;

			public void action() {
				hotels.add(hotel);
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

				int nrStele = Integer.parseInt(parts[0]);
				int nrPersoaneCamera = Integer.parseInt(parts[1]);

				Hotel hotelBun = null;
				for (Hotel hotel : hotels) {
					if (hotel.getNumarStele() == nrStele && hotel.getNumarPersoaneInCamera() == nrPersoaneCamera) {
						hotelBun = hotel;
					}
				}

				ACLMessage reply = msg.createReply();

				if (hotelBun != null) {
					reply.setPerformative(ACLMessage.PROPOSE);
					int price = hotelBun.getPret();
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

				int nrStele = Integer.parseInt(parts[0]);
				int nrPersoaneCamera = Integer.parseInt(parts[1]);

				Hotel hotelBun = null;
				for (Hotel hotel : hotels) {
					if (hotel.getNumarStele() == nrStele && hotel.getNumarPersoaneInCamera() == nrPersoaneCamera) {
						hotelBun = hotel;
					}
				}

				if (hotelBun != null) {
					hotelBun.incrementRefusals();
					myGui.refuseRoomAtHotel(hotelBun.getNume());
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
				int nrStele = Integer.parseInt(parts[0]);
				int nrPersoaneCamera = Integer.parseInt(parts[1]);

				Hotel hotelBun = null;
				for (Hotel hotel : hotels) {
					if (hotel.getNumarStele() == nrStele && hotel.getNumarPersoaneInCamera() == nrPersoaneCamera) {
						hotelBun = hotel;
					}
				}
				ACLMessage reply = msg.createReply();

				if (hotelBun != null) {
					AID aid = msg.getSender();
					if (!clients.containsKey(aid))
						clients.put(aid, new AtomicInteger(1));
					else
						clients.get(aid).incrementAndGet();

					hotelBun.buyRoom();
					myGui.buyRoomAtHotel(hotelBun.nume);
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
