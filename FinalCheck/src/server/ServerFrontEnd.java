package server;

import java.util.HashMap;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import CenterServerModule.ICenterServer;
import CenterServerModule.ICenterServerHelper;
import CenterServerModule.ICenterServerPOA;
import client.ManagerClient;
import threads.BackupServerMultiCastReceiver;

public class ServerFrontEnd extends ICenterServerPOA {

	public static ICenterServer serverImpl = null;
	public static int portMontreal3 = 4003;
	public static int portMontreal2 = 4002;
	public static int portMontreal1 = 4001;
	public static int portLaval3 = 5003;
	public static int portLaval2 = 5002;
	public static int portLaval1 = 5001;
	public static int portDollard3 = 6003;
	public static int portDollard2 = 6002;
	public static int portDollard1 = 6001;
	public static int currentPrimaryPortSendMontreal;
	public static int currentPrimaryPortSendLaval;
	public static int currentPrimaryPortSendDollard;
	public static int currentPrimaryPortReceive = 0;
	public static ICenterServer serverImplMTL1;
	public static ICenterServer serverImplMTL2 = null;
	public static String mtlHostAddress = "224.0.0.1";
	public static String lvlHostAddress = "224.0.0.2";
	public static String ddoHostAddress = "224.0.0.3";
	public static String loc = null;
	public static String objToInititate = null;
	public static  HashMap<String, ICenterServer> primarySeverHashMap = new HashMap<String, ICenterServer>();
	public static  HashMap<String, ICenterServer> secondarySeverHashMap = new HashMap<String, ICenterServer>();
	public static HashMap<String, ICenterServer> tertiaryHashMap = new HashMap<String, ICenterServer>();

	public void intializeServerLists(String[] args) {
		try {

			// primarySeverHashMap = new HashMap<String, ICenterServer>();
			// secondarySeverHashMap = new HashMap<String, ICenterServer>();
			// tertiaryHashMap = new HashMap<String, ICenterServer>();

			ORB orb = ORB.init(args, null);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			ICenterServer serverImplpMTL = ICenterServerHelper.narrow(ncRef.resolve_str(ManagerClient.MTL_SERVER_3));
			primarySeverHashMap.put("MTL", serverImplpMTL);
			ICenterServer serverImplpLVL = ICenterServerHelper.narrow(ncRef.resolve_str(ManagerClient.LVL_SERVER_3));
			primarySeverHashMap.put("LVL", serverImplpLVL);
			ICenterServer serverImplpDDO = ICenterServerHelper.narrow(ncRef.resolve_str(ManagerClient.DDO_SERVER_3));
			primarySeverHashMap.put("DDO", serverImplpDDO);

			ICenterServer serverImplsMTL = ICenterServerHelper.narrow(ncRef.resolve_str(ManagerClient.MTL_SERVER_2));
			secondarySeverHashMap.put("MTL", serverImplsMTL);
			serverImplMTL2 = serverImplsMTL;
			ICenterServer serverImplsLVL = ICenterServerHelper.narrow(ncRef.resolve_str(ManagerClient.LVL_SERVER_2));
			secondarySeverHashMap.put("LVL", serverImplsLVL);
			ICenterServer serverImplsDDO = ICenterServerHelper.narrow(ncRef.resolve_str(ManagerClient.DDO_SERVER_2));
			secondarySeverHashMap.put("DDO", serverImplsDDO);

			ICenterServer serverImpltMTL = ICenterServerHelper.narrow(ncRef.resolve_str(ManagerClient.MTL_SERVER_1));
			tertiaryHashMap.put("MTL", serverImpltMTL);
			serverImplMTL1 = serverImplsMTL;
			ICenterServer serverImpltLVL = ICenterServerHelper.narrow(ncRef.resolve_str(ManagerClient.LVL_SERVER_1));
			tertiaryHashMap.put("LVL", serverImpltLVL);
			ICenterServer serverImpltDDO = ICenterServerHelper.narrow(ncRef.resolve_str(ManagerClient.DDO_SERVER_1));
			tertiaryHashMap.put("DDO", serverImpltDDO);
			// implServers obj = new implServers(primarySeverHashMap, secondarySeverHashMap,
			// tertiaryHashMap);
			// obj.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ServerFrontEnd(String[] args, String location) {

		try {
			intializeServerLists(args);
			loc = location;
			// Identify location and set current primary
			setPrimary(loc);

		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	public static void setPrimary(String loc) {
		try {
			if (loc == "Montreal") {

				currentPrimaryPortReceive = 4000;
				if (!MTLServer2.isPrimary) {
					currentPrimaryPortSendMontreal = portMontreal3;
					currentPrimaryPortSendLaval = portLaval3;
					currentPrimaryPortSendDollard = portDollard3;
					ServerFrontEnd.serverImpl = primarySeverHashMap.get("MTL");
					
					
					
					new BackupServerMultiCastReceiver(currentPrimaryPortSendMontreal, mtlHostAddress,
							secondarySeverHashMap.get("MTL")).start();
					new BackupServerMultiCastReceiver(currentPrimaryPortSendMontreal, mtlHostAddress, tertiaryHashMap.get("MTL"))
							.start();

					// Laval
					new BackupServerMultiCastReceiver(currentPrimaryPortSendLaval, lvlHostAddress, secondarySeverHashMap.get("LVL"))
							.start();
					new BackupServerMultiCastReceiver(currentPrimaryPortSendLaval, lvlHostAddress, tertiaryHashMap.get("LVL"))
							.start();

					// Dollard
					new BackupServerMultiCastReceiver(currentPrimaryPortSendDollard, ddoHostAddress,
							secondarySeverHashMap.get("DDO")).start();
					new BackupServerMultiCastReceiver(currentPrimaryPortSendDollard, ddoHostAddress, tertiaryHashMap.get("DDO"))
							.start();
					
					

				} else {
					currentPrimaryPortSendMontreal = portMontreal2;
					ServerFrontEnd.serverImpl = secondarySeverHashMap.get("MTL");
					new BackupServerMultiCastReceiver(currentPrimaryPortSendMontreal, mtlHostAddress,
							tertiaryHashMap.get("MTL")).start();
				}

			}

			else if (loc == "Laval") {

				currentPrimaryPortReceive = 5000;
				if (primarySeverHashMap.containsKey("LVL")) {
					currentPrimaryPortSendLaval = portLaval3;
					currentPrimaryPortSendMontreal = portMontreal3;
					currentPrimaryPortSendDollard = portDollard3;
					ServerFrontEnd.serverImpl = primarySeverHashMap.get("LVL");
					initializeReceiveingThreads();
				} else {
					currentPrimaryPortSendLaval = portLaval2;
					ServerFrontEnd.serverImpl = secondarySeverHashMap.get("LVL");
					new BackupServerMultiCastReceiver(currentPrimaryPortSendLaval, lvlHostAddress,
							tertiaryHashMap.get("LVL")).start();
				}

			} else if (loc == "Dollard") {

				currentPrimaryPortReceive = 6000;
				if (primarySeverHashMap.containsKey("DDO")) {
					currentPrimaryPortSendDollard = portDollard3;
					currentPrimaryPortSendLaval = portLaval3;
					currentPrimaryPortSendMontreal = portMontreal3;
					ServerFrontEnd.serverImpl = primarySeverHashMap.get("DDO");
					initializeReceiveingThreads();
				} else {
					currentPrimaryPortSendDollard = portDollard2;
					ServerFrontEnd.serverImpl = secondarySeverHashMap.get("DDO");
					new BackupServerMultiCastReceiver(currentPrimaryPortSendDollard, ddoHostAddress,
							tertiaryHashMap.get("DDO")).start();
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void initializeReceiveingThreads() {

		// For Transfer record functionality - Initializing all threads

		// Montreal
		
	}

	@Override
	public boolean createTRecord(String firstName, String lastName, String address, String phone, String specialization,
			String location, String managerId) {
		Boolean returnValue = false;
		try {

			returnValue = serverImpl.createTRecord(firstName, lastName, address, phone, specialization, location,
					managerId);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// TODO Auto-generated method stub
		return returnValue;
	}

	@Override
	public boolean createSRecord(String firstName, String lastName, String courseRegistered, String status,
			String statusDate, String managerId) {

		Boolean returnValue = false;
		try {
			returnValue = serverImpl.createSRecord(firstName, lastName, courseRegistered, status, statusDate,
					managerId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return returnValue;
	}

	@Override
	public String getRecordCounts(String managerId) {

		String response = null;
		try {
			response = serverImpl.getRecordCounts(managerId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return response;
		// TODO Auto-generated method stub
	}

	@Override
	public String editRecord(String recordId, String fieldName, String newValue, String managerId) {

		String response = null;
		try {
			response = serverImpl.editRecord(recordId, fieldName, newValue, managerId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return response;
	}

	@Override
	public String editListRecord(String recordId, String fieldName, String newValue, String managerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean transferRecord(String managerId, String recordId, String targetCenterName) {
		Boolean returnValue = false;
		try {
			returnValue = serverImpl.transferRecord(managerId, recordId, targetCenterName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return returnValue;
	}

	@Override
	public void printHashMap() {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	public HashMap<String, ICenterServer> getPrimarySeverHashMap() {
		return primarySeverHashMap;
	}

	public HashMap<String, ICenterServer> getSecondarySeverHashMap() {
		return secondarySeverHashMap;
	}

	public HashMap<String, ICenterServer> getTertiaryHashMap() {
		return tertiaryHashMap;
	}

	public ServerFrontEnd() {
		// TODO Auto-generated constructor stub
	}

	public void killThreads() {
		serverImpl.killThreads();
	}
}
