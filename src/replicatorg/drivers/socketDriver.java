package replicatorg.drivers;

import java.util.EnumSet;

import javax.vecmath.Point3d;

import org.w3c.dom.Node;

import replicatorg.app.Base;
import replicatorg.app.exceptions.BuildFailureException;
import replicatorg.machine.model.AxisId;
import replicatorg.machine.model.MachineModel;
import replicatorg.util.Point5d;

public class socketDriver implements Driver {

	// models for our machine
	protected MachineModel machine;
	
	// For the second: ignore init requests, just pretend we can connect.
	boolean isInitialized = false;
	
	@Override
	public void loadXML(Node xml) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPassthroughDriver() {
		return false;
	}

	@Override
	public void executeGCodeLine(String code) {
		Base.logger.severe("Ignoring executeGCode command: " + code);
	}

	@Override
	public boolean isFinished() {
		Base.logger.severe("Crowbaring the 'is Finished' query");
		return true;
	}

	@Override
	public boolean isBufferEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void assessState() {
		Base.logger.severe("Ignoring Assess State Request");
	}

	@Override
	public boolean hasError() {
		return false;
	}

	@Override
	public DriverError getError() {
		return null;
	}

	@Override
	public void checkErrors() throws BuildFailureException {
	}

	@Override
	public void initialize() throws VersionException {
		Base.logger.severe("Faking initialize");
		isInitialized = true;
	}

	@Override
	public void uninitialize() {
		Base.logger.severe("Faking uninitialize");
		isInitialized = false;
	}

	@Override
	public boolean isInitialized() {
		// TODO Auto-generated method stub
		return isInitialized;
	}

	@Override
	public void dispose() {
		Base.logger.severe("Ignoring Dispose Request");
	}

	@Override
	public MachineModel getMachine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMachine(MachineModel m) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDriverName() {
		return "Socket Driver";
	}

	@Override
	public String getFirmwareInfo() {
		return "No Firmware";
	}

	@Override
	public Version getVersion() {
		return new replicatorg.drivers.Version(0,0);
	}

	@Override
	public void updateManualControl() {
		Base.logger.severe("Ignoring Update Manual Control Request");
	}

	@Override
	public Version getMinimumVersion() {
		return new replicatorg.drivers.Version(0,0);
	}

	@Override
	public Version getPreferredVersion() {
		return new replicatorg.drivers.Version(0,0);
	}

	@Override
	public void setCurrentPosition(Point5d p) throws RetryException {
		Base.logger.severe("Ignoring Set Current Position Request");
	}

	@Override
	public void storeHomePositions(EnumSet<AxisId> axes) throws RetryException {
		Base.logger.severe("Ignoring Store Home Positions Request");
	}

	@Override
	public void recallHomePositions(EnumSet<AxisId> axes) throws RetryException {
		Base.logger.severe("Ignoring Recall Home Positions Request");
	}

	@Override
	public boolean positionLost() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Point5d getCurrentPosition(boolean update) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void invalidatePosition() {
		Base.logger.severe("Ignoring Invalidate Position Request");
	}

	@Override
	public void queuePoint(Point5d p) throws RetryException {
		Base.logger.severe("Ignoring Queue Point Request");
	}

	@Override
	public Point3d getOffset(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOffsetX(int i, double j) {
		Base.logger.severe("Ignoring setOffsetX request");
	}

	@Override
	public void setOffsetY(int i, double j) {
		Base.logger.severe("Ignoring setOffsetY request");
	}

	@Override
	public void setOffsetZ(int i, double j) {
		Base.logger.severe("Ignoring setOffsetZ request");
	}

	@Override
	public Point5d getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void requestToolChange(int toolIndex, int timeout)
			throws RetryException {
		Base.logger.severe("Ignoring Request Tool Change Request");
	}

	@Override
	public void selectTool(int toolIndex) throws RetryException {
		Base.logger.severe("Ignoring Select Tool Request");
	}

	@Override
	public void setFeedrate(double feed) {
		Base.logger.severe("Ignoring Set Feedrate Request");
	}

	@Override
	public double getCurrentFeedrate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void homeAxes(EnumSet<AxisId> axes, boolean positive, double feedrate)
			throws RetryException {
		Base.logger.severe("Ignoring Home Axes Request");
	}

	@Override
	public void delay(long millis) throws RetryException {
		Base.logger.severe("Ignoring Delay Request");
	}

	@Override
	public void openClamp(int clampIndex) {
		Base.logger.severe("Ignoring Open Clamp Request");
	}

	@Override
	public void closeClamp(int clampIndex) {
		Base.logger.severe("Ignoring Close Clamp Request");
	}

	@Override
	public void enableDrives() throws RetryException {
		Base.logger.severe("Ignoring Enable Drives Request");
	}

	@Override
	public void disableDrives() throws RetryException {
		Base.logger.severe("Ignoring Disable Drives Request");
	}

	@Override
	public void changeGearRatio(int ratioIndex) {
		Base.logger.severe("Ignoring Change Gear Ratio Request");
	}

	@Override
	public void readToolStatus() {
		Base.logger.severe("Ignoring Read Tool Status Request");
	}

	@Override
	public int getToolStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMotorDirection(int dir) {
		Base.logger.severe("Ignoring Set Motor Direction Request");
	}

	@Override
	public void setMotorRPM(double rpm) throws RetryException {
		Base.logger.severe("Ignoring Set Motor RPM Request");
	}

	@Override
	public void setMotorSpeedPWM(int pwm) throws RetryException {
		Base.logger.severe("Ignoring Set Motor Speed PWM Request");
	}

	@Override
	public double getMotorRPM() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMotorSpeedPWM() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void enableMotor() throws RetryException {
		Base.logger.severe("Ignoring Enable Motor Request");
	}

	@Override
	public void enableMotor(long millis) throws RetryException {
		Base.logger.severe("Ignoring Enable Motor (millis()) Request");
	}

	@Override
	public void disableMotor() throws RetryException {
		Base.logger.severe("Ignoring Disable Motor Request");
	}

	@Override
	public void setSpindleRPM(double rpm) throws RetryException {
		Base.logger.severe("Ignoring Set Spindle RPM Request");
	}

	@Override
	public void setSpindleSpeedPWM(int pwm) throws RetryException {
		Base.logger.severe("Ignoring Set Spindle Speed PWM Request");
	}

	@Override
	public void setSpindleDirection(int dir) {
		Base.logger.severe("Ignoring Set Spindle Direction Request");
	}

	@Override
	public double getSpindleRPM() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSpindleSpeedPWM() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void enableSpindle() throws RetryException {
		Base.logger.severe("Ignoring Enable Spindle Request");
	}

	@Override
	public void disableSpindle() throws RetryException {
		Base.logger.severe("Ignoring Disable Spindle Request");
	}

	@Override
	public void setTemperature(double temperature) throws RetryException {
		Base.logger.severe("Ignoring Set Temperature Request");
	}

	@Override
	public void readTemperature() {
		Base.logger.severe("Ignoring Read Temperature Request");
	}

	@Override
	public double getTemperature() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTemperatureSetting() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPlatformTemperature(double temperature)
			throws RetryException {
		Base.logger.severe("Ignoring Set Platform Temperature Request");
	}

	@Override
	public void readPlatformTemperature() {
		Base.logger.severe("Ignoring Read Platform Temperature Request");
	}

	@Override
	public double getPlatformTemperature() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getPlatformTemperatureSetting() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setChamberTemperature(double temperature) {
		Base.logger.severe("Ignoring Set Chamber Temperature Request");
	}

	@Override
	public void readChamberTemperature() {
		Base.logger.severe("Ignoring Read Chamber Temperature Request");
	}

	@Override
	public double getChamberTemperature() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void enableFloodCoolant() {
		Base.logger.severe("Ignoring Enable Flood Coolant Request");
	}

	@Override
	public void disableFloodCoolant() {
		Base.logger.severe("Ignoring Disable Flood Coolant Request");
	}

	@Override
	public void enableMistCoolant() {
		Base.logger.severe("Ignoring Enable Mist Coolant Request");
	}

	@Override
	public void disableMistCoolant() {
		Base.logger.severe("Ignoring Disable Mist Coolant Request");
	}

	@Override
	public void enableFan() throws RetryException {
		Base.logger.severe("Ignoring Enable Fan Request");
	}

	@Override
	public void disableFan() throws RetryException {
		Base.logger.severe("Ignoring Disable Fan Request");
	}

	@Override
	public void openValve() throws RetryException {
		Base.logger.severe("Ignoring Open Valve Request");
	}

	@Override
	public void closeValve() throws RetryException {
		Base.logger.severe("Ignoring Close Valve Request");
	}

	@Override
	public void openCollet() {
		Base.logger.severe("Ignoring Open Collet Request");
	}

	@Override
	public void closeCollet() {
		Base.logger.severe("Ignoring Close Collet Request");
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void unpause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop(boolean abort) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasSoftStop() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasEmergencyStop() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean heartbeat() {
		// TODO Auto-generated method stub
		return false;
	}

}
