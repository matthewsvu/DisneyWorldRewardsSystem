/*
 * @author: Matthew Vu
 * ID: MSV180000
 */
//package disney;


public class Gold extends Customer {
	

	   // Member variable discount
	   private double discountPercentage;

	   // Default constructor
	   public Gold()
	   {
	       super();
	   }

	   // Parameterized overloaded constructor
	   // Constructor chaining calls parent class constructor
	   public Gold(String guestID, String firstName, String lastName, double amountSpent, double discountPercentage)
	   {
	       super(guestID, firstName, lastName, amountSpent);
	       this.discountPercentage = discountPercentage;
	   }

	   //Getter and setter
	   public double getDiscountPercentage()
	   {
	       return discountPercentage;
	   }

	   public void setDiscountPercentage(double discountPercentage)
	   {
		   this.discountPercentage = discountPercentage;
	   }
	
}
