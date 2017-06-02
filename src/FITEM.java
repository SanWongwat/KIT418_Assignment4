import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class FITEM {
	static class SortObject implements Comparable<SortObject>{

        private String key;
        private int value;

        public SortObject(String key, int value) {
            super();
            this.key = key;
            this.value = value;
        }
        @Override
        public int compareTo(SortObject o) {
            //ascending order
            //return this.value - o.value;

            //descending order
            return o.value - this.value;
        }
    }
	public static void main(String[] args) throws IOException {
		String k = args[0];
		String ip = args[1];
		String port1 = args[2];
		int port = Integer.parseInt(port1);
		int k1 =Integer.parseInt(k);
		Socket RecieveStream;
		DataInputStream input;
		try {
			 HashMap<String, Integer> map = new HashMap<String, Integer> ();
			 RecieveStream = new Socket("ip", port);
			 input = new DataInputStream(RecieveStream.getInputStream());
			 String content = input.readUTF();
			 //StringTokenizer text = new StringTokenizer(content);
			 //System.out.println(content);
			 String[] word = content.split(" ");	 
			 int count =0;
			 //int k=1;
			 //while(text.hasMoreTokens()) {
			 int i= 0;
			 while(count< word.length) {	
				 
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
			System.out.println(objects[j].key+ ":" +objects[j].value);
		}
		
			
		} catch (IOException e){
			System.out.println(e);
		}
		}
		
}