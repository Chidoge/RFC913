package lcho484_compsys725_a1;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileSystemHandler {
	
	
	public String LIST(String[] args) {
		
		String response = "";
		
		String format = args[1].toUpperCase();
		String directoryPath = "";
		
		/* Only load custom directory path is argument is passed */
		if (args.length > 2) {
			directoryPath = args[2];
		}
		
		/* Get files in file directory */
		String directory = System.getProperty("user.dir") + "/" + directoryPath;
		File fileDirectory = new File(directory);
		File[] fileList = fileDirectory.listFiles();
		
		/* Check format */
		/* Standard */
		if (format.equals("F")) {

			response = directory + "\r\n";
			
			for (int i = 0; i < fileList.length; i++) {
				response += fileList[i].getName() + "\r\n";
			}
			response += "EOF";
		}
		/* Verbose - shows file size in KB and last modified date */
		else if (format.equals("V")) {

			response = directory + "\r\n";
			
			for (int i = 0; i < fileList.length; i++) {
				response += fileList[i].getName() + 
							", Size: " + fileList[i].length()/1024 + "KB" + 
							", Last modified: " + convertToReadable(fileList[i].lastModified()) + "\r\n";
			}
			response += "EOF";
		}
		return response;
	}
	
	
	public String KILL(String[] args) {
		
		/* Open file to prepare for deletion */
		String filename = args[1];
		String directory = System.getProperty("user.dir") + "/" + filename;
		File file = new File(directory);
		
		/* Respond successfully if deleted */
		if (file.delete()) {
			return "+" + filename + " deleted";
		}
		/* Respond fail if not deleted for some reason */
		else {
			return "-" + filename + " not deleted";
		}
	}
	
	
	private String convertToReadable(long epoch) {
		Date date = new Date(epoch);
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		return dateFormat.format(date);
	}
	
}
