package StreamGenerator;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

public class StreamEngine {
	public static String Server_HOSTNAME = "localhost";
	public static int SERVER_PORT = 1254;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 1 read a file line by line
		// output them to a socket.

		try {
			URL path = StreamEngine.class.getResource("intimeof.txt");
			FileReader fr = new FileReader(path.getFile());
			char[] cbuf = new char[32];
			int hasRead = 0;
			Socket socket = new Socket(Server_HOSTNAME, SERVER_PORT);
			DataOutputStream _dos = new DataOutputStream(socket.getOutputStream());
			System.out.println("Streaming content...");
			while ((hasRead = fr.read(cbuf)) > 0) {
				_dos.writeUTF(new String(cbuf, 0, hasRead));
				//System.out.println(new String(cbuf, 0, hasRead));
				Thread.sleep(2000);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
