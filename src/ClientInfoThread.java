
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;

import SharedObject.Utils;

public class ClientInfoThread implements Runnable {
	Socket s;
	BufferedReader br = null;
	public String content;
	private final String TAG ="ClientInfoThread";
	private final String file;

	public ClientInfoThread(Socket socket, String pfile) throws IOException {
		this.s = socket;
		file = pfile;

	}

	public void run() {
		try {
			OutputStream s1out = s.getOutputStream();
			DataOutputStream dos = new DataOutputStream(s1out);
			String filepath = System.getProperty("user.dir") + file;
			Utils.Log(TAG, String.format("Text file path: %s", filepath));
			Utils.Log(TAG,"");
			FileInputStream fstream = new FileInputStream(filepath);
			Utils.Log(TAG, "Read file: "+ filepath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// StringBuilder sb = new StringBuilder();
			// String lineBreak = System.getProperty("line.separator");

			Utils.Log(TAG, "Start streaming.");
			while ((strLine = br.readLine()) != null) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// System.out.print(strLine);

				dos.writeUTF(strLine);

			}
			Utils.Log(TAG, "Stop, reached end of file.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
