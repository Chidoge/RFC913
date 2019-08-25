
package lcho484_compsys725_a1;

import java.io.*;
import java.net.*;

public class Server {
	
	public static void main(String[] args) throws IOException {
		
		/* Open socket */
		int port = 3000;
		@SuppressWarnings("resource")
		ServerSocket welcomeSocket = new ServerSocket(port);
		System.out.println("Server started at port " + port + "\n");
		
		while(true) {
			
			/* Create new socket for each connection request */
			Socket socket = welcomeSocket.accept();
			SocketHandler socketHandler = new SocketHandler(socket);
			socketHandler.start();
	
		}
	}
}
