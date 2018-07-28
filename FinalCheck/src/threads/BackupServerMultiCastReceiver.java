package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import CenterServerModule.ICenterServer;
import CenterServerModule.ICenterServerHelper;
import CenterServerModule.ICenterServerOperations;
import server.MTLServer2;
import utility.Record;

public class BackupServerMultiCastReceiver extends Thread {

	MulticastSocket multicastsocket;
	InetAddress address;
	public ICenterServer serverImpl;
	private int returnPort;

	public BackupServerMultiCastReceiver(int portNum, String hostAddress, ICenterServer serverRef) {
		// TODO Auto-generated constructor stub

		try {
			multicastsocket = new MulticastSocket(portNum);
			address = InetAddress.getByName(hostAddress);
			multicastsocket.joinGroup(address);
			this.serverImpl = serverRef;
			getPort(portNum);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public void getPort(int portNo) {
		if (String.valueOf(portNo).charAt(0) == '4') {
			returnPort = 4000;
		} else if (String.valueOf(portNo).charAt(0) == '5') {
			returnPort = 5000;
		} else if (String.valueOf(portNo).charAt(0) == '6') {
			returnPort = 6000;
		}
	}

	public void run() {
		boolean response = false;
		while (true) {
			byte[] mydata = new byte[100];
			DatagramPacket packet = new DatagramPacket(mydata, mydata.length);
			try {
				multicastsocket.receive(packet);
				String requestContent = new String(packet.getData()).trim();
				String[] requestChunks = requestContent.split("\\|");

				String recordId;
				String firstName;
				String lastName;
				String managerId;
				String editResponse = "invalid";

				switch (requestChunks[0]) {
				case "createTRecord":
					recordId = requestChunks[1];
					firstName = requestChunks[2];
					lastName = requestChunks[3];
					String address = requestChunks[4];
					String phone = requestChunks[5];
					String specialization = requestChunks[6];
					String location = requestChunks[7];
					managerId = requestChunks[8] + "|" + recordId;

					response = serverImpl.createTRecord(firstName, lastName, address, phone, specialization, location,
							managerId);

					break;

				case "createSRecord":
					recordId = requestChunks[1];
					firstName = requestChunks[2];
					lastName = requestChunks[3];
					String courseRegistered = requestChunks[4];
					String status = requestChunks[5];
					String statusDate = requestChunks[6];
					managerId = requestChunks[7] + "|" + recordId;
					response = serverImpl.createSRecord(firstName, lastName, courseRegistered, status, statusDate,
							managerId);
					break;

				case "editRecord":
					recordId = requestChunks[1];
					String fieldName = requestChunks[2];
					String newValue = requestChunks[3];
					managerId = requestChunks[4] + "|" + recordId;
					editResponse = serverImpl.editRecord(recordId, fieldName, newValue, managerId);
					break;

				case "transferRecord":
					recordId = requestChunks[1];
					String locToSend = requestChunks[2];
					managerId = requestChunks[3];
					response = serverImpl.transferRecord(managerId, recordId, locToSend);
					break;

				default:
					break;
				}

				DatagramSocket dgsocket = new DatagramSocket();
				byte[] send = new byte[1000];
				if (response || !editResponse.contains("invalid")) {
					send = ("success").getBytes();
					//System.out.println("Success");
				} else {
					send = ("failure").getBytes();
				}

				DatagramPacket dp = new DatagramPacket(send, send.length,
						InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()), returnPort);
				dgsocket.send(dp);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
