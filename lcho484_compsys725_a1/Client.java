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
			
			if(line.toUpperCase().equals("DONE")) {
				System.out.println("Socket closed");
				clientSocket.close();
				break;
			}
			
			
			/* Get server response */
			
			/* Print result on multiple lines when calling LIST */
			if (line.split(" ")[0].toUpperCase().equals("LIST")) {
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
			else {
				serverResponse = inFromServer.readLine(); 
				System.out.println(serverResponse);	
			}

		
		}
	}
}
