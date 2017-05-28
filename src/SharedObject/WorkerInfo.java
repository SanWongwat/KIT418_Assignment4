package SharedObject;

public class WorkerInfo {

	private String Address;
	private String Name;
	private int Port;
	private long CPUUsage;
	private long MemoryUsage;
	private int NumberOfProcess;

	public WorkerInfo() {
		// TODO Auto-generated constructor stub
	}
	public String getName() {
		return this.Name;
	}

	public void setName(String value) {
		this.Name = value;
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

	public long getCPUUsage() {
		return this.CPUUsage;
	}

	public void setCPUUsge(long value) {
		this.CPUUsage = value;
	}

	public long getMemoryUsage() {
		return this.MemoryUsage;
	}

	public void setMemoryUsage(long value) {
		this.MemoryUsage = value;
	}

	public int getNumberOfProcess() {
		return this.NumberOfProcess;
	}

	public void setNumberOfProcess(int value) {
		this.NumberOfProcess = value;
	}

}
