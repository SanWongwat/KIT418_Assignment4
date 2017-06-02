import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class FITEM implements Runnable{
	public static List<SortObject> object = new ArrayList();
	static class SortObject implements Comparable<SortObject>, Serializable{

        private String key;
        private int value;

        public SortObject(String key, int value) {
            super();
            this.key = key;
            this.value = value;
        }
        @Override
        public int compareTo(SortObject o) {
            
            return o.value - this.value;
        }
    }
	ServerSocket ss;
	int PORT = 1270;
	public static void main(String[] args) throws IOException {
		String k = args[0];
		String ip = args[1];
		String port1 = args[2];
		int PORT1 = Integer.parseInt(port1);
		int k1 =Integer.parseInt(k);
		//int k1=4;
		FITEM f = new FITEM();
		Thread t = new Thread(f);
		t.start();
		
		Socket RecieveStream;
		DataInputStream input;
		try {
			 HashMap<String, Integer> map = new HashMap<String, Integer> ();
			 RecieveStream = new Socket("localhost", PORT1);
			 input = new DataInputStream(RecieveStream.getInputStream());
			 while(true)
			 {
			 String content = input.readUTF();
			 //StringTokenizer text = new StringTokenizer(content);
			 //System.out.println(content);
			  //content = content.trim();
			 //content=content.replaceAll("\\r\\n|\\r|\\n", " ");
			 //content=content.replaceAll("/r", "");
			 String[] word = content.split("\\s+");	
			 
			 int count =0;
			 //int k=1;
			 //while(text.hasMoreTokens()) {
			 int i= 0;
			 while(count< word.length) {	
				// System.out.println("Recieved word-" + word[i] + "g" + word[i].length());
				if( word[i].length() == 0)
				 {
					 i++;
					 count++;
					 continue;
				 }
				 //word[i]= word[i].replaceAll("[^A-Za-z]+", "");
				 if (map.containsKey(word[i]))
				 {
		          int counts =map.get(word[i]) + 1;
		          map.put(word[i], counts);
			   }
				 else {
					 map.put(word[i], 1);
				 }
				 count++;
				 i++;
			 }
			  
			/* for (Map.Entry<String, Integer> entry : map.entrySet()) {
				 
				 System.out.println(entry);
			 }*/
		
		Set set =map.entrySet();
		Iterator g = set.iterator();
		int index = 0;
		SortObject[] objects = new SortObject[map.size()];
		while(g.hasNext())
		{ 
			 Map.Entry e = (Map.Entry)g.next();
			 String tempS = (String) e.getKey();
			 int tempI = (int) e.getValue();
			 objects[index] = new SortObject( tempS, tempI);
			 index++;
		}
		System.out.println();
		Arrays.sort(objects);
		for(int j=0; j< k1; j++)
		{
			SortObject s = new SortObject(objects[j].key, objects[j].value);
//			object[j].key = objects[j].key;
//			object[j].value = objects[j].value;
			object.add(s);
			System.out.println(objects[j].key+ ":" +objects[j].value);
		}
			 }
			 
			
		} catch (IOException e){
			System.out.println(e);
		}
		}
	@Override
	public void run() {
		int flag=0;
		try {
			ss = new ServerSocket(PORT);
			while(true){
				Socket sk = ss.accept();
				DataInputStream dis = new DataInputStream(sk.getInputStream());
				String requestType = dis.readUTF();
				System.out.println(requestType);
				if(!ss.isClosed()){
					  flag=1;
					  // treat a disconnected serverSocket
					}
				//when connect check flag
				
				//if get result
				//send current list to master
				if (flag==1)
				{
				if (requestType.equals("GetResult")) {
					ObjectOutputStream out = new ObjectOutputStream(sk.getOutputStream());
					out.writeObject(object);
				//else
				//
				} }
				else {
					System.out.println("System has lost the connection");
				}
			}
		} 
		
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
		
}