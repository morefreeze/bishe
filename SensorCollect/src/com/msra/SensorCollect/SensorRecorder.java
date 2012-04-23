package com.msra.SensorCollect;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class SensorRecorder implements SensorEventListener {
	private final static long MS2NS = 1000000;
	private long _delayWrite = 500;
	private FileWriter _fileWriter;
	private long _lastSaveTime = -1;
	private ArrayList<float[]> _saveData = new ArrayList<float[]>();
	private ArrayList<Long> _saveTimeStamp = new ArrayList<Long>();

	public SensorRecorder(long delayWrite,FileWriter fileWriter) {
		super();
		this._delayWrite = delayWrite;
		this._fileWriter = fileWriter;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		event.timestamp /= MS2NS;
		_saveData.add(event.values);
		_saveTimeStamp.add(event.timestamp);
		if(_lastSaveTime == -1)
			_lastSaveTime = event.timestamp;
		else{
			if(event.timestamp - _lastSaveTime >= _delayWrite){
				for(int i = 0;i < _saveData.size();++i)
				{
					try {
						_fileWriter.write(""+_saveTimeStamp.get(i)+",");
						float[] vals = _saveData.get(i);
						double vari =  Math.sqrt(vals[0]*vals[0] + vals[1]*vals[1] + vals[2]*vals[2]);
						for(int j = 0;j < vals.length;++j)
						{
							_fileWriter.write(""+vals[j]+",");
						}
						_fileWriter.write(""+vari+"\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				_saveData.clear();
				_saveTimeStamp.clear();
				_lastSaveTime = event.timestamp;
			}
		}
	}

}
