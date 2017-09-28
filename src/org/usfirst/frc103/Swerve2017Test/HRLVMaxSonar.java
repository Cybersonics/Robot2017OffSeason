package org.usfirst.frc103.Swerve2017Test;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.ControllerPower;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.tables.ITable;

public class HRLVMaxSonar implements LiveWindowSendable {

	private AnalogInput rangeInput;
	private DigitalOutput enableOutput;

	public HRLVMaxSonar(AnalogInput rangeInput, DigitalOutput enableOutput) {
		this.rangeInput = rangeInput;
		this.enableOutput = enableOutput;
		rangeInput.setOversampleBits(0);
		rangeInput.setAverageBits(8);
	}
	
	public boolean getEnabled() {
		return enableOutput.get();
	}
	
	public void setEnabled(boolean enable) {
		enableOutput.set(enable);
	}
	
	public double getDistance() {
		return rangeInput.getAverageVoltage() / (ControllerPower.getVoltage5V() / 5120.0);
	}

	@Override
	public String getSmartDashboardType() {
		return "Ultrasonic";
	}

	private ITable table;

	@Override
	public void initTable(ITable subtable) {
		table = subtable;
		updateTable();
	}

	@Override
	public ITable getTable() {
		return table;
	}

	@Override
	public void updateTable() {
		if (table != null) {
			table.putNumber("Value", getDistance());
		}
	}

	@Override
	public void startLiveWindowMode() {
	}

	@Override
	public void stopLiveWindowMode() {
	}
}
