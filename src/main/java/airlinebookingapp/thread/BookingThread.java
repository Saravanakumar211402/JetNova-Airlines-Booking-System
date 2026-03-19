package airlinebookingapp.thread;

import airlinebookingapp.model.Flight;
import airlinebookingapp.service.FlightService;
import airlinebookingapp.exception.FlightFullException;
import airlinebookingapp.exception.FlightNotFoundException;

public class BookingThread extends Thread {
	private Flight flight;
    private int seats;

    public BookingThread(String name, Flight flight, int seats) {
        super(name);
        this.flight = flight;
        this.seats = seats;
    }

    @Override
    public void run() {
        synchronized (flight) { // ensure thread safety
            if (flight.bookSeat(seats)) {
                System.out.println(getName() + " booked ticket on flight: "
                        + flight.getFlightId()
                        + " | Remaining seats: "
                        + flight.getAvailableSeats());
            } else {
                System.out.println(getName() + " failed: Not enough seats!");
            }
        }
    }

}
