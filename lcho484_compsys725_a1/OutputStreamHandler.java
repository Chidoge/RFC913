package lcho484_compsys725_a1;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class OutputStreamHandler {
	
	private Socket socket;
	
	public OutputStreamHandler(Socket socket) {
		this.socket = socket;
	}
	
	public void writeResult(String response) {
		
	    try {
			DataOutputStream  outToClient = new DataOutputStream(socket.getOutputStream());
			outToClient.writeBytes(response+ "\n");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	
	public void sendData(String filename) {
		
		try {
			InputStream inputStream = new FileInputStream(filename);
			DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
			
			byte[] buffer = new byte[8];
			int data = 0;
			
			while ((data = inputStream.read(buffer)) != -1) {
				outToClient.write(buffer, 0, data);
			}
			inputStream.close();
			outToClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void writeInvalid() {
	    try {
			DataOutputStream  outToClient = new DataOutputStream(socket.getOutputStream());
			outToClient.writeBytes("Invalid command " + '\n');
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
