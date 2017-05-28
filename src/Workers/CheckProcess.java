package Workers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import SharedObject.*;

public class CheckProcess extends Thread {

	private String masterIP;
	private int masterPort;

	private final String TAG = "CheckProcess";

	public CheckProcess(String pmasterIP, int pmasterPort) {
		super();
		masterIP = pmasterIP;
		masterPort = pmasterPort;
	}

	public void run() {
		while (true) {
			try {
				// check every minutes
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Utils.Log(TAG, "Check process...");
			List<WordCountInstance> listupdate = new ArrayList<WordCountInstance>();
			try {
				List<WordCountInstance> list = Worker.processes;
				for (WordCountInstance wi : list) {
					boolean isClose = false;
					try (Socket sk = new Socket(Worker.WORKER_IP, wi.getPort());) {
						DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
						dos.writeUTF(ServiceEnum.CheckStatus.toString());
						sk.close();
					} catch (UnknownHostException e) {
						isClose = true;
					} catch (IOException e) {
						isClose = true;
					}
					if (isClose) {
						int port = wi.getPort();
						while (true) {
							if (Utils.isPortAvailble(port)) {
								break;
							} else {
								port++;
							}

						}
						List<String> command = new ArrayList<String>();
						command.add("java");
						command.add("-jar");
						// path of jar file
						command.add(Worker.WORDCOUNTPATH);
						command.add(String.valueOf(port));
						ProcessBuilder pb = new ProcessBuilder(command);
						Process p = null;
						p = pb.start();
						wi.setProcess(p);
						wi.setPort(port);
						listupdate.add(wi);
						Utils.Log(TAG, "Restart process: " + wi.getPasscode());
					}
				}
				if (listupdate.size() > 0) {
					Socket sk = new Socket(masterIP, masterPort);
					DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
					dos.writeUTF(ServiceEnum.Sync.toString());
					ObjectOutputStream out = new ObjectOutputStream(sk.getOutputStream());
					out.writeObject(listupdate);
					out.close();
					sk.close();
				}

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
