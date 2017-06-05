package SharedObject;

import java.io.Serializable;

public class Word implements Serializable{

	private String Word;
	private int Count;

	public Word(String word) {
		// TODO Auto-generated constructor stub
		Word = word;
	}

	public String getWord() {
		return Word;
	}

	public int getCount() {
		return Count;
	}

	public void increateCount() {
		Count++;
	}

	public void decreaseCount() {
		Count--;
	}

}
