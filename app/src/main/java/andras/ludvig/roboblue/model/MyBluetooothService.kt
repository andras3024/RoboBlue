package andras.ludvig.roboblue.model

import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.Handler
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream



private const val TAG = "BT2"

// Defines several constants used when transmitting messages between the
// service and the UI.
const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2
// ... (Add other message types here as needed.)


class MyBluetoothService(
    // handler that gets info from Bluetooth service
    private val mHandler: Handler
) {

    inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

        private var mBuffread: Int = 0
        private var ciklus:Int = 0
        private var smBuffer2: String = "" // mmBuffer store for the stream

        override fun run() {

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                // Read from the InputStream.
                val sb = StringBuilder()
                do {
                    //readByte = dInputStream.readUnsignedByte();
                    mBuffread = mmInStream.read()
                    //Log.i(TAG,mBuffread.toString())
                    sb.append(mBuffread.toChar())
                    ciklus++
                } while (mBuffread != 13)
                ciklus = 0
                smBuffer2 = sb.toString()
                Log.i(TAG,smBuffer2)


               // Send the obtained bytes to the UI activity.
                val readMsg = mHandler.obtainMessage(MESSAGE_READ, smBuffer2)
                readMsg.sendToTarget()
            }


        }

        // Call this from the main activity to send data to the remote device.
        fun write(bytes: ByteArray) {
            try {
                Log.i(TAG,"MSG sent")
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)

                // Send a failure message back to the activity.
                val writeErrorMsg = mHandler.obtainMessage(MESSAGE_TOAST)
                val bundle = Bundle().apply {
                    putString("toast", "Couldn't send data to the other device")
                }
                writeErrorMsg.data = bundle
                mHandler.sendMessage(writeErrorMsg)
                return
            }

            // Share the sent message with the UI activity.
            val writtenMsg = mHandler.obtainMessage(
                MESSAGE_WRITE, -1, -1, mmBuffer)
            writtenMsg.sendToTarget()
        }

        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }
}