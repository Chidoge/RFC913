package SFTP.client;

import java.net.*;

public class Test {
	
	public static void main(String[] args) throws Exception {
		
		String IP = "localhost";
		int port = 3000;
		ClientTester client = new ClientTester(new Socket(IP, port), true, args);
		client.start();
	}
}
