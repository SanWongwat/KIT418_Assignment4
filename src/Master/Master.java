package Master;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import SharedObject.*;

public class Master implements Runnable {

	private static int MASTER_PORT = 1255;
	public static List<WorkerInfo> listWorker = new ArrayList<WorkerInfo>();
	private static String INIT_WORKERFILE = "\\workerlist.txt";
	public static int MAX_PROCESS = 5;
	public static List<WordCountInstance> listWCInstance = new ArrayList<WordCountInstance>();
	private static final String TAG = "Master";

	public static void main(String[] args) {
		if (args.length == 3) {
			MASTER_PORT = Integer.parseInt(args[0]);
			MAX_PROCESS = Integer.parseInt(args[1]);
			INIT_WORKERFILE = args[2];
		} else {
			Utils.Log(TAG, "Invalid arguments");
			return;
		}
		try (BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + INIT_WORKERFILE))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				String[] workerArr = sCurrentLine.split(":");
				WorkerInfo w = new WorkerInfo();
				w.setAddress(workerArr[0]);
				w.setPort(Integer.parseInt(workerArr[1]));
				w.setName(String.format("%s:%s", w.getAddress(), w.getPort()));
				listWorker.add(w);
			}

		} catch (IOException e) {
			Utils.Log(TAG, "Cannot find workerlist.txt");
			Utils.Log(TAG, "Exit.");
			return;
		}

		Master master = new Master();
		Thread t = new Thread(master);
		t.start();
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				String command = in.readLine();
				if (command.equals("w")) {
					for (WorkerInfo w : listWorker) {
						System.out.println(String.format("Address: %s:%d", w.getAddress(), w.getPort()));
					}
				} else if (command.equals("i")) {
					for (WordCountInstance w : listWCInstance) {
						System.out.println(String.format("Address: %s:%d", w.getAddress(), w.getPort()));
					}
				} else if (command.equals("exit")) {
					break;
				}
			} catch (IOException e) {
			}
		}
	}

	@Override
	public void run() {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(MASTER_PORT);
			Utils.Log(TAG, "Start Master");
			while (true) {
				Socket sk = ss.accept();
				new RequestHandler(sk).start();

			}
		} catch (IOException e) {
			Utils.Log(TAG, "Client disconnected.");
			try {
				if (ss != null) {
					ss.close();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}
