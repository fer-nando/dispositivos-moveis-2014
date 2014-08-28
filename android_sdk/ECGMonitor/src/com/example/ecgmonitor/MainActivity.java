package com.example.ecgmonitor;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chartview.ChartView;

/**
 * This is the main Activity that displays the current chat session.
 */
public class MainActivity extends Activity {
    // Debugging
    private static final String TAG = "HeartMonitor";
    private static final boolean D = false;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothService mChatService = null;
    // Heart monitor device
    private HeartMonitor mHeartMonitor = null;
	private long 	startTime, currentTime;
    
    // GUI
    private ChartView chart;
    private ImageView mLeadRAStatus, mLeadLAStatus;
    private TextView mTextBluetooth, mTextHeartRate;
    //private TextView mTextRA, mTextLA;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");

        // fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // keep device awake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Set up the window layout
        setContentView(R.layout.activity_main);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        // Setup heart monitor
        mHeartMonitor = new HeartMonitor(false);
       
        // GUI
        chart = (ChartView) findViewById(R.id.chartView1);
        mLeadRAStatus = (ImageView) findViewById(R.id.imageView1);
        mLeadLAStatus = (ImageView) findViewById(R.id.imageView2);
        mTextBluetooth = (TextView) findViewById(R.id.tv_bluetooth_state);
        //mTextRA = (TextView) findViewById(R.id.tv_RA_state);
        //mTextLA = (TextView) findViewById(R.id.tv_LA_state);
        mTextHeartRate = (TextView) findViewById(R.id.tv_heartrate);

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
    }

    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothService.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");
        
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothService(this, mHandler, mHeartMonitor);

    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        startSampling(false);
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        startSampling(false);
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        startSampling(false);
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

    private void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    private final void setStatus(int resId) {
        final ActionBar actionBar = getActionBar();
        if(actionBar != null)
        	actionBar.setSubtitle(resId);
        mTextBluetooth.setText(resId);
    }

    private final void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getActionBar();
        if(actionBar != null)
        	actionBar.setSubtitle(subTitle);
        mTextBluetooth.setText(subTitle);
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                    setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                    break;
                case BluetoothService.STATE_CONNECTING:
                    setStatus(R.string.title_connecting);
                    break;
                case BluetoothService.STATE_LISTEN:
                case BluetoothService.STATE_NONE:
                    setStatus(R.string.title_not_connected);
                    break;
                }
                break;
            case MESSAGE_WRITE:
                /*byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                mConversationArrayAdapter.add("Me:  " + writeMessage);*/
                break;
            case MESSAGE_READ:
                /*byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);*/
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
            	mHeartMonitor.setTestMode(false);
                connectDevice(data, true);
                startSampling(true);
            }
            break;
        case REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
            	mHeartMonitor.setTestMode(false);
                connectDevice(data, false);
                startSampling(true);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                //finish();
            }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
        case R.id.secure_connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            return true;
        case R.id.insecure_connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
            return true;
        case R.id.discoverable:
            // Ensure this device is discoverable by others
            ensureDiscoverable();
            return true;
        case R.id.test_mode:
        	// Enable/disable test mode
        	boolean testMode = !mHeartMonitor.getTestMode();
        	mHeartMonitor.setTestMode(testMode);
        	startSampling(testMode);
        	return true;
        }
        return false;
    }
    

    private void setLeadStatus(int leadRA, int leadLA) {
    	int leadOff = 0;
    	String msg = "";
    	
		if(leadRA == HeartMonitor.LEAD_CONNECTED) {
			//mTextRA.setText(R.string.text_lead_connected);
			mLeadRAStatus.setImageResource(R.drawable.ic_lead_on);
		} else {
			//mTextRA.setText(R.string.text_lead_disconnected);
			mLeadRAStatus.setImageResource(R.drawable.ic_lead_off);
			msg +=  " RA ";
			leadOff++;
		}
		
		if(leadLA == HeartMonitor.LEAD_CONNECTED) {
			//mTextLA.setText(R.string.text_lead_connected);
			mLeadLAStatus.setImageResource(R.drawable.ic_lead_on);
		} else {
			//mTextLA.setText(R.string.text_lead_disconnected);
			mLeadLAStatus.setImageResource(R.drawable.ic_lead_off);
			if(leadOff > 0)
				msg += " " + getString(R.string.text_and) + " LA ";
			else 
				msg +=  " LA ";
			leadOff++;
		}
		
		if(leadOff > 0) {
			if(leadOff > 1)
				msg = getString(R.string.text_electrodes) + msg + getString(R.string.text_disconnecteds);
			else
				msg = getString(R.string.text_electrode) + msg + getString(R.string.text_disconnected);
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		}
    }
    
    private final void setHeartRate(int heartRate) {
    	mTextHeartRate.setText(String.valueOf(heartRate));
    }
    
    private void startSampling(boolean sampling) {
    	if(sampling) {
        	startTime = System.currentTimeMillis();
    		viewHandler.postDelayed(updateView, (long)(mHeartMonitor.getRefreshPeriod()*1000));
    	} else {
    		viewHandler.removeCallbacks(updateView);
    	}
    }
    
    // Update the graph periodically
    Handler viewHandler = new Handler();
    Runnable updateView = new Runnable(){

		@Override
		public void run() {
			currentTime = System.currentTimeMillis();
			
			long updatePeriod = (long)(mHeartMonitor.getRefreshPeriod()*1000.0f);
			long nextUpdate = startTime + 2*updatePeriod;
			int remainingSamples = (int)((currentTime - startTime)/1000.0f/mHeartMonitor.getSamplePeriod());
			
			chart.addValues(mHeartMonitor.readECGSamples(remainingSamples));
			setHeartRate(mHeartMonitor.getHeartRate());

			
			if(mHeartMonitor.getLeadStatusChanged()) {
				int leadRA = mHeartMonitor.getLeadStatus(HeartMonitor.LEAD_RA);
				int leadLA = mHeartMonitor.getLeadStatus(HeartMonitor.LEAD_LA);
				setLeadStatus(leadRA, leadLA);
			}
			
			//viewHandler.postAtTime(updateView, currentTime+updatePeriod);
			viewHandler.postDelayed(updateView, nextUpdate-currentTime);
			//viewHandler.postDelayed(updateView, updatePeriod);
			startTime = currentTime;
			
			chart.invalidate();
		}
    	
    };

}
