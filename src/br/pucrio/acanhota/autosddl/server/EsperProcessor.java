package br.pucrio.acanhota.autosddl.server;

import java.util.Date;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import br.pucrio.acanhota.autosddl.commons.VehicleMessage;

public abstract class EsperProcessor {
	protected	EPRuntime  cepRT;

    protected VehicleDatabase vehicleDb;

	protected void startEsperProcessor() {
		vehicleDb = new VehicleDatabase();
		
		Configuration cepConfig = new Configuration();
		cepConfig.addEventType("VehicleMessage",VehicleMessage.class.getName());
		EPServiceProvider cep = EPServiceProviderManager.getProvider("myCEPEngine",cepConfig);
		this.cepRT = cep.getEPRuntime();
		
		// Esper rule to detect vehicle crash
		EPAdministrator cepAdm = cep.getEPAdministrator();
		EPStatement cepStatement = cepAdm.createEPL("select * from " +
				"VehicleMessage().win:length(2) " +
				"having vehicleAcceleration < -3");		
		/*
		EPStatement cepStatement = cepAdm.createEPL("select * from " +
				"VehicleMessage().win:length(1) " +
				"having rpm < 0");
		*/
		cepStatement.addListener(new CEPListener());		
	}
	
	public class CEPListener implements UpdateListener {
		
		@Override
		public void update(EventBean[] newData, EventBean[] oldData) {
			VehicleMessage vehicleMessage = (VehicleMessage) newData[0].getUnderlying();			
			String crashMessage = "Possible accident at " + vehicleMessage.getCreatedAtAsStr() + " at " + vehicleMessage.getCoordinatesAsStr() + ".";
			
			Vehicle vehicle = vehicleDb.getVehicle(vehicleMessage.getLicensePlate());
			if (vehicle != null) {
				vehicle.setStatus(crashMessage);
			}
			
			System.out.println((new Date()) + ": ALERT! [vehicle = " + vehicle + ", alert = '" + crashMessage + "']");
		}
		
	}
}
