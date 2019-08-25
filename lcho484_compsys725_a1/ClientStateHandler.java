package lcho484_compsys725_a1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientStateHandler {
	
	private Socket socket;
	private String prevCommand = "";
	private String fileToSave = "";
	
	private long fileSize = 0;
	
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
		
		while (true) {
			
			String line = writeAndSendInputToServer(reader, outToServer);
			
			if(line.toUpperCase().equals("DONE")) {
				System.out.println("Socket closed");
				socket.close();
				break;
			}
			
			/* --------------- Get server response ---------------- */
			
			
			/* Print result on multiple lines when calling LIST */
			switch(prevCommand) {
				case "LIST":
					fileResponseHandler.LIST(inFromServer);
					break;
				case "RETR":
					fileSize = fileResponseHandler.RETR(inFromServer);
					break;
				case "SEND":
					fileSize = fileResponseHandler.SEND(fileSize, fileToSave, socket);
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
		prevCommand = args[0].toUpperCase();
		if (prevCommand.equals("RETR")) {
			if (args.length >= 2) {
				fileToSave = args[1];
			}
		}
		
		/* Write to server */
		outToServer.writeBytes(line + '\n');
		
		return line;
	}
	
}
