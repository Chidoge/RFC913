package lcho484_compsys725_a1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ClientStateHandler {
	
	private Socket socket;
	private String prevCommand = "";
	private String fileToSave = "";
	
	public ClientStateHandler(Socket socket) { 
		this.socket = socket;
	}

	public void start() throws IOException {
		
		/* Request outputter */
		DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());	
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		/* Response reader */
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String serverResponse;
		
		while (true) {
			
			String line = writeAndSendInputToServer(reader, outToServer);
			
			if(line.toUpperCase().equals("DONE")) {
				System.out.println("Socket closed");
				socket.close();
				break;
			}
			
			/* --------------- Get server response ---------------- */
			
			
			/* Print result on multiple lines when calling LIST */
			if (prevCommand.equals("LIST")) {
				while ((serverResponse = inFromServer.readLine()) != null) { 
					
					/* Stop printing when no more files to list or if not enough arguments called with LIST*/
					if (serverResponse.equals("EOF")) {
						break;
					}
					else if (serverResponse.equals("Invalid command ")) {
						System.out.println(serverResponse);
						break;
					}
					System.out.println(serverResponse);
				}
			}
			else if (prevCommand.equals("SEND")) {
				InputStream inputStream = socket.getInputStream();
				OutputStream outputStream = new FileOutputStream(fileToSave);
				byte[] buffer = new byte[8];
				int data;
				while ((data = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, data);
				}
				inputStream.close();
				outputStream.close();
			}
			else {
				serverResponse = inFromServer.readLine(); 
				System.out.println(serverResponse);	
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
