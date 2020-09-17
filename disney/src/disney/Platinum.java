/*
 * @author: Matthew Vu
 * ID: MSV180000
 */
//package disney;


public class Platinum extends Customer {

	   // Member variable discount
	   private int bonusBucks;

	   // Default constructor
	   public Platinum()
	   {
	       super();
	  
	   }
	   // Parameterized overloaded constructor
	   // Constructor chaining calling parent class constructor
	   public Platinum(String guestID, String firstName, String lastName, double amountSpent, int bonusBucks)
	   {
	       super(guestID, firstName, lastName, amountSpent);
	       this.bonusBucks = bonusBucks;
	   }
	   
	   //Mutators and Accessors
	   public double getBonusBucks()
	   {
	       return bonusBucks;
	   }
	   public void setBonusBucks(int bonusBucks)
	   {
		   this.bonusBucks = bonusBucks;
	   }
}