package org.usfirst.frc103.Swerve2017Test.commands;

import org.usfirst.frc103.Swerve2017Test.Robot;
import org.usfirst.frc103.Swerve2017Test.RobotMap;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveForward extends Command {
	
	public static final double OMEGA_SCALE = 1.0 / 30.0;
	//public static final double DISTANCE = 4600; // started at 4500
	
    private double originHeading = 0.0;
	private double targetHeading;
	private double orientation;
	private double distance;
	private boolean isDone;
	private boolean isDoneRotate;
	//public static double angle = 60.0;
    
	public DriveForward(double orientation, double distance) {
		requires(Robot.drive);
		this.orientation = orientation;
		this.distance = distance;
	}
	
	@Override
	protected void initialize() {
		//originHeading = RobotMap.navX.getFusedHeading();
		originHeading = Robot.zeroHeading;
		targetHeading = originHeading + orientation;
		targetHeading = targetHeading % 360.0;
		if (targetHeading < 0.0) {
			targetHeading += 360.0;
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
		
		//double angleError = angle - RobotMap.navX.getYaw();
		double angleError = targetHeading - RobotMap.navX.getFusedHeading();
		if (Math.abs(angleError) > 180.0) {
			angleError -= 360.0 * Math.signum(angleError);
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
		
		if (Math.abs(angleError) > 2.0){
			omega = Math.max(Math.min((angleError / 360) * 0.2, 0.03), -0.03);//start at 0.08 (was 0.02 on comp bot)
		} else {
			omega = 0.0;
			isDoneRotate = true;
		}
		 
		SmartDashboard.putBoolean("done rotation", isDoneRotate);
		
		double originCorrection = Math.toRadians(originHeading - RobotMap.navX.getFusedHeading());
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
