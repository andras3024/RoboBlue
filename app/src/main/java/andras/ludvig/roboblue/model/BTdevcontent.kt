package andras.ludvig.roboblue.model

import android.bluetooth.BluetoothDevice
import java.util.*


object BTdevcontent {


    val mDeviceList: MutableList<String> = ArrayList()
    val macList: MutableList<BluetoothDevice> = ArrayList()


    private val COUNT = 25

    init {
        // Add some sample items.
        /**for (i in 1..COUNT) {
            addItem("HEY")
        }*/
    }

    private fun addItem(item: String) {
        mDeviceList.add(item)
    }

}
