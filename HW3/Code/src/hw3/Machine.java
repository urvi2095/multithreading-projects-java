package hw3;

/**
 * A Machine is used to make a particular Food.  Each Machine makes
 * just one kind of Food.  Each machine has a capacity: it can make
 * that many food items in parallel; if the machine is asked to
 * produce a food item beyond its capacity, the requester blocks.
 * Each food item takes at least item.cookTimeMS milliseconds to
 * produce.
 */
public class Machine {
	public final String machineName;
	public final Food machineFoodType;

	//YOUR CODE GOES HERE...
	private int currentlyServing;
	public int capacityIn;
	private Object shopManager = null;


	/**
	 * The constructor takes at least the name of the machine,
	 * the Food item it makes, and its capacity.  You may extend
	 * it with other arguments, if you wish.  Notice that the
	 * constructor currently does nothing with the capacity; you
	 * must add code to make use of this field (and do whatever
	 * initialization etc. you need).
	 */
	public Machine(String nameIn, Food foodIn, int capacityIn) {
		this.machineName = nameIn;
		this.machineFoodType = foodIn;
		
		//YOUR CODE GOES HERE...
		Simulation.logEvent(SimulationEvent.machineStarting(this, foodIn, capacityIn));
		this.capacityIn = capacityIn;
		currentlyServing = 0;
		shopManager = new Object();
	}
	

	

	/**
	 * This method is called by a Cook in order to make the Machine's
	 * food item.  You can extend this method however you like, e.g.,
	 * you can have it take extra parameters or return something other
	 * than Object.  It should block if the machine is currently at full
	 * capacity.  If not, the method should return, so the Cook making
	 * the call can proceed.  You will need to implement some means to
	 * notify the calling Cook when the food item is finished.
	 */
	public boolean makeFood() throws InterruptedException {
		//YOUR CODE GOES HERE...
		synchronized(cm)
		{
			//System.out.println("Machine: "+this.toString()+" currentlyServing:"+currentlyServing);
			boolean itemCooked = false;
			while(currentlyServing == capacityIn)
			{
				//Machine is currently at its full capacity
				//System.out.println("Waiting to cook: "+orderNum + " by: "+this.toString()+" by: "+Thread.currentThread().getName());
				cm.wait(10);
			}

			//Machine has capacity to cook. Incrementing the count of 'currentlyServing'
			currentlyServing++;

			//Calling the thread with CookAnItem object to cook the order
			CookAnItem c = new CookAnItem(orderNum);
			Thread t1 = new Thread(c, "machineThread-"+orderNum);
			t1.start();

			//t1.join();
			cm.notifyAll();
			itemCooked = true;

			//Finished Cooking
			currentlyServing--;
			return itemCooked;

		}
	}

	//THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
	private class CookAnItem implements Runnable {
		public void run() {
			try {
				//YOUR CODE GOES HERE...
				synchronized(cm)
				{
					Simulation.logEvent(SimulationEvent.machineCookingFood(Machine.this, f, orderNum));
					//System.out.println("Cooking: "+orderNum+" by machine: "+this.toString()+" by thread: "+Thread.currentThread().getName());

					//System.out.println("Thread sleeping: "+Thread.currentThread().getName());
					Thread.sleep(f.cookTimeMS);

					//System.out.println("Thread wokeup: "+Thread.currentThread().getName());
					Simulation.logEvent(SimulationEvent.machineDoneFood(Machine.this, f, orderNum));
				}
				//System.out.println("Cooking completed: "+orderNum+" by machine: "+this.toString()+" by thread: "+Thread.currentThread().getName());

			} catch(InterruptedException e) { }
		}
	}

	public Food getMachineFoodType() {
		return machineFoodType;
	}

	public String toString() {
		return machineName;
	}
}