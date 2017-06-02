package StreamGenerator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;

public class ClientInfoThread implements Runnable{
	Socket s;
	BufferedReader br=null;
	public String content;
	public ClientInfoThread(Socket socket) throws IOException{
		this.s=socket;
		
	}
	public void run(){
		try{
			URL path = StreamEngine.class.getResource("intimeof.txt");
			FileReader fr = new FileReader(path.getFile());
			char[] cbuf = new char[32];
			int hasRead = 0;
			DataOutputStream _dos = new DataOutputStream(s.getOutputStream());
			System.out.println("Streaming content...");
			while ((hasRead = fr.read(cbuf)) > 0) {
				//_dos.writeUTF(new String(cbuf, 0, hasRead));
				System.out.println(new String(cbuf, 0, hasRead));
				Thread.sleep(2000);
			}
		}catch(IOException | InterruptedException e){
			e.printStackTrace();
		}
	}

}
