package andras.ludvig.roboblue

import andras.ludvig.roboblue.fragment.ControllerFragment
import andras.ludvig.roboblue.fragment.DevicesFragment
import andras.ludvig.roboblue.fragment.TerminalFragment
import andras.ludvig.roboblue.model.BTdevcontent
import andras.ludvig.roboblue.model.MyBluetoothService
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*




const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2



class MainActivity : AppCompatActivity(), DevicesFragment.OnListFragmentInteractionListener
    ,TerminalFragment.OnSendButtonClickListener,ControllerFragment.OnButtonClickListener{

    private val mBluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val enableBTcode = 1
    private val mDeviceList = ArrayList<String>()
    private var filterbt = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
    //private val BTMODULEUUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66")
    private val BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val TAG = "BT2"
    private val text ="Hi"
    private lateinit var connThread: ConnectThread
    private lateinit var accThread: AcceptThread
    private lateinit var terminalFragment: TerminalFragment
    private lateinit var btServices: MyBluetoothService
    private lateinit var btServicesSend: MyBluetoothService.ConnectedThread
    private val charset = Charsets.UTF_8



    override fun onListFragmentInteraction(item: String?,mac: String?) {

        Log.i(TAG, "Item clicked: \n" + item.toString())

        val address: String? = item?.substring(item?.length - 17)
        val device = mBluetoothAdapter!!.getRemoteDevice(address)
        Toast.makeText(this, "Connecting to: "  + item.toString(), Toast.LENGTH_LONG).show()
        if(::btServices.isInitialized && btServicesSend.isAlive)
        {
            btServicesSend.cancel()
        }
        if (::connThread.isInitialized && connThread.isAlive) {
            connThread.cancel()
        }
        else{
            connThread = ConnectThread(device)
            connThread.start()
        }

        if (::accThread.isInitialized && accThread.isAlive) {
            accThread.cancel()
        }
        else
        {
            accThread = AcceptThread(this)
            accThread.start()
        }
    }

    override fun OnSendButtonClickListener(message: String){
        Log.i(TAG,"MSGSend: " + message)
        Log.i(TAG,"Sending MSG")
        val messagemod = message +"\n"
        val byteArray = messagemod.toByteArray(charset)
        if (::btServices.isInitialized) {
            btServicesSend.write(byteArray)
        }
    }

    override fun OnButtonClickListener(message: String){
        val msgSent = "Message Sent: " + message
        Log.i(TAG,msgSent)
        Log.i(TAG,"Sending MSG")
        val messagemod = message +"\n"
        val byteArray = messagemod.toByteArray(charset)
        if (::btServices.isInitialized) {
            btServicesSend.write(byteArray)
            Toast.makeText(this, msgSent, Toast.LENGTH_SHORT).show()
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray
                    val writeMessage = String(writeBuf)
                }
                MESSAGE_READ -> {
                    val readBuf = msg.obj as String
                    val currentDateTime = LocalDateTime.now()
                    val readMessage = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))+ " " + readBuf
                    if (::terminalFragment.isInitialized) {
                        terminalFragment.changeText(readMessage)
                    }


                }
                MESSAGE_TOAST -> Toast.makeText(
                    applicationContext, msg.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


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

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_devices -> {
                val devicesFragment = DevicesFragment.newInstance(1)
                openFragment(devicesFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_terminal -> {
                terminalFragment = TerminalFragment.newInstance()
                openFragment(terminalFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_controller -> {
                val controllerFragment = ControllerFragment.newInstance()
                openFragment(controllerFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_main)
        //activateBT()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val devicesFragment = DevicesFragment.newInstance(1)
        openFragment(devicesFragment)
    }

    override fun onResume() {
        super.onResume()
        activateBT()
        registerReceiver(btReceiver,filterbt)

        BTdevcontent.mDeviceList.clear()
        BTdevcontent.macList.clear()

        val pairedDevices: Set<BluetoothDevice>? = mBluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address
            Log.i("BT2", deviceName  + "\n" + deviceHardwareAddress)
            mDeviceList.add(deviceName + "\n" + deviceHardwareAddress)
            BTdevcontent.mDeviceList.add(deviceName + "\n" + deviceHardwareAddress)
            BTdevcontent.macList.add(device)
        }



    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(btReceiver)
        if (::connThread.isInitialized) {
            connThread.cancel()

        }
        if (::accThread.isInitialized){
            accThread.cancel()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == enableBTcode )
        {
            if(resultCode == Activity.RESULT_OK)
            {
                // Bluetooth module found
                Toast.makeText(this, R.string.bt_turned_on, Toast.LENGTH_LONG).show()
                val devicesFragment = DevicesFragment.newInstance(1)
                openFragment(devicesFragment)
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

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private inner class AcceptThread (context: Context): Thread() {

        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            mBluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord("RoboBlue22", BTMODULEUUID)
        }

        private val context = context

        override fun run() {
            // Keep listening until exception occurs or a socket is returned.

            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    Log.i(TAG, "Accepting")
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.i(TAG, "Socket's accept() method failed", e)
                    Toast.makeText(context, "Disconnected" , Toast.LENGTH_LONG).show()
                    shouldLoop = false
                    null
                }
                socket?.also {
                    manageMyConnectedSocket(it)
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                Log.i(TAG, "Disconnected accept")
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }

    private inner class ConnectThread(device: BluetoothDevice) : Thread() {

        private val socket: BluetoothSocket by lazy(LazyThreadSafetyMode.NONE) {
            device.createInsecureRfcommSocketToServiceRecord(BTMODULEUUID)
        }

        public override fun run() {
                var clazz = socket.remoteDevice.javaClass
                var paramTypes = arrayOf<Class<*>>(Integer.TYPE)
                var m = clazz.getMethod("createRfcommSocket", *paramTypes)
                var fallbackSocket = m.invoke(socket.remoteDevice, Integer.valueOf(1)) as BluetoothSocket
                try {
                Log.i(TAG,"Connecting")
                fallbackSocket.connect()
                manageMyConnectedSocket(fallbackSocket)
                } catch (e: Exception) {
                e.printStackTrace()
                Log.i(TAG,"MSG error")
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                Log.i(TAG, "Disconnected connect")
                socket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }


    private fun manageMyConnectedSocket(socket: BluetoothSocket) {
        Log.i(TAG,"Connected")
        btServices = MyBluetoothService(mHandler)
        btServicesSend = btServices.ConnectedThread(socket)
        btServicesSend.start()
        Log.i(TAG,"Services started")
    }


}
