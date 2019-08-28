package SFTP.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientTester {
	
	private Socket socket;
	private String[] prevArgs;
	private OutputGenerator tester;
	
	private String PATH = System.getProperty("user.dir");
	private FileResponseHandler fileResponseHandler;
	
	public ClientTester(Socket socket, boolean runFromCMD, String[] args) { 
		this.socket = socket;
		fileResponseHandler = new FileResponseHandler();
		
		if (runFromCMD) {
			PATH += "/SFTP/client/";
		}
		else {
			PATH +="/src/SFTP/client/";
		}
		
		/* Clear output file */
		File file = new File(PATH + "/" + "output.txt");
		file.delete();
		
		/* Prepare to generate test commands */
		tester = new OutputGenerator(PATH, args);
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
				System.out.println(inFromServer.readLine());
				System.out.println("Connection closed");
				socket.close();
				break;
			}
			
			/* --------------- Get server response ---------------- */
			
			/* Handles server responses for different commands */
			switch(prevArgs[0].toUpperCase()) {
				case "LIST":
					saveResp(fileResponseHandler.LIST(inFromServer));
					break;
				case "RETR":
					saveResp(fileResponseHandler.RETR(inFromServer, prevArgs));
					break;
				case "SEND":
					saveResp(fileResponseHandler.SEND(inFromServer, socket));
					break;
				case "STOR":
					saveResp(fileResponseHandler.STOR(inFromServer, prevArgs, socket));
					break;
				case "STOP":
					saveResp(fileResponseHandler.STOP(inFromServer));
					break;
				default:
					String response = inFromServer.readLine();
					saveResp(response);
					System.out.println(response); 
					break;
			}

		}
		
		tester.compareOutputs();
	}
	
	
	private String writeAndSendInputToServer(BufferedReader reader, DataOutputStream outToServer) throws IOException {
		
		System.out.println("Input: ");
		String line = tester.getNext();
		
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
	
	
	/* Save server responses to test behaviour */
	private void saveResp(String resp) throws IOException {
		
		File file = new File(PATH + "/" + "output.txt");
		FileWriter fileWriter = new FileWriter(file, true);
		
		fileWriter.write(resp  + "\n");
		fileWriter.close();
	}
	
}
