package org.usfirst.frc103.Swerve2017Test;

import static org.usfirst.frc103.Swerve2017Test.RobotMap.driveLeftFront;
import static org.usfirst.frc103.Swerve2017Test.RobotMap.driveLeftRear;
import static org.usfirst.frc103.Swerve2017Test.RobotMap.driveRightFront;
import static org.usfirst.frc103.Swerve2017Test.RobotMap.driveRightRear;
import static org.usfirst.frc103.Swerve2017Test.RobotMap.navX;
import static org.usfirst.frc103.Swerve2017Test.RobotMap.shooterFlyWheel;
import static org.usfirst.frc103.Swerve2017Test.RobotMap.steerLeftFront;
import static org.usfirst.frc103.Swerve2017Test.RobotMap.steerLeftRear;
import static org.usfirst.frc103.Swerve2017Test.RobotMap.steerRightFront;
import static org.usfirst.frc103.Swerve2017Test.RobotMap.steerRightRear;

import java.util.List;
import java.util.stream.Collectors;

import org.usfirst.frc103.Swerve2017Test.Pixy2.Block;
import org.usfirst.frc103.Swerve2017Test.commands.CenterDriveForwardSpinSequence;
import org.usfirst.frc103.Swerve2017Test.commands.CenterDriveLeftForwardSpinSequence;
import org.usfirst.frc103.Swerve2017Test.commands.DoNothingAuto;
import org.usfirst.frc103.Swerve2017Test.commands.DriveFieldCentric;
import org.usfirst.frc103.Swerve2017Test.commands.DriveForward;
import org.usfirst.frc103.Swerve2017Test.commands.LeftDriveForwardContinueSequence;
import org.usfirst.frc103.Swerve2017Test.commands.LeftDriveForwardSequence;
import org.usfirst.frc103.Swerve2017Test.commands.LeftDriveForwardSpinSequence;
import org.usfirst.frc103.Swerve2017Test.commands.RightDriveForwardContinueSequence;
import org.usfirst.frc103.Swerve2017Test.commands.RightDriveForwardSequence;
import org.usfirst.frc103.Swerve2017Test.commands.RightDriveForwardSpinSequence;
import org.usfirst.frc103.Swerve2017Test.commands.VisionAutoSequence;
import org.usfirst.frc103.Swerve2017Test.subsystems.Climber;
import org.usfirst.frc103.Swerve2017Test.subsystems.Drive;
import org.usfirst.frc103.Swerve2017Test.subsystems.GearManipulator;
import org.usfirst.frc103.Swerve2017Test.subsystems.RangeFinder;
import org.usfirst.frc103.Swerve2017Test.subsystems.Shooter;
import org.usfirst.frc103.Swerve2017Test.subsystems.Vision;
import org.usfirst.frc103.Swerve2017Test.subsystems.Vision.VisionTarget;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	
	SendableChooser<Command> autonomousChooser;
	Command autonomousCommand;
    public static OI oi;
    public static Drive drive;
    public static Shooter shooter;
    public static GearManipulator gearManipulator;
    public static Climber climber;
    
    public static double zeroHeading;
    
    @Override
	public void robotInit() {
    	RobotMap.init();
    	
        drive = new Drive();
        shooter = new Shooter();
        gearManipulator = new GearManipulator();
        climber = new Climber();

        oi = new OI();
        
        autonomousChooser = new SendableChooser<Command>();
        
        //autonomousChooser.addObject("01Right Drive Forward", new DriveForward(-70.0, 3800));
        //autonomousChooser.addObject("02Center Drive Forward", new DriveForward (0, 3000));
        //autonomousChooser.addObject("03Left Drive Forward", new DriveForward(70.0, 4800));
        autonomousChooser.addObject("02Left Gear Place Spin", new LeftDriveForwardSpinSequence());
        autonomousChooser.addObject("04Left Gear Place", new LeftDriveForwardSequence());
        autonomousChooser.addObject("05Center Gear Place", new VisionAutoSequence());
        autonomousChooser.addObject("06Right Gear Place", new RightDriveForwardSequence());
        autonomousChooser.addObject("07Right Gear Place Spin", new RightDriveForwardSpinSequence());
        autonomousChooser.addObject("08Center Right Gear Place Spin", new CenterDriveForwardSpinSequence());
        autonomousChooser.addObject("09Center Left Gear Place Spin", new CenterDriveLeftForwardSpinSequence());
        autonomousChooser.addObject("10Left Gear Place Continue To Narnia", new LeftDriveForwardContinueSequence());
        autonomousChooser.addObject("11Right Gear Place Continue To Narnia", new RightDriveForwardContinueSequence());
        
        //autonomousChooser.addObject("Place Gear", new PlaceGear());
        
        autonomousChooser.addDefault("Do Nothing", new DoNothingAuto());
        
        SmartDashboard.putData("AutonomousCommands", autonomousChooser);

        //SmartDashboard.putData("DriveForwardTurnLeft", new DriveForward(-60.0, 4600));
        //SmartDashboard.putData("DriveForwardTurnRight", new DriveForward(60.0, 4600));
        //SmartDashboard.putData("LeftGearPlace", new LeftDriveForwardSequence());
        //SmartDashboard.putData("CenterGearPlace", new VisionAutoSequence());
        //SmartDashboard.putData("RightGearPlace", new RightDriveForwardSequence());

        /*SmartDashboard.putData("DriveForward+60", new DriveForward(60, 1000));
        SmartDashboard.putData("DriveForward-60", new DriveForward(-60, 1000));
        SmartDashboard.putData("DriveFieldCentricTest", new CommandGroup() {
        	{
        		requires(Robot.drive);
        		addSequential(new DriveForward(60, 1000));
        		addSequential(new DriveFieldCentric(0, 100000, -60));
        	}
        });*/
        
        /*pixyUpdater.schedule(new TimerTask() {
			@Override
			public void run() {
				RobotMap.pixy.getObjects();
			}
		}, 0, 20);*/
        
        /*pixyUpdater.schedule(new TimerTask() {
			@Override
			public void run() {
				List<Pixy2.Block> blocks = RobotMap.pixy2.getBlocks();
		    	SmartDashboard.putNumber("PixyCount", blocks.size());
		    	if (!blocks.isEmpty()) {
			    	SmartDashboard.putString("Pixy", blocks.stream().sorted(
			    		(Pixy2.Block o1, Pixy2.Block o2) -> o1.centerX - o2.centerX
			    	).collect(Collectors.toList()).toString());
		    	}
			}
		}, 0, 10);*/
        zeroHeading = navX.getFusedHeading();
    }

    @Override
	public void disabledInit(){
    	//navX.resetDisplacement();
        //navX.zeroYaw();

    }

    @Override
	public void disabledPeriodic() {
        Scheduler.getInstance().run();
        drive.encoderReset();
        
        updateDashboard();
    }

    @Override
	public void autonomousInit() {
        zeroHeading = navX.getFusedHeading();
        //RobotMap.shooterLEDRelay0.set(Value.kOn);
        RobotMap.gearPlaceLEDRelay1.set(Value.kOn);
        // schedule the autonomous command
    	autonomousCommand = (Command) autonomousChooser.getSelected();
    	if (autonomousCommand != null) {
    		autonomousCommand.start();
    	}
    }

    @Override
	public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        updateDashboard();
        //System.out.println(RobotMap.lidar.getDistance());
    }

    @Override
	public void teleopInit() {
        //zeroHeading = navX.getFusedHeading();
    	// This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (autonomousCommand != null) autonomousCommand.cancel();
       
    }

    @Override
	public void teleopPeriodic() {
        Scheduler.getInstance().run();
        updateDashboard();
        /*double servoSpeed = (oi.controller.getXButton() ? 1.0 : (oi.controller.getYButton() ? 0.0 : 0.5));
        RobotMap.spinner.set(servoSpeed);
        RobotMap.spinner2.set(1.0 - servoSpeed);*/
        //double servoSpeed = (Math.cos(Timer.getFPGATimestamp()) + 1.0) / 2.0;
        //RobotMap.spinner.set(servoSpeed);
        //RobotMap.relay1.set(((int) Timer.getFPGATimestamp() % 2 == 0 ? Value.kOn : Value.kOff));
        /*if (oi.rightJoy.getRawButton(2) || oi.rightJoy.getRawButton(3)) {
            RobotMap.shooterLEDRelay0.set(Relay.Value.kOn);
        } else {
        	RobotMap.shooterLEDRelay0.set(Relay.Value.kOff);
        }*/
//        double p = SmartDashboard.getNumber("shooterP", 0.03); SmartDashboard.putNumber("shooterP", p);
//        double i = SmartDashboard.getNumber("shooterI", 0.00016); SmartDashboard.putNumber("shooterI", i);
//        double d = SmartDashboard.getNumber("shooterD", 0.45); SmartDashboard.putNumber("shooterD", d);
//        double f = SmartDashboard.getNumber("shooterF", 1.0); SmartDashboard.putNumber("shooterF", f);
//        shooterFlyWheel.setPID(p, i, d);
//        shooterFlyWheel.setF(f);
    	if (Robot.oi.leftJoy.getRawButton(10)) zeroHeading = RobotMap.navX.getFusedHeading();
    }

    @Override
    public void testPeriodic() {
        LiveWindow.run();
    }
    
    private void updateDashboard() {
    	SmartDashboard.putNumber("LF Steer Position", steerLeftFront.getPosition());
    	SmartDashboard.putNumber("LR Steer Position", steerLeftRear.getPosition());
    	SmartDashboard.putNumber("RF Steer Position", steerRightFront.getPosition());
    	SmartDashboard.putNumber("RR Steer Position", steerRightRear.getPosition());

    	SmartDashboard.putNumber("LF Drive Position", driveLeftFront.getPosition());
    	SmartDashboard.putNumber("LR Drive Position", driveLeftRear.getPosition());
    	SmartDashboard.putNumber("RF Drive Position", driveRightFront.getPosition());
    	SmartDashboard.putNumber("RR Drive Position", driveRightRear.getPosition());
    	
    	SmartDashboard.putNumber("FlyWheelSpeed", -shooterFlyWheel.getSpeed());
    	
    	SmartDashboard.putNumber("NavXHeading", navX.getFusedHeading());
    	SmartDashboard.putNumber("NavX Angle", navX.getAngle());
    	SmartDashboard.putNumber("NavXCompass", navX.getCompassHeading());
    	SmartDashboard.putNumber("NavX Yaw", navX.getYaw());
    	//SmartDashboard.putNumber("NavX X Displacement", navX.getDisplacementX());
    	//SmartDashboard.putNumber("NavX Y Displacement", navX.getDisplacementY());
    	SmartDashboard.putNumber("ZeroHeading", zeroHeading);
    	
    	//SmartDashboard.putData("Ultrasonic", RobotMap.ultrasonic);
    	//SmartDashboard.putNumber("Ultrasonic SAE", RobotMap.ultrasonic.getDistance()/25.4);
    	
    	//SmartDashboard.putNumber("Ultrasonic Right", RobotMap.ultrasonicRight.getDistance()/25.4);
    	
    	/*List<PixyObject> objects = RobotMap.pixy.getObjects(false);
    	SmartDashboard.putNumber("PixyCount", objects.size());
    	SmartDashboard.putString("Pixy", objects.stream().sorted(
    		(PixyObject o1, PixyObject o2) -> o1.centerX - o2.centerX
    	).collect(Collectors.toList()).toString());*/
    	
    	List<Block> blocks = RobotMap.pixy.getDetectedBlocks();
    	/*List<Block> filteredBlocks = blocks
    			.stream()
    			.filter((Block b) -> (double) b.height / (double) b.width > 2.0)
    			.sorted((Block o1, Block o2) -> o1.centerX - o2.centerX)
    			.collect(Collectors.toList());*/
    	SmartDashboard.putNumber("PixyCount", blocks.size());
    	for (int i = 0; i < 5; i++) {
    		SmartDashboard.putString("PixyBlock" + i, (i < blocks.size() ? blocks.get(i).toString() : ""));
    	}
    	VisionTarget target = Vision.getGearTarget();
    	SmartDashboard.putString("GearTarget", (target != null ? target.toString() : "None"));
    	
    	List<Block> blocksShooter = RobotMap.pixyShooter.getDetectedBlocks();
    	
    	/*List<Block> filteredBlocksShooter = blocksShooter
    			.stream()
    			.filter((Block b) -> (double) b.width / (double) b.height > 2.0)
    			.sorted((Block o1, Block o2) -> o1.centerX - o2.centerX)
    			.collect(Collectors.toList());
    	SmartDashboard.putNumber("PixyCountShooter", blocksShooter.size());*/
    	
    	
    	for (int i = 0; i < 5; i++) {
    		SmartDashboard.putString("PixyBlockShooter" + i, (i < blocksShooter.size() ? blocksShooter.get(i).toString() : ""));
    	}
    	VisionTarget shooterTarget = Vision.getShooterTarget();
    	SmartDashboard.putString("ShooterTarget", (shooterTarget != null ? shooterTarget.toString() : "None"));
    	

    	/*SmartDashboard.putNumber("Lidar cm", RobotMap.lidar.getDistance());
    	SmartDashboard.putNumber("Lidar inches", RobotMap.lidar.getDistance() / 2.54);*/
    	
    	SmartDashboard.putNumber("RangeFinder", RangeFinder.getDistance());
    }
    
}
