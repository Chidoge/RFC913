package lcho484_compsys725_a1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CredentialsHandler {
	
	private int userIndex = -1;
	
	private String ID = null;
	private String account = null;
	private String password = null;
	private boolean isAuthorized = false;

	
	public String USER(String[] args) {
		
		String response = null;
		String userID = args[1];
		
		ArrayList<String> userList = readFile("src/users.txt");
		
		
		for (int i = 0; i < userList.size(); i++) {
			if (userID.equals(userList.get(i))) {
				ID = userID;
				response = "+User-id valid, send account and password";
				return response;
			}
		}
		response = "-Invalid user-id, try again";
		return response;
	}
	
	
	public String ACCT(String[] args) {
		
		String accountName = args[1];
		
		/* Check if account exists, if it does, set this account and index to know which password to match to */
		ArrayList<String> accountList = readFile("src/accounts.txt");
		for (int i = 0; i < accountList.size(); i++) {
			if (accountName.equals(accountList.get(i))) {
				account = accountName;
				this.userIndex = i;
				return "+Account valid, send password";
			}
		}
		return "-Invalid account, try again";	
	}
	
	
	public String PASS(String[] args) {
		
		String response = null;
		String password = args[1];
		
		ArrayList<String> passwords = readFile("src/passwords.txt");
		
		if (account == null) {
			response = "+Send account";
		}
		else {
			if (password.equals(passwords.get(userIndex))) {
				response = "! Logged in";
				isAuthorized = true;
			}
			else {
				response = "-Wrong password, try again";
			}
		}
		return response;
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

	
	public void setID(String ID) {
		this.ID = ID;
	}
	
	public void setAccount(String account) {
		this.account = account;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getID() {
		return ID;
	}
	
	public String getAccount() {
		return account;
	}
	
	public String getPassword() {
		return password;
	}
	
	public boolean isAuthorized() {
		return isAuthorized;
	}
	
}
