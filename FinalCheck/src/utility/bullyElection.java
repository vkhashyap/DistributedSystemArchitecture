package utility;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import server.DDOServer1;
import server.DDOServer2;
import server.LVLServer1;
import server.LVLServer2;
import server.MTLServer1;
import server.MTLServer2;
import server.MTLServer3;

public class bullyElection extends Thread {
	
	private int port;
	int dest;

	public bullyElection(int port) {
		this.port = port;
	}
	
	public void startElection() {
		System.out.println("sending election message");
		DatagramSocket datagramSocket = null;
		try {
			datagramSocket = new DatagramSocket();
			byte[] message = String.valueOf(port).getBytes();
			InetAddress host = InetAddress.getByName("localhost");
			
			switch(port)
			{
				case 4101:
					dest = 4102;
					break;
				case 4102:
					dest = 4101;
					break;
				case 5101:
					dest = 5102;
					break;
				case 5102:
					dest = 5101;
					break;
				case 6101:
					dest = 6102;
					break;
				case 6102:
					dest = 6101;
					break;
					
			}

			DatagramPacket heartBeatPacket = new DatagramPacket(message, message.length, host, dest);
			datagramSocket.send(heartBeatPacket);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (datagramSocket != null)
				datagramSocket.close();
		}
	}
	
	@Override
	public void run() {
		System.out.println("receiving election message");
		DatagramSocket datagramSocket = null;
		InetAddress inetAddress = null;

		try {
			// create belonging socket
			datagramSocket = new DatagramSocket(port);
			System.out.println();
			inetAddress = InetAddress.getByName("localhost");

			byte[] buffer = new byte[500];

			// listening heatBeat
			while (true) {
				DatagramPacket heartBeat = new DatagramPacket(buffer, buffer.length);
				datagramSocket.receive(heartBeat);
				String source = new String(heartBeat.getData()).trim();
				int origin = Integer.parseInt(source);
				//System.out.println("FailureDetector:  " + source.trim() + " is alive");

				if (origin > this.port) {
					//origin is the new primary
					
					switch(origin)
					{
						case 4101:
							MTLServer1.togglePrimary();
							break;
						case 4102:
							MTLServer2.togglePrimary();
							break;
						case 5101:
							LVLServer1.togglePrimary();
							break;
						case 5102:
							LVLServer2.togglePrimary();
							break;
						case 6101:
							DDOServer1.togglePrimary();
							break;
						case 6102:
							DDOServer2.togglePrimary();
							break;
							
					}
					
				} else {
					//this port starts election
					//temp
					switch(this.port)
					{
						case 4101:
							MTLServer1.togglePrimary();
							break;
						case 4102:
							MTLServer2.togglePrimary();
							break;
						case 5101:
							LVLServer1.togglePrimary();
							break;
						case 5102:
							LVLServer2.togglePrimary();
							break;
						case 6101:
							DDOServer1.togglePrimary();
							break;
						case 6102:
							DDOServer2.togglePrimary();
							break;
							
					}
				}
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (datagramSocket != null)
				datagramSocket.close();
		}
	}
}
