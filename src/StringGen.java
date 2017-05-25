import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class StringGen {
	public static void main(String[] args) throws IOException {
		ServerSocket s = new ServerSocket(1254);
	    System.out.println("Server started");
	    Socket s1 =s.accept();
	    OutputStream s1out = s1.getOutputStream();
	    DataOutputStream dos = new DataOutputStream (s1out);
	    //DataOutputStream out = new DataOutputStream(s1.getOutputStream());
	    FileInputStream fstream = new FileInputStream("c:/Textfile1.txt");
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;
	    
	    //StringBuilder sb =  new StringBuilder();
	    //String lineBreak = System.getProperty("line.separator");
	    while((strLine =br.readLine()) != null) 
	    { 
	    	//System.out.print(strLine);
	    	dos.writeUTF(strLine);
	    	
	    }
	    fstream.close();
	    
	    
	    
	    
	
}
}
