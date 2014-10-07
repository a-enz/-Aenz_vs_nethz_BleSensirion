package aenz.vs_nethz_blesensirion;

//import aenz.vs_neth_sensors.R;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class LeActivity extends Activity{
	
	private BluetoothDevice device;
	private static final UUID SERVICEUUID = UUID.fromString("0000AA20-0000-1000-8000-00805f9b34fb");
	private static final UUID CHARUUID = UUID.fromString("0000AA21-0000-1000-8000-00805f9b34fb");
	private BluetoothGatt bGatt;
	private BluetoothGattCallback bGattCallback;
	private BluetoothGattCharacteristic rht;
	private List<BluetoothGattService> services;
	private TextView textView1;
	private TextView hTxt;
	private TextView tTxt;
	Context thisContext;

	
	private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    
    private static final String TEMP = "Temperature: ";
    private static final String HUM = "Humidity: ";

	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		thisContext = this;
		setContentView(R.layout.activity_le);
		textView1 = (TextView) findViewById(R.id.textView3);
		hTxt = (TextView) findViewById(R.id.hTxtView);
		tTxt = (TextView) findViewById(R.id.tTxtView);
		device = this.getIntent().getExtras().getParcelable("LeDevice");
		textView1.setText("Name: " + device.getName());
		
		bGattCallback = bCallbackInit();
		bGatt = device.connectGatt(this, false, bGattCallback);
	}
	
	private BluetoothGattCallback bCallbackInit() {
		return new BluetoothGattCallback() {
			
			@Override
			public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState){
				if(newState == BluetoothProfile.STATE_CONNECTING) {
			        displayToast("Connecting");
				} else if(newState == BluetoothProfile.STATE_CONNECTED) {
					displayToast("Connected");
					gatt.discoverServices();
				} else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
					displayToast("Disconnected");
				}
			}
			
			@Override
			public void onServicesDiscovered(BluetoothGatt gatt, int status){
			    rht = new BluetoothGattCharacteristic(
		    		CHARUUID,
		            BluetoothGattCharacteristic.PROPERTY_READ
		            | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
		            BluetoothGattCharacteristic.PERMISSION_READ
		            );
		       gatt.getService(SERVICEUUID).addCharacteristic(rht);
		       services = gatt.getService(SERVICEUUID).getIncludedServices();
		       gatt.readCharacteristic(rht);
			}
			
			@Override
			public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
				//displayData("blabla");
				//displayData(Integer.valueOf(characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0)).toString());
				//gatt.readCharacteristic(rht);
				displayData(characteristic);
				gatt.readCharacteristic(rht);
			}
		};
	}
	
	public void displayText(final String data){
		runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView1.setText(data);
            }
		});
	}
	
	public void displayData(BluetoothGattCharacteristic c) {
		final String temp = TEMP + ((double) c.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 0)/100);
		final String hum = HUM + ((double) c.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 2)/100);
		runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hTxt.setText(temp);
                tTxt.setText(hum);
            }
		});
	}
	
	public void displayToast(final String msg){
		runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	Toast.makeText(thisContext, msg, Toast.LENGTH_LONG).show();
            }
        });
	}
	
    @Override
    public void onStop(){
    	super.onStop();
        if (bGatt == null) {
            return;
        }
        bGatt.close();
        bGatt = null;
    }
    
    public String dumpServices(){
    	String res = "";
    	if(services != null) {
    		for(BluetoothGattService s : services) {
    			res = res + "\n" + s.getUuid().toString();
    		}
    	}
    	return res;
    }
}
