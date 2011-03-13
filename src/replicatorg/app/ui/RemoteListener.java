package replicatorg.app.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumSet;

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
		else if (command.contentEquals("HOME")) {
			Base.logger.info("Home!");
			EnumSet<AxisId> r = EnumSet.noneOf(AxisId.class);
			r.add(AxisId.X);
			driver.homeAxes(r, false, 3000);
			driver.setCurrentPosition(new Point5d());
		}
		else if (command.contentEquals("LE")) {
			Base.logger.info("Lights on!");
			driver.openValve();
		}
		else if (command.contentEquals("LD")) {
			Base.logger.info("Lights disable!");
			driver.closeValve();
		}
		else if(command.startsWith("PA")) {
			String bits[] = command.split(" ");
			
			if (bits.length == 3) {
				Point5d point = new Point5d();
				point.setAxis(AxisId.X , Double.parseDouble(bits[1]));
				point.setAxis(AxisId.Y , Double.parseDouble(bits[2]));
				driver.queuePoint(point);
			}
		}
		else if (command.contentEquals("SD")) {
			Base.logger.info("Steppers disable!");
			driver.disableDrives();
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
					boolean finished = false;
					while (!finished) {
						try {
							runCommand(inputLine);
							finished = true;
						} catch (RetryException e) {
							Base.logger.severe("retrying command: " + inputLine);
						}
					}
				}				    
			    
			    serverSocket.close();
			} catch (IOException e) {
			    Base.logger.severe("oops! This error: " + e.getMessage());
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
