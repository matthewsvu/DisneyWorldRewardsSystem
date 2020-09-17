/*
 * @author: Matthew Vu
 * ID: MSV180000
 */

//package disney;


//Import packages.
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	
	// formats to hundredth's and tenth's place
	private static DecimalFormat df = new DecimalFormat("0.00");
	private static DecimalFormat noDf = new DecimalFormat("0");
	
	// Arrays that all customers all stored in
	static Customer[] regularCustomers = null;
	static Customer[] preferredCustomers = null;
	
	// Main method
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		String filename;
		Scanner input = new Scanner(System.in);
		
		//read file sample_customer.dat and create array
		System.out.println("Enter the regular customer file: ");
		filename = input.next();
		regularCustomers = readInCustomer(filename);
		//read file sample_preferred.dat
		System.out.println("Enter the preferred customer file: ");
		filename = input.next();
		preferredCustomers = readInPreferred(filename);
		// read file sample_orders.dat
		System.out.println("Enter the orders file: ");
		filename = input.next();
		// process the orders and promote customers as needed
		processOrders(filename);
		input.close();
		// write to the output files preferred.dat and customer.dat
		writeCustomerOutput(regularCustomers);
		writePreferredOutput(preferredCustomers);
	}
	public static int countLinesFile(Scanner fileInput) {
		int numLines = 0;
		while(fileInput.hasNextLine()) {
			@SuppressWarnings("unused")
			String line = fileInput.nextLine();
			numLines++;
		}
		return numLines;
	}
	// Reads in the regular customers into an array
	// Returns: Array of regular customers
	public static Customer[] readInCustomer(String filename) throws FileNotFoundException {
		String line; // will be used to split the lines 
		Scanner fileInput = new Scanner(new FileInputStream(filename));
		int currCustomerNum = 0; // used to store current index of regCustomer
		int numLines = countLinesFile(fileInput); // used to count num lines
		fileInput.close();
		
		// opens the file again to read in information
		fileInput = new Scanner(new FileInputStream(filename));
		// create array with number of customers
		Customer[] regularCustomer = new Customer[numLines];
		// read in custID, firstname, lastname, and then amount spent (double) (in that order)
		while(fileInput.hasNextLine()) {
			line = fileInput.nextLine(); // gets next line
			String[] attributes = line.split("\\s"); // splits line by whitespace
			
			// declare an instance of customer and initializes values from attributes
			Customer newCustomer = new Customer(attributes[0], attributes[1], attributes[2],
					Double.parseDouble(attributes[3]));
			regularCustomer[currCustomerNum] = newCustomer; // store it into regularCustomer
			currCustomerNum++; // increment to next customer
		}
		fileInput.close();
		return regularCustomer;
	}
	// reads in preferred customers into an array
	// Returns: Array of preferred customers
	public static Customer[] readInPreferred(String filename) throws FileNotFoundException {
		String line; // will be used to split the lines 
		boolean isGold = false; // checks whether to create gold object or platinum
		int currPreferredNum = 0; // used to store current index of preferredCustomer
		
		File file = new File(filename);
		// checks for non-existent file and returns empty array for preferred customers
		if(!file.exists()) {
			Customer[] preferredCustomer = {};
			return preferredCustomer;
		}
		Scanner fileInput = new Scanner(file);
		int numLines = countLinesFile(fileInput); // used to count num lines
		
		// if the file doesn't exists or empty return an empty array
		if(numLines == 0) {
			Customer[] preferredCustomer = {};
			fileInput.close();
			return preferredCustomer;
		}
		fileInput.close();
		
		// opens the file again to read in information
		fileInput = new Scanner(new FileInputStream(filename));
		// create array with number of lines found
		Customer[] preferredCustomer = new Customer[numLines];
		
		// read in custID, firstname, lastname, amount spent(float), and discountPercent/bonusBucks
		while(fileInput.hasNextLine()) {
			line = fileInput.nextLine(); // gets next line
			String[] attributes = line.split("\\s"); // splits line by whitespace
			// checks if the 5th attribute has a percent sign
			if(attributes[4].contains("%")) {
				isGold = true;
			}
			if(isGold) { // create and set goldObject attributes
				Gold newCustomer = new Gold(
						attributes[0],
						attributes[1], 
						attributes[2],
						Double.parseDouble(attributes[3]), 
						Double.parseDouble(attributes[4].replace("%", "")) / 100); // replaces the percent sign and makes it a double
				
				preferredCustomer[currPreferredNum] = newCustomer; // store it into preferredCustomer
			}
			else // create a platinum object and set it's attributes
			{
				Platinum newCustomer = new Platinum(attributes[0], attributes[1], attributes[2],
						Double.parseDouble(attributes[3]), Integer.parseInt(attributes[4]));
				preferredCustomer[currPreferredNum] = newCustomer; // store it into preferredCustomer
			}
			currPreferredNum++; // increment the current index
			isGold = false;
		}
		fileInput.close();
		return preferredCustomer;
	}
	// Main driver function that will go through each order line by and line and process them accordingly
	public static void processOrders(String filename) {
		try {
			Scanner file = new Scanner(new File(filename));
			String line;
			double totalPrice, discountedPrice;
			// will process orders one by one and store them into arrays
			while(file.hasNextLine()) {
				line = file.nextLine();
		
				// split the line into 5 orders stored within an array
				String[] orders = line.split("\\s"); 
				if(orders.length != 5) {
					continue;
				}
				// create a customer object when it finds a matching customer
				Customer customer = getCustomerByID(orders[0], regularCustomers, preferredCustomers);
				if(customer.getGuestID().contentEquals("-1")) { // continue loop if it doesn't find a customer
					continue;
				}
				// checks if the rest of the orders are valid
				if(!checkValid(orders[1], orders[2], orders[3], orders[4])) {
					continue;
				}
				// orders are: 1 : size, 2 : type, 3 : costpersqrinch, and 4 : numordered
				totalPrice = calculateDrinkPrice(orders[1], orders[2], Double.parseDouble(orders[3]), Integer.parseInt(orders[4]));
				discountedPrice = applyDiscount(customer, totalPrice); // first apply the discount
				Customer updatedCustomer = promoteUser(customer, discountedPrice, totalPrice); // try to promote user after
				
				if((updatedCustomer instanceof Gold)) {
					discountedPrice = applyDiscount(updatedCustomer, totalPrice); // apply the discount again in case there was a promotion
					updatedCustomer.setAmountSpent(updatedCustomer.getAmountSpent() + discountedPrice); // set amountSpent to updatedPrice
				}		// in order to set the platinum object's amount spent and apply a 15% discount to the order
						// check if the customer was previously a gold object or regular object
				else if((updatedCustomer instanceof Platinum) && (customer instanceof Gold || 
						(!(customer instanceof Gold) && !(customer instanceof Platinum)))) { 
					// set the amount spent of a Platinum customer + discountedTotal
					discountedPrice = (totalPrice - (totalPrice * 0.15));
					updatedCustomer.setAmountSpent(updatedCustomer.getAmountSpent() + discountedPrice);
				}
				else { // set the amount spent of a regular customer + discountedTotal
					updatedCustomer.setAmountSpent(updatedCustomer.getAmountSpent() + discountedPrice);
				}
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
	// input validation for a single order
	public static boolean checkValid(String size, String type, String sqrInchPrice , String quantity) {
		boolean valid = true;
		switch(size) {
			case "S":
				break;
			case "M":
				break;
			case "L":
				break;
			default:
				valid = false;
				break;
		}
		switch(type) {
			case "soda":
				break;
			case "punch":
				break;
			case "tea":
				break;
			default:
				valid = false;
				break;
		}
		if(isAlpha(sqrInchPrice)) { // if one of them have alphabetical chars
			valid = false;
		}
		if(isAlpha(quantity)) {
			valid = false;
		}
		return valid; // return true if all of them are correct otherwise return false
	}
	// checks if there is alphabetical character in a string
	public static boolean isAlpha(String name) {
	    char[] chars = name.toCharArray();
	    for (char c : chars) {
	        if(Character.isLetter(c)) { // there is an alphabetical letter
	            return true;
	        }
	    }
	    return false;
	}
	// gets the drinkPrice through calculating with known values and orders
	public static double calculateDrinkPrice(String drinkSize, String drinkType, double costPerSqrInch, int numOrdered) {
		double totalPrice = 0, drinkDesign = 0, ouncesOrderedPrice = 0;
		// drinkdesign formula is the lateral surface area of a cylinder * the design cost
		switch(drinkSize) {
			case "S":
				drinkDesign = ((2 * Math.PI * (SMALL_DRINK_DIAMETER / 2) * SMALL_DRINK_HEIGHT) * costPerSqrInch);
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
				drinkDesign = ((2 * Math.PI * (MED_DRINK_DIAMETER / 2) * MED_DRINK_HEIGHT) * costPerSqrInch);
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
				drinkDesign = ((2 * Math.PI * (LARGE_DRINK_DIAMETER / 2) * LARGE_DRINK_HEIGHT) * costPerSqrInch);
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
		// add the design price and the price of the drink itself together. Then multiply by num ordered
		totalPrice = (drinkDesign + ouncesOrderedPrice) * numOrdered;
		return totalPrice;
	}
	// Promotes user based on the customer's amount spent and discounted total added together
	// creates an object for gold or platinum or nothing at all depending on the total added together
	public static Customer promoteUser(Customer customer, double discountedTotal, double totalPrice) {
		double total = customer.getAmountSpent() + discountedTotal;
		double discount = -1;
		int bonusBucks = -1;
		boolean isGold = false, isPlatinum = false;
		if(total < 50) {
			return customer;
		}
		// We know object is gold, so set its discount percentage
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
			// checks if the original customer is regular customer or a gold customer 
			if(customer instanceof Gold || (!(customer instanceof Gold) && !(customer instanceof Platinum))) {
				discountedTotal = (totalPrice - (totalPrice * 0.15)); // apply a 15% discount if it is one of those two
			}
			bonusBucks = calculateBonusBucks(customer, discountedTotal);
		}
		// create a new updated object and cast it to different object
		Customer updatedCustomer = new Customer();
		if(discount != -1 && isGold) { // create object of gold type and set it's attributes
			updatedCustomer = new Gold(customer.getGuestID(), 
									   customer.getFirstName(), 
									   customer.getLastName(), 
									   customer.getAmountSpent(), 
									   discount);
		}
		else if(bonusBucks != -1 && isPlatinum) { // create object of Platinum type
			updatedCustomer = new Platinum(customer.getGuestID(), 
										   customer.getFirstName(),
										   customer.getLastName(), 
										   customer.getAmountSpent(),
										   bonusBucks);
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
		if(add && resize) {
			updatedArr = new Customer[customerArray.length + 1]; // make a temp array of size + 1 longer than the original array
			System.arraycopy(customerArray, 0, updatedArr, 0, customerArray.length); // copy over the contents of the original array
			updatedArr[customerArray.length] = updatedCustomer; // sets the last index as the updated customer
		}
		else if(!add && resize) { // decrease the size of the regular customers array
			updatedArr = new Customer[customerArray.length - 1];	
			// Ex: [0, 1 ,2 ,3 ,4] -> [0,1,3,4]
			System.arraycopy(customerArray, 0, updatedArr, 0, index); 
			// copying all elements from original array up until the object we want to delete from our array
			System.arraycopy(customerArray, index+1, updatedArr, index, customerArray.length - index - 1);
		}
		if(!add && !resize) { // whenever we're changing the value of a preferred customer within the array;
			updatedArr = new Customer[customerArray.length];
			System.arraycopy(customerArray, 0, updatedArr, 0, customerArray.length); // copy over the contents of the original array
			updatedArr[index] = updatedCustomer;
		}

		return updatedArr;
	}
	// returns the index of a customer object using a linear scan of the array
	public static int indexOf(Customer[] arr, Customer customer) {
        // find length of array 
        int len = arr.length; 
        
        for(int i = 0; i < len; i++) { // search through the array for matching ID, return the index of matching ID
        	if (arr[i].getGuestID().equals(customer.getGuestID()) ) { 
                return i; 
            } 
        }
        return -1; 
	}
	// applies discount to the customer based on object class
	public static double applyDiscount(Customer customer, double totalPrice) {
		if(customer instanceof Gold) {
			totalPrice = totalPrice - (totalPrice * ((Gold) customer).getDiscountPercentage());
		}
		else if (customer instanceof Platinum) {
			totalPrice = totalPrice - ((Platinum) customer).getBonusBucks();
		}
		return totalPrice;
	}
	// get bonusbucks from customer object
	public static int calculateBonusBucks(Customer customer, double totalPrice) {
		double prevAmountSpent = customer.getAmountSpent();
		int bonusBucks = -1;
		if(prevAmountSpent >= 200) { // if it was platinum already
			bonusBucks = (int) (((prevAmountSpent + totalPrice) - (prevAmountSpent - 
					(prevAmountSpent % 5))) / 5);
		}
		else { // if it was not a platinum object before
			bonusBucks = (int) (((prevAmountSpent + totalPrice) - 200) / 5);
		}
		return bonusBucks;
	}
	// loops through both reg and pref customer arrays to find ID of matching customer
	public static Customer getCustomerByID(String id, Customer[] customerArray, Customer[] preferredArray) {
		for(int i = 0; i < customerArray.length; i++) {
			if(customerArray[i].getGuestID().contentEquals(id)) {
				return customerArray[i];
			}
		}
		for(int j = 0; j < preferredArray.length; j++) {
			if(preferredArray[j].getGuestID().contentEquals(id)) {
				return preferredArray[j];
			}
		}
		// happens when there is invalid data in orders file
		Customer failedCustomer = new Customer();
		failedCustomer.setGuestID("-1");
		// if it reaches this point return a customer with an id of -1, check in return function for error
		return failedCustomer;
	}
	// Write output to customer.dat
	public static void writeCustomerOutput(Customer[] customerArray) {
		try {
			File filestream = new File("customer.dat");
			PrintWriter output = new PrintWriter(filestream);
			
			for(int i = 0; i < customerArray.length; i++) {
				Customer customer = customerArray[i];
				output.print(customer.getGuestID() + " " +
							customer.getFirstName() + " " + 
							customer.getLastName() + " " +
							df.format(customer.getAmountSpent()));
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
				File filestream = new File("preferred.dat");
				PrintWriter output = new PrintWriter(filestream);
				
				for(int i = 0; i < customerArray.length; i++) {
					Customer customer = customerArray[i];
					output.print(customer.getGuestID() + " " + 
							     customer.getFirstName() + " " + 
							     customer.getLastName() + " " + 
							     df.format(customer.getAmountSpent()));
					// prints out either discount percentage or bonusbucks depending on object type
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
}