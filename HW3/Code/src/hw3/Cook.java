package hw3;

import java.util.List;

/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {
	private final String name;

	//Initialzing the object of the Manager to keep the status of currently serving customer synchronized with the cook.
	private ShopManager shopManager = null;

	//Added a boolean field in order to keep track of the status of the cook.
	public boolean busy = false;

	/**
	 * You can feel free modify this constructor.  It must
	 * take at least the name, but may take other parameters
	 * if you would find adding them useful. 
	 *
	 * @param: the name of the cook
	 */
	//Modifying the constructor to define the added field parameters.
	public Cook(String name) {
		Simulation.logEvent(SimulationEvent.cookStarting(this));
		this.name = name;
		this.busy = false;
		this.shopManager = Simulation.getShopManager();
	}

	public String toString() {
		return name;
	}

	/**
	 * This method executes as follows.  The cook tries to retrieve
	 * orders placed by Customers.  For each order, a List<Food>, the
	 * cook submits each Food item in the List to an appropriate
	 * Machine, by calling makeFood().  Once all machines have
	 * produced the desired Food, the order is complete, and the Customer
	 * is notified.  The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some
	 * other thread calls the interrupt() method on it, which could
	 * raise InterruptedException if the cook is blocking), then it
	 * terminates.
	 */
	public void run() {

		Simulation.logEvent(SimulationEvent.cookStarting(this));
		try {
			synchronized (shopManager) {
				while (true) {
					//YOUR CODE GOES HERE...

					while(shopManager.getCurrentlyServing().size() == 0)
					{
						shopManager.wait(10);
					}

					if(shopManager.getCurrentlyServing().size() > 0) {
						busy = true;
						Customer servingCustomer = shopManager.getCurrentlyServing().get(0);

						List<Food> order = servingCustomer.getOrder();
						int orderNum = servingCustomer.getOrderNum();
						Simulation.logEvent(SimulationEvent.customerPlacedOrder(servingCustomer, order, orderNum));
						Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, order, orderNum));
						for(Food f : order)
						{
							Machine m = shopManager.getMachineInstance(f);
							if(m != null)
							{
								//System.out.println("Got machine:"+m.toString()+" for food:"+f.toString()+" for cust:"+
								//servingCustomer.toString()+" by cook:"+this.toString()+" by:"+Thread.currentThread().getName());
								Simulation.logEvent(SimulationEvent.cookStartedFood(this, f, orderNum));
								boolean itemStatus = m.makeFood(orderNum);

								//Thread.currentThread().join();
								if(!itemStatus)
								{
									System.out.println("Cooking failed order:"+servingCustomer.toString()+" by cook: "+this.toString()+" by: "+Thread.currentThread().getName());
								}
								else
									Simulation.logEvent(SimulationEvent.cookFinishedFood(this, f, orderNum));
							}
							else
								System.out.println("Machine was not found in Cook class for food type:"+f.name);
						}

						Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, orderNum));
						shopManager.getOrderStatus().put(servingCustomer, true);
						Simulation.logEvent(SimulationEvent.customerReceivedOrder(servingCustomer, order, orderNum));

						shopManager.getCurrentlyServing().remove(servingCustomer);
						Simulation.logEvent(SimulationEvent.customerLeavingCoffeeShop(servingCustomer));
						System.out.println(servingCustomer+" removed, size: "+shopManager.getCurrentlyServing().size());
						shopManager.notifyAll();
					}
					else
						System.out.println("No orders by :"+this.toString()+" by:"+Thread.currentThread().getName());
					busy = false;

				}
			}
		}
		catch(InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
}