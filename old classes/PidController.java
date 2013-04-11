package com.example.rovercontrol;

public class PidController
{
	double mPreviousError;      ///<  Previous error of system
	double mPreviousSolution;   ///<  Previous feedback result (used with upper and lower bounds to prevent winding)
	double mP;              	 ///<  Proportional Gain
	double mI;              	 ///<  Integral Gain
	double mD;             		 ///<  Differential Gain
	double mSumOfError;    		 ///<  Sum of error
	double mOffset;             ///<  Offset to add onto solution (default 0)
	double mUpperBound;         ///<  Upper bound on solution (prevents winding/large sum of errors) default is INFINITE
	double mLowerBound;         ///<  Lower bound on solution (prevents winding/large sum of erros)  default is -INFINITE
	double mDT = 1;                 ///<  DT term.


	/**
	 * Set the update interval for PID constants
	 * @param dt Delta Time variable for PID calculations, default is 1.
	 */
	public void SetRate(double dt)
	{
		mDT = dt;
	}



	/**
	 * Uses error feedback to provide new input to the system.
	 * @param error The feedback error of the system.
	 */
	double UpdatePID(double error)
	{
		double solution;

		//  Prevent over winding the sum of error.  This basically
		//  means, if you are already giving the system 100%, you can't
		//  give it higher than that, so don't keep increasing
		//  the error
		if(mPreviousSolution > mLowerBound && mPreviousSolution < mUpperBound)
		{
			mSumOfError += error;
		}
		solution = (mP*error + mI*mSumOfError*mDT + mD*(error - mPreviousError))/mDT;
		System.out.printf("rover_debug PID: %f\n", solution);
		mPreviousError = error;

		//  Add any offset for the system
		solution += mOffset;

		//  Check upper and lower bounds of solution for
		//  the system being used
		if(solution <= mLowerBound)
			solution = mLowerBound;
		if(solution >= mUpperBound)
			solution = mUpperBound;

		mPreviousSolution = solution;

		return solution;
	}


	/**
	 * Sets and desired offset for calculating solution from the PID.
	 * @param offset  The offset to add to the PID solutions.
	 */
	void SetOffset(double offset)
	{
		mOffset = offset;
	}


	/**
	 * Sets boundaries for the solutions the PID can calculate.  
	 * @param min The lower bound of your system inputs.
	 * @param max The upper bound of your system inputs.
	 */
	void SetBounds(double min, double max)
	{
		if(max > min)
		{
			mLowerBound = min;
			mUpperBound = max;
		}
	}

	/**
	 * Sets the PID gain values of your controller. 
	 * @param p The proportional gain
	 * @param i The integral gain
	 * @param d The derivative gain
	 */
	void SetGains(double p, double i, double d)
	{
		mP = p;
		mI = i;
		mD = d;
	}


	/**
	 * Clears out any summed error.
	 */
	void ClearError()
	{
		mSumOfError = 0;
	}

	
	/**
	 * Sets all values back to defaults.
	 */
	void Reset()
	{
		mLowerBound = -Long.MAX_VALUE;
		mUpperBound = Long.MAX_VALUE;

		mP = 0;
		mI = 0;
		mD = 0;
		mSumOfError = 0;
		mPreviousSolution = 0;
		mPreviousError = 0;
		mOffset = 0;
	}

	
	/**
	 * Resets the sum of errors, and configures the controller based on the inputs.
	 * @param lower Lower bound of system inputs
	 * @param upper Upper bound of system inputs
	 * @param p The proportional gain
	 * @param i The integral gain
	 * @param d The derivative gain
	 */
	void Reset(double lower, double upper, double offset, double p, double i, double d)
	{
		SetBounds(lower, upper);
		SetGains(p, i, d);
		SetOffset(offset);
		mSumOfError = 0;
		mPreviousSolution = mOffset;
		mPreviousError = 0;
	}
}
