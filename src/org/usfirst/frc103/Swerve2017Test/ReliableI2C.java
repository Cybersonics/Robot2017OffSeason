package org.usfirst.frc103.Swerve2017Test;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.sun.jna.Library;
import com.sun.jna.Native;

import edu.wpi.first.wpilibj.DigitalOutput;

public class ReliableI2C {
	private static DigitalOutput scl, sda;
	
	public interface I2CLibrary extends Library {
		public static I2CLibrary INSTANCE = (I2CLibrary) Native.loadLibrary("i2c", I2CLibrary.class);
		
		int i2clib_open(String device);
		void i2clib_close(int handle);
		int i2clib_read(int handle, byte dev_addr, Buffer recv_buf, int recv_size);
		int i2clib_write(int handle, byte dev_addr, Buffer send_buf, int send_size);
		int i2clib_writeread(int handle, byte dev_addr, Buffer send_buf, int send_size, Buffer recv_buf, int recv_size);
	}
	
	public interface MXPSpecialLibrary extends Library {
		public static MXPSpecialLibrary INSTANCE = (MXPSpecialLibrary) Native.loadLibrary("mxp_specialness", MXPSpecialLibrary.class);
		
		int read_mxp_specialness();
		void write_mxp_specialness(int value);
	}
	
	public static enum I2CPort {
		ONBOARD("/dev/i2c-2"), MXP("/dev/i2c-1");
		
		public final String deviceFile;
		//private final Lock mutex = new ReentrantLock();
		private boolean isOpen = false;
		private int handle;
		
		private I2CPort(String deviceFile) {
			this.deviceFile = deviceFile;
		}
		
		private void open() {
			if (!isOpen) {
				if (this == MXP) {
					MXPSpecialLibrary.INSTANCE.write_mxp_specialness(MXPSpecialLibrary.INSTANCE.read_mxp_specialness() | 0xC000);
				}
				handle = I2CLibrary.INSTANCE.i2clib_open(deviceFile);
				isOpen = true;
			}
		}
		
		private void close() {
			if (isOpen) {
				I2CLibrary.INSTANCE.i2clib_close(handle);
				if (this == MXP) {
					MXPSpecialLibrary.INSTANCE.write_mxp_specialness(MXPSpecialLibrary.INSTANCE.read_mxp_specialness() & ~0xC000);
				}
				isOpen = false;
			}
		}
		
		private void reopen() {
			close();
			if (this == I2CPort.MXP) {
				//enable i2c scl function but leave sda in dio mode
				MXPSpecialLibrary.INSTANCE.write_mxp_specialness(MXPSpecialLibrary.INSTANCE.read_mxp_specialness() | 0x4000);
				//pull sda low to recover
				sda.set(false);
				try { Thread.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
			}
			open();
		}
	}
	
	public static class I2CDevice {
		private final I2CPort port;
		private final byte deviceAddress;
		
		public I2CDevice(I2CPort port, byte deviceAddress) {
			this.port = port;
			this.deviceAddress = deviceAddress;
			synchronized (port) {
				port.open();
			}
		}
		
		private void checkBuffer(ByteBuffer buffer, int count) {
			if (!buffer.isDirect()) {
				throw new IllegalArgumentException("Must use a direct buffer");
			}
			if (buffer.capacity() < count) {
				throw new IllegalArgumentException("Buffer is too small, must have a capacity of at least " + count);
			}
		}
		
		public boolean readRaw(ByteBuffer readBuffer, int count) {
			checkBuffer(readBuffer, count);
			synchronized (port) {
				if (I2CLibrary.INSTANCE.i2clib_read(port.handle, deviceAddress, readBuffer, count) < 0) {
					System.out.println("readRaw: reopening i2c device");
					port.reopen();
					return false;
				} else {
					return true;
				}
			}
		}
		
		public boolean writeRaw(ByteBuffer writeData, int count) {
			checkBuffer(writeData, count);
			synchronized (port) {
				if (I2CLibrary.INSTANCE.i2clib_write(port.handle, deviceAddress, writeData, count) < 0) {
					System.out.println("writeRaw: reopening i2c device");
					port.reopen();
					return false;
				} else {
					return true;
				}
			}
		}
		
		public boolean readRegister(byte register, ByteBuffer readBuffer, int count) {
			checkBuffer(readBuffer, count);
			synchronized (port) {
				ByteBuffer registerBuffer = ByteBuffer.allocateDirect(1);
				registerBuffer.put(register).rewind();
				if (I2CLibrary.INSTANCE.i2clib_write(port.handle, deviceAddress, registerBuffer, 1) < 0) {
					System.out.println("readRegister [write register address]: reopening i2c device");
					port.reopen();
					return false;
				} else {
					if (I2CLibrary.INSTANCE.i2clib_read(port.handle, deviceAddress, readBuffer, count) < 0) {
						System.out.println("readRegister [read data]: reopening i2c device");
						port.reopen();
						return false;
					} else {
						return true;
					}
				}
			}
		}

		public boolean writeRegister(byte register, ByteBuffer writeData, int count) {
			synchronized (port) {
				ByteBuffer registerBuffer = ByteBuffer.allocateDirect(writeData.remaining() + 1);
				registerBuffer.put(register).put(writeData).rewind();
				if (I2CLibrary.INSTANCE.i2clib_write(port.handle, deviceAddress, registerBuffer, registerBuffer.capacity()) < 0) {
					System.out.println("writeRegister: reopening i2c device");
					port.reopen();
					return false;
				} else {
					return true;
				}
			}
		}
		
		public boolean writeRegister(byte register, byte value) {
			return writeRegister(register, ByteBuffer.wrap(new byte[] { value }), 1);
		}
	}
	
	public static void init() {
		scl = new DigitalOutput(24);
        sda = new DigitalOutput(25);
	}
	
	public static I2CDevice openDevice(I2CPort port, byte deviceAddress) {
		return new I2CDevice(port, deviceAddress);
	}

}
