package org.usfirst.frc103.Swerve2017Test.commands;

import org.usfirst.frc103.Swerve2017Test.Robot;
import org.usfirst.frc103.Swerve2017Test.RobotMap;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PlaceGear extends Command {
	
	public static final double DISTANCE = 444.5; //444.5 mm = 17.5inches		
    private double originHeading = 0.0;
    private boolean isPlaced = false;
    
	public PlaceGear() {
		requires(Robot.drive);
		originHeading = RobotMap.navX.getFusedHeading();
	
	}
	
	@Override
	protected void execute() {
		
		double strafe = 0;
		double forward = 0;
		double omega = 0;
		//SmartDashboard.putNumber("Ultrasonic SAE", RobotMap.ultrasonic.getDistance()/25.4);
		
		/*if (RobotMap.ultrasonic.getDistance() > DISTANCE) {
			
			forward = 0.2;
		} else {
			forward = 0.0;
			isPlaced = true;
		}*/
		
		Robot.drive.swerveDrive(strafe, forward, omega);
		if (isPlaced) {
			
		}
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

    @Override
	protected void end() {
    }

    @Override
	protected void interrupted() {
    	end();
    }

}
