package SharedObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class WordCountInstance {
	private String Address;
	private int Port;
	private String Passcode;
	private Date StartTime;
	private Date StopTime;
	private Process Process;

	public WordCountInstance(String pPasscode) {
		// TODO Auto-generated constructor stub
		Passcode = pPasscode;
	}

	public String getAddress() {
		return this.Address;
	}

	public void setAddress(String value) {
		this.Address = value;
	}

	public int getPort() {
		return this.Port;
	}

	public void setPort(int value) {
		this.Port = value;
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

	public Date getStopTime() {
		return this.StopTime;
	}

	public void StopService() {
		this.StopTime = new Date();
	}
	public Process getProcess(){
		return this.Process;
	}
	public void setProcess(Process value){
		this.Process = value;
	}

}