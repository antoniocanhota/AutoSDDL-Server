package br.pucrio.acanhota.autosddl.server;

import java.util.ArrayList;
import java.util.Date;

public class VehicleDatabase {

	protected ArrayList<Vehicle> vehicles;
	
	public VehicleDatabase() {
		vehicles = new ArrayList<Vehicle>();
	}
	
	public Vehicle getVehicle(String licensePlate) {		
		Vehicle vehicleFound = null;
		for (Vehicle vehicle : vehicles) {
			if (vehicle.getLicensePlate().equals(licensePlate)){				
				vehicleFound = vehicle;
			}
		}
		return vehicleFound;
	}

	public void insert(Vehicle newVehicle) {
		System.out.println((new Date()) + ": Added vehicle " + newVehicle);
		vehicles.add(newVehicle);
	}
	
}
