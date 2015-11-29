package br.pucrio.acanhota.autosddl.server;

import java.util.ArrayList;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import br.pucrio.acanhota.autosddl.commons.Subscriber;
import br.pucrio.acanhota.autosddl.commons.VehicleMessage;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.serialization.Serialization;
import lac.cnet.sddl.objects.PrivateMessage;
import lac.cnet.sddl.udi.core.SddlLayer;

public abstract class EsperProcessor {
	protected	EPRuntime  cepRT;
	
    /*The SDDL Layer : DDS Abstraction */
    protected static SddlLayer sddlLayer;
    
    protected ArrayList<Subscriber> subscribers = new ArrayList<Subscriber>();

	protected void startEsperProcessor() {
		Configuration cepConfig = new Configuration();
		cepConfig.addEventType("VehicleMessage",VehicleMessage.class.getName());
		EPServiceProvider cep = EPServiceProviderManager.getProvider("myCEPEngine",cepConfig);
		this.cepRT = cep.getEPRuntime();
		
		// Esper rule to detect vehicle crash
		EPAdministrator cepAdm = cep.getEPAdministrator();
		//TODO: Mudar essa regra!!!
		EPStatement cepStatement = cepAdm.createEPL("select * from " +
		"VehicleMessage().win:length(2) " +
		"having vehicleAcceleration < -4");
		cepStatement.addListener(new CEPListener());
	}
	
	public class CEPListener implements UpdateListener {
		
		@Override
		public void update(EventBean[] newData, EventBean[] oldData) {
			VehicleMessage vehicleMessage = (VehicleMessage) newData[0].getUnderlying();
			
			String crashMessage = "Possible vehicle crash at " + vehicleMessage.getCreatedAtAsStr() + " on " + vehicleMessage.getCoordinatesAsStr() + ".";			
			ApplicationMessage appMes = new ApplicationMessage();
			appMes.setContentObject(crashMessage);
			
			System.out.println("IMPORTANT!" + crashMessage);
			
			for (Subscriber subscriber : subscribers) {
				if (subscriber.getLicensePlate() == vehicleMessage.getLicensePlate()) {
					PrivateMessage message = new PrivateMessage();				
					message.setMessage(Serialization.toProtocolMessage(appMes));
					message.setNodeId(subscriber.getUuid());
					
					sddlLayer.writeTopic(PrivateMessage.class.getSimpleName(), message);
				}
			}
		}
		
	}
}
