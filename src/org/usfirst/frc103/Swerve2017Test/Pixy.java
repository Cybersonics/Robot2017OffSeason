package org.usfirst.frc103.Swerve2017Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Pixy {
	public static final int MAX_OBJECTS = 3;
	public static final int FRAME_SYNC_SIZE = 2, OBJECT_BLOCK_SIZE = 14;
	public static final int BUFFER_SIZE = FRAME_SYNC_SIZE + MAX_OBJECTS * OBJECT_BLOCK_SIZE;
	public static final int FRAME_SYNC = 0xAA55;
	public static final int OBJECT_SYNC_NORMAL = 0xAA55, OBJECT_SYNC_COLOR_CODE = 0xAA56;
	public static final byte[] ZERO = new byte[BUFFER_SIZE];
	
	private final I2C i2c;
	private final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(ByteOrder.LITTLE_ENDIAN);
	private List<PixyObject> frame = new LinkedList<>();
	
	public Pixy(I2C i2c) {
		this.i2c = i2c;
	}
	
	private int getUnsignedShort() {
		return buffer.getShort() & 0xFFFF;
	}
	
	private int getUnsignedShort(int index) {
		return buffer.getShort(index) & 0xFFFF;
	}
	
	private void dumpBuffer() {
		byte[] bs = new byte[BUFFER_SIZE];
		int pos = buffer.position();
		buffer.position(0);
		buffer.get(bs);
		buffer.position(pos);
		String hex = "";
		for (byte b : bs) hex += String.format("%02X", b & 0xFF);
		System.out.println(hex);
	}
	
	private void updateFrame() {
		buffer.rewind();
		buffer.put(ZERO);
		buffer.rewind();
		SmartDashboard.putBoolean("I2CStatus", i2c.readOnly(buffer, BUFFER_SIZE));
		//dumpBuffer();
		
		boolean frameFound = false;
		int frameStart;
		for (frameStart = 0; frameStart < BUFFER_SIZE - 2 * FRAME_SYNC_SIZE; frameStart++) {
			if (getUnsignedShort(frameStart) == FRAME_SYNC) {
				int objectSync = getUnsignedShort(frameStart + 2);
				if (objectSync == OBJECT_SYNC_NORMAL || objectSync == OBJECT_SYNC_COLOR_CODE) {
					frameFound = true;
					break;
				}
			}
		}
		
		if (frameFound) {
			frame.clear();
			buffer.position(frameStart + FRAME_SYNC_SIZE);
			while (buffer.remaining() >= OBJECT_BLOCK_SIZE) {
				int sync = getUnsignedShort(),
					checksum = getUnsignedShort(),
					signature = getUnsignedShort(),
					centerX = getUnsignedShort(), centerY = getUnsignedShort(),
					width = getUnsignedShort(), height = getUnsignedShort();
				int computedChecksum = (signature + centerX + centerY + width + height) & 0xFFFF;
				switch (sync) {
					case OBJECT_SYNC_NORMAL:
					case OBJECT_SYNC_COLOR_CODE:
						if (checksum == OBJECT_SYNC_NORMAL || checksum == OBJECT_SYNC_COLOR_CODE) {
							System.out.println("Sync in checksum (frameStart = " + frameStart + ")");
							dumpBuffer();
							break;
						} else if (checksum == computedChecksum) {
							frame.add(new PixyObject(signature, centerX, centerY, width, height));
						} else {
							System.out.println("Checksum " + computedChecksum + " != " + checksum + " (frameStart = " + frameStart + ")");
							dumpBuffer();
							break;
						}
					default:
						break;
				}
			}
		} else {
			System.out.println("No frame found");
			dumpBuffer();
		}
	}
	
	public synchronized List<PixyObject> getObjects(boolean update) {
		if (update) {
			updateFrame();
		}
		return new ArrayList<>(frame);
	}
	
	public List<PixyObject> getObjects() {
		return getObjects(true);
	}
	
	public List<PixyObject> getObjects(int signature, boolean update) {
		return getObjects(update).stream().filter(o -> o.signature == signature).collect(Collectors.toList());
	}
	
	public List<PixyObject> getObjects(int signature) {
		return getObjects(signature, true);
	}
	
	public static class PixyObject {
		public final int signature;
		public final int centerX, centerY;
		public final int width, height;
		
		public PixyObject(int signature, int centerX, int centerY, int width, int height) {
			this.signature = signature;
			this.centerX = centerX;
			this.centerY = centerY;
			this.width = width;
			this.height = height;
		}

		@Override
		public String toString() {
			return "PixyObject [signature=" + signature + ", centerX=" + centerX + ", centerY=" + centerY + ", width="
					+ width + ", height=" + height + "]";
		}
	}
}
