package SFTP.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
	Class is used to generate commands used for testing the SFTP server */

public class OutputGenerator {
	
	private ArrayList<String> commands;
	private String PATH;
	private int i = 0;
	private String test_number = "";
	
	public OutputGenerator(String path, String[] args) {
		
		PATH = path;
		
		if (args.length > 0) {
			test_number = args[0];
			if (args[0].equals("1")) {
				commands = readFile(path + "commands.txt");		
			}
			else if (args[0].equals("2"))  {
				commands = readFile(path + "commands_2.txt");	
			}
		}
		else {
			test_number = "1";
			commands = readFile(path + "commands.txt");	
		}
		
		System.out.println("\n----- RUNNING " + (commands.size() -1) + " COMMANDS -----\n");
		
	}


	public String getNext() {
		if (i == commands.size()) {
			return "";
		}
		return commands.get(i++);
	}
	

	public void compareOutputs() {
		
		ArrayList<String> answers;
		if (test_number.equals("1")) {
			answers = readFile(PATH + "/" + "correct_outputs.txt");
		}
		else {
			answers = readFile(PATH + "/" + "correct_outputs_2.txt");
		}
		
		ArrayList<String> outputs = readFile(PATH + "/" + "output.txt");
		
		int testsPassed = 0;
		System.out.println();
		for (int i = 0; i < answers.size(); i++) {
			if (answers.get(i).equals(outputs.get(i))) {
				System.out.println("TEST " + i + " PASSED");
				testsPassed++;
			}
			else {
				System.out.println("TEST " + i + " FAILED: ");
				System.out.println("Expected output was " + answers.get(i) + "\nYour output was " + outputs.get(i));
			}
		}
		
		System.out.println("\n-----  " + testsPassed + " / " + answers.size() + " TESTS PASSED -----\n\n");
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
	

}
