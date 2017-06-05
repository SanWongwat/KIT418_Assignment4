import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import SharedObject.Utils;
import SharedObject.*;

public class FITEM implements Runnable {
	public static List<SortObject> object = new ArrayList();
	private final String TAG = "FITEM";
	private static int PORT = 1270;
	private static String ip = "localhost";
	private static int k1 = 4;
	private static List<Word> wordlist = new ArrayList<Word>();

	ServerSocket ss;
	static int StreamGenPort = 2000;

	public static void main(String[] args) throws IOException {
		String k = args[0];
		ip = args[1];
		String port1 = args[2];
		PORT = Integer.parseInt(port1);
		k1 = Integer.parseInt(k);
		System.out.println(String.format("Info: %s, %s, %d", k, ip, PORT));
		FITEM f = new FITEM();
		Thread t = new Thread(f);
		t.start();

		Socket RecieveStream;
		DataInputStream input;
		try {
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			RecieveStream = new Socket(ip, StreamGenPort);
			input = new DataInputStream(RecieveStream.getInputStream());
			while (true) {
				String content = input.readUTF();
				// String[] word = content.split("\\s+");
				String[] word = content.split("[^\\w']+");
				for (String s : word) {
					boolean notFound = false;
					if (!s.isEmpty()) {
						System.out.println(" [" + s + "] ");
						if (wordlist.size() < k1) {
							Word w = new Word(s);
							w.increateCount();
							wordlist.add(w);
						} else if (wordlist.size() == k1) {
							for (int i = 0; i < wordlist.size(); i++) {
								if (wordlist.get(i).getWord().equals(s)) {
									wordlist.get(i).increateCount();
									break;
								}
								if (i == k1 - 1) {
									notFound = true;
								}
							}
							if (notFound) {
								for (Iterator<Word> iterator = wordlist.iterator(); iterator.hasNext();) {
									Word w = iterator.next();
									w.decreaseCount();
									if (w.getCount() <= 0) {
										iterator.remove();
									}
								}
							}
						}
					}
				}

				// int count = 0;
				// int i = 0;
				// while (count < word.length) {
				// if (word[i].length() == 0) {
				// i++;
				// count++;
				// continue;
				// }
				// if (map.containsKey(word[i])) {
				// int counts = map.get(word[i]) + 1;
				// map.put(word[i], counts);
				// } else {
				// map.put(word[i], 1);
				// }
				// count++;
				// i++;
				// }
				//
				// Set set = map.entrySet();
				// Iterator g = set.iterator();
				// int index = 0;
				// SortObject[] objects = new SortObject[map.size()];
				// while (g.hasNext()) {
				// Map.Entry e = (Map.Entry) g.next();
				// String tempS = (String) e.getKey();
				// int tempI = (int) e.getValue();
				// objects[index] = new SortObject(tempS, tempI);
				// index++;
				// }
				// System.out.println();
				// Arrays.sort(objects);
				// for (int j = 0; j < k1; j++) {
				// SortObject s = new SortObject(objects[j].key,
				// objects[j].value);
				// object.add(s);
				// System.out.println(objects[j].key + ":" + objects[j].value);
				// }
			}

		} catch (IOException e) {
			System.out.println(e);
		}
	}

	@Override
	public void run() {
		int flag = 0;
		try {
			ss = new ServerSocket(PORT);
			while (true) {
				Socket sk = ss.accept();
				Utils.Log(TAG, "get connection");
				DataInputStream dis = new DataInputStream(sk.getInputStream());
				String requestType = dis.readUTF();
				System.out.println(requestType);
				if (!ss.isClosed()) {
					flag = 1;
				}
				if (flag == 1) {
					if (requestType.equals("GetResult")) {
						ObjectOutputStream out = new ObjectOutputStream(sk.getOutputStream());
						Utils.Log(TAG, "Send Result");
						// out.writeObject(object);
						out.writeObject(wordlist);
						// else
						//
					}
				} else {
					System.out.println("System has lost the connection");
				}
			}
		}

		catch (IOException e) {
			e.printStackTrace();
		}

	}

}