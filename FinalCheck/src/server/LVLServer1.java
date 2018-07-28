package server;

import CenterServerModule.*;
import logger.LogManager;
import threads.PrimaryServerThreadToReceiveResponse;
import threads.ServerMultiCastSender;
import utility.FailureDetector;
import utility.HeartBeat;
import utility.Record;
import utility.Student;
import utility.Teacher;
import client.ManagerClient;

import org.omg.CosNaming.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class LVLServer1 extends ICenterServerPOA implements Runnable {
	private ORB orbLaval;
	public static HashMap<String, ArrayList<Record>> lvlDB = new HashMap<String, ArrayList<Record>>();
	private static HashMap<String, String> idToLastName = new HashMap<String, String>();
	private static int count = 0;
	private LogManager lvlLogger;
	static String location = "lvl";
	public static int LVLport = 2345;
	public static boolean LVLFlag;
	public static boolean isPrimary = false;
	public static long lastHeartBeatTime1 = System.currentTimeMillis();

	public LVLServer1() throws Exception {
		super();
	}

	public void initializeLogger() {
		lvlLogger = new LogManager("lvl1");
		lvlLogger.mLogger.setUseParentHandlers(true);
	}

	public void setORB(ORB orb_val) {
		orbLaval = orb_val;
	}
	
	public static void togglePrimary() throws Exception {
		System.out.println("Primary is changed to lvlserver1");
		isPrimary = !isPrimary;
		
		if (isPrimary) {

//			Thread threadSocket = new Thread(new MTLServer1());
//			threadSocket.start();

			HeartBeat heartbeatToBackUp2 = new HeartBeat(ServerFrontEnd.portLaval1, ServerFrontEnd.portLaval2);
			heartbeatToBackUp2.startUp();

			HeartBeat heartbeatToBackUp3 = new HeartBeat(ServerFrontEnd.portLaval1, ServerFrontEnd.portLaval3);
			heartbeatToBackUp3.startUp();

		} else {

			// listening to heartbeat from the primary server
			Thread heartbeat = new Thread(new HeartBeat(5001));
			heartbeat.start();

			// check for primary's failure
			Thread t = new Thread(new FailureDetector(5001));
			t.start();
		}
	}

	public boolean createTRecord(String firstName, String lastName, String address, String phone, String specialization,
			String location, String managerId) {

		Record objRecord = new Teacher(firstName, lastName, address, phone, specialization, location);

		// locking the server database for synchronized access
		synchronized (lvlDB) {

			// checking if the key already exists in hash map
			if (lvlDB.containsKey(lastName.substring(0, 1))) {
				if (isPrimary) {
					lvlDB.get(lastName.substring(0, 1)).add(objRecord);
				} else {
					// use the old recordID
					objRecord.setRecordId(managerId.split("\\|")[1]);
					lvlDB.get(lastName.substring(0, 1)).add(objRecord);
				}

			} else {
				if (isPrimary) {
					ArrayList<Record> alRecord = new ArrayList<Record>();
					alRecord.add(objRecord);
					lvlDB.put(lastName.substring(0, 1), alRecord);
				} else {
					// use the old recordID
					objRecord.setRecordId(managerId.split("\\|")[1]);
					ArrayList<Record> alRecord = new ArrayList<Record>();
					alRecord.add(objRecord);
					lvlDB.put(lastName.substring(0, 1), alRecord);
				}

			}

			idToLastName.put(objRecord.getRecordId(), lastName.substring(0, 1));

			count++;

			if (isPrimary) {
				String request = "createTRecord" + "|" + objRecord.getRecordId() + "|" + firstName + "|" + lastName
						+ "|" + address + "|" + phone + "|" + specialization + "|" + location + "|" + managerId;

				broadcast(request);
			}
		}

		// adding the operation to the log file
		lvlLogger.mLogger.info(managerId + " created Teacher record with values: " + objRecord + '\n');

		return true;
	}
	
	public boolean createSRecord(String firstName, String lastName, String courseRegistered, String status,
			String statusDate, String managerId) {

		Record objRecord = new Student(firstName, lastName, courseRegistered, status, statusDate);

		// locking the server database for synchronized access
		synchronized (lvlDB) {

			// checking if the key already exists in hash map
			if (lvlDB.containsKey(lastName.substring(0, 1))) {
				if (isPrimary) {
					lvlDB.get(lastName.substring(0, 1)).add(objRecord);
				} else {
					// use the old recordID
					objRecord.setRecordId(managerId.split("\\|")[1]);
					lvlDB.get(lastName.substring(0, 1)).add(objRecord);
				}

			} else {
				if (isPrimary) {
					ArrayList<Record> alRecord = new ArrayList<Record>();
					alRecord.add(objRecord);
					lvlDB.put(lastName.substring(0, 1), alRecord);
				} else {
					// use the old recordID
					objRecord.setRecordId(managerId.split("\\|")[1]);
					ArrayList<Record> alRecord = new ArrayList<Record>();
					alRecord.add(objRecord);
					lvlDB.put(lastName.substring(0, 1), alRecord);
				}

			}

			idToLastName.put(objRecord.getRecordId(), lastName.substring(0, 1));

			count++;

			if (isPrimary) {
				String request = "createSRecord" + "|" + objRecord.getRecordId() + "|" + firstName + "|" + lastName
						+ "|" + courseRegistered + "|" + status + "|" + statusDate + "|" + managerId;

				broadcast(request);
			}
		}

		// adding the operation to the log file
		lvlLogger.mLogger.info(managerId + " created Student record with values: " + objRecord + '\n');

		return true;
	}

	public synchronized String getRecordCounts(String managerId) {

		String str = location + " " + count + "\n";

		DatagramSocket socket1 = null;
		DatagramSocket socket2 = null;
		byte[] message1 = location.getBytes();
		byte[] message2 = location.getBytes();

		try {
			// locking the server database for synchronized access
			synchronized (lvlDB) {

				lvlLogger.mLogger.info(managerId + " sent request for total record count" + '\n');
				socket1 = new DatagramSocket();
				socket2 = new DatagramSocket();
				InetAddress address = InetAddress.getByName("localhost");

				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, 1234);
				socket1.send(request1);
				lvlLogger.mLogger.info(location + " server sending request to mtl sever for total record count" + '\n');

				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				lvlLogger.mLogger
						.info("mtl server sent response to " + location + " sever for total record count " + '\n');

				str = str.concat(new String(reply1.getData()));
				str = str.trim();
				str = str.concat("\n");

				DatagramPacket request2 = new DatagramPacket(message2, message2.length, address, 3456);
				socket2.send(request2);
				lvlLogger.mLogger.info(location + " sever sending request to ddo sever for total record count" + '\n');

				byte[] receive2 = new byte[1000];
				DatagramPacket reply2 = new DatagramPacket(receive2, receive2.length);
				socket2.receive(reply2);
				lvlLogger.mLogger
						.info("ddo server sent response to " + location + " sever for total record count " + '\n');

				str = str.concat(new String(reply2.getData()));
				str = str.trim();
				str = str.concat("\n");
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			socket1.close();
			socket2.close();
		}

		lvlLogger.mLogger.info("Get record count query is used, total count is : \n" + str + '\n');
		return str;
	}

	public String editRecord(String recordId, String fieldName, String newValue, String managerId) {

		lvlLogger.mLogger.info(
				managerId + " sent request to edit Record with ID: " + recordId + " new value is: " + newValue + '\n');
		String key;

		if (idToLastName.containsKey(recordId))
			key = idToLastName.get(recordId);
		else {
			lvlLogger.mLogger.info("Record couldn't be updated as record value: " + recordId + " doesnt exist" + "\n");

			return "The given record id invalid";
		}

		StringBuilder output = new StringBuilder();

		// locking the server database for synchronized access
		synchronized (lvlDB) {

			for (Record temp : lvlDB.get(key)) {
				String id = temp.getRecordId();
				if (id.equalsIgnoreCase(recordId)) {
					if (recordId.startsWith("ST")) {
						if (fieldName.equalsIgnoreCase("status")) {
							output.append(printMessage(((Student) temp).getStatus(), newValue));
							((Student) temp).setStatus(newValue);
							lvlLogger.mLogger.info("Record Updated, new value: " + ((Student) temp) + '\n');
						} else if (fieldName.equalsIgnoreCase("statusDate")) {
							output.append(printMessage(((Student) temp).getStatusDate(), newValue));
							((Student) temp).setStatusDate(newValue);
							lvlLogger.mLogger.info("Record Updated, new value: " + ((Student) temp) + '\n');
						} else if (fieldName.equalsIgnoreCase("courseRegistered")) {
							output.append(printMessage(((Student) temp).getCourseRegistered(), newValue));
							((Student) temp).setCourseRegistered(newValue);
							lvlLogger.mLogger.info("Record Updated, new value: " + ((Student) temp) + '\n');
						} else {
							return "The given field name is invalid for student record";
						}
					} else if (recordId.startsWith("TR")) {
						if (fieldName.equalsIgnoreCase("address")) {
							output.append(printMessage(((Teacher) temp).getAddress(), newValue));
							((Teacher) temp).setAddress(newValue);
							lvlLogger.mLogger.info("Record Updated, new value: " + ((Teacher) temp) + '\n');
						} else if (fieldName.equalsIgnoreCase("phone")) {
							output.append(printMessage(((Teacher) temp).getPhone(), newValue));
							((Teacher) temp).setPhone(newValue);
							lvlLogger.mLogger.info("Record Updated, new value: " + ((Teacher) temp) + '\n');
						} else if (fieldName.equalsIgnoreCase("location")) {
							output.append(printMessage(((Teacher) temp).getLocation(), newValue));
							((Teacher) temp).setLocation(newValue);
							lvlLogger.mLogger.info("Record Updated, new value: " + ((Teacher) temp) + '\n');
						} else {
							return "The given field name is invalid for teacher record";
						}
					}
				}
			}
		}

		if (isPrimary) {
			String request = "editRecord" + "|" + recordId + "|" + fieldName + "|" + newValue + "|" + managerId;

			broadcast(request);
		}

		return output.toString();

	}

	public String editListRecord(String recordId, String fieldName, String newValue, String managerId) {

		// lvlLogger.mLogger.info(
		// managerId + " sent request to edit Record with ID: " + recordId + " new value
		// is: " + newValue + '\n');
		// String key;
		// if (idToLastName.containsKey(recordId))
		// key = idToLastName.get(recordId);
		// else
		// return "The given record id doesn't exist";
		StringBuilder output = new StringBuilder();
		// for (Record temp : lvlDB.get(key)) {
		// if (temp.getRecordId() == recordId && recordId.startsWith("ST")
		// && fieldName.equalsIgnoreCase("courseRegistered")) {
		// output.append(printMessage(((Student) temp).getCourseRegistered().toString(),
		// newValue.toString()));
		// ((Student) temp).setCourseRegistered(newValue);
		// lvlLogger.mLogger.info("Record Updated, new value: " + ((Student) temp) +
		// '\n');
		// } else {
		// return "The given field name is invalid for student record";
		// }
		// }

		return output.toString();

	}

	public String printMessage(String str1, String str2) {
		return "Old Value:" + str1 + " " + " New value updated:" + str2;
	}

	public void printHashMap() {
	}

	public boolean transferRecord(String managerId, String recordId, String targetCenterName) {

		lvlLogger.mLogger.info(managerId + " sent request to transfer Record with ID: " + recordId + " to center: "
				+ targetCenterName + '\n');
		String key;
		if (idToLastName.containsKey(recordId))
			key = idToLastName.get(recordId);
		else {
			lvlLogger.mLogger.info("Record cannot be transfered as record value: " + recordId + " doesnt exist" + "\n");
			return false;
		}

		// Creating data for broadcast
		if (isPrimary) {
			String reqToBroadcast;
			reqToBroadcast = "transferRecord" + "|" + recordId + "|" + targetCenterName + "|" + managerId;

			// locking the server database for synchronized access
			synchronized (lvlDB) {

				for (int i = 0; i < lvlDB.get(key).size(); i++) {

					Record temp = lvlDB.get(key).get(i);
					String id = temp.getRecordId();

					if (id.equalsIgnoreCase(recordId)) {

						DatagramSocket socket = null;
						String transferContent = "";

						if (recordId.startsWith("ST")) {
							String courses = ((Student) temp).getCourseRegistered();
							transferContent = id + "|" + ((Student) temp).getFirstName() + "|"
									+ ((Student) temp).getLastName() + "|" + courses + "|"
									+ ((Student) temp).getStatus() + "|" + ((Student) temp).getStatusDate() + "|" + managerId;
						} else if (recordId.startsWith("TR")) {
							transferContent = id + "|" + ((Teacher) temp).getFirstName() + "|"
									+ ((Teacher) temp).getLastName() + "|" + ((Teacher) temp).getAddress() + "|"
									+ ((Teacher) temp).getPhone() + "|" + ((Teacher) temp).getSpecialization() + "|"
									+ ((Teacher) temp).getLocation() + "|" + managerId;
						}

						byte[] message = transferContent.getBytes();

						try {
							lvlLogger.mLogger.info(managerId + " sent request for record transfer" + '\n');
							socket = new DatagramSocket();
							InetAddress address = InetAddress.getByName("localhost");

							DatagramPacket request = new DatagramPacket(message, message.length, address,
									targetCenterName.equalsIgnoreCase("mtl") ? 1234 : 3456);
							socket.send(request);
							lvlLogger.mLogger.info(location + " sever sending request to " + targetCenterName
									+ " sever for record transfer" + '\n');

							byte[] receive = new byte[1000];
							DatagramPacket reply = new DatagramPacket(receive, receive.length);
							socket.receive(reply);
							lvlLogger.mLogger.info(targetCenterName + " server sent response to " + location
									+ " sever regarding the record transfer " + '\n');

							String replyStr = new String(reply.getData()).trim();

							if (replyStr.equals("success")) {

								broadcast(reqToBroadcast);
								lvlDB.get(key).remove(i);
								count--;
								idToLastName.remove(recordId);
								// break the loop if there is a single record in the ArrayList for a key
								if (lvlDB.get(key).isEmpty()) {
									lvlDB.remove(key);
									break;
								}
							} else {
								return false;
							}

						} catch (SocketException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							socket.close();
						}
					}
				}
			}
		} else {
			for (Record rec : lvlDB.get(key)) {
				if (recordId == rec.getRecordId()) {
					lvlDB.get(key).remove(rec);
					break;
				}
			}
		}

		return true;
	}

	public void shutdown() {
		orbLaval.shutdown(false);
	}

	public static void main(String[] args) throws Exception {

		LVLServer1 lvl = new LVLServer1();
		lvl.initializeLogger();
		lvl.InitializeThreads(args);

		LVLFlag = true;
		ArrayList<Record> alRecordInitialLVL = new ArrayList<Record>();
		Record objRecord = new Student("lvlFirstName", "lvlLastName", "maths", "active", "11/11/2015");
		alRecordInitialLVL.add(objRecord);
		lvlDB.put("l", alRecordInitialLVL);
		idToLastName.put("ST10001", "l");
		LVLFlag = false;
		count++;
		
		if (isPrimary) {

			Thread threadSocket = new Thread(new LVLServer1());
			threadSocket.start();

			HeartBeat heartbeatToBackUp2 = new HeartBeat(ServerFrontEnd.portLaval1, ServerFrontEnd.portLaval2);
			heartbeatToBackUp2.startUp();

			HeartBeat heartbeatToBackUp3 = new HeartBeat(ServerFrontEnd.portLaval1, ServerFrontEnd.portLaval3);
			heartbeatToBackUp3.startUp();

		} else {

			// listening to heartbeat from the primary server
			Thread heartbeat = new Thread(new HeartBeat(5001));
			heartbeat.start();

			// check for primary's failure
			Thread t = new Thread(new FailureDetector(5001));
			t.start();
		}

		try {

			ORB orb = ORB.init(args, null);
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			// create servant and register it with the ORB
			lvl.setORB(orb);
			// get object reference from the servant
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(lvl);
			ICenterServer href = ICenterServerHelper.narrow(ref);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			NameComponent path[] = ncRef.to_name(ManagerClient.LVL_SERVER_1);
			ncRef.rebind(path, href);
			System.out.println("Laval Server ready and waiting ...");
			lvl.lvlLogger.mLogger.info("Dummy Student record is created for testing with values: " + objRecord);

			orb.run();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void InitializeThreads(String[] args) {

		if (isPrimary) {
			new PrimaryServerThreadToReceiveResponse(5000).start();
		}

	}
	
	@Override
	public void run() {
		DatagramSocket socket = null;
		try {
			initializeLogger();
			socket = new DatagramSocket(LVLport);
			byte[] get = new byte[1000];
			byte[] send = new byte[1000];

			while (true) {
				DatagramPacket request = new DatagramPacket(get, get.length);
				socket.receive(request);

				String requestContent = new String(request.getData()).trim();
				String[] requestChunks = requestContent.split("\\|");

				if (requestContent.startsWith("ST") || requestContent.startsWith("TR")) {

					String recordId;
					String firstName;
					String lastName;
					Record objRecord;

					switch (requestContent.substring(0, 2)) {
					case "ST":
						recordId = requestChunks[0];
						firstName = requestChunks[1];
						lastName = requestChunks[2];
						String courseRegistered = requestChunks[3];
						String status = requestChunks[4];
						String statusDate = requestChunks[5];
						objRecord = new Student(firstName, lastName, courseRegistered, status, statusDate);

						// use the old recordID
						objRecord.setRecordId(recordId);
						
						// locking the server database for synchronized access
						synchronized (lvlDB) {

							// checking if the key already exists in hash map
							if (lvlDB.containsKey(lastName.substring(0, 1))) {
								lvlDB.get(lastName.substring(0, 1)).add(objRecord);
							} else {
								ArrayList<Record> alRecord = new ArrayList<Record>();
								alRecord.add(objRecord);
								lvlDB.put(lastName.substring(0, 1), alRecord);
							}

							idToLastName.put(objRecord.getRecordId(), lastName.substring(0, 1));

							count++;

							broadcast("createSRecord|" + requestContent);
						}

						// adding the operation to the log file
						lvlLogger.mLogger.info("Student record transfered with values:" + objRecord + '\n');

						break;

					case "TR":
						recordId = requestChunks[0];
						firstName = requestChunks[1];
						lastName = requestChunks[2];
						String address = requestChunks[3];
						String phone = requestChunks[4];
						String specialization = requestChunks[5];
						String location = requestChunks[6];
						objRecord = new Teacher(firstName, lastName, address, phone, specialization, location);

						// use the old recordID
						objRecord.setRecordId(recordId);
						
						// locking the server database for synchronized access
						synchronized (lvlDB) {

							// checking if the key already exists in hash map
							if (lvlDB.containsKey(lastName.substring(0, 1))) {
								lvlDB.get(lastName.substring(0, 1)).add(objRecord);
							} else {
								ArrayList<Record> alRecord = new ArrayList<Record>();
								alRecord.add(objRecord);
								lvlDB.put(lastName.substring(0, 1), alRecord);
							}

							idToLastName.put(objRecord.getRecordId(), lastName.substring(0, 1));

							count++;

							broadcast("createTRecord|" + requestContent);
						}

						// adding the operation to the log file
						lvlLogger.mLogger.info("Teacher record transfered with values: " + objRecord + '\n');
						break;
					}

					send = ("success").getBytes();
					DatagramPacket reply = new DatagramPacket(send, send.length, request.getAddress(),
							request.getPort());
					socket.send(reply);
				} else {
					lvlLogger.mLogger.info("LVL server received request to send record count\n");
					send = (location + " " + count).getBytes();
					DatagramPacket reply = new DatagramPacket(send, send.length, request.getAddress(),
							request.getPort());
					socket.send(reply);
				}
			}
		} catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		} finally {
			socket.close();
		}
	}

	private void broadcast(String request) {
		new ServerMultiCastSender(5001, ServerFrontEnd.lvlHostAddress, request).start();
	}

	@Override
	public void killThreads() {
		// TODO Auto-generated method stub
		
	}
}
