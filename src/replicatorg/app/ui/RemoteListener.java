package replicatorg.app.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import replicatorg.app.Base;
import replicatorg.drivers.Driver;
import replicatorg.drivers.RetryException;
import replicatorg.machine.model.AxisId;
import replicatorg.util.Point5d;

/* Stupid hack to inject GCode on the machine */
public class RemoteListener extends Thread {
	Driver driver;
	int port;
	
	ServerSocket serverSocket;
	
	public RemoteListener(Driver driver, int port) {
		this.driver = driver;
		this.port = port;
	}
	
	private void runCommand(String command) throws RetryException {
		if (command.contentEquals("PD")) {
			Base.logger.info("Pen down!");
			driver.enableFan();
		}
		else if (command.contentEquals("PU")) {
			Base.logger.info("Pen up!");
			driver.disableFan();
		}
		else if(command.startsWith("PA")) {
			String bits[] = command.split(" ");
			
			if (bits.length == 3) {
				Point5d point = new Point5d();
				point.setAxis(AxisId.Y , Double.parseDouble(bits[1]));
				point.setAxis(AxisId.X , Double.parseDouble(bits[2]));
				driver.queuePoint(point);
			}
		}
		else {
			Base.logger.severe("Didn't understand command:" + command);
		}
	}
	
	public void run() {
		while (true) {
			Socket clientSocket = null;
			
			try {
			    serverSocket = new ServerSocket(port);
			    
			    clientSocket = serverSocket.accept();
			    
				PrintWriter out = new PrintWriter(
				        clientSocket.getOutputStream(), true);
	
				BufferedReader in = new BufferedReader(
		                  new InputStreamReader(
		                      clientSocket.getInputStream()));
				String inputLine, outputLine;
				
				//initiate conversation with client
				
				while ((inputLine = in.readLine()) != null) {
					try {
						runCommand(inputLine);						
					} catch (RetryException e) {
						Base.logger.severe("dropped command: " + inputLine);
					}
				}				    
			    
			    serverSocket.close();
			} catch (IOException e) {
			    Base.logger.severe("oops!");
			}
			
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
}
