package br.pucrio.acanhota.autosddl.server;

public class Vehicle {

	private String licensePlate;
	private String status;
	
	public Vehicle(String licensePlate, String status) {
		this.setLicensePlate(licensePlate);
		this.setStatus(status);
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
