# DisneyWorldRewardsSystem
A program that simulates Disney world's rewards system for buying customly designed cups

# Description:
There are 3 tiers:
- Customer (0-49 dollars spent)
- Gold Customer (50-199 dollars spent)
- Platinum Customer (200+ dollars spent)

 A regular Customer receives not discount or benefits, however a Gold customer will receive a discount ranging from 5-15%, and Platinum customer will receive bonus bucks that will
be added to the next order.

The orders will be given in this order within the file: guestID, cup size, drink type, square inch price of design, and quantity ordered.

The program will read 3 files 
1. A regular customer file
2. A preferred customer file
3. An orders file

After doing so the program will allocate the information of the first two files into arrays of class Customer.
The main point of the program is to demonstrate usage of polymorphism in Java so preferred customer file will be filled with Gold and Platinum customers
Then after checking the guest ID's and general input validation the orders will be processed.

The most difficult part of this project was the unallowed usage of arraylists. The only other way to resize the array was to create a new array of size + 1 or - 1 and copy the contents
over from the original array. So while the orders are being processed I have to promote the regular customers to Gold or Platinum, Gold customers to different tiers of Gold,
and calculate the bonus bucks of a Platinum customer. Which is a demonstration of polymorphism.

After all the orders has been processed. The program outputs the results to a preferred.dat file, and customer.dat file.
