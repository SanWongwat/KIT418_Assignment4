package StreamGenerator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class StreamEngine {
	public static  String Server_HOSTNAME;
    public static  int SERVER_PORT;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//1 read a file line by line 
		// output them to a socket.
		PrintWriter out=null;
		    try{
			FileReader fr=new FileReader("C:\\Users\\caihu\\OneDrive\\Documents\\KIT418_Assignment4\\src\\StreamGenerator\\string.txt");
			char[] cbuf=new char[32];
			int hasRead=0;
			Socket socket=new Socket(Server_HOSTNAME,SERVER_PORT);
			out=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			while((hasRead=fr.read(cbuf))>0){
				System.out.println(new String(cbuf,0,hasRead));
				Thread.sleep(1000);
				out.println(new String(cbuf,0,hasRead));
				out.flush();
			}
		    }catch(FileNotFoundException e){
		    	e.printStackTrace();
		    }catch(IOException e){
		    	e.printStackTrace();
		    } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   
	}

}
