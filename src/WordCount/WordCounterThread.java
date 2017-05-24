package WordCount;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class WordCounterThread extends Thread {

	Socket _sk = null;
	DataOutputStream _dos = null;
	DataInputStream _din = null;

	public WordCounterThread(Socket pSk) {
		super();
		_sk = pSk;
	}

	public void run() {

		try {
			_din = new DataInputStream(_sk.getInputStream());
			while (true) {
				String text = _din.readUTF();
				System.out.println(text);
				//String[] textArr = text.split(" ");
				
			}
		} catch (Exception ex) {

		}
	}
	
	private void CountWord(String[] text){
		
	}

}
