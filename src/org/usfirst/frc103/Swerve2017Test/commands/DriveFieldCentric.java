package org.usfirst.frc103.Swerve2017Test.commands;

import org.usfirst.frc103.Swerve2017Test.Robot;
import org.usfirst.frc103.Swerve2017Test.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

public class DriveFieldCentric extends Command {
	public static final double OMEGA_SCALE = 1.0 / 30.0;
	
	private double heading;
	private double absoluteHeading;
	private double distance;
	private double orientation;
	private double targetOrientation;
	private boolean isDone;
	private boolean isDoneRotate;
	
	public DriveFieldCentric(double heading, double distance, double orientation) {
		this.heading = heading;
		this.distance = distance;
		this.orientation = orientation;
	}
	
	@Override
	protected void initialize() {
		absoluteHeading = Robot.zeroHeading + heading;
		absoluteHeading = absoluteHeading % 360.0;
		if (absoluteHeading < 0) {
			absoluteHeading += 360.0;
		}
		targetOrientation = Robot.zeroHeading + orientation;
		targetOrientation = targetOrientation % 360.0;
		if (targetOrientation < 0.0) {
			targetOrientation += 360.0;
		}
		
		isDone = false;
		isDoneRotate = false;
		Robot.drive.encoderReset();
	}
	
	@Override
	protected void execute() {
		double strafe = 0;
		double forward = 0;
		double omega = 0;
		
		double orientationError = targetOrientation - RobotMap.navX.getFusedHeading();
		if (Math.abs(orientationError) > 180.0) {
			orientationError -= 360.0 * Math.signum(orientationError);
    	}
		
		if ((Math.abs(RobotMap.driveLeftFront.getPosition()) < distance) &&
				(Math.abs(RobotMap.driveRightFront.getPosition()) < distance) &&
				(Math.abs(RobotMap.driveLeftRear.getPosition()) < distance) &&
				(Math.abs(RobotMap.driveRightRear.getPosition()) < distance)) {
			
			forward = 0.4;//0.35
		} else {
			forward = 0.0;
			isDone = true;
		}
		// Rotate the velocity vector from the joystick by the difference between our
		// current orientation and the current origin heading
		
		if (Math.abs(orientationError) > 2.0){
			omega = Math.max(Math.min((orientationError / 360) * 0.2, 0.02), -0.02);//start at 0.08
		} else {
			omega = 0.0;
			isDoneRotate = true;
		}
		 
		double originCorrection = Math.toRadians(absoluteHeading - RobotMap.navX.getFusedHeading());
		double temp = forward * Math.cos(originCorrection) - strafe * Math.sin(originCorrection);
		strafe = strafe * Math.cos(originCorrection) + forward * Math.sin(originCorrection);
		forward = temp;
		
		Robot.drive.swerveDrive(strafe, forward, omega);
	}

	@Override
	protected boolean isFinished() {
		return isDone && isDoneRotate;
	}

    @Override
	protected void end() {
		Robot.drive.swerveDrive(0, 0, 0);
    }

    @Override
	protected void interrupted() {
    	end();
    }

}
