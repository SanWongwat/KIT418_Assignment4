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
	public static final String TAG = "Worker";
	public static String Name;
	public static List<WordCountInstance> processes = new ArrayList<WordCountInstance>();

	public static void main(String[] args) throws IOException {

		if (args.length == 4) {
			WORKER_IP = args[0];
			WORKER_PORT = Integer.parseInt(args[1]);
			WORDCOUNTJAR = args[2];
			INIT_PORT = Integer.parseInt(args[3]);
		}
		WORDCOUNTPATH = System.getProperty("user.dir") + WORDCOUNTJAR;
		Name=String.format("%s:%s", WORKER_IP,WORKER_PORT);
		Worker worker = new Worker();
		Thread t = new Thread(worker);
		t.start();
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
				if(command[0].equals("c")){
					for (WordCountInstance w: processes){
						if(w.getPort() == Integer.parseInt(command[1])){
							if(w.getProcess().isAlive()){
								System.out.println("Process is alive.");
							}
							else{
								System.out.println("Process is dead.");
							}
						}
					}
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
			if (isPortAvialble(WORKER_PORT))
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
