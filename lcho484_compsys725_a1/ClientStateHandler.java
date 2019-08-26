package lcho484_compsys725_a1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientStateHandler {
	
	private Socket socket;
	private String[] prevArgs;

	private FileResponseHandler fileResponseHandler;
	
	public ClientStateHandler(Socket socket) { 
		this.socket = socket;
		fileResponseHandler = new FileResponseHandler();
	}

	public void start() throws IOException {
		
		/* Request outputter */
		DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());	
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		/* Response reader */
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		String greeting = inFromServer.readLine();
		System.out.println(greeting);
		
		while (true) {
			
			String line = writeAndSendInputToServer(reader, outToServer);
			
			if(line.toUpperCase().equals("DONE")) {
				System.out.println("Socket closed");
				socket.close();
				break;
			}
			
			/* --------------- Get server response ---------------- */
			
			/* Handles server responses for different commands */
			switch(prevArgs[0].toUpperCase()) {
				case "LIST":
					fileResponseHandler.LIST(inFromServer);
					break;
				case "RETR":
					fileResponseHandler.RETR(inFromServer, prevArgs);
					break;
				case "SEND":
					fileResponseHandler.SEND(socket);
					break;
				case "STOR":
					fileResponseHandler.STOR(inFromServer, prevArgs);
					break;
				case "SIZE":
					fileResponseHandler.SIZE(inFromServer, socket);
					break;
				default:
					System.out.println(inFromServer.readLine()); 
					break;
			}

		}
	}
	
	
	private String writeAndSendInputToServer(BufferedReader reader, DataOutputStream outToServer) throws IOException {
		
		System.out.println("Input: ");
		String line = reader.readLine();
		
		prevArgs = line.split(" ");
		
		/* Write to server */
		outToServer.writeBytes(line + '\n');
		
		return line;
	}
	
}
