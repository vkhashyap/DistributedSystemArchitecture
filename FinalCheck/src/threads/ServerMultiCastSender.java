package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ServerMultiCastSender extends Thread {

	MulticastSocket multicastsocket;
	InetAddress address;
	String data;
	int portNum;

	public ServerMultiCastSender(int portNum, String hostNameAddress, String request) {
		try {
			this.portNum = portNum;
			multicastsocket = new MulticastSocket(portNum);
			address = InetAddress.getByName(hostNameAddress);
			multicastsocket.joinGroup(address);
			this.data = request;
		} 
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void run() {
		try {
			DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), address, portNum);
			multicastsocket.send(packet);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
