package com.example.rovercontrol.control;

public class PID {
	
	private double _previousError;
	private double _integral;
	
	private double _pGain, _iGain, _dGain;
	
	private double _setPoint;
	
	public PID() {
		_pGain = 1.0;
		_iGain = _dGain = 0;
	}
	
	/**
	 * Must call this to enable control
	 * @param p Proportional gain (default 1.0)
	 * @param i Integral gain (default 0.0)
	 * @param d Derivative gain (default 0.0)
	 */
	public void configure(double p, double i, double d) {
		_pGain = p;
		_iGain = i;
		_dGain = d;
	}
	/**
	 * Reset Integral term
	 */
	public void reset() {
		_previousError = 0;
		_integral = 0;
	}
	/**
	 * Set feedback value to aim for
	 * @param point
	 */
	public void setTarget(double point) {
		_setPoint = point;
	}
	
	/**
	 * Run a single cycle of control
	 * @param measured feedback value
	 * @param dt time between calls
	 * @return amount to adjust by
	 */
	public double update(double measured, double dt) {
		double error = _setPoint - measured;
		_integral += error*dt;
		double derivative = (error - _previousError)/dt;
		double output = (_pGain*error) + (_iGain*_integral) + (_dGain*derivative);
		_previousError = error;
		return output;
	}
}
