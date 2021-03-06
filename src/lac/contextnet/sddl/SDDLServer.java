package lac.contextnet.sddl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import br.pucrio.acanhota.autosddl.commons.VehicleMessage;
import br.pucrio.acanhota.autosddl.server.EsperProcessor;
import br.pucrio.acanhota.autosddl.server.Vehicle;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.serialization.Serialization;
import lac.cnet.sddl.objects.ApplicationObject;
import lac.cnet.sddl.objects.Message;
import lac.cnet.sddl.objects.PrivateMessage;
import lac.cnet.sddl.udi.core.SddlLayer;
import lac.cnet.sddl.udi.core.UniversalDDSLayerFactory;
import lac.cnet.sddl.udi.core.UniversalDDSLayerFactory.SupportedDDSVendors;
import lac.cnet.sddl.udi.core.listener.UDIDataReaderListener;

public class SDDLServer extends EsperProcessor implements UDIDataReaderListener<ApplicationObject> {
	
	/* The SDDL vendor supported */
    private SupportedDDSVendors supportedDDSVendor;

    /*The SDDL Layer : DDS Abstraction */
    protected static SddlLayer sddlLayer;
    
    /* Gateway ID */
	public static UUID gatewayId;
	
	/* Mobile node ID */
	private static UUID nodeId;
    
	public SDDLServer () 
	{
		System.out.println("SDDLServer: starting...");
				
		System.out.println("SDDLServer: setting OpenSplice as supportedDDSVendor...");
		supportedDDSVendor = SupportedDDSVendors.OpenSplice;
		
		System.out.println("SDDLServer: initializing Esper...");
		startEsperProcessor();
		
	    /*create the SDDL layer with a Subscriber listener*/
		System.out.println("SDDLServer: initializing DDS and SDDL...");
	    sddlLayer = UniversalDDSLayerFactory.getInstance(supportedDDSVendor);
	    sddlLayer.createParticipant(UniversalDDSLayerFactory.CNET_DOMAIN);
	    sddlLayer.createPublisher();
	    sddlLayer.createSubscriber();
	    Object receiveTopic = sddlLayer.createTopic(Message.class, Message.class.getSimpleName());
	    Object sendTopic = sddlLayer.createTopic(PrivateMessage.class, PrivateMessage.class.getSimpleName());
	    sddlLayer.createDataReader(this, receiveTopic);
	    sddlLayer.createDataWriter(sendTopic);
	    System.out.println("SDDLServer: initialized!");
	}
		
	public static void main(String[] args) {
		new SDDLServer();

		System.out.println("SDDLServer: started successfully.");
		try {
			while(true) {
				/*print on screen the input message*/
				System.out.print("Escreva a mensagem: ");
				
				/*create and get the input from console*/
				BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			    String inputMsg = bufferRead.readLine();
			    
			    /*create a private message*/
			    PrivateMessage pMsg = new PrivateMessage();
			    pMsg.setGatewayId(gatewayId);
			    pMsg.setNodeId(nodeId);
			    
			    /*create a application message with the MESSAGE*/
			    ApplicationMessage appMsg = new ApplicationMessage();
			    appMsg.setContentObject(inputMsg);
			    
			    /*assign the private message the application message to be sent to mobile node*/
			    pMsg.setMessage(Serialization.toProtocolMessage(appMsg));
			    
			    /*write topic to DDS*/
			    sddlLayer.writeTopic(PrivateMessage.class.getSimpleName(), pMsg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onNewData(ApplicationObject topicSample) {
		Message msg = null;
		
		if (topicSample instanceof Message) {
			msg = (Message) topicSample;
			
			if(gatewayId == null || nodeId == null) //first message to be received
			{
				gatewayId = msg.getGatewayId();
				nodeId = msg.getSenderId();
			}
			
			Serializable rawData = Serialization.fromJavaByteStream(msg.getContent());
			treatDataReceival(msg, rawData);
		}
	}
	
	/* Private Methods */
	/**
	 * This method is the one you should customize so that the server is capable of 
	 * handling different types of objects.
	 * 
	 * @param msg
	 * @param rawData
	 */
	private void treatDataReceival(Message msg, Serializable rawData) {
		if(rawData instanceof VehicleMessage) {
			VehicleMessage vehicleMessage = (VehicleMessage) rawData;
			
			Vehicle vehicle = vehicleDb.getVehicle(vehicleMessage.getLicensePlate());
			if (vehicle == null) {
				/* Add new vehicle */
				Vehicle newVehicle = new Vehicle(vehicleMessage.getLicensePlate(), "No alerts");
				vehicleDb.insert(newVehicle);
			}
			
			System.out.println(new Date() + ": " + vehicleMessage.toString());
			cepRT.sendEvent(vehicleMessage);
		}
		if (rawData instanceof String) {
			String licensePlate = (String) rawData;
			
			ApplicationMessage appMsg = new ApplicationMessage();
			
			Vehicle vehicle = vehicleDb.getVehicle(licensePlate);
			if (vehicle == null) {
				appMsg.setContentObject("Vehicle not found!");
			} else {
				appMsg.setContentObject(vehicle.getStatus());
			}
									
			PrivateMessage privateMessage = new PrivateMessage();
			privateMessage.setGatewayId(gatewayId);
			privateMessage.setNodeId(nodeId);
			privateMessage.setMessage(Serialization.toProtocolMessage(appMsg));

			sddlLayer.writeTopic(PrivateMessage.class.getSimpleName(), privateMessage);			
		}		
	}
}
