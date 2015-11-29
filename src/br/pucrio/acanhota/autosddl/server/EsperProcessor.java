package br.pucrio.acanhota.autosddl.server;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import br.pucrio.acanhota.autosddl.commons.VehicleMessage;

public class EsperProcessor {
	protected	EPRuntime  cepRT;

	protected void startEsperProcessor() {
		Configuration cepConfig = new Configuration();
		cepConfig.addEventType("VehicleStatus",VehicleMessage.class.getName());
		EPServiceProvider cep = EPServiceProviderManager.getProvider("myCEPEngine",cepConfig);
		this.cepRT = cep.getEPRuntime();
		
		// Esper rule to detect vehicle crash
		EPAdministrator cepAdm = cep.getEPAdministrator();
		//TODO: Mudar essa regra!!!
		EPStatement cepStatement = cepAdm.createEPL("select * from " +
		"VehicleStatus().win:length(1) " +
		"having accelerometer > 1.2");
		cepStatement.addListener(new CEPListener());
	}
	
	private static class CEPListener implements UpdateListener {
		public void update(EventBean[] newData, EventBean[] oldData) {
			System.out.println("VEHICLE CRASH: " + newData[0].getUnderlying());
			System.out.println("@TODO: Sending messages to vehicle subscribers");
		}
	}
	
}
