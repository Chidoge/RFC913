package lcho484_compsys725_a1;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
	
	public static void main(String[] args) throws Exception {
		
		String IP = "localhost";
		int port = 3000;
		
		/* Open socket */
		Socket clientSocket = new Socket(IP, port);
		System.out.println("Client started with server -  " + IP + ":" + port);
		
		/* Request outputter */
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());	
		BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
		
		/* Response reader */
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String serverResponse;
		
		while (true) {
			
			System.out.println("Input: ");
			String line = userInput.readLine();
			
			/* Write to server */
			outToServer.writeBytes(line + '\n');
			
			if(line.equals("DONE")) {
				System.out.println("Closing socket...");
				clientSocket.close();
				break;
			}
			
			/* Get server response */
			
			/* Print on multiple lines when calling LIST */
			if (line.split(" ")[0].equals("LIST")) {
				while ((serverResponse = inFromServer.readLine()) != null) { 
					if (serverResponse.equals("EOF")) {
						break;
					}
					System.out.println(serverResponse);
				}
			}
			else {
				serverResponse = inFromServer.readLine(); 
				System.out.println(serverResponse);	
			}

		
		}
	}
}
