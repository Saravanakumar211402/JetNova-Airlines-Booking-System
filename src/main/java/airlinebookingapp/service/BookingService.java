package airlinebookingapp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import airlinebookingapp.dao.BookingDAO;
import airlinebookingapp.dao.FlightDAO;
import airlinebookingapp.dao.PassengerDAO;
import airlinebookingapp.exception.BookingNotFoundException;
import airlinebookingapp.exception.FlightFullException;
import airlinebookingapp.model.Booking;
import airlinebookingapp.model.Flight;
import airlinebookingapp.model.Passenger;

public class BookingService {

    // In-memory (used for testing)
    List<Booking> bookingList = new ArrayList<>();
    HashMap<String, Booking> bookingMap = new HashMap<>();
    protected PassengerDAO passengerDAO = new PassengerDAO();
    protected BookingDAO bookingDAO = new BookingDAO();
    protected FlightDAO flightDAO = new FlightDAO();

    // Creating Booking (DB-backed)
    public synchronized Booking createBooking(Flight flight, Passenger passenger, int seats)
            throws FlightFullException {
        if (!flight.bookSeat(seats)) {
            throw new FlightFullException("Sorry only " + flight.getAvailableSeats() + " seat(s) available!");
        }
        String timeStamp = java.time.LocalDate.now().toString();
        Booking booking = new Booking(passenger, flight, seats, timeStamp);
        passengerDAO.savePassenger(passenger);
        bookingDAO.saveBooking(booking);
        boolean updated = flightDAO.updateAvailableSeats(flight.getFlightId(), -seats);
        if (!updated) {
            throw new FlightFullException("Not enough seats available in DB!");
        }
        return booking;
    }

    // For JUnit test without DB
    public synchronized Booking createBookingTest(Flight flight, Passenger passenger, int seats)
            throws FlightFullException {
        if (!flight.bookSeat(seats)) {
            throw new FlightFullException("Sorry only " + flight.getAvailableSeats() + " Available!");
        }
        String timeStamp = java.time.LocalDate.now().toString();
        Booking booking = new Booking(passenger, flight, seats, timeStamp);
        bookingList.add(booking);
        bookingMap.put(booking.getBookingId(), booking);
        return booking;
    }

    public Booking searchByBookingId(String bookingId) throws BookingNotFoundException {
        return bookingDAO.findBookingById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking '" + bookingId + "' not found"));
    }

    public void cancelBooking(Booking booking, String bookingId) {
        bookingDAO.cancelBooking(bookingId);
        flightDAO.updateAvailableSeats(booking.getFlight().getFlightId(), booking.getSeats());
        booking.setStatus("CANCELLED");
    }

    public int confirmedCount() { return bookingDAO.countBookingsByStatus("CONFIRMED"); }
    public int cancelledCount() { return bookingDAO.countBookingsByStatus("CANCELLED"); }
    public double calculateRevenue() { return bookingDAO.calculateRevenue(); }

    // DB-backed confirmed bookings
    public List<Booking> getAllConfirmedBooking() {
        return bookingDAO.findAllConfirmedBookings();
    }

    // DB-backed passenger bookings
    public List<Booking> getBookingByPassenger(String passengerId) {
        return bookingDAO.findBookingsByPassenger(passengerId);
    }

    // MultiThreading (in-memory, for concurrent-test simulation)
    public synchronized Booking createBookingConcurrent(Flight flight, Passenger passenger, int seats, String bookDate)
            throws FlightFullException {
        if (flight.getAvailableSeats() < seats) {
            throw new FlightFullException("Not enough seats available!");
        }
        flight.setAvailableSeats(flight.getAvailableSeats() - seats);
        Booking booking = new Booking(passenger, flight, seats, bookDate);
        bookingList.add(booking);
        return booking;
    }
}
