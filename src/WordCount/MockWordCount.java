package WordCount;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class MockWordCount {

	static HashMap<String, Integer> testData;

	public static void main(String[] args) {
		testData = new HashMap<String, Integer>();
		testData.put("One", 1);
		testData.put("Two", 2);
		testData.put("Three", 3);
		int port = Integer.parseInt(args[0]);
		ServerSocket ss;
		try {
			ss = new ServerSocket(port);
			System.out.println("Wordcount start at:" + ss.getLocalPort());
			while (true) {
				Socket sk = ss.accept();
				ObjectOutputStream out = new ObjectOutputStream(sk.getOutputStream());
				out.writeObject(testData);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
