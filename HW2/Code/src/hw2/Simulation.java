package hw2;

/**
 * Class provided for ease of test. This will not be used in the project 
 * evaluation, so feel free to modify it as you like.
 */ 
public class Simulation
{
    public static void main(String[] args)
    {                
        int nrSellers = 50;
        int nrBidders = 20;
        
        Thread[] sellerThreads = new Thread[nrSellers];
        Thread[] bidderThreads = new Thread[nrBidders];
        Seller[] sellers = new Seller[nrSellers];
        Bidder[] bidders = new Bidder[nrBidders];
        
        // Start the sellers
        for (int i=0; i<nrSellers; ++i)
        {
            sellers[i] = new Seller(
            		AuctionServer.getInstance(), 
            		"hw2.Seller"+i,
            		100, 50, i
            );
            sellerThreads[i] = new Thread(sellers[i]);
            sellerThreads[i].start();
        }
        
        // Start the buyers
        for (int i=0; i<nrBidders; ++i)
        {
            bidders[i] = new Bidder(
            		AuctionServer.getInstance(),
            		"Buyer"+i, 
            		1000, 20, 150, i
            );
            bidderThreads[i] = new Thread(bidders[i]);
            bidderThreads[i].start();
        }
        
        // Join on the sellers
        for (int i=0; i<nrSellers; ++i)
        {
            try
            {
                sellerThreads[i].join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        
        // Join on the bidders
        for (int i=0; i<nrBidders; ++i)
        {
            try
            {
                bidderThreads[i].join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        
        // TODO: Add code as needed to debug

        System.out.println("The Total Revenue generated is: "+ AuctionServer.getInstance().revenue());
        System.out.println("The Total Sold Items Count is: "+ AuctionServer.getInstance().soldItemsCount());

        int TotalMoneySpentByBidders = 0;
        for(Bidder b : bidders){
            TotalMoneySpentByBidders = TotalMoneySpentByBidders + b.cashSpent();
        }
        System.out.println("Total money spent by Bidders " + TotalMoneySpentByBidders );
        if (TotalMoneySpentByBidders == AuctionServer.getInstance().revenue()){
            System.out.println("Revenue is equal to Total Money Spent, Auction Successful");
        }
        else {
            System.out.println("Fault in auction server");
        }

    }
}