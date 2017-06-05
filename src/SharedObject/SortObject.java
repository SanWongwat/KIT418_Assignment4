package SharedObject;

import java.io.Serializable;

public class SortObject implements Comparable<SortObject>, Serializable {

	public String key;
	public int value;

	public SortObject(String key, int value) {
		super();
		this.key = key;
		this.value = value;
	}

	@Override
	public int compareTo(SortObject o) {

		return o.value - this.value;
	}
}