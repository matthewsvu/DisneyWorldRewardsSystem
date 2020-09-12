/*
 * @author: Matthew Vu
 * ID: MSV180000
*/

package disney;


class Customer
{
	// Member variables
	private String firstName;
	private String lastName;
	private String guestID;
	private double amountSpent;
	  
	// Default constructor
	public Customer()
	{
	}
	
	// Overloaded constructor
	public Customer(String firstName, String lastName, String guestID, double amountSpent)
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.guestID = guestID;
		this.amountSpent = amountSpent;
	}
	  
	// Mutators and Accessors
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getGuestID() {
		return guestID;
	}
	public void setGuestID(String guestID) {
		this.guestID = guestID;
	}
	public double getAmountSpent() {
		return amountSpent;
	}
	public void setAmountSpent(double amountSpent) {
		this.amountSpent = amountSpent;
	}
}

//Create the class Customer.
