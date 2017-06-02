package Master;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
	private double PRICE_PER_SECOND = 0.005;
	private SecureRandom random = new SecureRandom(); 

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

			ServiceEnum sType = null;
			String[] requestArr;
			while (true) {
				requestArr = _cDis.readUTF().split(",");
				if (Utils.IsEnum(requestArr[0])) {
					sType = ServiceEnum.valueOf(requestArr[0]);
				} else {
					_cDos.writeUTF(String.format("%s,%s", ServiceEnum.Error.toString(), "Wrong Option."));
					continue;
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
				case Disconnect: {
					Utils.Log(TAG, "Client disconnected");
					return;
					// Intentional fallthrough
				}
				case Sync: {
					this.UpdateInstance();
					return;
				}
				}
				// end while
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (EOFException e) {
			e.printStackTrace();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void UpdateInstance() throws IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(_cSocket.getInputStream());
		List<WordCountInstance> updatelist = (List<WordCountInstance>) in.readObject();
		for (WordCountInstance wWorker : updatelist) {
			for (WordCountInstance wMaster : Master.listWCInstance) {
				if (wMaster.getPasscode().equals(wWorker.getPasscode())) {
					wMaster.setPort(wWorker.getPort());
				}
			}
		}
	}

	public void StartService() throws UnknownHostException, IOException {
		_cDos.writeUTF("Please specify how many frequent words you need to know (e.g. 4, 5, 10).");
		String kvalue = _cDis.readUTF();
		Socket wSk = null;
		DataInputStream wDis = null;
		DataOutputStream wDos = null;
		// decide worker
		WorkerInfo pWorker = null;
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
			if (pWorker == null) {
				pWorker = new WorkerInfo();
				pWorker.setAddress(w.getAddress());
				pWorker.setPort(w.getPort());
				pWorker.setMemoryUsage(tempMem);
				pWorker.setNumberOfProcess(tempNoP);
				pWorker.setName(w.getName());
			} else {
				if (pWorker.getNumberOfProcess() > tempNoP) {
					pWorker.setAddress(w.getAddress());
					pWorker.setPort(w.getPort());
					pWorker.setMemoryUsage(tempMem);
					pWorker.setNumberOfProcess(tempNoP);
					pWorker.setName(w.getName());
				}
			}
			if (isFull) {
				// Start new worker instance

				// Update worker list in master

				// Select the new instance to submit request
			}
			wDos.close();
			wDis.close();
			wSk.close();
		}

		// submit request to selected worker to start service
		wSk = new Socket(pWorker.getAddress(), pWorker.getPort());
		wDis = new DataInputStream(wSk.getInputStream());
		wDos = new DataOutputStream(wSk.getOutputStream());
		String passcode = generatePasscode();
		wDos.writeUTF(String.format("%s,%s,%s", ServiceEnum.StartService.toString(), passcode, kvalue));

		// get wordcount instance from worker.
		String portNo = wDis.readUTF();

		// add new wordcount instance
		WordCountInstance wc = new WordCountInstance(passcode);
		wc.setAddress(pWorker.getAddress());
		wc.setWorkerName(pWorker.getName());
		wc.setPort(Integer.parseInt(portNo));
		wc.StartService();
		Master.listWCInstance.add(wc);

		// send passcode back to client
		_cDos.writeUTF(String.format("%s,%s", ServiceEnum.OK, passcode));
		Utils.Log(TAG, String.format("Start wordcount instance at: %s:%d with K value of", wSk.getInetAddress(),
				wc.getPort(), kvalue));
		wDis.close();
		wDos.close();
		wSk.close();
	}

	public String generatePasscode() {
		return new BigInteger(130, random).toString(32);
	}

	public void RetrievingResult(String passcode) throws IOException, ClassNotFoundException {
		WordCountInstance target = null;
		for (WordCountInstance w : Master.listWCInstance) {
			if (passcode.equals(w.getPasscode())) {
				target = w;
				break;
			}
		}
		if (target == null) {
			_cDos.writeUTF(String.format("%s,%s", ServiceEnum.Error.toString(), "Invalid passcode."));
		} else {
			_cDos.writeUTF(ServiceEnum.OK.toString());
			Utils.Log(TAG, target.getAddress() + " : " + target.getPort());
			Socket sk = new Socket(target.getAddress(), target.getPort());
			DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
			dos.writeUTF(ServiceEnum.GetResult.toString());
			ObjectInputStream in = new ObjectInputStream(sk.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(_cSocket.getOutputStream());
			HashMap<String, Integer> data = (HashMap<String, Integer>) in.readObject();
			out.writeObject(data);

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
				if (w.getName().equals(target.getWorkerName())) {
					wi = w;
				}
			}
			Socket sk = new Socket(wi.getAddress(), wi.getPort());
			DataInputStream dis = new DataInputStream(sk.getInputStream());
			DataOutputStream dos = new DataOutputStream(sk.getOutputStream());

			dos.writeUTF(String.format("%s,%s", ServiceEnum.StopService, passcode));
			String[] responseArr = dis.readUTF().split(",");
			if (ServiceEnum.valueOf(responseArr[0]) == ServiceEnum.OK) {
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
						String.format("Total time: %2d:%2d:%2d Hour. Cost: $%.2f", hours, mins, secs, totalPrice));
				Master.listWCInstance.remove(wc);
				dis.close();
				dos.close();
				sk.close();
			}

		}
	}
}
