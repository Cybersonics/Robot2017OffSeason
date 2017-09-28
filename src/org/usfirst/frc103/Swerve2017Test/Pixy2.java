package org.usfirst.frc103.Swerve2017Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.usfirst.frc103.Swerve2017Test.ReliableI2C.I2CDevice;

public class Pixy2 {

	private static final int PIXY_START_WORD = 0xAA55, PIXY_START_WORD_CC = 0xAA56, PIXY_START_WORDX = 0x55AA;
	private static final int MAX_BLOCKS = 10;
	private static final double STALE_FRAME_TIME = 0.1; //seconds

	private final I2CDevice i2c;
	private final ByteBuffer buffer = ByteBuffer.allocateDirect(32);

	private BlockType blockType;
	private boolean skipStart = false;
	
	private double frameLastUpdatedTime = 0.0;
	private List<Block> frame = new ArrayList<>(MAX_BLOCKS);
	private List<Block> frameCopy = new ArrayList<>();
	private Timer updater = new Timer(true);
	
	//private StringBuilder hex = new StringBuilder();

	public Pixy2(I2CDevice i2c) {
		this.i2c = i2c;
		updater.schedule(new TimerTask() {
			@Override
			public void run() {
				updateFrame();
			}
		}, 0, 5);
	}
	
	private void sleep(long millis) {
		try { Thread.sleep(millis); } catch (InterruptedException e) { e.printStackTrace(); }
	}

	private int getByte() {
		// Ignore the return value, since wpilib will lie and say that 
		// the read failed when it was successful
		// Then it will fail silently later...
		//i2c.readOnly(buffer, 1);
		//i2c.read(0, 1, buffer);
		/*int attempt = 0;
		while (i2c.readOnly(buffer, 1) && attempt < 10) attempt++;
		if (attempt >= 10) {
			System.out.println("i2c read timeout");
			edu.wpi.first.wpilibj.Timer.delay(0.1);
		}*/
		/*int ret;
		while ((ret = I2CJNI.i2CRead(i2cPort, i2cAddress, buffer, (byte) 1)) < 0) {
			System.out.println("ret = " + ret + ", buffered = " + hex + ", read = " + String.format("%02X ", buffer.get(0)));
			edu.wpi.first.wpilibj.Timer.delay(1.0);
			try {
				Thread.sleep(10);
				I2CJNI.i2CClose(i2cPort);
				Thread.sleep(100);
				I2CJNI.i2CInitialize(i2cPort);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		while (!i2c.readRaw(buffer, 1)) sleep(1);
		int b = buffer.get(0) & 0xFF;
		//hex.append(String.format("%02X ", b));
		return b;
	}

	private int getWord() {
		int w = getByte() | (getByte() << 8);
		return w;
	}

	private boolean getStart() {
		int w = 0, lastw = 0xFFFF;
		while (true) {
			w = getWord();
			if (w == 0 && lastw == 0) {
				Thread.yield();
				return false;
			} else if (w == PIXY_START_WORD && lastw == PIXY_START_WORD) {
				blockType = BlockType.NORMAL_BLOCK;
				return true;
			} else if (w == PIXY_START_WORD_CC && lastw == PIXY_START_WORD) {
				blockType = BlockType.CC_BLOCK;
				return true;
			} else if (w == PIXY_START_WORDX) {
				getByte();
				System.out.println("Pixy reorder");
			}
			lastw = w;
		}
	}

	private List<Block> getBlocks() {
		//hex = new StringBuilder();
		List<Block> blocks = new ArrayList<>(MAX_BLOCKS);

		if (!skipStart) {
			if (!getStart()) {
				return blocks;
			}
		} else {
			skipStart = false;
		}

		for (int blockCount = 0; blockCount < MAX_BLOCKS;) {
			int checksum = getWord();
			if (checksum == PIXY_START_WORD) {
				skipStart = true;
				blockType = BlockType.NORMAL_BLOCK;
				return blocks;
			} else if (checksum == PIXY_START_WORD_CC) {
				skipStart = true;
				blockType = BlockType.CC_BLOCK;
				return blocks;
			} else if (checksum == 0) {
				return blocks;
			}

			int signature = getWord();
			int x = getWord();
			int y = getWord();
			int width = getWord();
			int height = getWord();
			int angle = (blockType == BlockType.NORMAL_BLOCK ? 0 : getWord());
			int sum = (signature + x + y + width + height + angle) & 0xFFFF;
			if (checksum == sum) {
				blocks.add(new Block(signature, x, y, width, height));
				blockCount++;
			} else {
				System.out.println("checksum error (" + sum + " != " + checksum + ")");
			}

			int w = getWord();
			if (w == PIXY_START_WORD) {
				blockType = BlockType.NORMAL_BLOCK;
			} else if (w == PIXY_START_WORD_CC) {
				blockType = BlockType.CC_BLOCK;
			} else {
				return blocks;
			}
		}

		return blocks;
	}
	
	private void updateFrame() {
		List<Block> blocks;
		// Pixy gets angry if you don't read its frame all at once
		synchronized (Pixy2.class) {
			blocks = getBlocks();
		}
		double currentTime = edu.wpi.first.wpilibj.Timer.getFPGATimestamp();
		if (!blocks.isEmpty()) {
			synchronized (this) {
				frame.clear();
				frame.addAll(blocks);
				frameCopy = new ArrayList<>(frame);
				frameLastUpdatedTime = currentTime;
			}
		} else if (currentTime - frameLastUpdatedTime > STALE_FRAME_TIME) {
			synchronized (this) {
				frame.clear();
				frameCopy = new ArrayList<>();
			}
		}
	}
	
	public synchronized List<Block> getDetectedBlocks() {
		/*List<Block> blocks = new ArrayList<>(frame);
		return blocks;*/
		return frameCopy;
	}

	public static enum BlockType {
		NORMAL_BLOCK, CC_BLOCK
	}

	public static class Block {
		public final int signature;
		public final int centerX, centerY;
		public final int width, height;

		public Block(int signature, int centerX, int centerY, int width, int height) {
			this.signature = signature;
			this.centerX = centerX;
			this.centerY = centerY;
			this.width = width;
			this.height = height;
		}

		@Override
		public String toString() {
			return "PixyObject [signature=" + signature + ", centerX=" + centerX + ", centerY=" + centerY + ", width=" + width + ", height=" + height + "]";
		}
	}

}
