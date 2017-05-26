package SharedObject;

public class Utils {

	public static void Log(String tag, String message) {
		System.out.println(String.format("%s: %s", tag, message));
	}
	
	public static boolean IsEnum(String value){
		try{
			ServiceEnum.valueOf(value);
			return true;
		}
		catch(IllegalArgumentException ex){
			return false;
		}
	}

}
