package replicatorg.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumSet;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.ValueNode;

import replicatorg.app.Base;
import replicatorg.drivers.Driver;
import replicatorg.drivers.RetryException;
import replicatorg.machine.model.AxisId;
import replicatorg.util.Point5d;

/* Stupid hack to inject GCode on the machine */
public class RemoteController extends Thread {
	Driver driver;
	int port;
	boolean running;
	
	ServerSocket serverSocket;
	Socket clientSocket;
	
	public RemoteController(Driver driver, int port) {
		this.driver = driver;
		this.port = port;
		
		running = true;
	}
	
	private void doOpenFile(String filename) {
		Base.getEditor().handleOpen(filename);
	}
	
	private void doSkein() {
		Base.getEditor().runToolpathGenerator(true);
	}

	private void doBuild(String target, String fileName) {
		if (target.contentEquals("direct")) {
			Base.getEditor().handleBuild();
		}
		else if (target.contentEquals("file")) {
			Base.getEditor().handleBuildToFile(fileName);
		}
		else if (target.contentEquals("remote_file")) {
			Base.getEditor().handleUpload(fileName);
		}
		else {
			Base.logger.severe("didn't understand target: " + target);
		}
	}
		
	private void runInstruction(String instruction) throws RetryException {
		// TODO: bury this :-)

		ObjectMapper m = new ObjectMapper();

		String command = null;
		
		try {
			
			JsonNode rootNode = m.readTree(instruction);
			
			command = rootNode.path("command").getTextValue();
			
			Base.logger.fine("Got command: " + command);
			
			if (command.contentEquals("open")) {
				String filename = rootNode.path("filename").getTextValue();
				Base.logger.fine("Attempting to open file: " + filename);
				doOpenFile(filename);
			}
			else if (command.contentEquals("skein")) {
				Base.logger.fine("Attempting to skein file");
				doSkein();
			}
			else if (command.contentEquals("build")) {
				String target = rootNode.path("target").getTextValue();
				String fileName = rootNode.path("filename").getTextValue();
				
				Base.logger.fine("Attempting to build to target: " + target + " with filename: " + fileName);
				doBuild(target, fileName);
			}
			// TODO: This goes away.
			else if (command.contentEquals("printToFile")) {
				// First open the file
				String inputFile = rootNode.path("inputFile").getTextValue();
				Base.logger.fine("Attempting to open file: " + inputFile);
				doOpenFile(inputFile);
				
				// Next, skein it
				Base.logger.fine("Attempting to skein file");
				doSkein();
				
				// TODO: wait for skein to finish, how?
				while (Base.getEditor().toolpathGeneratorBusy()) {
					// la la la
				}
				
				// Finally, print it.
				String destinationFile = rootNode.path("destinationFile").getTextValue();
				
				Base.logger.fine("Attempting to build to file: " + destinationFile);
				doBuild("file", destinationFile);
			}
			else if (command.contentEquals("updateViewport")) {
				Double zoom = rootNode.path("zoom").getDoubleValue();
				
				Base.logger.fine("Updating viewport: " + zoom);
				Base.getEditor().getPreviewPanel().updateZoom(zoom);
			}
			else {
				Base.logger.severe("Didn't understand command: " + command);
			}
		} catch (JsonProcessingException e) {
			Base.logger.severe("Missing something, did you use a good command?: " + instruction);
			e.printStackTrace();
			return;
		} catch (IOException e) {
			Base.logger.severe("Invalid JSON string: " + instruction);
			e.printStackTrace();
			return;
		}

//		// ensure that "last name" isn't "Xmler"; if is, change to "Jsoner"
//		JsonNode nameNode = rootNode.path("name");
//		String lastName = nameNode.path("last").getTextValue();
//		if ("xmler".equalsIgnoreCase(lastName)) {
//		  ((ObjectNode)nameNode).put("last", "Jsoner");
//		}
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
							runInstruction(inputLine);
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

