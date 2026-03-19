package airlinebookingapp.model;

public class Booking {

	private String bookingId;
	private Passenger passenger;
	private Flight flight;
	private int seats;
	private String bookDate;
	private String status;
	private double farePaid;

	public Booking( Passenger passenger, Flight flight,int seats,String bookDate) {
		super();
		this.bookingId=generateBookingId();
		this.passenger = passenger;
		this.flight = flight;
		this.seats = seats;
		this.bookDate = bookDate;
		this.status ="CONFIRMED";
		this.farePaid = flight.getPrice() * seats;
	}
	
	//for storing data from DATABASE
	public Booking(String bookingId, String passengerId, String name,String flightId ,int seats, String bookDate, String status,double farepaid) {
        this.bookingId = bookingId;
        this.passenger = new Passenger(passengerId, name, null,0);
        this.flight = new Flight(flightId,null,null, null,0, 0,0);
        this.seats = seats;
        this.bookDate = bookDate;
        this.status = status;
        this.farePaid=farepaid;
    }

	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String generateBookingId() {
		return "B"+(int)(Math.random()*1000);
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public Flight getFlight() {
		return flight;
	}
	
	public int getSeats() {
		return seats;
	}
	
	public String getBookingId() {
		return bookingId;
	}

	public double getFarePaid() {
		return farePaid;
	}

	
	public String getBookDate() {
		return bookDate;
	}

	public void setBookDate(String bookDate) {
		this.bookDate = bookDate;
	}

	public void setFarePaid(double farePaid) {
		this.farePaid = farePaid;
	}

	@Override
	public String toString() {
		return "===========================================\n----- BOOKING DETAILS! -----\n===========================================\n" +
		           "PASSENGER ID  : " + passenger.getPassengerId() + "\n" +
		           "Booking ID    : " + bookingId + "\n" +
		           "Flight        : " + flight.getFlightId() + " (" + flight.getFlightNumber() + ")\n" +
		           "Passenger     : " + passenger.getName() + "\n" +
		           "Booking Date  : " + bookDate + "\n" +
		           "No Of Seats   : " + seats + "\n" +
		           "Status        : " + status + "\n" +
		           "Fare Paid     : ₹" + getFarePaid() + "\n" +
		           "--------------------------\n";
	}
	
	public String ticketDetails(Booking booking) {
		return "===========================================\n----- BOOKING DETAILS! -----\n===========================================\n" +
		           "PASSENGER ID  : " + passenger.getPassengerId() + "\n" +
		           "Booking ID    : " + bookingId + "\n" +
		           "Flight        : " + flight.getFlightId() + " (" + flight.getFlightNumber() + ")\n" +
		           "Passenger     : " + passenger.getName() + "\n" +
		           "Booking Date  : " + bookDate + "\n" +
		           "No Of Seats   : " + seats + "\n" +
		           "Status        : " + status + "\n" +
		           "Fare Paid     : ₹" + flight.getPrice()*seats + "\n" +
		           "--------------------------\n";
	}
}
