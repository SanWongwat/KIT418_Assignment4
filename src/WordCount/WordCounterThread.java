package WordCount;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;

public class WordCounterThread extends Thread {

	private Socket _sk = null;
	private DataInputStream _din = null;

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
				// String[] textArr = text.split(" ");
				String[] textArr = text.split("[^\\w']+");
				for (String t : textArr) {
					System.out.print("[" + t + "] ");
				}
				System.out.println();
				CountWord(textArr);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void CountWord(String[] text) {
		System.out.println("**Current List**");
		for (Word w : WordCountEngine.wordList) {
			System.out.println(w.getWord() + ": " + w.getCount() + " ");
		}
		int arrSize = text.length;
		for (int i = 0; i < arrSize; i++) {
			if (!text[i].isEmpty()) {
				boolean isAdd = false;
				if (WordCountEngine.wordList.size() < 5) {
					for (Word w : WordCountEngine.wordList) {
						if (text[i].equals(w.getWord())) {
							w.increateCount();
							isAdd = true;
							break;
						}
					}
					if (!isAdd) {
						Word w = new Word(text[i]);
						w.increateCount();
						WordCountEngine.wordList.add(w);
					}
				} else {

					for (Iterator<Word> iterator = WordCountEngine.wordList.iterator(); iterator.hasNext();) {
						Word w = iterator.next();
						if (w.getWord() == text[i]) {
							continue;
						}
						w.decreaseCount();
						if (w.getCount() == 0) {
							iterator.remove();
						}
					}

					// for (Word w : WordCountEngine.wordList) {
					// if (w.getWord() == text[i]) {
					// continue;
					// }
					// w.decreaseCount();
					// if (w.getCount() == 0) {
					// WordCountEngine.wordList.remove(w);
					// }
					// }
				}
			}
		}
	}

}
