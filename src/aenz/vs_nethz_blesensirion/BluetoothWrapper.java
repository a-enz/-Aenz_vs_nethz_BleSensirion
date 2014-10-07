package aenz.vs_nethz_blesensirion;

import android.bluetooth.BluetoothDevice;

public class BluetoothWrapper{
	private final BluetoothDevice device;
	
	public BluetoothWrapper(BluetoothDevice device) {
		this.device = device;
	}
	
	public BluetoothDevice getDevice(){
		return device;
	}
	
	@Override
	public String toString() {
		return "Name: " + device.getName() + "\n" + "Address: " + device.getAddress();
	}
	
	@Override
	public boolean equals(Object device) {
		if(device instanceof BluetoothWrapper){
			return (((BluetoothWrapper) device).getDevice().getAddress().equals(this.device.getAddress()));
		} else {
			return false;
		}
	}
	
//	@Override
//	public hash
	
	
}
