package org.usfirst.frc103.Swerve2017Test.commands;
import org.usfirst.frc103.Swerve2017Test.Robot;
import org.usfirst.frc103.Swerve2017Test.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

public class GearManipulate extends Command {
	
	//boolean doorClosed;
	
	public GearManipulate() {
		requires(Robot.gearManipulator);
	}
	
	@Override
	protected void initialize() {
		//doorClosed = false;
	}
	
	@Override
	protected void execute() {
		/*double speed = -Robot.oi.controller.getY(Hand.kRight);
		speed = (Math.abs(speed) > 0.2 ? speed : 0.0);*/
		
		
		/*double speed = 0.0;
		if (Robot.oi.controller.getAButton()){ 
			speed = 1.0;
			doorClosed = false;
		} else if (!RobotMap.gearManipulatorDoors.isRevLimitSwitchClosed() && !doorClosed) {
			speed = -1.0;
		}  else if (RobotMap.gearManipulatorDoors.isRevLimitSwitchClosed()){
			doorClosed = true;
		}
		Robot.gearManipulator.setGearDoor(speed);*/
		
		Robot.gearManipulator.setDoors(Robot.oi.controller.getAButton());
		
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
