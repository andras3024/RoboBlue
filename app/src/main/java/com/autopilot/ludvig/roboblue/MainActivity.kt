package com.autopilot.ludvig.roboblue

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import com.autopilot.ludvig.roboblue.R.id.vpBTPanels
import com.autopilot.ludvig.roboblue.adapter.BTDevicesAdapter
import com.autopilot.ludvig.roboblue.adapter.BTPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {

    private val mBluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val enableBTcode = 1
    private val mDeviceList = ArrayList<String>()
    private var filterbt = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)


    private val btReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action
            when(action){
                BluetoothAdapter.ACTION_STATE_CHANGED ->{
                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    if(state == BluetoothAdapter.STATE_OFF) {
                        activateBT()
                    }
                }
            }
        }
    }

    /**
    // Create a BroadcastReceiver for ACTION_FOUND.
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    //mDeviceList.add(deviceName + "\n" + deviceHardwareAddress)
                    Log.i("BT2", deviceName  + "\n" + deviceHardwareAddress)
                }
            }
        }
    }
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vpBTPanels.adapter = BTPagerAdapter(supportFragmentManager)
        activateBT()
    }

    override fun onResume() {
        super.onResume()
        activateBT()
        registerReceiver(btReceiver,filterbt)

        val pairedDevices: Set<BluetoothDevice>? = mBluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address
            Log.i("BT2", deviceName  + "\n" + deviceHardwareAddress)
            mDeviceList.add(deviceName + "\n" + deviceHardwareAddress)
        }


    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(btReceiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == enableBTcode )
        {
            if(resultCode == Activity.RESULT_OK)
            {
                // Bluetooth module found
                Toast.makeText(this, R.string.bt_turned_on, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun activateBT(){
        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(this, R.string.no_bt_module, Toast.LENGTH_LONG).show()
        }
        else
        {
            if (!mBluetoothAdapter?.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, enableBTcode)
            }
        }

    }
}
