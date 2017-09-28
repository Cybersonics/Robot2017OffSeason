package org.usfirst.frc103.Swerve2017Test;

import org.usfirst.frc103.Swerve2017Test.ReliableI2C.I2CPort;
import org.usfirst.frc103.Swerve2017Test.subsystems.RangeFinder;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Direction;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
	
    public static CANTalon driveLeftFront;
    public static CANTalon driveLeftRear;
    public static CANTalon driveRightFront;
    public static CANTalon driveRightRear;
    public static CANTalon steerLeftFront;
    public static CANTalon steerLeftRear;
    public static CANTalon steerRightFront;
    public static CANTalon steerRightRear;
    public static CANTalon shooterFlyWheel;
    public static CANTalon shooterElevator;
    public static CANTalon gearManipulatorDoors;
    public static CANTalon climberLiftWinch;
    public static CANTalon climberLiftWinchFollower;
    public static CANTalon agitator;
    public static AHRS navX;
    //public static HRLVMaxSonar ultrasonic;
    //public static HRLVMaxSonar ultrasonicRight;
    public static Pixy2 pixy;
    public static Pixy2 pixyShooter;
    public static Relay shooterLEDRelay0, gearPlaceLEDRelay1;
    //public static Lidar lidar;
    //public static DigitalOutput lidarReset;
    public static Ultrasonic ultrasonic;

    public static void init() {
        driveLeftFront = new CANTalon(10);
        driveLeftFront.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        driveLeftFront.reverseSensor(true);
        LiveWindow.addActuator("Drive", "LeftFrontDrive", driveLeftFront);
        
        driveLeftRear = new CANTalon(11);
        driveLeftRear.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        LiveWindow.addActuator("Drive", "LeftRearDrive", driveLeftRear);
        
        driveRightFront = new CANTalon(12);
        driveRightFront.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        driveRightFront.setInverted(true);
        LiveWindow.addActuator("Drive", "RightFrontDrive", driveRightFront);
        
        driveRightRear = new CANTalon(13);
        driveRightRear.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        driveRightRear.setInverted(true);
        driveRightRear.reverseSensor(true);
        LiveWindow.addActuator("Drive", "RightRearDrive", driveRightRear);
        
        steerLeftFront = new CANTalon(16);
        steerLeftFront.setFeedbackDevice(FeedbackDevice.AnalogEncoder);
        steerLeftFront.setPID(10.0, 0.02, 0.0);
        steerLeftFront.setIZone(100);
        steerLeftFront.setAllowableClosedLoopErr(5);
        steerLeftFront.changeControlMode(TalonControlMode.Position);
        LiveWindow.addActuator("Steer", "LeftFrontSteer", steerLeftFront);
        
        steerLeftRear = new CANTalon(17);
        steerLeftRear.setFeedbackDevice(FeedbackDevice.AnalogEncoder);
        steerLeftRear.setPID(10.0, 0.02, 0.0);
        steerLeftRear.setIZone(100);
        steerLeftRear.setAllowableClosedLoopErr(5);
        steerLeftRear.changeControlMode(TalonControlMode.Position);
        LiveWindow.addActuator("Steer", "LeftRearSteer", steerLeftRear);
        
        steerRightFront = new CANTalon(18);
        steerRightFront.setFeedbackDevice(FeedbackDevice.AnalogEncoder);
        steerRightFront.setPID(10.0, 0.02, 0.0);
        steerRightFront.setIZone(100);
        steerRightFront.setAllowableClosedLoopErr(5);
        steerRightFront.changeControlMode(TalonControlMode.Position);
        LiveWindow.addActuator("Steer", "RightFrontSteer", steerRightFront);
        
        steerRightRear = new CANTalon(19);
        steerRightRear.setFeedbackDevice(FeedbackDevice.AnalogEncoder);
        steerRightRear.setPID(10.0, 0.02, 0.0);
        steerRightRear.setIZone(100);
        steerRightRear.setAllowableClosedLoopErr(5);
        steerRightRear.changeControlMode(TalonControlMode.Position);
        LiveWindow.addActuator("Steer", "RightRearSteer", steerRightRear);
        
        shooterFlyWheel = new CANTalon(2);
        shooterFlyWheel.setInverted(true);
        shooterFlyWheel.reverseSensor(true);
        shooterFlyWheel.configNominalOutputVoltage(0, 0);
        shooterFlyWheel.configPeakOutputVoltage(0.0, -12.0);
        shooterFlyWheel.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        shooterFlyWheel.setPID(0.15, 0.0, 0.375);//Old set 0.5, 0.0, 0.5
        shooterFlyWheel.setF(0.032);//Old set 0.032
        //shooterFlyWheel.setF(0.035);
        //shooterFlyWheel.setF(10.416);
        //shooterFlyWheel.setPID(0, 0, 0);
        shooterFlyWheel.enableBrakeMode(false);
        
        shooterFlyWheel.changeControlMode(TalonControlMode.Speed);
        //shooterFlyWheel.changeControlMode(TalonControlMode.PercentVbus);
        LiveWindow.addActuator("Shooter", "FlyWheel", shooterFlyWheel);
        
        shooterElevator = new CANTalon(3);
        shooterElevator.setInverted(true);
        LiveWindow.addActuator("Shooter", "Elevator", shooterElevator);
        
        gearManipulatorDoors = new CANTalon(1);
        LiveWindow.addActuator("GearManipulator", "Doors", gearManipulatorDoors);
        
        climberLiftWinch = new CANTalon(4);
        LiveWindow.addActuator("Climber", "LiftWinch", climberLiftWinch);
        
        climberLiftWinchFollower = new CANTalon(6);
        climberLiftWinchFollower.changeControlMode(TalonControlMode.Follower);
        climberLiftWinchFollower.set(climberLiftWinch.getDeviceID());
        LiveWindow.addActuator("Climber", "LiftWinchFollower", climberLiftWinchFollower);
        
        //spinner = new Servo(0);
        //spinner2 = new Servo(1);
        
        agitator = new CANTalon(5);
        LiveWindow.addActuator("Shooter", "Agitator", agitator);
        
        //LiveWindow.addActuator("Shooter", "Spinner1", spinner);
        //LiveWindow.addActuator("Shooter", "Spinner2", spinner2);
        
        navX = new AHRS(SPI.Port.kMXP);
        
        //Shooter Ring light
        shooterLEDRelay0 = new Relay(0, Direction.kForward);
        shooterLEDRelay0.setSafetyEnabled(false);
        //shooterLEDRelay0.set(Value.kOn); 
        shooterLEDRelay0.set(Value.kOff);
        
        //Gear Place Ring light
        gearPlaceLEDRelay1 = new Relay(1, Direction.kForward);
        gearPlaceLEDRelay1.setSafetyEnabled(false);
        gearPlaceLEDRelay1.set(Value.kOn); // 
        
        
        /*ultrasonic = new HRLVMaxSonar(new AnalogInput(0), new DigitalOutput(0));
        ultrasonic.setEnabled(false);
        
        ultrasonicRight = new HRLVMaxSonar(new AnalogInput(1), new DigitalOutput(1));
        ultrasonicRight.setEnabled(true);*/

        /*System.setProperty("jna.library.path", "/usr/local/frc/lib");
        System.setProperty("jna.debug_load", "true");
        System.loadLibrary("mxp_specialness");*/
        
        ReliableI2C.init();
        pixy = new Pixy2(ReliableI2C.openDevice(I2CPort.MXP, (byte) 0x54));
        pixyShooter = new Pixy2(ReliableI2C.openDevice(I2CPort.MXP, (byte) 0x53));
        //lidar = new Lidar(ReliableI2C.openDevice(I2CPort.MXP, (byte) 0x62));
        
        ultrasonic = new Ultrasonic(8, 9);
        RangeFinder.start();
    }
    
}
