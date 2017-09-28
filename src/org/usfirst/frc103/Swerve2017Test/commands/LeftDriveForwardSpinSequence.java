package org.usfirst.frc103.Swerve2017Test.commands;

import org.usfirst.frc103.Swerve2017Test.Robot;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class LeftDriveForwardSpinSequence extends CommandGroup {
	
	public LeftDriveForwardSpinSequence() {
		requires(Robot.drive);
		requires(Robot.gearManipulator);
		requires(Robot.shooter);
		
		/*addSequential(new DriveForward(60.0, 4600));
		addSequential(new VisionPlaceGear(60.0));
		addSequential(new VisionLeaveGear());*/
		addSequential(new LeftDriveForwardSequence());
		//before vision
		//addSequential(new DriveForward(-144.0, 0));// was (4-7-17) 147 relative
		//addSequential(new ShootAuto(3370)); //was 3425
		addSequential(new DriveForward(170.0, 0));//was -170
		addSequential(new ShootAuto(3120));
	}
	
}
