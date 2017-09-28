package org.usfirst.frc103.Swerve2017Test;

import java.nio.ByteBuffer;
import java.util.TimerTask;

import org.usfirst.frc103.Swerve2017Test.ReliableI2C.I2CDevice;

public class Lidar {
	
	//private I2C i2c;
	private I2CDevice i2c;
	private java.util.Timer updater;
	private ByteBuffer buffer = ByteBuffer.allocateDirect(3);
	private volatile double distance = 0.0;
	private int measurementCount = 0;
	//private DigitalOutput reset;
	
	private static final double ALPHA = 0.5; // bigger means more responsive, smaller means more smoothing
	
	private static final int LIDAR_BUSY_MASK = 0x01;
	private static final int LIDAR_COMMAND_ACQUIRE_WITHOUT_CORRECTION = 0x03;
	private static final int LIDAR_COMMAND_ACQUIRE_WITH_CORRECTION = 0x04;
	private static final int LIDAR_ACQ_COMMAND = 0x00;
	private static final int LIDAR_STATUS = 0x01;
	private static final int LIDAR_SIG_COUNT_VAL = 0x02;
	private static final int LIDAR_ACQ_CONFIG_REG = 0x04;
	private static final int LIDAR_THRESHOLD_BYPASS = 0x1c;
	private static final int LIDAR_DISTANCE_REGISTER = 0x8f;
	
	private static final int UPDATE_PERIOD = 20; // in milliseconds
	private static final int RETRY_COUNT = 10;
	
	public Lidar(I2CDevice i2c) {
		//i2c = new I2C(port, address);
		//I2CJNI.i2CInitialize((byte) 1);
		//I2CJNI.i2CClose((byte) 1);
		this.i2c = i2c;
		/*reset = new DigitalOutput(resetPort);
		reset.set(true);*/
		
		setup();
		
		updater = new java.util.Timer();
		updater.schedule(new TimerTask() {
			@Override
			public void run() {
				distance = distance * (1.0 - ALPHA) + getUpdatedDistance() * ALPHA;
			}
		}, 0, UPDATE_PERIOD);
	}
	
	// Distance in cm
	public double getDistance() {
		return distance;
	}
	
	private void setup() {
		//i2c.write(LIDAR_SIG_COUNT_VAL, 0x80);
		while (!i2c.writeRegister((byte) LIDAR_SIG_COUNT_VAL, (byte) 0x80)) sleep(500);
		sleep(1);
		//i2c.write(LIDAR_ACQ_CONFIG_REG, 0x08);
		while (!i2c.writeRegister((byte) LIDAR_ACQ_CONFIG_REG, (byte) 0x08)) sleep(500);
		sleep(1);
		//i2c.write(LIDAR_THRESHOLD_BYPASS, 0x00);
		while (!i2c.writeRegister((byte) LIDAR_THRESHOLD_BYPASS, (byte) 0x00)) sleep(500);
		sleep(1);
	}
	
	// Update distance variable
	private double getUpdatedDistance() {
		int command = (measurementCount++ % 50 == 0 ? LIDAR_COMMAND_ACQUIRE_WITH_CORRECTION : LIDAR_COMMAND_ACQUIRE_WITHOUT_CORRECTION);
		//i2c.write(LIDAR_ACQ_COMMAND, command); // Initiate measurement
		while (!i2c.writeRegister((byte) LIDAR_ACQ_COMMAND, (byte) command)) sleep(500); 
		int busyCount = 0;
		do {
			sleep(1);
			int status = readUnsignedByte(LIDAR_STATUS);
			boolean busy = (status & LIDAR_BUSY_MASK) == LIDAR_BUSY_MASK;
			/*SmartDashboard.putString("statusString", Integer.toBinaryString(status));
			SmartDashboard.putBoolean("busyFlag", busy);*/
			if (!busy) {
				return readUnsignedShort(LIDAR_DISTANCE_REGISTER);
			} else {
				busyCount++;
			}
		} while (busyCount < RETRY_COUNT);
		System.out.println("Distance read timed out");
		/*reset.set(false);
		sleep(500);
		reset.set(true);
		sleep(50);*/
		return distance;
	}
	
	private void sleep(long millis) {
		try { Thread.sleep(millis); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	private int readUnsignedByte(int register) {
		/*buffer.put(0, (byte) register);
		i2c.writeBulk(buffer, 1);
		i2c.readOnly(buffer, 1);*/
		while (!i2c.readRegister((byte) register, buffer, 1)) sleep(500);
		return buffer.get(0) & 0xFF;
	}
	
	private int readUnsignedShort(int register) {
		/*buffer.put(0, (byte) register);
		i2c.writeBulk(buffer, 1);
		i2c.readOnly(buffer, 2);*/
		while (!i2c.readRegister((byte) register, buffer, 2)) sleep(500);
		return buffer.getShort(0) & 0xFFFF;
	}

}

