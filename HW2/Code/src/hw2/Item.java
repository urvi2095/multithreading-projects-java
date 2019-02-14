package hw2;

import java.util.Date;


/**
 * Stores the initial information for an item submitted for bidding.
 *
 */
public class Item
{	
    private String seller;
    private String name;
    private int listingID;
    private int lowestBiddingPrice;
    private int biddingDurationMs;
    
    private Date biddingStart;
    
	public Item(String seller, String name, int listingID, int lowestBiddingPrice, int biddingDurationMs)
	{
	    this.seller = seller;
		this.name = name;
		this.listingID = listingID;
		this.lowestBiddingPrice = lowestBiddingPrice;
		this.biddingDurationMs = biddingDurationMs;
		
		this.biddingStart = new Date();
	}
	
	public String seller()
	{
	    return this.seller;
	}
	
	public String name()
	{
		return this.name;
	}
	
	public int listingID()
	{
		return this.listingID;		
	}
	
	public int lowestBiddingPrice()
	{
	    return this.lowestBiddingPrice;
	}
	
	public int biddingDurationMs()
	{
	    return this.biddingDurationMs;
	}
	
	/**
	 * Returns true if the bidding is open (active) for the current item.
	 * 
	 * In other words, if the time elapsed from the moment the bidding 
	 * started for this item is less than the bidding duration, it will
	 * return true. Otherwise it returns false. 
	 */
	public boolean biddingOpen()
	{
	    Date now = new Date();
	    return (now.getTime() - this.biddingStart.getTime()) / 1 < this.biddingDurationMs;
	}
	
	@Override
	public boolean equals(Object obj)
	{
	    if (obj == null) { return false; }
	    
	    if (!obj.getClass().equals(this.getClass()))
	    {
	        return false;
	    }
	    
	    Item item = (Item)obj;
	    
	    return item.listingID == this.listingID;
	}
	
	@Override
	public int hashCode()
	{
	    return this.listingID;
	}
	
	@Override
	public String toString()
	{
	    return ((Integer)this.listingID).toString();
	}
}