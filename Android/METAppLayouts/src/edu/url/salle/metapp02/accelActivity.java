package edu.url.salle.metapp02;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import android.widget.ImageButton;
import android.widget.TextView;

import android.widget.Toast;

public class accelActivity extends Activity implements SensorEventListener{
	int x,y,z;
	private Sensor mAccelerometer;
	  TextView text2,text4,text6;
	
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accel);
        
        x=0;
        y=0;
        z=0;
      
        ImageButton Bvolver;
		
		Bvolver = (	ImageButton) findViewById(R.id.button1);
		Bvolver.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
   
        	   sendData();
        	   
                // Perform action on click
            	Toast toast1 =Toast.makeText(getApplicationContext(),"Id clicleada volver ", Toast.LENGTH_SHORT);
				toast1.show();
        	   volver(null);
				
           }

        });
		
		text2 = (TextView) findViewById(R.id.text2);
		text4 = (TextView) findViewById(R.id.text4);
		text6 = (TextView) findViewById(R.id.text6);
        
	

}
	
	
 

	protected void onResume()

{

     super.onResume();

     SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);

     List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);

     if (sensors.size() > 0) //dispositivo android tiene acelerometro

     {

         sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);

     }

}

protected void onPause()

{

    SensorManager mSensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);

    mSensorManager.unregisterListener(this, mAccelerometer);

    super.onPause();

}

protected void onStop()

{

    SensorManager mSensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);

    mSensorManager.unregisterListener(this, mAccelerometer);

    super.onStop();

}

public void onSensorChanged1(SensorEvent event) {

    this.text2.setText("X = "+event.values[SensorManager.DATA_X]);

    this.text4.setText("Y = "+event.values[SensorManager.DATA_Y]);

    this.text6.setText("Z = "+event.values[SensorManager.DATA_Z]);

}

@Override
public void onAccuracyChanged(Sensor sensor, int accuracy) {   

}



	public void volver(View view) {
		   
		Intent intent = new Intent(this, 
               MainActivity.class);
		
startActivity(intent);

}



	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	public boolean sendData(){
		try {
			final DatagramSocket socket = new DatagramSocket ();
			InetAddress address;
			address = InetAddress.getByName ("172.20.10.9");
			
			byte[] buf = new byte[256];
	        String s = "A2553MY"; //TESTING
	        buf = s.getBytes ();
			        
	        final DatagramPacket packet = new DatagramPacket (buf, buf.length, address, 55056);
			
	        new Thread ()
            {
                  public void run ()
                  {
                        try
                        {
                              System.out.println ("About to send message");
                              socket.send (packet);
                              System.out.println ("Sent message");
                        }
                        catch (IOException e1)
                        {
                              // TODO Auto-generated catch block
                              e1.printStackTrace();
                        }
                        socket.close();
                  }

            }.start ();
	        
            return true;
            
		}catch (SocketException e) {e.printStackTrace();
		}catch (UnknownHostException e) {e.printStackTrace();
		}catch (IOException e) {e.printStackTrace();
		}      
		return false;
	}
	
	class SocketListener implements Runnable
    {
          String str;
          public void run()
          {
                DatagramSocket socket;
                DatagramPacket packet;
                byte[] buf = new byte[256];
                System.out.println ("Thread running");
                try
                {
                      //socket = new DatagramSocket (4560);
                	  socket = new DatagramSocket (55056);
                      while (true)
                      {                          
                            packet = new DatagramPacket (buf, buf.length);
                            socket.receive (packet);
                            System.out.println ("Received packet");
                            String s = new String (packet.getData());
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                      }
                }
                catch (IOException e)
                {
                      Log.e(getClass().getName(), e.getMessage());
                }
          }
    }
	
	
}
