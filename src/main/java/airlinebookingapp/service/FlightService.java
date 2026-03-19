package airlinebookingapp.service;

import java.util.ArrayList;
import java.util.List;

import airlinebookingapp.dao.FlightDAO;
import airlinebookingapp.exception.FlightFullException;
import airlinebookingapp.exception.FlightNotFoundException;
import airlinebookingapp.model.Flight;



public class FlightService {
 
	List<Flight> flights = new ArrayList<Flight>();
	private final FlightDAO flightDAO = new FlightDAO();


	// initFlights
	public void initFlights(Flight flight) {
		 flightDAO.saveFlight(flight);
		 flights.add(flight);
	}

	// getAllFlights
	public List<Flight> getAllFlights() {
        return flightDAO.fetchAllFlights();
    } 
 
	// searchByRoute
	public List<Flight> searchByRoute(String source, String destination) throws FlightNotFoundException {//multiple flight return

		List<Flight> flights = flightDAO.findFlightsByRoute(source, destination);
        if (flights.isEmpty()) {
            throw new FlightNotFoundException("Flight not found");
        }
        return flights;
	}

	// availableFlight
	public synchronized Flight getAvailabFlight(String id) throws FlightNotFoundException, FlightFullException {//single flight return
		Flight flight = flightDAO.fetchAvailableFlightById(id);
        if (flight == null) {
            throw new FlightNotFoundException("Cannot fetching flight");
        }
        if (flight.getAvailableSeats() <= 0) {
            throw new FlightFullException("No Seats Available! Choose Another Flight");
        }
        return flight;
    }

	//MultiThreading
	public synchronized Flight bookSeats(String flightId,int seats) 
	        throws FlightNotFoundException, FlightFullException {
	    
	    // Find the flight by ID
	    Flight flight = flights.stream()
	            .filter(f -> f.getFlightId().equalsIgnoreCase(flightId))
	            .findFirst()
	            .orElseThrow(() -> new FlightNotFoundException("Flight not found"));

	    
	    if (flight.getAvailableSeats() < seats) {
	        throw new FlightFullException("Not enough seats available!");
	    }

	    flight.setAvailableSeats(flight.getAvailableSeats() - seats);

	    return flight;
	}

}

