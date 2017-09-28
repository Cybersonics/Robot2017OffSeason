package org.usfirst.frc103.Swerve2017Test.subsystems;

import static org.usfirst.frc103.Swerve2017Test.RobotMap.gearManipulatorDoors;

import org.usfirst.frc103.Swerve2017Test.RobotMap;
import org.usfirst.frc103.Swerve2017Test.commands.GearManipulate;

import edu.wpi.first.wpilibj.command.Subsystem;

public class GearManipulator extends Subsystem {
	
	private boolean doorClosed = false;

    public void initDefaultCommand() {
    	setDefaultCommand(new GearManipulate());
    }
    
    public void setGearDoor(double speed) {
    	gearManipulatorDoors.set(speed);
    }
    
    public void setDoors(boolean open) {
    	double speed = 0.0;
    	if (open) {
    		speed = 1.0;
    		doorClosed = false;
    	} else if (!gearManipulatorDoors.isRevLimitSwitchClosed() && !doorClosed) {
    		speed = -1.0;
    	} else if (RobotMap.gearManipulatorDoors.isRevLimitSwitchClosed()) {
    		doorClosed = true;
    	}
    	gearManipulatorDoors.set(speed);
    }
    
}

