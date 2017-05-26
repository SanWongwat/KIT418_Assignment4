package Master;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

import SharedObject.ServiceEnum;
import SharedObject.Utils;
import SharedObject.WordCountInstance;
import SharedObject.WorkerInfo;

public class RequestHandler extends Thread {

	private final String TAG = "RequestHandler";
	private Socket _cSocket;
	private DataInputStream _cDis;
	private DataOutputStream _cDos;
	private final String optionStr = "Please specify service.\r\n1. Start service\r\n" + "2. Get current word count\r\n"
			+ "3. Stop service\r\n" + "4. Exit";
	private String WORKER_IP;
	private int WORKER_PORT;
	private final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private SecureRandom rnd = new SecureRandom();
	private int PASSCODE_LENGTH = 12;
	private double PRICE_PER_SECOND = 0.005;

	public RequestHandler(Socket sk) {
		super();
		_cSocket = sk;
	}

	public void run() {
		// show option to client
		/*
		 * 1. start service 2. get current word count 3. stop service
		 */
		try {
			_cDos = new DataOutputStream(_cSocket.getOutputStream());
			_cDis = new DataInputStream(_cSocket.getInputStream());
			_cDos.writeUTF(optionStr);

			ServiceEnum sType;
			String[] requestArr;
			while (true) {
				requestArr = _cDis.readUTF().split(",");

				if (Utils.IsEnum(requestArr[0])) {
					sType = ServiceEnum.valueOf(requestArr[0]);
					break;
				} else {
					_cDos.writeUTF(String.format("%s,%s", ServiceEnum.Error.toString(), "Wrong Option."));
				}
			}
			switch (sType) {
			case StartService: {
				// Start service
				Utils.Log(TAG, "Start service");
				this.StartService();
			}
				break;
			case GetResult: {
				// get word count
				Utils.Log(TAG, "Request result");
				this.RetrievingResult(requestArr[1]);
			}
				break;
			case StopService: {
				// stop service
				Utils.Log(TAG, "Stop service");
				this.StopService(requestArr[1]);
			}
				break;
			default:
				break;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (_cDos != null) {
					_cDos.close();
				}
				if (_cDis != null) {
					_cDis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void StopService(String passcode) throws IOException {
		WordCountInstance target = null;
		for (WordCountInstance w : Master.listWCInstance) {
			if (passcode.equals(w.getPasscode())) {
				target = w;
				break;
			}
		}
		if (target == null) {
			_cDos.writeUTF(String.format("%s,%s", ServiceEnum.Error, "Invalid passcode."));
		} else {
			_cDos.writeUTF(ServiceEnum.OK.toString());
			WorkerInfo wi = null;
			for (WorkerInfo w : Master.listWorker) {
				if (w.getAddress().equals(target.getAddress())) {
					wi = w;
				}
			}
			Socket sk = new Socket(wi.getAddress(), wi.getPort());
			DataInputStream dis = new DataInputStream(sk.getInputStream());
			DataOutputStream dos = new DataOutputStream(sk.getOutputStream());

			dos.writeUTF(String.format("%s,%s", ServiceEnum.StopService, passcode));
			if (ServiceEnum.valueOf(dis.readUTF()) == ServiceEnum.OK) {
				WordCountInstance wc = null;
				for (WordCountInstance wci : Master.listWCInstance) {
					if (wci.getPasscode().equals(passcode)) {
						wc = wci;
					}
				}
				wc.StopService();

				// cal price
				Date st = wc.getStartTime();
				Date et = wc.getStopTime();
				LocalDateTime start = LocalDateTime.of(st.getYear(), st.getMonth(), st.getDate(), st.getHours(),
						st.getMinutes(), st.getSeconds());
				LocalDateTime stop = LocalDateTime.of(et.getYear(), et.getMonth(), et.getDate(), et.getHours(),
						et.getMinutes(), et.getSeconds());
				Duration d = Duration.between(start, stop);

				int hours = (int) d.getSeconds() / 3600;
				int remainder = (int) d.getSeconds() - hours * 3600;
				int mins = remainder / 60;
				remainder = remainder - mins * 60;
				int secs = remainder;

				double totalPrice = d.getSeconds() * PRICE_PER_SECOND;
				_cDos.writeUTF(
						String.format("Total time: %d:%d:%d. Cost: %s", hours, mins, secs, String.valueOf(totalPrice)));

			}

		}
	}

	public void StartService() throws UnknownHostException, IOException {
		Socket wSk = null;
		DataInputStream wDis = null;
		DataOutputStream wDos = null;
		// decide worker
		WorkerInfo preferedWorker = null;
		boolean isFull = false;
		for (WorkerInfo w : Master.listWorker) {
			// ite through worker list to check which one has the least
			// workload
			wSk = new Socket(w.getAddress(), w.getPort());
			wDis = new DataInputStream(wSk.getInputStream());
			wDos = new DataOutputStream(wSk.getOutputStream());
			wDos.writeUTF(ServiceEnum.CheckStatus.toString());
			String[] statusArr = wDis.readUTF().split(",");
			long tempMem = Long.parseLong(statusArr[0]);
			int tempNoP = Integer.parseInt(statusArr[1]);
			if (tempNoP >= Master.MAX_PROCESS) {
				isFull = true;
			} else {
				isFull = false;
			}
			if (preferedWorker == null) {
				preferedWorker = new WorkerInfo();
				preferedWorker.setAddress(w.getAddress());
				preferedWorker.setPort(w.getPort());
				preferedWorker.setMemoryUsage(tempMem);
				preferedWorker.setNumberOfProcess(tempNoP);
			} else {
				// if (preferedWorker.getMemoryUsage() > tempMem) {
				// preferedWorker.setAddress(w.getAddress());
				// preferedWorker.setPort(w.getPort());
				// preferedWorker.setMemoryUsage(tempMem);
				// preferedWorker.setNumberOfProcess(tempNoP);
				// }
				if (preferedWorker.getNumberOfProcess() > tempNoP) {
					preferedWorker.setAddress(w.getAddress());
					preferedWorker.setPort(w.getPort());
					preferedWorker.setMemoryUsage(tempMem);
					preferedWorker.setNumberOfProcess(tempNoP);
				}
			}
			if (isFull) {
				// start new worker and send info to client
			}
			wDos.close();
			wDis.close();
			wSk.close();
		}

		// send request to worker to start service
		WORKER_IP = preferedWorker.getAddress();
		WORKER_PORT = preferedWorker.getPort();
		wSk = new Socket(WORKER_IP, WORKER_PORT);
		wDis = new DataInputStream(wSk.getInputStream());
		wDos = new DataOutputStream(wSk.getOutputStream());
		String passcode = PasscodeGenerator(PASSCODE_LENGTH);
		wDos.writeUTF(String.format("%s,%s", ServiceEnum.StartService.toString(), passcode));

		String portNo = wDis.readUTF();

		WordCountInstance wc = new WordCountInstance(passcode);
		wc.setAddress(WORKER_IP);
		wc.setPort(Integer.parseInt(portNo));
		wc.StartService();

		// send passcode to client
		// _cDos = new DataOutputStream(_cSocket.getOutputStream());
		_cDos.writeUTF(String.format("%s,%s", ServiceEnum.OK, passcode));
		Utils.Log(TAG, "Start new word count service.");

	}

	public String PasscodeGenerator(int len) {
		StringBuilder sb = new StringBuilder(len);
		boolean isDone = false;
		while (!isDone) {
			for (int i = 0; i < len; i++)
				sb.append(AB.charAt(rnd.nextInt(AB.length())));
			for (int i = 0; i < Master.listWCInstance.size(); i++) {
				WordCountInstance w = Master.listWCInstance.get(i);
				if (w.getPasscode().equals(sb.toString())) {
					isDone = false;
					break;
				}
				isDone = true;
			}
		}
		return sb.toString();
	}

	public void RetrievingResult(String passcode) throws IOException {
		WordCountInstance target = null;
		for (WordCountInstance w : Master.listWCInstance) {
			if (passcode.equals(w.getPasscode())) {
				target = w;
				break;
			}
		}
		if (target == null) {
			_cDos.writeUTF(String.format("%s,%s", ServiceEnum.Error, "Invalid passcode."));
		} else {
			_cDos.writeUTF(ServiceEnum.OK.toString());
			Socket sk = new Socket(target.getAddress(), target.getPort());
			ObjectInputStream in = new ObjectInputStream(sk.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(sk.getOutputStream());
			out.writeObject(new String(passcode));
			out.close();

			try {
				// receive data and forward to user without deserialise
				// HashMap<String, Integer> data = (HashMap<String, Integer>)
				// in.readObject();
				out = new ObjectOutputStream(_cSocket.getOutputStream());
				out.writeObject(in.readObject());

				out.close();
				in.close();
				sk.close();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
