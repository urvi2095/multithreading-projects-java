package hw3;

import java.util.List;

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order.  When running, an
 * customer attempts to enter the coffee shop (only successful if the
 * coffee shop has a free table), place its order, and then leave the 
 * coffee shop when the order is complete.
 */
public class Customer implements Runnable {
	//JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final List<Food> order;
	private final int orderNum;

	//Initialzing the object of the Manager to keep the status of currently serving synchronized with the cook.
	private ShopManager shopManager = null;
	
	private static int runningCounter = 0;

	/**
	 * You can feel free modify this constructor.  It must take at
	 * least the name and order but may take other parameters if you
	 * would find adding them useful.
	 */
	public Customer(String name, List<Food> order) {
		Simulation.logEvent(SimulationEvent.customerStarting(this));
		this.name = name;
		this.order = order;
		this.orderNum = ++runningCounter;
		this.shopManager = Simulation.getShopManager();
	}

	public String toString() {
		return name;
	}

	public int getOrderNum() {
		return orderNum;
	}

	public List<Food> getOrder() {
		return order;
	}

	/** 
	 * This method defines what an Customer does: The customer attempts to
	 * enter the coffee shop (only successful when the coffee shop has a
	 * free table), place its order, and then leave the coffee shop
	 * when the order is complete.
	 */
	public void run() {
		//YOUR CODE GOES HERE...

		synchronized (shopManager) {
			//System.out.println(name+ " currentlyServing size:"+shopManager.getCurrentlyServing().size());
			while(shopManager.getCurrentlyServing().size() >= shopManager.getNumTables())
			{
				//Case when there is no empty table in the Coffee Shop
				try {
					//System.out.println("Waiting customer: "+name+" currentlyServing size:"+shopManager.getCurrentlyServing().size()+" by: "+Thread.currentThread().getName());
					shopManager.wait(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			if(shopManager.getCurrentlyServing().size() < shopManager.getNumTables())
			{
				//Case when there is a empty table in the Coffee Shop
				Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));

				//Adding to the list of currently serving in order to notify the cook of the customer.
				shopManager.getCurrentlyServing().add(this);
				System.out.println(name+" added, size: "+shopManager.getCurrentlyServing().size());

				//Order Status: False, since it is yet to be completed by the Cook.
				shopManager.getOrderStatus().put(this, false);
				shopManager.notifyAll();
			}
		}
		
	}
}