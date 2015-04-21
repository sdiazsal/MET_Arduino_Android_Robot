package edu.url.salle.metapp02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class WifiActivity extends Activity {
	private ServerSocket serverSocket; 
	Handler updateConversationHandler; 
	Thread serverThread = null; 
	private TextView text; 
	public static final int SERVERPORT = 6000; 
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi);
		 
		text = (TextView) findViewById(R.id.EditText01); 
		updateConversationHandler = new Handler(); 
		this.serverThread = new Thread(new ServerThread()); 
		this.serverThread.start(); 

	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wifi, menu);
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
	protected void onPause() {

	      super.onPause();
	   }

	   protected void onResume() {
	
	      super.onResume();
	   }
	   
	   

	   protected void onStop() { 

	       super.onStop(); 
	       try { 
	          serverSocket.close(); 
	       } catch (IOException e) { 
	          e.printStackTrace(); 
	       } 
  } 
	   
	   
	   

   class ServerThread implements Runnable { 
      public void run() { 
           Socket socket = null; 
         try { 
             serverSocket = new ServerSocket(SERVERPORT); 
         } catch (IOException e) { 
              e.printStackTrace(); 
           } 
          while (!Thread.currentThread().isInterrupted()) { 
                try { 
                  socket = serverSocket.accept(); 
                 CommunicationThread commThread = new CommunicationThread(socket); 
                new Thread(commThread).start(); 
           } catch (IOException e) { 
                   e.printStackTrace(); 
              } 

            } 
      } 
    } 


class CommunicationThread implements Runnable { 
      private Socket clientSocket; 
  private BufferedReader input; 
     public CommunicationThread(Socket clientSocket) { 
       this.clientSocket = clientSocket; 
         try { 
        this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream())); 
       } catch (IOException e) { 
             e.printStackTrace(); 
           } 
      } 
      public void run() { 
         while (!Thread.currentThread().isInterrupted()) { 
             try { 
                  String read = input.readLine(); 
                 updateConversationHandler.post(new updateUIThread(read)); 
               } catch (IOException e) { 
                    e.printStackTrace(); 
              } 
            } 
       } 
   } 


class updateUIThread implements Runnable { 
       private String msg; 
      public updateUIThread(String str) { 
           this.msg = str; 
      } 
       @Override 
     public void run() { 
          text.setText(text.getText().toString()+"Client Says: "+ msg + "\n"); 
     } 
  } 
	
}

