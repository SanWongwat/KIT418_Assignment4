package Master;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import SharedObject.*;

public class Master {

	private static int SERVER_PORT = 1255;
	public static List<WorkerInfo> listWorker = new ArrayList<WorkerInfo>();
	public static int MAX_PROCESS = 5;
	public static List<WordCountInstance> listWCInstance = new ArrayList<WordCountInstance>();
	private static final String TAG = "Master";
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServerSocket ss = null;
		
		try {
			Utils.Log(TAG, "Start Master.");
			ss = new ServerSocket(SERVER_PORT);
			while (true) {
				Socket sk = ss.accept();
				Utils.Log(TAG, "Client has connected.");
				new RequestHandler(sk).start();

			}
			// Socket sk = new Socket("localhost", 1256);
			// DataInputStream dis = new DataInputStream(sk.getInputStream());
			// DataOutputStream dos = new
			// DataOutputStream(sk.getOutputStream());
			// dos.writeUTF("4");
		} catch (IOException e) {
			e.printStackTrace();
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
