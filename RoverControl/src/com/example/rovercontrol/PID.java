package com.example.rovercontrol;

public class PID {
	
	private double _previousError;
	private double _integral;
	
	private double _pGain, _iGain, _dGain;
	private double _dt;
	
	private double _setPoint;
	
	public PID(double p, double i, double d, double dt) {
		_pGain = p;
		_iGain = i;
		_dGain = d;
		_dt = dt;
		_previousError = 0;
		_integral = 0;
	}
	public void reset() {
		_previousError = 0;
		_integral = 0;
	}
	public void setTarget(double point) {
		_setPoint = point;
	}
	public double update(double measured) {
		double error = _setPoint - measured;
		_integral += error*_dt;
		double derivative = (error - _previousError)/_dt;
		double output = (_pGain*error) + (_iGain*_integral) + (_dGain*derivative);
		_previousError = error;
		return output;
	}
}
