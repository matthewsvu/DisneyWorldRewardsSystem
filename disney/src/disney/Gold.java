/*
 * @author: Matthew Vu
 * ID: MSV180000
 */
package disney;


public class Gold extends Customer {
	

	   //Member variable discount
	   private double discountPercentage;

	   //Default constructor
	   public Gold()
	   {
	       super();
	   }

	   //Parameterized overloaded constructor
	   // Constructor chaining calling parent class constructor
	   public Gold(String firstName, String last_name, String guestID, double amountSpent, double discountPercentage)
	   {
	       super(firstName, last_name, guestID, amountSpent);
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
