package WordCount;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WordCountEngine {

	public static List<Word> wordList = new ArrayList<Word>();
	private static int PORT_NUMBER = 1254;
	public static BufferedReader br=null;
	//this is the result kept and calculation place.
	public static  HashMap<String,Integer> resultSet=new HashMap<String,Integer>();
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//ServerSocket ss;
		try{
			//ss = new ServerSocket(PORT_NUMBER);
			//System.out.println("WordCountEngine start at port 1254");
			//start listen to the stream generator
			Socket socket=new Socket("localhost",PORT_NUMBER);
			br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(br.readLine()!=null){
				String conent=br.readLine();
				System.out.println(conent);
				// for each content , done the calculation.
			}
			
			
			
		}
		catch(Exception ex){
			
		}
	}

}
