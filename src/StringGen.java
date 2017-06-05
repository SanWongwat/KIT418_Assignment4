import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import SharedObject.Utils;
import SharedObject.WordCountInstance;
import Workers.CheckProcess;
import Workers.Worker;


public class StringGen implements Runnable {
	private static int PORT;
	private static String FILE;
	
	public static void main(String[] args) throws IOException {
		if(args.length!=2){
			System.out.println("Invalid arguments");
			return;
		}
		PORT = Integer.parseInt(args[0]);
		FILE = args[1];
		StringGen gen = new StringGen();
		Thread t = new Thread(gen);
		t.start();
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				String[] command = in.readLine().split(" ");
				if (command[0].equals("p")) {
					
				}
				
				else if (command[0].equals("exit")) {
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ServerSocket serversocket = null;
		
		try {

			serversocket = new ServerSocket(PORT);
			System.out.println("Start stream generator.");
			while (true) {
				Socket ss = serversocket.accept();
				new Thread(new ClientInfoThread(ss, FILE)).start();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
