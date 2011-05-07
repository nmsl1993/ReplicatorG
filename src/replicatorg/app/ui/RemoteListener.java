package replicatorg.app.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumSet;

import replicatorg.app.Base;
import replicatorg.drivers.Driver;
import replicatorg.drivers.PenPlotter;
import replicatorg.drivers.RetryException;
import replicatorg.machine.model.AxisId;
import replicatorg.util.Point5d;

/* Stupid hack to inject GCode on the machine */
public class RemoteListener extends Thread {
	Driver driver;
	int port;
	boolean running;
  String penMode = "MEGA";

  double penUpAngle = 50;
  double penDownAngle = 30;
	
	ServerSocket serverSocket;
	Socket clientSocket;
	
	public RemoteListener(Driver driver, int port) {
		this.driver = driver;
		this.port = port;
		
		running = true;
	}
	
	private void runCommand(String command) throws RetryException {
		if (command.contentEquals("PD")) {
			Base.logger.info("Pen down!");
      if(penMode.equals("UNICORN")){
        ((PenPlotter)driver).setServoPos(0, penDownAngle);
      } else {
  			driver.enableFan();
      }
		}
		else if (command.contentEquals("PU")) {
			Base.logger.info("Pen up!");
      if(penMode.equals("UNICORN")){
        ((PenPlotter)driver).setServoPos(0, penUpAngle);
      } else {
        driver.disableFan();
      }
		}
		else if (command.contentEquals("HOME")) {
			Base.logger.info("Home!");
			EnumSet<AxisId> r = EnumSet.noneOf(AxisId.class);
			r.add(AxisId.X);
			driver.homeAxes(r, false, 3000);
			driver.setCurrentPosition(new Point5d());
		}
		else if (command.contentEquals("STOP")) {
			Base.logger.info("Stop!");
			driver.disableDrives();
			driver.disableFan();
			driver.closeValve();
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
				point.setAxis(AxisId.Y , -Double.parseDouble(bits[2]));
				driver.queuePoint(point);
			}
		}
    else if(command.startsWith("FR")) {
      String bits[] = command.split(" ");

      if (bits.length == 2) {
        driver.setFeedrate(Double.parseDouble(bits[1]));
      }
    }
    else if(command.startsWith("PM")) {
      String bits[] = command.split(" ");

      if (bits.length == 2) {
        penMode = bits[1];
      }
    }
    else if(command.startsWith("PUA")) {
      String bits[] = command.split(" ");

      if (bits.length == 2) {
        penUpAngle = (Double.parseDouble(bits[1]));
      }
    }
    else if(command.startsWith("PDA")) {
      String bits[] = command.split(" ");

      if (bits.length == 2) {
        penDownAngle = (Double.parseDouble(bits[1]));
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
	
	// TODO: What /should/ this be called?? It should also be thread-safe or something.
	public void shutdown() {
		if (clientSocket != null) {
			try {
				clientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Base.logger.severe("couldnt shutdown client listener");
			}
		}
		
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Base.logger.severe("couldnt shutdown socket server");
			}
		}
		running = false;
		interrupt();
	}
	
	public void run() {
		
		// Try to make a connection; if we can't, stop!
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			Base.logger.severe("Error Setting up port: " + e.getMessage());
			return;
		}	
		
		while (running) {
			Socket clientSocket = null;
			
			try {
				clientSocket = serverSocket.accept();
			} catch (IOException e1) {
				Base.logger.severe("Error accepting client socket: " + e1.getMessage());
				break;
			}
			Base.logger.info("Accepted connection from: " + clientSocket.getRemoteSocketAddress());
			
//				PrintWriter out = new PrintWriter(
//				        clientSocket.getOutputStream(), true);

			
			BufferedReader in;
			try {
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			} catch (IOException e1) {
				Base.logger.severe("Error creating buffered reader: " + e1.getMessage());
				break;
			}
			
			String inputLine;
//				String outputLine;
			
			//initiate conversation with client
			
			try {
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
			} catch (IOException e) {
				Base.logger.severe("Got IO exception while trying to read: " + e.getMessage());
			}
		}
		
		// If we need to clean up, do it now.
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				Base.logger.severe("Error closing port: " + e.getMessage());
			}
		}
	}
}
