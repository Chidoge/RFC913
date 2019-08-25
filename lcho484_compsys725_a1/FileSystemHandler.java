package lcho484_compsys725_a1;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileSystemHandler {
	
	private String currentDirectory = System.getProperty("user.dir");
	private String fileToRename = "";
	
	private String pendingPath = "";
	private String pendingCanonicalPath = "";
	private String CDIRState = "NONE";
	
	private String RETRState = "NONE";
	private String RETRFilename = "";
	
	
	public String CDIR(String[] args, CredentialsHandler client) {
		
		/* Updates directory according by using RELATIVE paths - cannot use absolute path to change directory*/
		String newDirectory = currentDirectory + "/" + args[1];
		
		/* Check validity of specified directory */
		File directory = new File(newDirectory);
		
		if (!directory.exists()) {
			return "-Can't connect to directory because: Directory does not exist";
		}
		else if (!directory.isDirectory()) {
			return "-Can't connect to directory because: Specified path is not a directory";
		}
		else {
			try {
				if (!client.isAuthorized()) {
					CDIRState = "PENDING";
					pendingPath = directory.getAbsolutePath();
					pendingCanonicalPath = directory.getCanonicalPath();
					return "+directory ok, send account/password";
				}
				else {
					CDIRState = "AUTHORIZED";
					currentDirectory = directory.getAbsolutePath();
					return "!Changed working dir to " + directory.getCanonicalPath();
				}	
			} catch (IOException e) {
				e.printStackTrace();
				return "-Can't connect to directory because: Something went wrong";
			}
		}
	}
	
	
	public String LIST(String[] args, CredentialsHandler credentialsHandler) {
	
		
		String response = "Invalid command ";
		
		String format = args[1].toUpperCase();
		String directory = currentDirectory;
		
		/* Only load custom directory path is argument is passed */
		if (args.length > 2) {
			directory = currentDirectory + "/" + args[2];
		}
		
		/* Get files in file directory */
		File fileDirectory = new File(directory);
		
		/* Check directory exist */
		if (fileDirectory.exists()) {
			File[] fileList = fileDirectory.listFiles();
			
			/* Check format */
			/* Standard */
			if (format.equals("F")) {
				
				response = "";
				
				for (int i = 0; i < fileList.length; i++) {
					response += fileList[i].getName() + "\r\n";
				}
				response += "EOF";
			}
			/* Verbose - shows file size in KB and last modified date */
			else if (format.equals("V")) {
				
				response = "";
				
				for (int i = 0; i < fileList.length; i++) {
					response += fileList[i].getName() + 
								", Size: " + fileList[i].length()/1024 + "KB" + 
								", Last modified: " + convertToReadable(fileList[i].lastModified()) + "\r\n";
				}
				response += "EOF";
			}
			return response;
		}
		else {
			return "-" + currentDirectory + "/" + args[2] +" does not exist";
		}

	}
	
	
	public String NAME(String[] args, CredentialsHandler client) {
		
		if (!client.isAuthorized()) {
			return "-send account/password";
		}
		File file = new File(currentDirectory + "/" + args[1]);
		if (file.exists()) {
			fileToRename = args[1];
			return "+File exists";
		}
		else {
			return "-Can't find " + currentDirectory + "/" + args[1];
		}
		
	}
	
	
	
	public String TOBE(String[] args, CredentialsHandler client) {
		
		if (fileToRename.equals("")) {
			return "-File wasn't renamed because you have not specified a file to rename";
		}
		
		/* Attempt to rename the specified file */
		String newFilename = args[1];
		File oldFile = new File(currentDirectory + "/" + fileToRename);
		File renamedFile = new File(currentDirectory + "/" + newFilename);
		boolean fileDidRename = oldFile.renameTo(renamedFile);
		
		if (fileDidRename) {
			String tempOldFilename = fileToRename;
			fileToRename = "";
			return tempOldFilename + " renamed to " + newFilename;
		}
		else {
			/* Old file may have been deleted by different user */
			if (!oldFile.exists()) {
				return "-File wasn't renamed because the old file does not exist";
			}
			else if (renamedFile.exists()) {
				return "-File wasn't renamed because the specified file name already exists";
			}
			else {
				return "-File wasn't renamed because something went wrong";
			}
		}
	}
	
	
	
	public String RETR(String[] args, CredentialsHandler client) {
		if (!client.isAuthorized()) {
			return "-send account/password";
		}
		else {
			File file = new File(currentDirectory + "/" + args[1]);
			if (!file.exists()) {
				return "-File doesn't exist";
			}
			else {
				RETRState = "PENDING";
				RETRFilename = currentDirectory + "/" + args[1];
				return Long.toString(file.length());
			}

		}
	}
	
	
	public String SEND(String[] args, CredentialsHandler client) {
		
		if (RETRState == "PENDING") {
			RETRState = "NONE";
			return RETRFilename;
		}
		else {
			return "";
		}
	}
	
	public String STOP(String[] args) {
		
		if (RETRState == "PENDING") {
			return "+ok, RETR aborted";
		}
		else {
			return "-RETR not in progress";
		}
	}
	
	
	public String KILL(String[] args, CredentialsHandler client) {
		
		/* Open file to prepare for deletion */
		String filename = args[1];
		String directory = System.getProperty("user.dir") + "/" + filename;
		File file = new File(directory);
		
		if (file.exists()) {
			/* Respond successfully if deleted */
			if (file.delete()) {
				return "+" + filename + " deleted";
			}
			/* Respond fail if not deleted for some reason */
			else {
				return "-" + filename + " not deleted";
			}
		}
		else {
			return "-" + filename + " does not exist";
		}
	}
	
	
	private String convertToReadable(long epoch) {
		Date date = new Date(epoch);
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

		return dateFormat.format(date);
	}
	
	public String getCDIRState() {
		return CDIRState;
	}
	
	public void authorizePendingPath() {
		CDIRState = "AUTHORIZED";
		currentDirectory = pendingPath;
	}
	
	public String getCanonicalPath() {
		return pendingCanonicalPath;
	}
	
}
