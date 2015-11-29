package br.pucrio.acanhota.autosddl.server;

import java.util.ArrayList;

public class VehicleDatabase {

	protected ArrayList<Vehicle> vehicles;
	
	public VehicleDatabase() {
		vehicles = new ArrayList<Vehicle>();
	}
	
	public Vehicle getVehicle(String licensePlate) {
		System.out.println("finding vehicle");
		Vehicle vehicleFound = null;
		for (Vehicle vehicle : vehicles) {
			if (vehicle.getLicensePlate().equals(licensePlate)){
				System.out.println("vehcile found");
				vehicleFound = vehicle;
			}
		}
		return vehicleFound;
	}

	public void insert(Vehicle newVehicle) {
		System.out.println("Adding vehcile");
		vehicles.add(newVehicle);
	}
	
}
