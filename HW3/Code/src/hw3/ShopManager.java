package com.neu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopManager {

    private int numCustomers;
    private int numCooks;
    private int numTables;
    private int machineCapacity;
    private boolean randomOrders;
    private final List<Customer> currentlyServing = new ArrayList<Customer>();
    private final List<Machine> activeMachines = new ArrayList<Machine>();
    private final HashMap<Customer, Boolean> orderStatus = new HashMap<>();

    public ShopManager(int numCustomers, int numCooks,
                             int numTables,
                             int machineCapacity,
                             boolean randomOrders)
    {
        this.numCustomers = numCustomers;
        this.machineCapacity = machineCapacity;
        this.randomOrders =  randomOrders;
        this.numTables = numTables;
        this.numCooks = numCooks;
    }

    public void initiateMachines()
    {
        activeMachines.add(new Machine("Grill", FoodType.burger, machineCapacity));
        activeMachines.add(new Machine("Fryer", FoodType.fries, machineCapacity));
        activeMachines.add(new Machine("CoffeeMaker2000", FoodType.coffee, machineCapacity));
    }

    public Machine getMachineInstance(Food f)
    {
        for(Machine m : activeMachines)
        {
            if(m.machineFoodType == f)
                return m;
        }
        return null;
    }


    public int getNumCustomers() {
        return numCustomers;
    }


    public void setNumCustomers(int numCustomers) {
        this.numCustomers = numCustomers;
    }


    public int getNumCooks() {
        return numCooks;
    }


    public void setNumCooks(int numCooks) {
        this.numCooks = numCooks;
    }


    public int getNumTables() {
        return numTables;
    }


    public void setNumTables(int numTables) {
        this.numTables = numTables;
    }


    public int getMachineCapacity() {
        return machineCapacity;
    }


    public void setMachineCapacity(int machineCapacity) {
        this.machineCapacity = machineCapacity;
    }


    public boolean isRandomOrders() {
        return randomOrders;
    }


    public void setRandomOrders(boolean randomOrders) {
        this.randomOrders = randomOrders;
    }


    public List<Customer> getCurrentlyServing() {
        return currentlyServing;
    }


    public List<Machine> getActiveMachines() {
        return activeMachines;
    }


    public HashMap<Customer, Boolean> getOrderStatus() {
        return orderStatus;
    }


}
