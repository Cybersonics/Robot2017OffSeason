package org.usfirst.frc103.Swerve2017Test.subsystems;

import static org.usfirst.frc103.Swerve2017Test.RobotMap.ultrasonic;

import java.util.Timer;
import java.util.TimerTask;

public class RangeFinder {
	private static final double ALPHA = 0.125;
	private static final double MAX_DISTANCE = 400.0;
	private static final int PING_PERIOD_MS = 70;
	private static Timer ultrasonicUpdater;
	private static volatile double distance = 0.0;
	
	public static void start() {
		ultrasonicUpdater = new Timer();
		ultrasonicUpdater.schedule(new TimerTask() {
			@Override
			public void run() {
				double newDistance = (ultrasonic.isRangeValid() ? 2.54 * ultrasonic.getRangeInches() : MAX_DISTANCE);
				if (Double.isFinite(newDistance)) {
					distance = distance * (1.0 - ALPHA) + newDistance * ALPHA;
				}
				ultrasonic.ping();
				/*double failTime = edu.wpi.first.wpilibj.Timer.getFPGATimestamp() + 0.5;
				do {
					try { Thread.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
					if (edu.wpi.first.wpilibj.Timer.getFPGATimestamp() > failTime) {
						System.out.println("Ultrasonic timeout");
						return;
					}
				} while (!ultrasonic.isRangeValid());
				double newDistance = 2.54 * ultrasonic.getRangeInches();
				if (Double.isFinite(newDistance)) {
					distance = distance * (1.0 - ALPHA) + newDistance * ALPHA;
				}*/
			}
		}, 0, PING_PERIOD_MS);
		//System.out.println("Rangefinder started");
	}
	
	public static double getDistance() {
		return distance;
	}

}
