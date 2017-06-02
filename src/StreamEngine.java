

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class StreamEngine {
	public static String Server_HOSTNAME = "localhost";
	public static int SERVER_PORT = 1254;
	public static ArrayList<Socket> socketList=new ArrayList<Socket>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 1 read a file line by line
		// output them to a socket.
		ServerSocket serversocket=null;
		try {
			
			serversocket=new ServerSocket(SERVER_PORT);
			while(true){
				Socket ss=serversocket.accept();
				socketList.add(ss);
				new Thread(new ClientInfoThread(ss)).start();
			}
			
			
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 

	}

}
