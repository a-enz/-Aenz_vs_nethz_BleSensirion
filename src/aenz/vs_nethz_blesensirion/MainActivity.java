package aenz.vs_nethz_blesensirion;

import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnItemClickListener{

	private BluetoothManager bManager;
	private BluetoothAdapter bAdapter;
	private final UUID SERVICEUUID = UUID.fromString("0000AA20-0000-1000-8000-00805f9b34fb");
	private final UUID CHARUUID = UUID.fromString("0000AA21-0000-1000-8000-00805f9b34fb");
	private Handler bHandler;
	private boolean bScanning;
	private LeScanCallback bScanCallback;
	private static final long SCAN_PERIOD = 5000;
	private ArrayAdapter<BluetoothWrapper> aAdapter;
	private ArrayList<BluetoothDevice> bDevices;
	
	private ListView listView;
	private TextView textView;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        bDevices = new ArrayList<BluetoothDevice>();
        aAdapter = new ArrayAdapter<BluetoothWrapper>(this, android.R.layout.simple_list_item_1, new ArrayList<BluetoothWrapper>());
        listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(aAdapter);
        textView = (TextView) findViewById(R.id.textView1);
        listView.setOnItemClickListener(this);
        
        bManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bAdapter = bManager.getAdapter();
        bHandler = new Handler();
        
        final int REQUEST_ENABLE_BT = 1;
        
        if(bAdapter == null || !bAdapter.isEnabled()) {
        	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        
    	bScanCallback = new LeScanCallback() {
    		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
    			runOnUiThread(new Runnable() {
    				public void run() {
    					if (aAdapter.getPosition(new BluetoothWrapper(device)) < 0)aAdapter.add(new BluetoothWrapper(device));
    					//bDevices.add(device);
    					textView.setText("Devices found");
    				}
    			});
    		}
    	};
    		
    	scanLeDevice(true); 
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    
    
    private void scanLeDevice(final boolean enable) {
    	if(enable) {
    		final Context thisContext = this;
    		bHandler.postDelayed(new Runnable() {
    			public void run() {
    				bScanning = false;
    				//bAdapter.stopLeScan(bScanCallback);
    				Toast.makeText(thisContext, R.string.scanning_finished, Toast.LENGTH_LONG).show();
    			}
    		}, SCAN_PERIOD);
    		
    		bScanning = true;
    		Toast.makeText(thisContext, R.string.scanning_started, Toast.LENGTH_LONG).show();
    		bAdapter.startLeScan(/*new UUID[]{SERVICEUUID}, */bScanCallback);
    	} else {
    		bScanning = false;
    		bAdapter.stopLeScan(bScanCallback);
    	}
    }


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, LeActivity.class);
		intent.putExtra("LeDevice", aAdapter.getItem(position).getDevice());
		this.startActivity(intent);
	}
}
 