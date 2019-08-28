package SFTP.client;

import java.net.*;

public class Client {
	
	public static void main(String[] args) throws Exception {
		
		String IP = "localhost";
		int port = 3000;
	
		System.out.println("Client started with server -  " + IP + ":" + port);
		ClientStateHandler client = new ClientStateHandler(new Socket(IP, port));
		client.start();
	}
}
