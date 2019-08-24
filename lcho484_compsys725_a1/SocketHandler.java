package lcho484_compsys725_a1;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SocketHandler {
	
	private Socket socket;
	private OutputStreamHandler outputStreamHandler; 
	private CredentialsHandler credentialsHandler;
	private FileSystemHandler fileSystemHandler;
	
	
	public SocketHandler(Socket socket) {
		this.socket = socket;
		this.outputStreamHandler = new OutputStreamHandler(socket);
		this.credentialsHandler = new CredentialsHandler();
		this.fileSystemHandler = new FileSystemHandler();
	}
	
	public void start() {
		
		while(true) {
			
			/* Get command */
			try {
				
				BufferedReader userInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String userMessage = userInput.readLine();
				String[] args = userMessage.split(" ");

				if (args[0].toUpperCase().equals("DONE")) {
					DONE();
				}
				else if (args.length > 1) {
					switch(args[0].toUpperCase()) {
						case "USER":
							outputStreamHandler.writeResult(credentialsHandler.USER(args));
							break;
						case "ACCT":
							outputStreamHandler.writeResult(credentialsHandler.ACCT(args));
							break;
						case "PASS":
							outputStreamHandler.writeResult(credentialsHandler.PASS(args));
							break;
						case "LIST":
							outputStreamHandler.writeResult(fileSystemHandler.LIST(args));
							break;
						case "KILL":
							outputStreamHandler.writeResult(fileSystemHandler.KILL(args));
							break;
						case "CDIR":
							outputStreamHandler.writeResult(fileSystemHandler.CDIR(args));
							break;
						case "NAME":
							outputStreamHandler.writeResult(fileSystemHandler.NAME(args));
							break;
						case "TOBE":
							outputStreamHandler.writeResult(fileSystemHandler.TOBE(args));
							break;
						default:
							outputStreamHandler.writeInvalid();
							break;
					}
				}
				else {
					outputStreamHandler.writeInvalid();
				}

								
			} catch (IOException e) {
				// TODO Auto-generated catch block
				try {
					socket.close();
					break;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}
	
	
	private void DONE() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
