package com.msra.SensorCollect;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.*;
import android.widget.*;

/**
 * <h3>Application that displays the values of the acceleration sensor graphically.</h3>

<p>This demonstrates the {@link android.hardware.SensorManager android.hardware.SensorManager} class.

<h4>Demo</h4>
OS / Sensors

<h4>Source files</h4>
 * <table class="LinkTable">
 *         <tr>
 *             <td >src/com.example.android.apis/os/Sensors.java</td>
 *             <td >Sensors</td>
 *         </tr>
 * </table> 
 */

public class SensorCollectActivity extends Activity {
	public static final String TAG = "SensorCollect";
	
    private SensorManager mSensorManager;
    private GraphView mGraphView;
    private DetectRate _detectFastestRate = new DetectRate();
    private DetectRate _detectGameRate = new DetectRate();
    private DetectRate _detectNormalRate = new DetectRate();
    private DetectRate _detectUiRate = new DetectRate();
    private SensorRecorder _sensorRecorder;
    private FileWriter _fileWriter;
    private MediaPlayer hintPlayer = new MediaPlayer();
    private List<String> _rateItem = new ArrayList<String>();
    private List<String> _intervalItem = new ArrayList<String>();
    private ArrayAdapter<String> _intervalAdapter;
    private Vibrator vibrator;
    ArrayList<Integer> _intervals = new ArrayList<Integer>();
    enum SAMPLERATE{
    	FASTEST,
    	GAME,
    	NORMAL,
    	UI
    }
    static final long DI = 200;
    static final long DA = DI * 2;
    static final int SETENABLE = 0x00;
    static final int CLEARTEXT = 0x01;
    static final long[] MORSEw = new long[]{0,DI,DI,DA,DI,DA};
    static final long[] MORSEs = new long[]{0,DI,DI,DI,DI,DI};
    
    /**
     * Initialization of the Activity after it is first created.  Must at least
     * call {@link android.app.Activity#setContentView setContentView()} to
     * describe what is to be displayed in the screen.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Be sure to call the super class.
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //mGraphView = new GraphView(this);
        //setContentView(mGraphView);
        setContentView(R.layout.main);
        
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        
        final Button btnDect = (Button)findViewById(R.id.btnDectRate);
        final Spinner spnRate = (Spinner)findViewById(R.id.spnRate);
        final Button btnStart = (Button)findViewById(R.id.btnStart);
        final Button btnStop = (Button)findViewById(R.id.btnStop);
        final TextView txtInterval = (TextView)findViewById(R.id.txtInterval);
        final EditText edtInterval = (EditText)findViewById(R.id.edtInterval);
        final Button btnAddInterval = (Button)findViewById(R.id.btnAddInterval);
        final Button btnUndoInterval = (Button)findViewById(R.id.btnUndoInterval);
        final ListView lstInterval = (ListView)findViewById(R.id.lstInterval);
        
		final Handler handler = new Handler(){
			public void handleMessage(Message msg){
				switch(msg.what)
				{
				case SETENABLE:
					Button btn = (Button)msg.obj;
					btn.setEnabled(msg.arg1!=0);
					break;
				case CLEARTEXT:
					EditText et = (EditText)msg.obj;
					et.setText("");
					break;
				}
			}
		};
        _intervalAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,_intervalItem);
        
        lstInterval.setAdapter(_intervalAdapter);
        
        final Handler updateUI = new Handler();
        btnDect.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				btnDect.setEnabled(false);

					new Thread(new Runnable(){

						public void run() {
							// TODO Auto-generated method stub
							try {
								btnDect.postInvalidate();
								spnRate.postInvalidate();
								mSensorManager.registerListener(_detectFastestRate, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
										SensorManager.SENSOR_DELAY_FASTEST);
								
								Thread.sleep(5000);
								mSensorManager.unregisterListener(_detectFastestRate);
								Log.d(TAG,"RUN DONE 1");

								mSensorManager.registerListener(_detectGameRate, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
						                SensorManager.SENSOR_DELAY_GAME);
								Thread.sleep(5000);
								mSensorManager.unregisterListener(_detectGameRate);
								Log.d(TAG,"RUN DONE 2");
								
								mSensorManager.registerListener(_detectUiRate, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
						                SensorManager.SENSOR_DELAY_UI);
								Thread.sleep(5000);
								mSensorManager.unregisterListener(_detectUiRate);
								Log.d(TAG,"RUN DONE 3");
								
								mSensorManager.registerListener(_detectNormalRate, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
						                SensorManager.SENSOR_DELAY_NORMAL);
								Thread.sleep(5000);
								mSensorManager.unregisterListener(_detectNormalRate);
								Log.d(TAG,"RUN DONE 4");
								
								updateUI.post(new Runnable(){

									public void run() {
										// TODO Auto-generated method stub
										
										_rateItem.add(""+(int)_detectFastestRate.GetSampleRate());
										_rateItem.add(""+(int)_detectGameRate.GetSampleRate());
										_rateItem.add(""+(int)_detectUiRate.GetSampleRate());
										_rateItem.add(""+(int)_detectNormalRate.GetSampleRate());
										
										ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
												android.R.layout.simple_spinner_item, _rateItem);
										adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
										
										spnRate.setAdapter(adapter);
										btnDect.setEnabled(true);
										//btnDect.invalidate();
										
									}
									
								});
								
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
					}).start();
			}
        	
        });
        
        
        btnStart.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub

				// if _intervals.size() > 0 then it can auto stop, so disenable "Stop" button
				btnStart.setEnabled(false);
				if(_intervals.size() > 0)
				{
					btnStop.setEnabled(false);
				}
				else{
					btnStop.setEnabled(true);
				}
				
				int sampleRate = 0;//spnRate.getSelectedItemPosition();
				final SimpleDateFormat formatter = new SimpleDateFormat("yyMMddhhmmss");
				String tmpFileName = "/sdcard/0sensorcollect/sensorCollect" + formatter.format(new Date(System.currentTimeMillis())) + ".csv";
				try {
					_fileWriter = new FileWriter(tmpFileName);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				_sensorRecorder = new SensorRecorder(500,_fileWriter);
				String tmpIntervalName = "/sdcard/0sensorcollect/sensorCollect" + formatter.format(new Date(System.currentTimeMillis())) + "int.csv";
				try {
					FileWriter intervalWriter = new FileWriter(tmpIntervalName);
					for(int i = 0;i < _intervals.size();++i)
					{
						if(i % 2 == 1)
							intervalWriter.write("Stay,");
						else
							intervalWriter.write("Walk,");
						intervalWriter.write(""+_intervals.get(i) + "\n");
					}
					intervalWriter.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				mSensorManager.registerListener(_sensorRecorder, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
		                sampleRate);
//				try {
//					hintPlayer.reset();
//					hintPlayer.setDataSource("/sdcard/myMP3/Color.mp3");
//					hintPlayer.prepare();
//					hintPlayer.start();
//				} catch (IllegalArgumentException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IllegalStateException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				new Thread(new Runnable(){

					public void run() {
						// TODO Auto-generated method stub
						for(int i = 0;i < _intervals.size();++i)
						{
							try {
								Thread.sleep(1000 * _intervals.get(i));
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							try{
								if(i % 2 == 0)//start stay
								{
									hintPlayer.reset();
									hintPlayer.setDataSource("/sdcard/myMP3/Freeze.mp3");
									hintPlayer.prepare();
									hintPlayer.start();
									vibrator.vibrate(MORSEs, -1);
									//vibrator.vibrate(600);
									//vibrator.vibrate(600);
								}
								else{//start walk
									hintPlayer.reset();
									hintPlayer.setDataSource("/sdcard/myMP3/Color.mp3");
									hintPlayer.prepare();
									hintPlayer.start();
									vibrator.vibrate(MORSEw, -1);
									//vibrator.vibrate(300);
									//vibrator.vibrate(300);
								}
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						if(_intervals.size() > 0)// auto stop
						{
							mSensorManager.unregisterListener(_sensorRecorder);
							
							// set btnStart enabled
							Message msg = new Message();
							msg.what = SETENABLE;
							msg.obj = btnStart;
							msg.arg1 = 1;
							handler.sendMessage(msg);
							
														
						}
					}// run()
					
				}).start();
			}
        	
        });
        
        btnStop.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
//				try {
//					hintPlayer.reset();
//					hintPlayer.setDataSource("/sdcard/myMP3/Freeze.mp3");
//					hintPlayer.prepare();
//					hintPlayer.start();
//				} catch (IllegalArgumentException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} catch (IllegalStateException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
				mSensorManager.unregisterListener(_sensorRecorder);
				try {
					_fileWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				btnStart.setEnabled(true);
			}
        	
        });
        
        btnAddInterval.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Log.d(TAG,edtInterval.getText().toString() + txtInterval.getText().toString() + (txtInterval.getText().toString() == "walk"));
				String strInterval = txtInterval.getText().toString() + ": " + edtInterval.getText().toString();
				_intervals.add(Integer.parseInt(edtInterval.getText().toString()));
				_intervalItem.add(strInterval);
				_intervalAdapter.notifyDataSetChanged();
				if(txtInterval.getText().toString().equals("walk") )
					txtInterval.setText("stay");
				else
					txtInterval.setText("walk");
				Message msg = new Message();
				msg.what = CLEARTEXT;
				msg.obj = edtInterval;
				handler.sendMessage(msg);
				
			}
        	
        });
        
        btnUndoInterval.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(_intervalAdapter.getCount() > 0)
				{
					_intervalItem.remove(_intervalItem.size() - 1);
					_intervalAdapter.notifyDataSetChanged();
					if(txtInterval.getText().toString().equals("walk"))
						txtInterval.setText("stay");
					else
						txtInterval.setText("walk");
					_intervals.remove(_intervals.size()-1);

				}
				
			}
        	
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"RESUME");
//        mSensorManager.registerListener(mGraphView,
//                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                SensorManager.SENSOR_DELAY_FASTEST);
//        mSensorManager.registerListener(mGraphView,
//                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
//                SensorManager.SENSOR_DELAY_FASTEST);
//        mSensorManager.registerListener(mGraphView, 
//                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
//                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onStop() {
        mSensorManager.unregisterListener(mGraphView);
        super.onStop();
    }
}