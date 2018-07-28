package utility;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.Timer;

import server.DDOServer1;
import server.DDOServer2;
import server.DDOServer3;
import server.LVLServer1;
import server.LVLServer2;
import server.LVLServer3;
import server.MTLServer1;
import server.MTLServer2;
import server.MTLServer3;

public class HeartBeat extends Thread implements ActionListener {

	private int dest;
	private Timer timer;
	private int origin;

	public HeartBeat(int originPortNo, int destPortNo) {
		this.timer = new Timer(5000, this);
		this.origin = originPortNo;
		this.dest = destPortNo;
	}

	public HeartBeat(int port) {
		// TODO Auto-generated constructor stub
		this.origin = port;
	}

	public HeartBeat() {
		// TODO Auto-generated constructor stub
	}

	public void startUp() {
		this.timer.start();
	}

	public void shutDown() {
		this.timer.stop();
	}

	public void sendHeartBeat() {
		DatagramSocket datagramSocket = null;
		try {
			datagramSocket = new DatagramSocket();
			byte[] message = String.valueOf(origin).getBytes();
			InetAddress host = InetAddress.getByName("localhost");

			DatagramPacket heartBeatPacket = new DatagramPacket(message, message.length, host, dest);
			datagramSocket.send(heartBeatPacket);
			System.out.println("heartbeat : " + origin + " is live");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (datagramSocket != null)
				datagramSocket.close();
		}
	}

	public void actionPerformed(ActionEvent e) {
		sendHeartBeat();
	}

	@Override
	public void run() {

		DatagramSocket datagramSocket = null;
		InetAddress inetAddress = null;

		try {
			// create belonging socket
			datagramSocket = new DatagramSocket(origin);
			inetAddress = InetAddress.getByName("localhost");

			byte[] buffer = new byte[500];

			// listening heatBeat
			while (true) {
				DatagramPacket heartBeat = new DatagramPacket(buffer, buffer.length);
				datagramSocket.receive(heartBeat);
				String source = new String(heartBeat.getData());
				System.out.println("FailureDetector:  " + source.trim() + " is alive");
				
				switch(origin)
				{
					case 4111:
						MTLServer1.lastHeartBeatTime1 = System.currentTimeMillis();
						break;
					case 4112:
						MTLServer2.lastHeartBeatTime2 = System.currentTimeMillis();
						break;
					case 4113:
						MTLServer3.lastHeartBeatTime3 = System.currentTimeMillis();
						break;
					case 5001:
						LVLServer1.lastHeartBeatTime1 = System.currentTimeMillis();
						break;
					case 5002:
						LVLServer2.lastHeartBeatTime2 = System.currentTimeMillis();
						break;
					case 5003:
						LVLServer3.lastHeartBeatTime3 = System.currentTimeMillis();
						break;
					case 6001:
						DDOServer1.lastHeartBeatTime1 = System.currentTimeMillis();
						break;
					case 6002:
						DDOServer2.lastHeartBeatTime2 = System.currentTimeMillis();
						break;
					case 6003:
						DDOServer3.lastHeartBeatTime3 = System.currentTimeMillis();
						break;
						
				}
				
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (datagramSocket != null)
				datagramSocket.close();
		}
	}
	public void makeMEsleep()
	{
		try {
			Thread.sleep(200000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
