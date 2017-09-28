package org.usfirst.frc103.Swerve2017Test.subsystems;

import java.util.List;
import java.util.stream.Collectors;

import org.usfirst.frc103.Swerve2017Test.RobotMap;
import org.usfirst.frc103.Swerve2017Test.Pixy2.Block;
import org.usfirst.frc103.Swerve2017Test.subsystems.Vision.VisionTarget;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Vision {
	
	// shooter target acceptable error (in pixels) to be on target
	public static final double SHOOTER_VISION_ACCEPTABLE_ERROR = 3.0;
	// p constant for the shooter omega error correction
	public static final double SHOOTER_CORRECTION_OMEGA_P = 0.035;
	// constant that determines how much the p constant is decreased based on current movement
	// range is [0, 1], larger values mean less p when the robot is moving
	public static final double SHOOTER_CORRECTION_MOVEMENT_FACTOR = 0.9;
    
    public static VisionTarget getGearTarget() {
    	List<Block> filteredBlocks = RobotMap.pixy.getDetectedBlocks()
    			.stream()
    			.filter((Block b) -> (double) b.height / (double) b.width >= 2.0)
    			.sorted((Block o1, Block o2) -> o1.centerX - o2.centerX)
    			.collect(Collectors.toList());
    	VisionTarget target = null;
    	if (filteredBlocks.size() >= 2) {
    		int targetIndex = 0;
    		int minPairDistance = Integer.MAX_VALUE;
    		for (int i = 0; i < filteredBlocks.size() - 1; i++) {
    			Block leftBlock = filteredBlocks.get(i);
    			for (int j = i; j < filteredBlocks.size(); j++) {
        			Block rightBlock = filteredBlocks.get(j);
        			double heightDifference =
        					(double) Math.abs(leftBlock.height - rightBlock.height) /
        					(double) Math.min(leftBlock.height, rightBlock.height);
        			if (heightDifference > 0.5) {
        				continue;
        			}
        			int pairDistance = rightBlock.centerX - leftBlock.centerX;
        			if (pairDistance < minPairDistance) {
        				targetIndex = i;
        				minPairDistance = pairDistance;
        			}
    			}
    		}
    		Block leftBlock = filteredBlocks.get(targetIndex);
    		Block rightBlock = filteredBlocks.get(targetIndex + 1);
    		double centerX = (double) (leftBlock.centerX + rightBlock.centerX) / 2.0;
    		double height = (double) (leftBlock.height + rightBlock.height) / 2.0;
    		target = new VisionTarget(centerX, height);
    	}
    	return target;
    }
    
    public static VisionTarget getShooterTarget() {
    	List<Block> filteredBlocks = RobotMap.pixyShooter.getDetectedBlocks()
    			.stream()
    			.filter((Block b) -> b.height > 5)
    			.sorted((Block o1, Block o2) -> o1.width * o1.height - o2.width * o2.height)
    			.collect(Collectors.toList());
    	if (filteredBlocks.isEmpty()) {
    		return null;
    	} else {
    		Block targetBlock = filteredBlocks.get(0);
    		return new VisionTarget(targetBlock.centerX, targetBlock.height);
    	}
    }
    
    public static boolean hasShooterTarget() {
    	return getShooterTarget() != null;
    }
    
    public static double getShooterOmega(double strafe, double forward) {
    	double omega = 0.0;
    	VisionTarget target = Vision.getShooterTarget();
		if (target != null) {
			double error = (target.centerX - 160.0) / 160.0;
			SmartDashboard.putNumber("VisionError", error);
			if (Math.abs(error) < SHOOTER_VISION_ACCEPTABLE_ERROR / 160.0) {
				omega = 0.0;
			} else {
				//omega = 0.075 * error;
				double magnitude = Math.hypot(strafe, forward) / 1.41;
				double p = SHOOTER_CORRECTION_OMEGA_P * (1.0 - SHOOTER_CORRECTION_MOVEMENT_FACTOR * Math.sqrt(magnitude)); //0.03
				omega = (p * Math.sqrt(Math.abs(error))) * Math.signum(error);
			}
		}
		return omega;
    }
    
    public static class VisionTarget {
    	public final double centerX;
    	public final double height;
    	//public final double distanceEstimate;
    	
		public VisionTarget(double centerX, double height) {
			this.centerX = centerX;
			this.height = height;
			//this.distanceEstimate = 0.0584974 * Math.pow(height, 2.0) - 4.96322 * (double) height + 143.824;
		}

		@Override
		public String toString() {
			return "VisionTarget [centerX=" + centerX + ", height=" + height/* + ", distanceEstimate=" + distanceEstimate*/ + "]";
		}
    	
    }

}
