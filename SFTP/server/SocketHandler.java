package SFTP.server;

import java.io.*;
import java.net.*;

public class SocketHandler {
	
	private Socket socket;
	private OutputStreamHandler outputStreamHandler; 
	private CredentialsHandler credentialsHandler;
	private FileSystemHandler fileSystemHandler;
	
	public SocketHandler(Socket socket) {
		this.socket = socket;
		this.outputStreamHandler = new OutputStreamHandler(socket);
		
		
		/* Boolean to test from Eclipse */
		boolean runFromCMD = true;
		this.credentialsHandler = new CredentialsHandler(runFromCMD);
		this.fileSystemHandler = new FileSystemHandler(runFromCMD);
	}
	
	public void start() throws IOException {
		
		outputStreamHandler.greet();
		while(true) {
			
			/* Get command */
			try {
				
				BufferedReader userInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String userMessage = userInput.readLine();
				String[] args = userMessage.split(" ");

				if (args[0].toUpperCase().equals("DONE")) {
					DONE();
				}
				else if (args[0].toUpperCase().equals("SEND")) {
					outputStreamHandler.sendData(fileSystemHandler.SEND(args, credentialsHandler));
				}
				else if (args[0].toUpperCase().equals("STOP")) {
					outputStreamHandler.writeResult(fileSystemHandler.STOP(args));
				}
				else if (args.length > 1) {
					switch(args[0].toUpperCase()) {
						case "USER":
							outputStreamHandler.writeResult(credentialsHandler.USER(args));
							break;
						case "ACCT":
							outputStreamHandler.writeResult(credentialsHandler.ACCT(args, fileSystemHandler));
							break;
						case "PASS":
							outputStreamHandler.writeResult(credentialsHandler.PASS(args, fileSystemHandler));
							break;
						case "CDIR":
							outputStreamHandler.writeResult(fileSystemHandler.CDIR(args, credentialsHandler));
							break;
						case "LIST":
							outputStreamHandler.writeResult(fileSystemHandler.LIST(args, credentialsHandler));
							break;
						case "KILL":
							outputStreamHandler.writeResult(fileSystemHandler.KILL(args, credentialsHandler));
							break;
						case "NAME":
							outputStreamHandler.writeResult(fileSystemHandler.NAME(args, credentialsHandler));
							break;
						case "TOBE":
							outputStreamHandler.writeResult(fileSystemHandler.TOBE(args, credentialsHandler));
							break;
						case "RETR":
							outputStreamHandler.writeResult(fileSystemHandler.RETR(args, credentialsHandler));
							break;
						case "STOR":
							outputStreamHandler.writeResult(fileSystemHandler.STOR(args, credentialsHandler));
							break;
						case "SIZE":
							outputStreamHandler.writeResult(fileSystemHandler.SIZE(args));
							if (fileSystemHandler.getSTORState().equals("WAITING")) {
								outputStreamHandler.writeResult(fileSystemHandler.waitFile(outputStreamHandler.getSocket()));
							}
							break;
						case "TYPE":
							outputStreamHandler.writeResult(fileSystemHandler.TYPE(args, credentialsHandler));
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
				try {
					socket.close();
					break;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}
	
	
	private void DONE() {
		try {
			outputStreamHandler.goodBye();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
