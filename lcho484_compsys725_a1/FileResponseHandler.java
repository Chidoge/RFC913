package lcho484_compsys725_a1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileResponseHandler {
	
	private String fileToSave = "";
	private long fileSize = 0;

	private String fileToSend = "";
	private long sendFileSize = 0;
	
	public void LIST(BufferedReader inFromServer) throws IOException {
		String serverResponse;
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
	
	
	/* Return -1 after calling send to disable flag */
	public void SEND(Socket socket) throws IOException {
		
		/* Only save file if RETR was called successfully */
		if (fileSize != -1) {
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = new FileOutputStream(fileToSave);
			
			byte[] buffer = new byte[1];
			int read = 0;
			long progress = 0;
			int nextMilestone = 0;
			
			/* Keep receiving data until reported number of bytes received */
			while (progress < fileSize) {
				read = inputStream.read(buffer);
				outputStream.write(read);
				progress++;
				
				/* Print download progress for user */
				if ((double)progress/fileSize * 100 >= nextMilestone) {
					System.out.println(nextMilestone + "%");
					if (nextMilestone == 100) {
						System.out.println(fileToSave + " successfully downloaded!");
					}
					nextMilestone+=10;
				}
			}
			outputStream.close();
			fileSize = -1;
		}
		else {
			System.out.println("Please call RETR first");
			fileSize = -1;
		}
	}
	
	
	/* Handles the server response after calling STOR then SIZE. */
	public void SIZE(BufferedReader inFromServer, Socket socket) throws IOException {
		String serverResponse = inFromServer.readLine();
		System.out.println(serverResponse);
		
		/* If ok to send file */
		if (serverResponse.equals("+ok, waiting for file")) {
			try {

				InputStream inputStream = new FileInputStream(fileToSend);
				DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
				
				byte[] buffer = new byte[1];
				int progress = 0;
				int read = 0;
				int nextMilestone = 0;
				
				while ((read = inputStream.read(buffer)) > 0) {
					outToClient.write(buffer, 0, read);
					progress++;
					if ((double)progress/sendFileSize * 100 >= nextMilestone) {
						System.out.println(nextMilestone + "%");
						nextMilestone+=10;
					}
				};
				outToClient.flush();
				inputStream.close();
				
				serverResponse = inFromServer.readLine();
				System.out.println(serverResponse);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/* Handle server side error */
		else {
			
		}
	}
	
	
	public void RETR(BufferedReader inFromServer, String[] args) throws IOException {
		
		String serverResponse = inFromServer.readLine();
		System.out.println(serverResponse);
		
		if (serverResponse.equals("-File doesn't exist")) {
			fileSize = -1;
		}
		/* Prepare to save file */
		else {
			fileToSave = args[1];
			fileSize =  Long.parseLong(serverResponse);
		}
	}
	
	
	public void STOR(BufferedReader inFromServer, String[] args) throws IOException {
		
		String serverResponse = inFromServer.readLine();
		System.out.println(serverResponse);
		
		/* Handle different responses depending on type for STOR*/
		String type = args[1].toUpperCase();
		if (type.equals("NEW")) {
			if (serverResponse.equals("+File exists, will create new generation of file") ||
				serverResponse.equals("+File does not exist will create new file")) {
				File file = new File(args[2]);
				sendFileSize = file.length();
				fileToSend = args[2];
				System.out.println("File size is " + Long.toString(sendFileSize) + " bytes");
			}			
		}
		else if (type.equals("OLD")) {
			if (serverResponse.equals("+Will write over old file") ||
				serverResponse.equals("+Will create new file")) {
				File file = new File(args[2]);
				sendFileSize = file.length();
				fileToSend = args[2];
				System.out.println("File size is " + Long.toString(sendFileSize) + " bytes");
			}
		}
		else if (type.equals("APP")) {
			
		}
	}
	
}
