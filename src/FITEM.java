import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class FITEM {
	public static void main(String[] args) throws IOException {
		//String k = args[0];
		//String ip = args[1];
		//String port1 = args[2];
		//int port = Integer.parseInt(port1);
		Socket RecieveStream;
		DataInputStream input;
		try {
			 RecieveStream = new Socket("localhost", 1254);
			 input = new DataInputStream(RecieveStream.getInputStream());
			 String content = input.readUTF();
			 System.out.println(content);
			 String[] word = content.split(" ");	 
			 int count =0;
			 String[] eachword;
			 eachword = getEachword(word);
			 for(String words : eachword)
		        {
		            if(null == words)
		            {
		                break;
		            }           
		            for(String s : word)
		            {
		                if(words.equals(s))
		                {
		                    count++;
		                }               
		            }
		            System.out.println("Count of ["+words+"] is : "+count);
		            count=0;
		        
		  
			 }
		} catch (IOException e){
			System.out.println(e);
		}
		}
		
	private static String[] getEachword (String[] word)
{
    String[] eachwords = new String[word.length];

    eachwords[0] = word[0];
    int uniquewordIndex = 1;
    boolean wordAlreadyExists = false;

    for(int i=1; i<word.length ; i++)
    {
        for(int j=0; j<=uniquewordIndex; j++)
        {
            if(word[i].equals(eachwords[j]))
            {
                wordAlreadyExists = true;
            }
        }           

        if(!wordAlreadyExists)
        {
            eachwords[uniquewordIndex] = word[i];
            uniquewordIndex++;               
        }
         wordAlreadyExists = false;
    }       
    return eachwords;
} }

