/*
 * @author: Matthew Vu
 * ID: MSV180000
 */

package disney;


//Import packages.
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

//Main
public class Main
{
	// Prices that can be easily be changed later, if we plan to increase or decrease profit of cups.
	static final double SODA_PRICE = 0.20;
	static final double TEA_PRICE = 0.12;
	static final double PUNCH_PRICE = 0.15;
	
	static final double SMALL_DRINK_DIAMETER = 4;
	static final double SMALL_DRINK_HEIGHT = 4.5;
	static final int SMALL_DRINK_OUNCES = 12;
	
	static final double MED_DRINK_DIAMETER = 4.5;
	static final double MED_DRINK_HEIGHT = 5.75;
	static final int MED_DRINK_OUNCES = 20;
	
	static final double LARGE_DRINK_DIAMETER = 5.5;
	static final double LARGE_DRINK_HEIGHT = 7;
	static final int LARGE_DRINK_OUNCES = 32;

	private static DecimalFormat df = new DecimalFormat("0.00");
	private static DecimalFormat noDf = new DecimalFormat("0");
	
	// Arrays that all customers all stored in
	static Customer[] regularCustomers = null;
	static Customer[] preferredCustomers = null;
	//Start the main() method.
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		String filename;
		Scanner input = new Scanner(System.in);
		
		//read file sample_customer.dat and create array
		System.out.println("Enter the regular customer file: ");
		filename = input.next();
		regularCustomers = readInCustomer(filename);
		writeTestArray(regularCustomers); // testing purposes get rid of soon
		//read file sample_preferred.dat
		System.out.println("Enter the preferred customer file: ");
		filename = input.next();
		preferredCustomers = readInPreferred(filename);
		writeTestArray(preferredCustomers); // testing purposes get rid of soon
		// read file sample_orders.dat
		System.out.println("Enter the orders file: ");
		filename = input.next();
		processOrders(filename);
		input.close();
		writeCustomerOutput(regularCustomers);
		writePreferredOutput(preferredCustomers);
		writeTestArray(regularCustomers);
		writeTestArray(preferredCustomers);
		
	}
	// Reads in the regular customers into an array
	// Returns: Array of regular customers
	public static Customer[] readInCustomer(String filename) throws FileNotFoundException {
		int numLines = 0; // used to count num lines
		int currCustomerNum = 0; // used to store current index of regCustomer
		String line; // will be used to split the lines 
		
		Scanner fileInput = new Scanner(new FileInputStream(filename));
		// Counts number of lines in customers
		while(fileInput.hasNextLine()) {
		    line = fileInput.nextLine();
			numLines++;
		}
		fileInput.close();
		// opens the file again to read in information
		fileInput = new Scanner(new FileInputStream(filename));
		// create array with number of customers
		Customer[] regularCustomer = new Customer[numLines];
		// read in custID, firstname, lastname, and then amount spent(float)
		while(fileInput.hasNextLine()) {
			line = fileInput.nextLine(); // gets next line
			String[] attributes = line.split("\\s"); // splits line by whitespace
			Customer newCustomer = new Customer(); // declare an instance of customer
			
			// initializes values for customer
			newCustomer.setGuestID(attributes[0]);
			System.out.println("This is uniqueID: " + newCustomer.getGuestID());
			newCustomer.setFirstName(attributes[1]);
			newCustomer.setLastName(attributes[2]);
			newCustomer.setAmountSpent(Double.parseDouble(attributes[3]));
			regularCustomer[currCustomerNum] = newCustomer; // store it into regularCustomer
			
			currCustomerNum++;
		}
		fileInput.close();
		return regularCustomer;
	}
	// reads in preferred customers into an array
	// Returns: Array of preferred customers
	public static Customer[] readInPreferred(String filename) throws FileNotFoundException {
		File file = new File(filename);
		Scanner fileInput = new Scanner(file);
		boolean isGold = false; // checks whether to create gold object or platinum
		int numLines = 0; // used to count num lines
		int currPreferredNum = 0; // used to store current index of preferredCustomer
		String line; // will be used to split the lines 
		
		// Counts number of lines in customers
		while(fileInput.hasNextLine()) {
		    line = fileInput.nextLine();
			numLines++;
		}
		// if the file doesn't exists or empty return an empty array
		if(numLines == 0 || !file.exists()) {
			Customer[] preferredCustomer = {};
			fileInput.close();
			return preferredCustomer;
		}
		fileInput.close();
		
		// opens the file again to read in information
		fileInput = new Scanner(new FileInputStream(filename));
		// create array with number of customers
		Customer[] preferredCustomer = new Customer[numLines];
		// read in custID, firstname, lastname, amount spent(float), and discountPercent/Bonusbucks
		while(fileInput.hasNextLine()) {
			line = fileInput.nextLine(); // gets next line
			String[] attributes = line.split("\\s"); // splits line by whitespace
			// checks if the 5th attribute has a percent sign
			if(attributes[4].contains("%")) {
				isGold = true;
			}
			if(isGold) { // create and set goldObject attributes
				Gold newCustomer = new Gold();
				// initializes values for Gold
				newCustomer.setGuestID(attributes[0]);
				newCustomer.setFirstName(attributes[1]);
				newCustomer.setLastName(attributes[2]);
				newCustomer.setAmountSpent(Double.parseDouble(attributes[3]));
				// get rid of the percent sign in the discount attribute and make it a decimal num
				newCustomer.setDiscountPercentage(Double.parseDouble(attributes[4].replace("%", "")) / 100);
				
				preferredCustomer[currPreferredNum] = newCustomer; // store it into preferredCustomer
			}
			else // create a platinum object and set it's attributes
			{
				Platinum newCustomer = new Platinum();
				// initializes values for Platinum
				newCustomer.setGuestID(attributes[0]);
				newCustomer.setFirstName(attributes[1]);
				newCustomer.setLastName(attributes[2]);
				newCustomer.setAmountSpent(Double.parseDouble(attributes[3]));
				newCustomer.setBonusBucks(Integer.parseInt(attributes[4]));
				
				preferredCustomer[currPreferredNum] = newCustomer; // store it into preferredCustomer
			}
			currPreferredNum++; // increment the current index
			isGold = false;
		}
		fileInput.close();
		return preferredCustomer;
	}
	public static void processOrders(String filename) {
		try {
			Scanner file = new Scanner(new File(filename));
			String line;
			double totalPrice, discountedPrice;
			while(file.hasNextLine()) {
				line = file.nextLine();
				// 5 total orders
				String[] orders = line.split("\\s"); 
				
				Customer customer = getCustomerByID(orders[0], regularCustomers, preferredCustomers);
				// DO LATER: Add check validity function that @returns boolean and pass in
				// @param orders.length, (int) customer ID, orders[1], orders[2], orders[3], orders[4] 
				if(orders.length != 5 || customer.getGuestID() == "-1" ) {
					continue;
				}
				System.out.println("This is while loop customerID, " + customer.getGuestID());
				
				// orders are: size, type, costpersqrinch, and num ordered
				totalPrice = calculateDrinkPrice(orders[1], orders[2], Double.parseDouble(orders[3]), Integer.parseInt(orders[4]));
				System.out.println("This is while loop total drink price, " + totalPrice);
				discountedPrice = applyDiscount(customer, totalPrice); // first apply the discount
				System.out.println("This is while loop discounted drink price, " + discountedPrice);
				Customer updatedCustomer = promoteUser(customer, discountedPrice); // try to promote user after
				if((updatedCustomer instanceof Gold) ) {
					System.out.println("This is total drink price, " + totalPrice);
					discountedPrice = applyDiscount(updatedCustomer, totalPrice); // apply the discount again in case there was a promotionc
					System.out.println("This is the guestID: " + updatedCustomer.getGuestID());
					System.out.println("This is discounted price: " + discountedPrice);
					System.out.println("This is customer amount spent: " + updatedCustomer.getAmountSpent());
					updatedCustomer.setAmountSpent(updatedCustomer.getAmountSpent() + discountedPrice); // set amountSpent to updatedPrice
					System.out.println("This is customer amount spent after being set: " + updatedCustomer.getAmountSpent());
				}
				else if(updatedCustomer instanceof Customer) {
					updatedCustomer.setAmountSpent(customer.getAmountSpent() + discountedPrice);
				}
				System.out.println("Made it to updatedCustomer");
				// checks if the updated customer is now a preferred customer and was previously a normal Customer
				if((updatedCustomer instanceof Gold || updatedCustomer instanceof Platinum) && 
						!(customer instanceof Gold || customer instanceof Platinum )) {
					preferredCustomers = updateArray(preferredCustomers, updatedCustomer, discountedPrice, true, true);
					regularCustomers = updateArray(regularCustomers, updatedCustomer, discountedPrice, false, true);
				} // now since we know the non-updated customer were gold or platinum only update their discount/bonusbucks in the array
				else if((updatedCustomer instanceof Gold || updatedCustomer instanceof Platinum)) {
					preferredCustomers = updateArray(preferredCustomers, updatedCustomer, discountedPrice, false, false);
				}
 			}
			file.close();
		} catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}
	// gets the drinkPrice through calculating with known values and orders
	public static double calculateDrinkPrice(String drinkSize, String drinkType, double costPerSqrInch, int numOrdered) {
		double totalPrice = 0, drinkDesign = 0, ouncesOrderedPrice = 0;
		
		switch(drinkSize) {
			case "S":
				drinkDesign = (2 * Math.PI * (SMALL_DRINK_DIAMETER / 2) * SMALL_DRINK_HEIGHT * costPerSqrInch);
				if(drinkType.equals("punch")) {
					ouncesOrderedPrice = PUNCH_PRICE * SMALL_DRINK_OUNCES;
				}
				if(drinkType.equals("tea")) {
					ouncesOrderedPrice = TEA_PRICE * SMALL_DRINK_OUNCES;
				}
				if(drinkType.equals("soda")) {
					ouncesOrderedPrice = SODA_PRICE * SMALL_DRINK_OUNCES;
				}
				break;
			case "M":
				drinkDesign = (2 * Math.PI * (MED_DRINK_DIAMETER / 2) * MED_DRINK_HEIGHT * costPerSqrInch);
				if(drinkType.equals("punch")) {
					ouncesOrderedPrice = PUNCH_PRICE * MED_DRINK_OUNCES;
				}
				if(drinkType.equals("tea")) {
					ouncesOrderedPrice = TEA_PRICE * MED_DRINK_OUNCES;
				}
				if(drinkType.equals("soda")) {
					ouncesOrderedPrice = SODA_PRICE * MED_DRINK_OUNCES;
				}
				break;
			case "L":
				drinkDesign = (2 * Math.PI * (LARGE_DRINK_DIAMETER / 2) * LARGE_DRINK_HEIGHT * costPerSqrInch);
				if(drinkType.equals("punch")) {
					ouncesOrderedPrice = PUNCH_PRICE * LARGE_DRINK_OUNCES;
				}
				if(drinkType.equals("tea")) {
					ouncesOrderedPrice = TEA_PRICE * LARGE_DRINK_OUNCES;
				}
				if(drinkType.equals("soda")) {
					ouncesOrderedPrice = SODA_PRICE * LARGE_DRINK_OUNCES;
				}
				break;
		}
		totalPrice = (drinkDesign + ouncesOrderedPrice) * numOrdered;
		return totalPrice;
	}
	// Promotes user based on the customer's amount spent and discounted total added together
	// creates an object for gold or platinum or nothing at all depending on the total added together
	public static Customer promoteUser(Customer customer, double addedAmount) {
		double total = customer.getAmountSpent() + addedAmount;
		double discount = -1;
		int bonusBucks = -1;
		boolean isGold = false, isPlatinum = false;
		System.out.println("This is total, " + total);
		if(total < 50) {
			return customer;
		}
		// We know object is gold, set its discount percentage
		if(total < 200) {
			isGold = true;
			if(total < 100) {
				discount = 0.05;
			}
			else if(total >= 100 && total < 150) {
				discount = 0.10;
			}
			else {
				discount = 0.15;
			}
		}
		else if(total >= 200) { // we know it's a platinum object now
			isPlatinum = true;
			bonusBucks = calculateBonusBucks(customer, addedAmount);
		}
		// create a new updated object and cast it to different object
		Customer updatedCustomer = null;
		if(discount != -1 && isGold) { // create object of gold type
			updatedCustomer = new Gold();
			updatedCustomer.setGuestID(customer.getGuestID());
			System.out.println("This is unique CustomerID: " + updatedCustomer.getGuestID());
			updatedCustomer.setFirstName(customer.getFirstName());
			updatedCustomer.setLastName(customer.getLastName());
			updatedCustomer.setAmountSpent(customer.getAmountSpent());
			System.out.println("This is amount spent gold: " + updatedCustomer.getAmountSpent());
			((Gold) updatedCustomer).setDiscountPercentage(discount);
			System.out.println("This is discountPercentage in promoteUser func, " + ((Gold) updatedCustomer).getDiscountPercentage());
		}
		else if(bonusBucks != -1 && isPlatinum) { // create object of Platinum type
			updatedCustomer = new Platinum();
			updatedCustomer.setGuestID(customer.getGuestID());
			updatedCustomer.setFirstName(customer.getFirstName());
			updatedCustomer.setLastName(customer.getLastName());
			updatedCustomer.setAmountSpent(customer.getAmountSpent());
			System.out.println("This is amount spent Platinum: " + updatedCustomer.getAmountSpent());
			((Platinum) updatedCustomer).setBonusBucks(bonusBucks);
			System.out.println("This is bonus bucks platinum: " + ((Platinum) updatedCustomer).getBonusBucks());
		}
		return updatedCustomer;
	}
	// Will update the array contents by making a temp array either one size larger/smaller/same 
	// and copying it's contents over. If we want to remove the customer find the index of the customer
	// passed in and use it to specify the object to be deleted
	public static Customer[] updateArray(Customer[] customerArray, Customer updatedCustomer, double totalPrice, boolean add, boolean resize) {
		Customer[] updatedArr = null;
		int index = indexOf(customerArray, updatedCustomer); // returns of the index of customer we want to update
		
		// create an array one size larger than given
		if(add == true) {
			updatedArr = new Customer[customerArray.length + 1]; // make a temp array of size + 1 longer than the original array
			System.arraycopy(customerArray, 0, updatedArr, 0, customerArray.length); // copy over the contents of the original array
			updatedArr[customerArray.length] = updatedCustomer; // sets the last index as the updated customer
		}
		else if(add == false) { // decrease the size of the regular customers array
			updatedArr = new Customer[customerArray.length - 1];
			
			System.out.println("Made it to index: " + index); // Ex: [0, 1 ,2 ,3 ,4] -> [0,1,3,4]
			System.arraycopy(customerArray, 0, updatedArr, 0, index); // copying all elements from original array up until the object we want to delete from our array
			System.arraycopy(customerArray, index+1, updatedArr, index, customerArray.length - index - 1);
		}
		if(!resize && !add) { // whenever we're changing the value of a preferred customer;
			updatedArr = new Customer[customerArray.length];
			System.arraycopy(customerArray, 0, updatedArr, 0, customerArray.length); // copy over the contents of the original array
			updatedArr[index] = updatedCustomer;
		}

		return updatedArr;
	}
	// returns the index of a customer object
	public static int indexOf(Customer[] arr, Customer customer) {
        // find length of array 
        int len = arr.length; 
        System.out.println(customer.getGuestID());
        for(int i = 0; i < len; i++) {
        	if (arr[i].getGuestID().equals(customer.getGuestID()) ) { 
        		System.out.println("Found matching id at: " + i + " " + arr[i].getGuestID());
                return i; 
            } 
        }
        return -1; 
	}
	// applies discount to the customer
	public static double applyDiscount(Customer customer, double totalPrice) {
		if(customer instanceof Gold) {
			totalPrice = totalPrice - (totalPrice * (((Gold) customer).getDiscountPercentage()));
		}
		else if (customer instanceof Platinum) {
			totalPrice = totalPrice - ((Platinum) customer).getBonusBucks();
		}
		return totalPrice;
	}
	// get bonusbucks from customer
	public static int calculateBonusBucks(Customer customer, double totalPrice) {
		double prevAmountSpent = customer.getAmountSpent();
		int bonusBucks = -1;
		if(prevAmountSpent >= 200) {
			bonusBucks = (int) (((prevAmountSpent + totalPrice) - (prevAmountSpent - 
					(prevAmountSpent % 5))) / 5);
		}
		else {
			bonusBucks = (int) (((prevAmountSpent + totalPrice) - 200) / 5);
		}
		return bonusBucks;
	}
	// loops through both reg and pref customer arrays to find ID
	public static Customer getCustomerByID(String id, Customer[] customer, Customer[] preferred) {
		for(int i = 0; i < customer.length; i++) {
			if(customer[i].getGuestID().contains(id)) {
				return customer[i];
			}
		}
		for(int j = 0; j < preferred.length; j++) {
			if(preferred[j].getGuestID().contains(id)) {
				return preferred[j];
			}
		}
		// happen when there is invalid data 
		Customer failedCustomer = new Customer();
		failedCustomer.setGuestID("-1");
		// if it reaches this point return a customer with an id of -1, check in return function for error
		return failedCustomer;
	}
	// Write output to customer.dat
	public static void writeCustomerOutput(Customer[] customerArray) {
		try {
			FileOutputStream filestream = new FileOutputStream("customer.dat");
			PrintWriter output = new PrintWriter(filestream);
			for(int i = 0; i < customerArray.length; i++) {
				Customer customer = customerArray[i];
				output.print(customer.getGuestID() + " " + customer.getFirstName() + " " + customer.getLastName() 
				+ " " + df.format(customer.getAmountSpent()));
				output.println("");
			}
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	// Write output to preferred.dat
		public static void writePreferredOutput(Customer[] customerArray) {
			try {
				FileOutputStream filestream = new FileOutputStream("preferred.dat");
				PrintWriter output = new PrintWriter(filestream);
				for(int i = 0; i < customerArray.length; i++) {
					Customer customer = customerArray[i];
					output.print(customer.getGuestID() + " " + customer.getFirstName() + " " + customer.getLastName() 
					+ " " + df.format(customer.getAmountSpent()));
					
					if(customer instanceof Gold) {
						output.println(" " + ((int) (((Gold) customer).getDiscountPercentage() * 100)) + "%");
					}
					else if(customer instanceof Platinum) {
						output.println(" " + noDf.format(((Platinum) customer).getBonusBucks()));
					}
					else {
						output.println("");
					}
				}
				output.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
	// just for testing purposes
	public static void writeTestArray(Customer[] customerArray) {
		for(int i = 0; i < customerArray.length; i++) {
			Customer customer = customerArray[i];
			System.out.print(customer.getGuestID() + " " + customer.getFirstName() + 
					" " + customer.getLastName() + " " + df.format(customer.getAmountSpent()));
			
			if(customer instanceof Gold) {
				// concatenate a percent sign after making the discount percentage an int again
				System.out.println(" " + ((int) (((Gold) customer).getDiscountPercentage() * 100)) + "%");
			}
			else if(customer instanceof Platinum) {
				System.out.println(" " + noDf.format(((Platinum) customer).getBonusBucks()));
			}
			else {
				System.out.println("");
			}
		}
	}
}