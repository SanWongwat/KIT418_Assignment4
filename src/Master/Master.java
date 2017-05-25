package Master;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Master {

	private static int SERVER_PORT = 1255;
	public static List<ClientInfo> clientList = new ArrayList<ClientInfo>();
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
			ServerSocket ss = new ServerSocket(SERVER_PORT);
			
	}

}
