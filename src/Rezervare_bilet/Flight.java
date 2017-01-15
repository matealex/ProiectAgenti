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
	private String date;
	
private String timeDeparture;
	
private String timeArrival;

	
private int price;
	
private int nrSeats;
	
private int sales;
	
private int refusals;
	
	
public Flight(String departure, 
	String arrival, String date, String timeDeparture, String timeArrival, 
	int price, int nrSeats)
{
		
this.departure = departure;
		
this.arrival = arrival;
		
this.timeDeparture = timeDeparture;
		
this.timeArrival = timeArrival;
		
this.date=date;
		
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

	
public Date getDate() {
		
Date date_format = new Date();
		
try {
			
DateFormat format = new SimpleDateFormat("DD-MM-YYYY", Locale.ENGLISH);
	
		date_format = format.parse(date);
		
}
		
catch(ParseException e){
		
}
		
return date_format;
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

	
public static Flight findFlight
(Collection<Flight> flights, String departure, String arrival, String date) {
		
Date date_gui = new Date();
		
try {
			
DateFormat format = new SimpleDateFormat("DD-MM-YYYY", Locale.ENGLISH);
			
date_gui = format.parse(date);
		
}
		
catch(ParseException e){
		
}
		
System.out.println(date);
		
for (Flight flight : flights)
			
if(flight.nrSeats > 0){
				
if (flight.departure.equals(departure))
					
if(flight.arrival.equals(arrival)){
						
int to = flight.getDate().getDay()- 3;
						
int from = flight.getDate().getDay()+3;
						
Date date_gui1 = null;
if(flight.date.equals(date)
 || (to <= date_gui1.getDay() && from >= date_gui1.getDay()))
						
	return flight;
					}	
			
}
		
return null;
	
}
}
