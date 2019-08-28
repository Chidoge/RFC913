package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
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
					fileResponseHandler.SEND(inFromServer, socket);
					break;
				case "STOR":
					fileResponseHandler.STOR(inFromServer, prevArgs, socket);
					break;
				case "STOP":
					fileResponseHandler.STOP(inFromServer);
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
		
		String[] args = line.split(" ");
		
		/* If calling STOR, make sure the file exists on the client side */
		if (args.length >= 3 && args[0].toUpperCase().equals("STOR")) {
			String filename = args[2];
			File file = new File(filename);
			if (!file.exists()) {
				System.out.println("-The file you are trying to send to the server does not exist");
				writeAndSendInputToServer(reader, outToServer);
				return line;
			}
		}
		
		prevArgs = args;
		
		/* Write to server */
		outToServer.writeBytes(line + '\n');
		
		return line;
	}
	
}
