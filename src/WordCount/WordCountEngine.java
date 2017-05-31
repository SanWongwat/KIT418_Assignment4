package WordCount;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
		int k=Integer.parseInt(args[1]);
		k=3;
		try{
			//ss = new ServerSocket(PORT_NUMBER);
			//System.out.println("WordCountEngine start at port 1254");
			//start listen to the stream generator
			Socket socket=new Socket("localhost",PORT_NUMBER);
			br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			int number=0;
			while(br.readLine()!=null){
				String content=br.readLine();
				System.out.println(content);
				// for each content , done the calculation.
				//if the string already exists, done the following
				
				if(resultSet.keySet().contains(content))
			     {
			          Integer count = resultSet.get(content) + 1;
			          resultSet.put(content, count);
			     }
				
			     else if(number<k){
			          resultSet.put(content, 1);
			          number=number+1;

			      }else{
			    	  Iterator<String> it=resultSet.keySet().iterator();
			    	  while(it.hasNext()){
			    		  Integer count=resultSet.get(it.next())-1;
			    		  if(count>0){
			    		  resultSet.put(it.next(), count);
			    		  }else{
			    			  resultSet.remove(it.next());
			    		  }
			    	  }
			      }
			}
			
			
			
			
			
		}
		catch(Exception ex){
			
		}
	}

}
