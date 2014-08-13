package com.example.android.BluetoothChat;

public class HeartMonitor {
	
	// ECG data buffer size
	private static final int BUFFER_SIZE = 512;
	// lead status flags
	public static final int LEAD_UNKNOWN 		= -1;
	public static final int LEAD_CONNECTED 		= 0;
	public static final int LEAD_DISCONNECTED 	= 1;
	// message status
	public static final int MESSAGE_OK 			= 0;
	public static final int MESSAGE_WRONG_CMD 	= 1;
	public static final int MESSAGE_INCOMPLETE 	= 2;
	
	// heart monitor variables
	private int 	ecgDataBegin 	= 0;
	private int 	ecgDataEnd 		= 0;
	private int 	ecgDataSize		= 0;
	private float[] ecgData 		= new float[BUFFER_SIZE];
	private int 	leadStatus 		= LEAD_UNKNOWN;
	private int 	heartRate 		= 0;
	private int 	sampleRate		= 250;
	private float 	samplePeriod	= 1.0f/sampleRate;
	private float 	ecgScale		= 5.0f/4096.0f;
	
	// constructor
	public HeartMonitor() {};
	
	public HeartMonitor(int sampleRate, float ecgScale) {
		this.sampleRate = sampleRate;
		this.ecgScale = ecgScale;
		samplePeriod = 1.0f/sampleRate;
	}
	
	// parse received messages
	public int parseMessage(String msg) {
		
		String[] fields = msg.split(" ");		
		if(fields.length < 2)
			return MESSAGE_INCOMPLETE;
		
		char cmd = fields[0].charAt(0);		
		switch(cmd) {
			case 'F':
				heartRate = Integer.valueOf(fields[1]);
				break;		
			case 'A':
				leadStatus = Integer.valueOf(fields[1]);
				break;
			case 'D':
				synchronized (ecgData) {
					for(int i = 1; i < fields.length && ecgDataSize < ecgData.length; i++) {
						float data = ecgScale * Integer.valueOf(fields[i]);
						ecgData[ecgDataEnd++] = data;
						ecgDataSize++;
						if(ecgDataEnd == ecgData.length)
							ecgDataEnd = 0;
					}
				}
				break;
			default:
				return MESSAGE_WRONG_CMD;
		}
		
		return MESSAGE_OK;
	}
	
	// return the current ECG data
	public float readECG() {
		float data = Float.NaN;
		synchronized (ecgData) {
			if(ecgDataSize > 0) {
				data = ecgData[ecgDataBegin++];
				ecgDataSize--;
				if(ecgDataSize == 0) {
					ecgDataBegin = 0;
					ecgDataEnd = 0;
				}
			}
		}
		return data;
	}
	
	// getters
	public int getHeartRate() {
		return heartRate;
	}
	public int getLeadStatus() {
		return leadStatus;
	}
	public int getSampleRate() {
		return sampleRate;
	}
	public float getSamplePeriod() {
		return samplePeriod;
	}
	
}
