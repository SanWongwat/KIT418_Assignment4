package Master;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RequestHandler extends Thread {

	private Socket _cSocket;
	private DataInputStream _dis;
	private DataOutputStream _dos;

	public RequestHandler(Socket sk) {
		// TODO Auto-generated constructor stub
		super();
		_cSocket = sk;
	}

	public void run() {
		// show option to client
		/*
		 * 1. start service 2. get current word count 3. stop service
		 */
		String optionStr = "1. Start service\r\n"
				+ "2. Get current word count\r\n"
				+ "3. Stop service\r\n"
				+ "4. Exit";
		try {
			_dos = new DataOutputStream(_cSocket.getOutputStream());
			_dis = new DataInputStream(_cSocket.getInputStream());
			_dos.writeUTF(optionStr);

			String response = _dis.readUTF();
			switch (response) {
			case "1": {
				// Start service
			}
				break;
			case "2": {
				// get word count
			}
				break;
			case "3": {
				//stop service
			}
				break;
			case "4": {
				//exit
			}
				break;
			default: {
				_dos.writeUTF("Wrong option.");
			}
				break;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
