package com.msra.SensorCollect;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class DetectRate implements SensorEventListener {
	public static final String TAG = "SensorCollect";
	private static final double NANOSEC2S = 1e-9;
	private long _count = 0;
	private long _timeStart = -1;
	private long _timeLast = -1;
	private double _sampleRate = 0;
	
	public DetectRate() {
		super();
		
	}
	public void Reset(){
		_count = 0;
		_timeStart = -1;
		_timeLast = -1;
		_sampleRate = 0;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		++_count;
		if(_timeStart == -1){
			_timeStart = event.timestamp;
			Log.d(TAG,"sensor start");
		}
		_timeLast = event.timestamp;
//		Log.d(TAG,"sensor changed "+ _timeLast);
	}
	public double GetSampleRate()
	{
		if(_timeStart == -1){
			_sampleRate = 0;
		}
		else{
			Log.d(TAG,"count "+_count +  " ");
			_sampleRate = (double)_count / ((double)(_timeLast - _timeStart) * NANOSEC2S);
		}
		return _sampleRate;
	}

}
