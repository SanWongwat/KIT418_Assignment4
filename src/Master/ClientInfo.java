package Master;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientInfo {
	private String Passcode;
	private Date StartTime;
	private Date StopTime;

	public ClientInfo(String pPasscode) {
		// TODO Auto-generated constructor stub
		Passcode = pPasscode;
	}

	public String getPasscode() {
		return this.Passcode;
	}

	public Date getStartTime() {
		return this.StartTime;
	}

	public void StartService() {
		this.StartTime = new Date();
	}

	public Date getStopTime(){
		return this.StopTime;
	}

	public void StopService() {
		this.StopTime = new Date();
	}

}
