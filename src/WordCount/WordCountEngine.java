package WordCount;

import java.net.ServerSocket;
import java.net.Socket;

public class WordCountEngine {

	private static int PORT_NUMBER = 1254;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServerSocket ss;
		try{
			ss = new ServerSocket(PORT_NUMBER);
			System.out.println("WordCountEngine start at port 1254");
			//start listen to the stream generator
			while(true){
				Socket sk = ss.accept();
				System.out.println("Connected to ...");
				new WordCounterThread(sk).start();
				
			}
		}
		catch(Exception ex){
			
		}
	}

}
