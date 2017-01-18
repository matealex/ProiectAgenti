package Rezervare_bilet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

public class Flight {

	private String departure;
	private String arrival;
	private String fromDate;
	private String toDate;
	private String timeDeparture;
	private String timeArrival;
	private int price;
	private int nrSeats;
	private int sales;
	private int refusals;

	public Flight(String departure, String arrival, String fromDate, String toDate, String timeDeparture,
			String timeArrival, int price, int nrSeats) {
		this.departure = departure;
		this.arrival = arrival;
		this.timeDeparture = timeDeparture;
		this.timeArrival = timeArrival;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.price = price;
		this.nrSeats = nrSeats;
		sales = 0;
		refusals = 0;
	}

	public String getDeparture() {
		return departure;
	}

	public String getArrival() {
		return arrival;
	}

	public String getFromDate() {
		return fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public String getTimeDeparture() {
		return timeDeparture;
	}

	public String getTimeArrival() {
		return timeArrival;
	}

	public int getPrice() {
		return price;
	}

	public int getNrSeats() {
		return nrSeats;
	}

	public void incrementSalesAndDecreaseSeats() {
		nrSeats--;
		sales++;
	}

	public int getSales() {
		return sales;
	}

	public int getRefusals() {
		return refusals;
	}

	public void incrementRefusals() {
		refusals++;
	}

	public static Flight findFlight(Collection<Flight> flights, String departure, String arrival, String fromDate,
			String toDate) {
		System.out.println(fromDate + "->" + toDate);
		for (Flight flight : flights)
			if (flight.nrSeats > 0) {
				if (flight.departure.equals(departure))
					if (flight.arrival.equals(arrival)) {
						int from = Integer.parseInt(fromDate) - 3;
						int to = Integer.parseInt(toDate) + 3;
						int flightFrom = Integer.parseInt(flight.fromDate);
						int flightTo = Integer.parseInt(flight.toDate);

						if (flightFrom <= from && to <= flightTo) {
							return flight;
						}
					}
			}

		return null;

	}
}
