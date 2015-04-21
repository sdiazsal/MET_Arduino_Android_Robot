package edu.url.salle.metapp02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class WifiChatActivity extends Activity {
    public Socket sender;
    public BufferedReader br;
    public PrintStream bw;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_chat);
	    TextView tv = (TextView)findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        Button send1 = (Button)findViewById(R.id.button1);

        send1.setOnClickListener(new OnClickListener()
        {   
              public void onClick(View v)
              {
                    final EditText et = (EditText)findViewById(R.id.editText1);
                    Editable e = et.getText();
                    String s = e.toString();
                    try
                    {
                          final DatagramSocket socket = new DatagramSocket ();
                          byte[] buf = new byte[256];
                          buf = s.getBytes ();
                          InetAddress address = InetAddress.getByName ("192.168.0.198");
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
                    }
                    catch (SocketException e1) {}
                    catch (UnknownHostException e2) {}
              }          
        });

        Thread t = new Thread (new SocketListener ());
        t.start();
  }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wifi_chat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
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
                      socket = new DatagramSocket (4569);
                      while (true)
                      {
                            final TextView t = (TextView)findViewById(R.id.textView1);                           
                            packet = new DatagramPacket (buf, buf.length);
                            socket.receive (packet);
                            System.out.println ("Received packet");
                            String s = new String (packet.getData());
                            CharSequence cs = t.getText ();
                            str = cs + "\r\n" +  s;
                            t.post(new Runnable()
                            {
                                  public void run()
                                  {
                                        t.setText(str);
                                  }
                            });
                      }
                }
                catch (IOException e)
                {
                      Log.e(getClass().getName(), e.getMessage());
                }
          }
    }
}
