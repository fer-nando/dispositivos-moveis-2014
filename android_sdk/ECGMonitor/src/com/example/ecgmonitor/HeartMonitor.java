package com.example.ecgmonitor;

public class HeartMonitor {
	
	// ECG data buffer size
	private static final int BUFFER_SIZE = 512;
	// lead status flags
	public static final int LEAD_UNKNOWN 		= -1;
	public static final int LEAD_CONNECTED 		= 0;
	public static final int LEAD_DISCONNECTED 	= 1;
	public static final int LEAD_RA 			= 0;
	public static final int LEAD_LA 			= 1;
	// message status
	public static final int MESSAGE_OK 			= 0;
	public static final int MESSAGE_WRONG_CMD 	= 1;
	public static final int MESSAGE_INCOMPLETE 	= 2;
	
	// heart monitor variables
	private int 	ecgDataBegin 	= 0;
	private int 	ecgDataEnd 		= 0;
	private int 	ecgDataSize		= 0;
	private float[] ecgData 		= new float[BUFFER_SIZE];
	private int[] 	leadStatus 		= new int[]{LEAD_DISCONNECTED, LEAD_DISCONNECTED};
	private int 	heartRate 		= 0;
	private float 	samplePeriod	= 1.0f/250.0f;
	private float 	ecgScale		= 5.0f/4096.0f;
	private float 	refreshPeriod	= 1.0f/60.0f;
	
	// constructor
	public HeartMonitor() {};
	
	public HeartMonitor(int sampleRate, float ecgScale) {
		this.samplePeriod = sampleRate;
		this.ecgScale = ecgScale;
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
				leadStatus[LEAD_RA] = Integer.valueOf(fields[1]);
				leadStatus[LEAD_LA] = Integer.valueOf(fields[2]);
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
		float data = 0;
		synchronized (ecgData) {
			if(ecgDataSize > 0) {
				data = ecgData[ecgDataBegin++];
				ecgDataSize--;
				if(ecgDataBegin == ecgData.length)
					ecgDataBegin = 0;
				if(ecgDataSize == 0) {
					ecgDataBegin = 0;
					ecgDataEnd = 0;
				}
			}
		}
		return data;
	}
	
	// getters
	public int getDataSize() {
		return ecgDataSize;
	}
	public int getHeartRate() {
		return heartRate;
	}
	public int getLeadStatus(int l) {
		return leadStatus[l];
	}
	public float getSamplePeriod() {
		return samplePeriod;
	}
	public float getRefreshPeriod() {
		return refreshPeriod;
	}
	
}
