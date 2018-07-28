package utility;

import server.DDOServer3;
import server.LVLServer3;
import server.MTLServer1;
import server.MTLServer2;
import server.MTLServer3;

public class Record {
	
	protected String recordId;
	protected String firstName;
	protected String lastName;
	static int id = 10003;
	
	public Record(String firstName, String lastName, String prefix)
	{
		this.firstName = firstName;
		this.lastName = lastName;
		if(MTLServer1.MTLFlag == true || MTLServer2.MTLFlag == true || MTLServer3.MTLFlag == true) setRecordId(prefix + 10000);
		//else if(LVLServer1.LVLFlag == true || LVLServer2.LVLFlag == true || LVLServer3.LVLFlag.MTLFlag == true) setRecordId(prefix + 10001);
		//else if(DDOServer1.DDOFlag == true || DDOServer2.DDOFlag == true || DDOServer3.DDOFlag == true) setRecordId(prefix + 10002);
		else 
			{setRecordId(prefix + id);
		id++;}
	}


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getRecordId() {
		return recordId;
	}


	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}	
}