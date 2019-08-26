package lcho484_compsys725_a1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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
	
	private String STORFilename = "";
	private String STORState = "NONE";
	private long STORFilesize = 0;
	
	
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
			return "-" + currentDirectory + "/" + args[2] +" does not exist\r\nEOF";
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
	
	
	/* Set up RETR status */
	public String RETR(String[] args, CredentialsHandler client) {
		
		/* Make sure client is authorized */
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
	
	
	/* If RETR was set up, return filename so handler can send to client */
	public String SEND(String[] args, CredentialsHandler client) {
		
		if (RETRState == "PENDING") {
			RETRState = "NONE";
			return RETRFilename;
		}
		else {
			return "";
		}
	}
	
	
	/* Cancel RETR operation */
	public String STOP(String[] args) {
		
		if (RETRState == "PENDING") {
			RETRState = "NONE";
			return "+ok, RETR aborted";
		}
		else {
			return "-RETR not in progress";
		}
	}
	
	
	/* Delete file */
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
	
	
	/* Prepare to receive file from client */
	public String STOR(String[] args, CredentialsHandler client) {
		
		if (args.length < 3) {
			return "-Not enough arguments";
		}
		
		if (!client.isAuthorized()) {
			//TODO
			//Make sure client handles this
			return "-Please log in";
		}
		
		/* Handle different types of STOR */
		String type = args[1].toUpperCase();
		if (type.equals("NEW")) {
			STORState = "PENDING";
			return newHandler(args[2]);
		}
		else if (type.equals("OLD")) {
			STORState = "PENDING";
			return oldHandler(args[2]);
		}
		else if (type.equals("APP")) {
			STORState = "PENDING";
			return appHandler(args[2]);
		}
		return "-Please specify a valid type";
	}
	
	
	/*  */
	public String SIZE(String[] args) {
		
		if (STORState.equals("NONE")) {
			return "-Please call STOR first";
		}
		
		/* Check if disk has sufficient space */
		STORFilesize = Long.parseLong(args[1]);
		File directory = new File(currentDirectory);
		
		if (directory.getUsableSpace() >= STORFilesize) {
			STORState = "WAITING";
			return "+ok, waiting for file";	
		}
		else {
			STORState = "NONE";
			return "-Not enough room, don't send it";
		}

	}
	
	
	public String waitFile(Socket socket) throws IOException {
		
		InputStream inputStream = socket.getInputStream();
		OutputStream outputStream = new FileOutputStream(STORFilename);
		int i = 0;
		int count = 0;
		byte[] buffer = new byte[1];
		
		while (i < STORFilesize) {
			count = inputStream.read(buffer);
			outputStream.write(buffer, 0, count);
			i++;
		}
		outputStream.close();
		STORState = "NONE";
		return "+Saved " + STORFilename;

	}
	
	
	public String newHandler(String filename) {
		File file = new File(currentDirectory + "/" + filename);
		if (!file.exists()) {
			return "+File does not exist, will create new file";
			
		}
		else {
			/* Check through 50 generations of the same file */
			for (int i = 1; i < 50; i++) {

				int cutOffPoint = filename.lastIndexOf(".");
				String newFilename = filename.substring(0, cutOffPoint) + 
									" (" + Integer.toString(i) + ")" +
									filename.substring(cutOffPoint, filename.length());
				File testFile = new File(currentDirectory + "/" + newFilename);
				
				if (!testFile.exists()) {
					STORFilename = currentDirectory + "/" + newFilename;
					return "+File exists, will create new generation of file";
					
				}
			}
			
			/* If have to create more than the 50th generation, do not create file */
			return "-File exists, but system does not support generations";		
		}
	}
	
	
	public String oldHandler(String filename) {
		
		File file = new File(currentDirectory + "/" + filename); 
		
		if (!file.exists()) {
			return "+Will create new file";
			
		}
		else {
			return "+Will write over old file";		
		}
	}
	
	
	public String appHandler(String filename) {
		
		return "";
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
	
	public String getSTORState() {
		return STORState;
	}

}
