

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

public class ClientInfoThread implements Runnable{
	Socket s;
	BufferedReader br=null;
	public String content;
	public ClientInfoThread(Socket socket) throws IOException{
		this.s=socket;
		
	}
	public void run(){
		try{
			    OutputStream s1out = s.getOutputStream();
			    DataOutputStream dos = new DataOutputStream (s1out);
			    FileInputStream fstream = new FileInputStream("c:/Textfile1.txt");
			    DataInputStream in = new DataInputStream(fstream);
			    BufferedReader br = new BufferedReader(new InputStreamReader(in));
			    String strLine;
			    
			    //StringBuilder sb =  new StringBuilder();
			    //String lineBreak = System.getProperty("line.separator");
			    
			    while((strLine =br.readLine()) != null) 
			    { 
			    	try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			    	//System.out.print(strLine);
			    	dos.writeUTF(strLine);
			    	
			    	
			    }
		}catch(IOException  e){
			e.printStackTrace();
		}
	}

}
