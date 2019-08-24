package lcho484_compsys725_a1;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SocketHandler {
	
	private Socket socket;
	
	private String userID = null;
	private String userAccount = null;
	private String userPassword = null;
	private int userIndex = -1;
	private boolean isLoggedIn = false;
	
	private String code = null;
	private String message = null;
	
	public SocketHandler(Socket socket) {
		this.socket = socket;
	}
	
	public void start() {
		
		while(true) {
			
			/* Get command */
			try {
				
				BufferedReader userInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String userMessage = userInput.readLine();
				String[] args = userMessage.split(" ");

				if (args[0].equals("DONE")) {
					DONE();
				}
				else if (args.length > 1) {
					switch(args[0]) {
						case "USER":
							USER(args[1]);
							writeResult();
							break;
						case "ACCT":
							ACCT(args[1]);
							System.out.println("DOING IT");
							writeResult();
							break;
						case "PASS":
							PASS(args[1]);
							writeResult();
							break;
						case "LIST":
							LIST(args[1], args[2]);
							writeResult();
							break;
						default:
							INVALID();
							break;
					}	
				}
				else {
					INVALID();
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
	
	
	private void USER(String userID) {
		
		ArrayList<String> userList = readFile("src/users.txt");
		for (int i = 0; i < userList.size(); i++) {
			if (userID.equals(userList.get(i))) {
				this.userID = userID;
				code = "+";
				message = "User-id valid, send account and password";
				return;
			}
		}
		code = "-";
		message = "Invalid user-id, try again";
	}
	
	
	private void ACCT(String accountName) {
		
		ArrayList<String> accountList = readFile("src/accounts.txt");
		for (int i = 0; i < accountList.size(); i++) {
			if (accountName.equals(accountList.get(i))) {
				code = "+";
				message = "Account valid, send password";
				this.userAccount = accountName;
				this.userIndex = i;
				return;
			}
		}
		code = "-";
		message = "Invalid account, try again";	
	}
	
	
	private void PASS(String password) {
		ArrayList<String> passwords = readFile("src/passwords.txt");
		
		if (userAccount == null) {
			code = "+";
			message = "Send account";
			return;
		}
		else {
			if (password.equals(passwords.get(userIndex))) {
				code = "! ";
				message = "Logged in";
				isLoggedIn = true;
			}
			else {
				code = "-";
				message = "Wrong password, try again";
			}
		}
	}
	
	
	private void LIST(String format, String directoryPath) {
		
		String directory = System.getProperty("user.dir") + "/" + directoryPath;
		
		File fileDirectory = new File(directory);
		
		File[] fileList = fileDirectory.listFiles();
		
		
		if (format.equals("F")) {
			code = "";
			message = directory + "  ";
			
			for (int i = 0; i < fileList.length; i++) {
				message += fileList[i].getName() + " ";
			}
			message += "\n";
		}
		else if (format.equals("V")) {
			code = "";
			message = directory + "\r\n";
			
			for (int i = 0; i < fileList.length; i++) {
				message += fileList[i].getName() + 
							", Size: " + fileList[i].length()/1024 + "KB" + 
							", Last modified: " + convertToReadable(fileList[i].lastModified()) + "\r\n";
			}
			message += "EOF";
		}
	}
	
	
	private String convertToReadable(long epoch) {
		Date date = new Date(epoch);
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		return dateFormat.format(date);
	}
	
	
	private void INVALID() {
	    try {
			DataOutputStream  outToClient = new DataOutputStream(socket.getOutputStream());
			outToClient.writeBytes("Invalid command" + '\n');
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	
	private ArrayList<String> readFile(String filename) {
		
		ArrayList<String> results = new ArrayList<String>();
		
		try {
			String line;
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			/* Read until end of file and then return results */
			while((line = reader.readLine()) != null) {
				results.add(line);
			}
			reader.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
	private void writeResult() {
		
	    try {
	    	
			DataOutputStream  outToClient = new DataOutputStream(socket.getOutputStream());
			outToClient.writeBytes(code + message + "\n");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
