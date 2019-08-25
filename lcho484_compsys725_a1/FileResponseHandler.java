package lcho484_compsys725_a1;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileResponseHandler {

	
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
	public long SEND(long fileSize, String fileToSave, Socket socket) throws IOException {
		
		/* Only save file if RETR was called successfully */
		if (fileSize != -1) {
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = new FileOutputStream(fileToSave);
			
			byte[] buffer = new byte[1];
			int count = 0;
			long i = 0;
			int milestone = 0;
			while (i < fileSize) {
				count = inputStream.read(buffer);
				outputStream.write(buffer, 0, count);
				i++;
				if ((double)i/fileSize * 100 >= milestone) {
					System.out.println(milestone + "%");
					if (milestone == 100) {
						System.out.println(fileToSave + " successfully downloaded!");
					}
					milestone+=10;
				}
			}
			
			outputStream.close();
			return -1;
		}
		return -1;
	}
	
	
	public long RETR(BufferedReader inFromServer) throws IOException {
		
		String serverResponse = inFromServer.readLine();

		System.out.println(serverResponse);
		if (!serverResponse.equals("-File doesn't exist")) {
			return Long.parseLong(serverResponse);
		}
		else {
			return -1;
		}
	}
}
