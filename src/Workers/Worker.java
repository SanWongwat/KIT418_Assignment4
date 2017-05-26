package Workers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import SharedObject.*;

public class Worker {

	public static String SERVER_IP = "localhost";
	public static int PORT_NUMBER = 1256;
	public static List<WordCountInstance> processes = new ArrayList<WordCountInstance>();

	public static void main(String[] args) throws IOException {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(PORT_NUMBER);
			while (true) {
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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}
