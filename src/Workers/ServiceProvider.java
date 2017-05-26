package Workers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import SharedObject.ServiceEnum;
import SharedObject.Utils;
import SharedObject.WordCountInstance;

public class ServiceProvider extends Thread {

	private Socket _mSocket;
	private DataInputStream _dis;
	private DataOutputStream _dos;
	private String[] _requestArr;
	private String TAG = "ServiceProvider";

	public ServiceProvider(Socket sk) {
		super();
		_mSocket = sk;
	}

	public void run() {
		try {
			_dis = new DataInputStream(_mSocket.getInputStream());
			_dos = new DataOutputStream(_mSocket.getOutputStream());
			_requestArr = _dis.readUTF().split(",");
			ServiceEnum sType = ServiceEnum.valueOf(_requestArr[0]);
			switch (sType) {
			case StartService:
				StartService();
				break;
			case StopService:
				// Stop service
				StopService(_requestArr[1]);
				break;
			case CheckStatus:
				GetStatus();
			default:
				break;
			}
			if (_dos != null) {
				_dos.close();
			}
			if (_dis != null) {
				_dis.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void StartService() throws IOException {
		// Start service
		String passcode = _requestArr[1];

		List<String> command = new ArrayList<String>();
		command.add("java");
		command.add("-jar");
		// path of jar file
		command.add("\\");
		// args
		command.add(passcode);

		int portNo = 0;
		for (int i = 0; i < Worker.processes.size(); i++) {
			if (Worker.processes.get(i).getPort() > portNo) {
				portNo = Worker.processes.get(i).getPort();
			}
		}
		portNo++;

		command.add(String.valueOf(portNo));
		// create new word count instance
		WordCountInstance wc = new WordCountInstance(passcode);
		wc.setAddress(Worker.SERVER_IP);
		wc.setPort(portNo);
		wc.StartService();
		ProcessBuilder pb = new ProcessBuilder(command);
		Process p = pb.start();
		wc.setProcess(p);
		Worker.processes.add(wc);
		_dos.writeUTF(String.valueOf(portNo));
		Utils.Log(TAG, "Start new word count instance");
	}

	public void StopService(String passcode) throws IOException {
		Utils.Log(TAG, "Stopping service: " + passcode);
		WordCountInstance wc = null;
		for (WordCountInstance w : Worker.processes) {
			if (w.getPasscode().equals(passcode)) {
				Process p = w.getProcess();
				if (p.isAlive()) {
					// nicely terminate process
					p.destroy();
					if (p.isAlive()) {
						// still alive? kill it
						p.destroyForcibly();
					}
				}
				if (!p.isAlive()) {
					Utils.Log(TAG, "Stopping service: " + passcode + " successfully.");
					wc = w;
					_dos.writeUTF(ServiceEnum.OK.toString());
					break;
				} else {
					_dos.writeUTF(String.format("%s,%s", ServiceEnum.Error.toString(), "Cannot stop process"));

				}
			}
		}
		Worker.processes.remove(wc);
	}

	public void GetStatus() throws IOException {
		Utils.Log(TAG, "Master check mem status and number of process");
		Utils.Log(TAG, Runtime.getRuntime().freeMemory() + "," + Worker.processes.size());

		String response = Runtime.getRuntime().freeMemory() + "," + Worker.processes.size();
		_dos.writeUTF(response);
		// // check workload (for master to use)
		// /* Total number of processors or cores available to the JVM */
		// System.out.println("Available processors (cores): " +
		// Runtime.getRuntime().availableProcessors());
		//
		// /* Total amount of free memory available to the JVM */
		// System.out.println("Free memory (bytes): " +
		// Runtime.getRuntime().freeMemory());
		//
		// /* This will return Long.MAX_VALUE if there is no preset limit */
		// long maxMemory = Runtime.getRuntime().maxMemory();
		// /* Maximum amount of memory the JVM will attempt to use */
		// System.out.println("Maximum memory (bytes): " + (maxMemory ==
		// Long.MAX_VALUE ? "no limit" : maxMemory));
		//
		// /* Total memory currently available to the JVM */
		// System.out.println("Total memory available to JVM (bytes): " +
		// Runtime.getRuntime().totalMemory());
		//
		// /* Get a list of all filesystem roots on this system */
		// File[] roots = File.listRoots();
		//
		// /* For each filesystem root, print some info */
		// for (File root : roots) {
		// System.out.println("File system root: " + root.getAbsolutePath());
		// System.out.println("Total space (bytes): " + root.getTotalSpace());
		// System.out.println("Free space (bytes): " + root.getFreeSpace());
		// System.out.println("Usable space (bytes): " + root.getUsableSpace());
		// }
	}

}
