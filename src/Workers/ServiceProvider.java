package Workers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
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
	private String TAG = "ServiceProvider";

	public ServiceProvider(Socket sk) {
		super();
		_mSocket = sk;
	}

	public void run() {
		try {
			Utils.Log(TAG, "New Request");
			_dis = new DataInputStream(_mSocket.getInputStream());
			_dos = new DataOutputStream(_mSocket.getOutputStream());
			String[] requestArr = _dis.readUTF().split(",");
			ServiceEnum sType = ServiceEnum.valueOf(requestArr[0]);
			switch (sType) {
			case StartService:
				StartService(requestArr[1], requestArr[2]);
				break;
			case StopService:
				StopService(requestArr[1]);
				break;
			case CheckStatus:
				GetStatus();
				break;
			default:
				break;
			}
			if (_dos != null) {
				_dos.close();
			}
			if (_dis != null) {
				_dis.close();
			}
			if (_mSocket != null) {
				_mSocket.close();
			}

		} catch (EOFException e) {
			Utils.Log(TAG, "Communication has ended.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void StartService(String passcode, String kvalue) throws IOException {
		// Start service
		Utils.Log(TAG, "Starting instance...");

		List<String> command = new ArrayList<String>();
		command.add("java");
		command.add("-jar");
		// path of jar file
		command.add(Worker.WORDCOUNTPATH);
		int portNo = Worker.INIT_PORT;
		while (true) {
			if (isPortAvialble(portNo)) {
				break;
			} else {
				portNo++;
			}
		}
		command.add(String.valueOf(portNo));
		command.add(kvalue);
		// create new word count instance
		WordCountInstance wc = new WordCountInstance(passcode);
		wc.setAddress(Worker.WORKER_IP);
		wc.setPort(portNo);
		wc.StartService();
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectOutput(new File("D:\\output.txt"));
		Process p = pb.start();
		wc.setProcess(p);
		Worker.processes.add(wc);
		_dos.writeUTF(String.valueOf(portNo));
		Utils.Log(TAG, "New wordcount instance started at port :" + portNo);
	}

	public void StopService(String passcode) throws IOException {
		Utils.Log(TAG, "Stopping service: " + passcode);
		boolean isOk = false;
		WordCountInstance wc = null;
		for (WordCountInstance w : Worker.processes) {
			if (w.getPasscode().equals(passcode)) {
				Process p = w.getProcess();
				if (p.isAlive()) {
					p.destroy();
					if (p.isAlive()) { 
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
		if (isOk) {
			Worker.processes.remove(wc);
		}
	}

	public void GetStatus() throws IOException {
		Utils.Log(TAG, "Master check number of processes.");
		// Utils.Log(TAG, Runtime.getRuntime().freeMemory() + "," +
		// Worker.processes.size());

		String response = Runtime.getRuntime().freeMemory() + "," + Worker.processes.size();
		_dos.writeUTF(response);
	}

	public boolean isPortAvialble(int port) {
		ServerSocket s = null;
		try {
			s = new ServerSocket(port);
			s.close();
			return true;
		} catch (IOException ex) {
			return false;
		}
	}
}
