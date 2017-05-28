package Client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;

import SharedObject.ServiceEnum;

public class Client {

	private static String MASTER_IP= "localhost";
	private static int MASTER_PORT= 1255;
	private static Socket _sk;
	private static DataInputStream _dis;
	private static DataOutputStream _dos;
	private static String selectedOption;

	public static void main(String[] args) {
		if (args.length == 2) {
			MASTER_IP = args[0];
			MASTER_PORT = Integer.parseInt(args[1]);
		}
		try {
			// connect to master;
			_sk = new Socket(MASTER_IP, MASTER_PORT);
			_dis = new DataInputStream(_sk.getInputStream());
			_dos = new DataOutputStream(_sk.getOutputStream());

			String options = _dis.readUTF();
			System.out.println(options);
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

			while (true) {
				String selectedOption = in.readLine();
				if (selectedOption.equals("1")) {
					StartService();
				} else if (selectedOption.equals("2")) {
					GetResult();
				} else if (selectedOption.equals("3")) {
					StopService();
				} else if (selectedOption.equals("4")) {
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
//		GetResult();
		if (validatePasscode(ServiceEnum.StopService)) {
			String summary = _dis.readUTF();
			System.out.println(summary);
		}

	}

	private static void GetResult() throws IOException, ClassNotFoundException {
		if (validatePasscode(ServiceEnum.GetResult)) {

			// send passcode and get result back
			// _dis.close();
			ObjectInputStream oin = new ObjectInputStream(_sk.getInputStream());
			HashMap<String, Integer> data = (HashMap<String, Integer>) oin.readObject();
			for (String k : data.keySet()) {
				System.out.println(String.format("%s: %d word", k, data.get(k)));
			}
		}
	}

	private static boolean validatePasscode(ServiceEnum s) throws IOException {
		System.out.println("Please specify your passcode.");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
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

	private static void StartService() throws IOException {
		selectedOption = ServiceEnum.StartService.toString();
		_dos.writeUTF(selectedOption);
		String response = _dis.readUTF();

		String[] responseArr = response.split(",");
		if (ServiceEnum.valueOf(responseArr[0]) == ServiceEnum.OK) {

			// receive passcode
			String passcode = responseArr[1];
			System.out.println("Starting service...");
			System.out.println("Your passcode is " + passcode);

			// connect to Worker and send start service request
			// String startServiceStr = String.format("1,%s", passcode);
			// sk = new Socket(SERVER_IP, SERVER_PORT);
			// _dos = new DataOutputStream(sk.getOutputStream());
			// _dos.writeUTF(startServiceStr);
		}
	}

}
