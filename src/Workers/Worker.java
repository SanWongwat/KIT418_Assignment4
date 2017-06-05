package Workers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import SharedObject.*;

public class Worker implements Runnable {

	public static String WORKER_IP = "localhost";
	public static int WORKER_PORT = 1256;
	public static String WORDCOUNTJAR = "mockwordcount.jar";
	public static int INIT_PORT = 1300;
	public static String WORDCOUNTPATH;
	public static String MASTER_IP = "localhost";
	public static int MASTER_PORT = 1250;
	public static final String TAG = "Worker";
	public static String Name;
	public static List<WordCountInstance> processes = new ArrayList<WordCountInstance>();

	public static void main(String[] args) throws IOException {

		if (args.length == 6) {
			MASTER_IP = args[0];
			MASTER_PORT = Integer.parseInt(args[1]);
			WORKER_IP = args[2];
			WORKER_PORT = Integer.parseInt(args[3]);
			WORDCOUNTJAR = args[4];
			INIT_PORT = Integer.parseInt(args[5]);
		} else {
			Utils.Log(TAG, "Invalid arguments");
			return;
		}
		WORDCOUNTPATH = System.getProperty("user.dir") + WORDCOUNTJAR;
		Name = String.format("%s:%s", WORKER_IP, WORKER_PORT);
		Worker worker = new Worker();
		Thread t = new Thread(worker);
		t.start();
		new CheckProcess(MASTER_IP, MASTER_PORT).start();
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				String[] command = in.readLine().split(" ");
				if (command[0].equals("p")) {
					System.out.println("Running " + Worker.processes.size() + " of Word Count process(es).");
					for (WordCountInstance w : processes) {
						System.out.println(String.format("Address: %s:%d", w.getAddress(), w.getPort()));
					}
				}
				if (command[0].equals("c")) {
					for (WordCountInstance w : processes) {
						if (w.getPort() == Integer.parseInt(command[1])) {
							if (w.getProcess().isAlive()) {
								System.out.println("Process is alive.");
							} else {
								System.out.println("Process is dead.");
							}
						}
					}
				}
				else if (command[0].equals("k")) {
					for (WordCountInstance w : processes) {
						if (w.getPasscode().equals(command[1]) ) {
							w.getProcess().destroy();
							Utils.Log(TAG, "Kill process: " + command[1]);
							if (w.getProcess().isAlive()) {
								w.getProcess().destroyForcibly();
							}
						}
					}
				}
				else if (command[0].equals("exit")) {
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		ServerSocket ss = null;
		while (true) {
			if (Utils.isPortAvailble(WORKER_PORT))
				break;
			else
				WORKER_PORT++;

		}
		try {
			ss = new ServerSocket(WORKER_PORT);
			Utils.Log(TAG, String.format("Worker started, the address is:%s:%d", WORKER_IP, WORKER_PORT));
			while (true) {
				Utils.Log(TAG, "Wait for connection.");
				Socket sk = ss.accept();
				new ServiceProvider(sk).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				if (ss != null) {
					ss.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
