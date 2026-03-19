package airlinebookingapp;

import java.util.List;
import java.util.Scanner;

import airlinebookingapp.exception.AdminInvalidException;
import airlinebookingapp.exception.BookingNotFoundException;
import airlinebookingapp.exception.FlightFullException;
import airlinebookingapp.exception.FlightNotFoundException;
import airlinebookingapp.model.Booking;
import airlinebookingapp.model.Flight;
import airlinebookingapp.model.Passenger;
import airlinebookingapp.service.BookingService;
import airlinebookingapp.service.FlightService;
import airlinebookingapp.thread.BookingThread;

public class Main {
	FlightService flightService = new FlightService();
	BookingService bookingService = new BookingService();

	public static void main(String[] args) {

		Main main = new Main();
		main.initFlights();
		main.showMenu(); 
	}

	public void initFlights() {
		flightService.initFlights(new Flight("F001", "AI101", "Mumbai", "Delhi", 5900.0, 150, 10));
		flightService.initFlights(new Flight("F002", "SG202", "Delhi", "Bangalore", 6480.0, 200, 40));
		flightService.initFlights(new Flight("F003", "UK303", "Mumbai", "Kolkata", 5200.0, 120, 2));
		flightService.initFlights(new Flight("F004", "AI104", "Bangalore", "Chennai", 4200.0, 150, 100));
		flightService.initFlights(new Flight("F005", "SG205", "Chennai", "Hyderabad", 2500.0, 250, 35));
		flightService.initFlights(new Flight("F006", "UK306", "Mumbai", "Delhi", 4900.0, 220, 55));
		flightService.initFlights(new Flight("F007", "test", "test", "test", 4900.0, 220, 50));
		
	}
 
	public void showMenu() {
		Scanner scan = new Scanner(System.in);

		while (true) {
			System.out.println("=".repeat(40));
			System.out.println("AIRLINE BOOKING SYSTEM");
			System.out.println("=".repeat(40));

			System.out.println("\n1. View All Flights");
			System.out.println("2. Search Flights");
			System.out.println("3. Book Flight");
			System.out.println("4. Cancel Bookings");
			System.out.println("5. View My Bookings");
			System.out.println("6. Show Revenue");
			System.out.println("7. Test Concurrent Booking");
			System.out.println("8. Exit");
			System.out.println("=".repeat(40));

			int choice = getIntValidator(scan, "Enter the Choice:");

			switch (choice) {
			case 1:
				System.out.println("=".repeat(40));
				System.out.println("----- AVAILABLE FLIGHTS -----");
				System.out.println("=".repeat(40)+"\n");
				List<Flight>flights= flightService.getAllFlights();
				flights.forEach(System.out::println);
				break;
			case 2:
				System.out.println("=".repeat(40));
				System.out.println("----- SEARCH FLIGHTS -----");
				System.out.println("=".repeat(40)+"\n");
				searchFlight(scan);
				break;
			case 3:
				System.out.println("=".repeat(40));
				System.out.println("----- TICKET BOOKING -----");
				System.out.println("=".repeat(40)+"\n");
				bookTicketMenu(scan);
				break;
			case 4:
				System.out.println("=".repeat(40));
				System.out.println("----- CANCEL BOOKING MENU-----");
				System.out.println("=".repeat(40)+"\n");
				cancelBookingMenu(scan);
				break;
			case 5:
				System.out.println("=".repeat(40));
				System.out.println("----- TICKET DETAILS -----");
				System.out.println("=".repeat(40)+"\n");
				viewMyBooking(scan);
				break;

			case 6:
				System.out.println("=".repeat(40));
				System.out.println("----- REVENUE DETAILS -----");
				System.out.println("=".repeat(40)+"\n");
				System.out.println("----- ADMIN LOGIN -----\n");
				showRevenueMenu(scan);
				break;
			case 7:
				System.out.println("=".repeat(40));
				System.out.println("----- CONCURRENT TESTING -----");
				System.out.println("=".repeat(40)+"\n");
				testConcurrentBooking();
				break;
			case 8:
				System.out.println("App Exits..");
				System.out.println("=".repeat(40));
				System.out.println("----- THANK YOU -----");
				System.out.println("=".repeat(40)+"\n");
				System.exit(0);
				break;
			default:
				System.out.println("Enter choice from 1 to 8:");
				break;
			}
		}
	}

	

	public void searchFlight(Scanner scan) {
		String source = stringValidator(scan, "Enter the Source:");
		String destination = stringValidator(scan, "Enter the Destination:");
		try {
			List<Flight> flight = flightService.searchByRoute(source, destination);
			System.out.println("-----FOUND " + flight.size() + " FLIGHTS-----\n");
			flight.forEach(System.out::println);
			System.out.println();
		} catch (FlightNotFoundException e) {
			System.out.println("Error:" + e.getMessage());
		}
	}

	public void bookTicketMenu(Scanner scan) {
		String id = stringValidator(scan, "Enter The Flight Id You Want To Book:");
		try {
			System.out.println("=".repeat(40));
			System.out.println("----- SELECTED FLIGHT -----");
			System.out.println("=".repeat(40)+"\n");
			Flight selectedflight = flightService.getAvailabFlight(id);
			System.out.println(selectedflight);
			System.out.println("=".repeat(40));
			System.out.println("----- PASSENGER DETAILS -----");
			System.out.println("=".repeat(40)+"\n");
			String name = stringValidator(scan, "Enter Passenger Name:");
			String email = emailValidator(scan, "Enter Passenger Email Id:");
			int phone = getIntValidator(scan, "Enter Passenger Phone:");
			int seats = getIntValidator(scan, "Enter Seats To Book:");
			Booking booking = bookingService.createBooking(selectedflight, new Passenger(name, email, phone), seats);
			System.out.println(booking.ticketDetails(booking));
		} catch (FlightFullException e) {
			System.out.println("Error: " + e.getMessage());
		} catch (FlightNotFoundException e) {
			System.out.println("Error:" + e.getMessage());
		}
	}
	
	 
	public void cancelBookingMenu(Scanner scan) {
		String bookingId = stringValidator(scan, "Enter Booking ID:");
		try {
			Booking bookingDetails = bookingService.searchByBookingId(bookingId);
			if (bookingDetails.getStatus().equalsIgnoreCase("CANCELLED")) {
				System.out.println("This booking is already cancelled!");
				return; // exit early
			}
			System.out.println(bookingDetails);
			while (true) {
				int action = getIntValidator(scan, "Do Want To Cancel TIcket Booking(1.Yes/2.No):");
				if (action == 1) {
					bookingService.cancelBooking(bookingDetails,bookingId);
					System.out.println("Processing......");
					System.out.println("Ticket Booking Cancelled!");
					break;
				} else if (action == 2) {
					System.out.println("Cancellation Aborted");
					break;
				} else {
					System.out.println("Invalid choice. Please enter 1 or 2.\n");
				}
			}
		} catch (BookingNotFoundException e) {
			System.out.println("Error:" + e.getMessage());
		}
	}

	public void viewMyBooking(Scanner scan) {
		String bookingId = stringValidator(scan, "Enter Booking ID:");
		try {
			Booking bookingDetails = bookingService.searchByBookingId(bookingId);
			if (bookingDetails.getStatus().equalsIgnoreCase("CANCELLED")) {
				System.out.println("This booking is already cancelled!");
				return; // exit early
			}
			System.out.println(bookingDetails);
		} catch (BookingNotFoundException e) {
			System.out.println("Error:" + e.getMessage());
		}
	}

	
	//validators
	public int getIntValidator(Scanner scan, String prompt) {
		int value = 0;
		boolean valid = false;
		while (!valid) {
			System.out.println(prompt);
			try {
				value = Integer.parseInt(scan.nextLine().trim());
				valid = true;
			} catch (NumberFormatException e) {
				System.out.println("Invalid input! Please enter valid input\n");
			}
		}
		return value;
	}

	public String emailValidator(Scanner scan,String prompt) {
		String value;
		while (true) {
			System.out.println(prompt);
			value=scan.nextLine().trim();
			if (!value.contains("@")) {
				System.out.println("Enter Valid email id with @.com\n");
			}
			else {
				return value;
			}
		}
	}
	
	public String stringValidator(Scanner scan, String prompt) {
		String value;
		while (true) {
			System.out.println(prompt);
			value = scan.nextLine().trim();
			if (value.isEmpty()) {
				System.out.println("Please Enter Something");
			} else {
				return value;
			}
		}
	}

	public boolean adminValidator(String username, String password) throws AdminInvalidException {
//		List<String>password=new ArrayList<String>();
//		password.add(value);
//		boolean valid=password.stream()
//				.allMatch(pass->pass.length()>=8 &&pass.matches(".*[!@#$%^&*].*"));
		if (username.equals("pumo@123") && password.equals("pumo@321")) {
			return true;
		}
		throw new AdminInvalidException("Invalid Admin Credentials");
	}
	
	//Extra methods
	public void passengerDetails(Scanner scan) {
		String passengerId = stringValidator(scan, "Enter Passenger ID:");
		List<Booking> passengerDetails = bookingService.getBookingByPassenger(passengerId);
		System.out.println(passengerDetails);
	}

	public void confirmBookings() {
		System.out.println("All Confirmed Bookings");
		List<Booking> confirmBookingdetails = bookingService.getAllConfirmedBooking();
		confirmBookingdetails.forEach(System.out::println);
	}

	public void showRevenueMenu(Scanner Scan) {
		String username = stringValidator(Scan, "Enter The UserName(pumo@123):");
		String password = stringValidator(Scan, "Enter The Password(pumo@321):");
		try {
			boolean valid = adminValidator(username, password);
			if (valid) {
				double revenue = bookingService.calculateRevenue();
				int confirmedCount = bookingService.confirmedCount();
				int cancelledCount = bookingService.cancelledCount();
				System.out.println("Total Revenue:" + revenue);
				System.out.println("Total Confirmed Bookings:" + confirmedCount);
				System.out.println("Total Cancelled Booking:" + cancelledCount);
			} else {
				System.out.println("Admin Username or Password is incorrect");
			}
		} catch (AdminInvalidException e) {
			System.out.println("Error:" + e.getMessage());
		}
	}

	// multiThreading
	public void testConcurrentBooking() {
		Flight flight = null;
	    try {
	        // Try to fetch the flight first
	        flight = flightService.getAvailabFlight("F003"); // UK303 with 2 seats
	    } catch (FlightNotFoundException e) {
	        System.out.println("Flight not found! Cannot run concurrent test.");
	        return; // exit test gracefully
	    } catch (FlightFullException e) {
	        System.out.println("Cannot run test: " + e.getMessage());
	        return; // exit test gracefully
	    }

	    System.out.println("Flight: " + flight.getFlightNumber() + " (" + flight.getAvailableSeats() + " seats available)");
	    System.out.println("Creating 4 booking threads...");

	    BookingThread t1 = new BookingThread("Passenger-1", flight, 1);
	    BookingThread t2 = new BookingThread("Passenger-2",  flight, 1);
	    BookingThread t3 = new BookingThread("Passenger-3", flight, 1);
	    BookingThread t4 = new BookingThread("Passenger-4", flight, 1);

	    t1.start();
	    t2.start();
	    t3.start();
	    t4.start();

	    try {
	        t1.join();
	        t2.join();
	        t3.join();
	        t4.join();
	    } catch (InterruptedException e) { 
	        e.printStackTrace();
	    }

	    System.out.println("Final Seats: " + flight.getAvailableSeats());
	    System.out.println("Test Complete");
	}
	
}


