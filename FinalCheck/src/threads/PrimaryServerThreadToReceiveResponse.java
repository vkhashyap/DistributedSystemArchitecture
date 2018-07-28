package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class PrimaryServerThreadToReceiveResponse extends Thread {

	DatagramSocket dgsocket;

	public PrimaryServerThreadToReceiveResponse(int portNum) {
		// TODO Auto-generated constructor stub

		try {
			dgsocket = new DatagramSocket(portNum);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void run() {
		while (true) {
			byte[] mydata = new byte[100];
			DatagramPacket packet = new DatagramPacket(mydata, mydata.length);
			try {
				dgsocket.receive(packet);
		
				if (new String(packet.getData()).trim().equals("success")) {
					// need to add log here for primary server
					//System.out.println("success");
				} else {
					// need to add log here for primary server
					//System.out.println("failure");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
