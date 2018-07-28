package utility;

import server.DDOServer1;
import server.DDOServer2;
import server.LVLServer1;
import server.LVLServer2;
import server.MTLServer1;
import server.MTLServer2;
import server.MTLServer3;
import server.ServerFrontEnd;

public class FailureDetector extends Thread {
	
	//private long timeOfLastMessage;
	int server;
	static boolean electionInProgress = false;
	
	public FailureDetector(int port) {
		this.server = port;
		//this.timeOfLastMessage = time;
	}
	

	public void run() {
		while(true) {
			if(!electionInProgress) {
//				System.out.println("electing algo running");
				switch(server)
				{
					case 4111:
						if (System.currentTimeMillis() - MTLServer1.lastHeartBeatTime1 > 5000) {
							System.out.println("electing mtl1 as primary");
							electionInProgress = true;
							
							Thread receive = new Thread(new bullyElection(4102));
							receive.start();
							
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							// failure occurs, detected by MTLServer1
							bullyElection obj = new bullyElection(4101);
							obj.startElection();
							
						}
						break;
					case 4112:
						if (System.currentTimeMillis() - MTLServer2.lastHeartBeatTime2 > 5000) {
							
							System.out.println("electing mtl2 as primary");
							electionInProgress = true;
							
							Thread receive = new Thread(new bullyElection(4101));
							receive.start();
							
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							// failure occurs, detected by MTLServer1
							bullyElection obj = new bullyElection(4102);
							obj.startElection();
							
							
						}
						break;
					case 5001:
						if (System.currentTimeMillis() - LVLServer1.lastHeartBeatTime1 > 5000) {
							System.out.println("electing lvl1 as primary");
							electionInProgress = true;
						
						Thread receive = new Thread(new bullyElection(5102));
						receive.start();
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							
							// failure occurs, detected by MTLServer1
							bullyElection obj = new bullyElection(5101);
							obj.startElection();
						}
						break;
					case 5002:
						if (System.currentTimeMillis() - LVLServer2.lastHeartBeatTime2 > 5000) {
							System.out.println("electing LVL2 as primary");
							electionInProgress = true;
						
						Thread receive = new Thread(new bullyElection(5101));
						receive.start();
						
						try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
							
							// failure occurs, detected by MTLServer1
							bullyElection obj = new bullyElection(5102);
							obj.startElection();
						}
						break;
					case 6001:
						if (System.currentTimeMillis() - DDOServer1.lastHeartBeatTime1 > 5000) {
						System.out.println("electing ddo1 as primary");
						electionInProgress = true;
						
						Thread receive = new Thread(new bullyElection(6102));
						receive.start();
						
						try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
						// failure occurs, detected by MTLServer1
						bullyElection obj = new bullyElection(6101);
						obj.startElection();
					}
						break;
					case 6002:
						if (System.currentTimeMillis() - DDOServer2.lastHeartBeatTime2 > 5000) {
						System.out.println("electing ddo2 as primary");
						electionInProgress = true;
						
						Thread receive = new Thread(new bullyElection(6101));
						receive.start();
						
						try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
						// failure occurs, detected by MTLServer1
						bullyElection obj = new bullyElection(6102);
						obj.startElection();
					}
						break;
						
				}
			}
			
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
