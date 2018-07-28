package server;

import java.util.HashMap;

import CenterServerModule.ICenterServer;

public class implServers extends Thread{

	public static HashMap<String, ICenterServer> primarySeverHashMap = new HashMap<String, ICenterServer>();
	public static HashMap<String, ICenterServer> secondarySeverHashMap = new HashMap<String, ICenterServer>();
	public static HashMap<String, ICenterServer> tertiaryHashMap = new HashMap<String, ICenterServer>();
	
	public implServers(HashMap<String, ICenterServer> primarySeverHashMap, HashMap<String, ICenterServer> secondarySeverHashMap, HashMap<String, ICenterServer> tertiaryHashMap) {
		// TODO Auto-generated constructor stub
		implServers.primarySeverHashMap = primarySeverHashMap;
		implServers.secondarySeverHashMap = secondarySeverHashMap;
		implServers.tertiaryHashMap = tertiaryHashMap;
	}

	public void run() {
		while(true) {
			System.out.println(secondarySeverHashMap.size());
			System.out.println(secondarySeverHashMap.get("MTL"));
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public implServers() {
		// TODO Auto-generated constructor stub
	}

	public ICenterServer getsecondaryMontreal(String loc) {
		
		
		// TODO Auto-generated method stub
		return secondarySeverHashMap.get(loc);
	}
}
