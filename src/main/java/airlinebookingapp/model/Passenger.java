package airlinebookingapp.model;

public class Passenger {

	private String passengerId;
	private String name;
	private String email;
	private long phone;

	//for input storing in memory temporary
	public Passenger( String name, String email, long phone) {
		super();
		this.passengerId =generatePassengerId();
		this.name = name;
		this.email = email;
		this.phone = phone;
	}
	
	
	//getting output from database
	public Passenger(String passengerId, String name, String email, long phone) {
		super();
		this.passengerId = passengerId;
		this.name = name; 
		this.email = email;
		this.phone = phone;
	}

	
	//for junit testing
	public Passenger() {}


	// getters
	public String getPassengerId() {
		return passengerId;
	}

	public String getName() {
		return name;
	}

	public void setPassengerId(String passengerId) {
		this.passengerId = passengerId;
	}


	public String getEmail() {
		return email;
	}

	public long getPhone() {
		return phone;
	}
	
	public String generatePassengerId() {
		return "P"+(int)(Math.random()*1000);
	}

	@Override
	public String toString() {
		return "Passenger Id:" + passengerId + ", Name:" + name + ", Email ID:" + email + ", Phone:" + phone;
	}
}

