package SharedObject;

import java.io.IOException;
import java.net.ServerSocket;

public class Utils {

	public static void Log(String tag, String message) {
		System.out.println(String.format("%s: %s", tag, message));
	}

	public static boolean IsEnum(String value) {
		try {
			ServiceEnum.valueOf(value);
			return true;
		} catch (IllegalArgumentException ex) { 
			return false;
		} 
	}

	public static boolean isPortAvailble(int port) {
		ServerSocket s = null;
		try {
			s = new ServerSocket(port);
			s.close();
			return true;
		} catch (IOException ex) {
			return false;
		}
	}

}
