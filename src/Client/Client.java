package Client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import SharedObject.ServiceEnum;
import SharedObject.Utils;
import SharedObject.Word;

public class Client {

	private static final String TAG = "Client";
	private static String MASTER_IP = "localhost";
	private static int MASTER_PORT = 1255;
	private static Socket _sk;
	private static DataInputStream _dis;
	private static DataOutputStream _dos;
	private static String selectedOption;
	private static BufferedReader in;

	static long startTime = 0;
	static long endTime = 0;
	SimpleDateFormat sdf = new SimpleDateFormat("mm:ss.SSS");

	public static void main(String[] args) {
		if (args.length == 2) {
			MASTER_IP = args[0];
			MASTER_PORT = Integer.parseInt(args[1]);
		} else {
			Utils.Log(TAG, "Invalid arguments");
			return;
		}
		try {
			// connect to master;
			_sk = new Socket(MASTER_IP, MASTER_PORT);
			_dis = new DataInputStream(_sk.getInputStream());
			_dos = new DataOutputStream(_sk.getOutputStream());

			String options = _dis.readUTF();
			System.out.println(options);
			in = new BufferedReader(new InputStreamReader(System.in));

			while (true) {
				String[] selectedOption = in.readLine().split(" ");
				if (selectedOption[0].equals("1")) {
					StartService(selectedOption[1]);
				} else if (selectedOption[0].equals("2")) {
					GetResult();
				} else if (selectedOption[0].equals("3")) {
					StopService();
				} else if (selectedOption[0].equals("4")) {
					System.out.println("Bye Bye.");
					_dos.writeUTF(ServiceEnum.Disconnect.toString());
					in.close();
					_dos.close();
					_dis.close();
					_sk.close();
					break;
				} else {
					System.out.println("Invalid options.");
				}

			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void StopService() throws IOException, ClassNotFoundException {
		// GetResult();
		if (validatePasscode(ServiceEnum.StopService)) {
			String summary = _dis.readUTF();
			System.out.println(summary);
		}

	}

	private static void GetResult() throws IOException, ClassNotFoundException {

		startTime = System.currentTimeMillis();
		if (validatePasscode(ServiceEnum.GetResult)) {

			// send passcode and get result back
			// _dis.close();
			ObjectInputStream oin = new ObjectInputStream(_sk.getInputStream());
			List<Word> data = (List<Word>) oin.readObject();
			endTime = System.currentTimeMillis();
			for (Word w : data) {
				System.out.println(String.format("[%s]: %d word", w.getWord(), w.getCount()));
			}
			long diff = endTime - startTime;
			System.out.println(String.format("Time takes: %d ms.", diff));
		}
	}

	private static boolean validatePasscode(ServiceEnum s) throws IOException {
		System.out.println("Please specify your passcode.");
		in = new BufferedReader(new InputStreamReader(System.in));
		String passcode = null;
		int count = 0;
		while (count < 3) {
			passcode = in.readLine();
			_dos.writeUTF(String.format("%s,%s", s.toString(), passcode));
			String[] status = _dis.readUTF().split(",");
			ServiceEnum check = ServiceEnum.valueOf(status[0]);
			if (check == ServiceEnum.Error) {
				System.out.println(status[1]);
			} else {
				break;
			}
			count++;
		}
		if (count == 3) {
			System.out.println("Too many attempt. Please specify service again.");
			return false;
		}
		return true;
	}

	private static void StartService(String StreamGenIP) throws IOException {
		selectedOption = ServiceEnum.StartService.toString();
		_dos.writeUTF(String.format("%s,%s", selectedOption, StreamGenIP));

		System.out.println(_dis.readUTF());
		String kvalue;
		while (true) {
			try {
				kvalue = in.readLine();
				Integer.parseInt(kvalue);
				break;
			} catch (Exception e) {
				System.out.print("Please input a number.");
			}
		}
		_dos.writeUTF(kvalue);

		String response = _dis.readUTF();

		String[] responseArr = response.split(",");
		if (ServiceEnum.valueOf(responseArr[0]) == ServiceEnum.OK) {

			String passcode = responseArr[1];
			System.out.println("Starting service...");
			System.out.println("Your passcode is " + passcode);
		}
	}

}
