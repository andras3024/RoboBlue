package andras.ludvig.roboblue.fragment

import andras.ludvig.roboblue.FunctionButtonApplication
import andras.ludvig.roboblue.R
import andras.ludvig.roboblue.database.FunctionButtonsDbLoader
import andras.ludvig.roboblue.model.Functionbuttons
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView


class ControllerFragment : Fragment() {

    private lateinit var v: View
    private var sendfun: ControllerFragment.OnButtonClickListener? = null
    private lateinit var dbLoader: FunctionButtonsDbLoader
    private lateinit var funcButton: Functionbuttons

    data class FunctionReturn(val name: String, val value: String)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_controller, container, false)

        dbLoader = FunctionButtonApplication.functionButtonsLoader

        /**funcButton = Functionbuttons(id=1,title = "FB1",value = "Function1")
        dbLoader.createFunctionButton(funcButton)
        dbLoader.updateFunctionButton(funcButton)
        funcButton = Functionbuttons(id=2,title = "FB2",value = "Function2")
        dbLoader.createFunctionButton(funcButton)
        dbLoader.updateFunctionButton(funcButton)
        funcButton = Functionbuttons(id=3,title = "FB3",value = "Function3")
        dbLoader.createFunctionButton(funcButton)
        dbLoader.updateFunctionButton(funcButton)
        funcButton = Functionbuttons(id=4,title = "FB4",value = "Function4")
        dbLoader.createFunctionButton(funcButton)
        dbLoader.updateFunctionButton(funcButton)*/

        val buttonStart = v.findViewById<Button>(R.id.buttonStart)
        val buttonStop = v.findViewById<Button>(R.id.buttonStop)
        val buttonFunc1 = v.findViewById<Button>(R.id.buttonFunc1)
        val buttonFunc2 = v.findViewById<Button>(R.id.buttonFunc2)
        val buttonFunc3 = v.findViewById<Button>(R.id.buttonFunc3)
        val buttonFunc4 = v.findViewById<Button>(R.id.buttonFunc4)
        val buttonForward = v.findViewById<Button>(R.id.buttonForward)
        val buttonBrake = v.findViewById<Button>(R.id.buttonBrake)
        val buttonLeft = v.findViewById<Button>(R.id.buttonLeft)
        val buttonRight = v.findViewById<Button>(R.id.buttonRight)

        buttonFunc1.text = dbLoader.fetchFunctionButton(1)?.title ?: "Function1"
        buttonFunc2.text = dbLoader.fetchFunctionButton(2)?.title ?: "Function2"
        buttonFunc3.text = dbLoader.fetchFunctionButton(3)?.title ?: "Function3"
        buttonFunc4.text = dbLoader.fetchFunctionButton(4)?.title ?: "Function4"

        buttonStart.setOnClickListener{
            sendfun?.OnButtonClickListener("Start")
        }
        buttonStop.setOnClickListener{
            sendfun?.OnButtonClickListener("Stop")
        }
        buttonFunc1.setOnClickListener{
            sendfun?.OnButtonClickListener(dbLoader.fetchFunctionButton(1)?.value ?: "Error")
        }
        buttonFunc2.setOnClickListener{
            sendfun?.OnButtonClickListener(dbLoader.fetchFunctionButton(2)?.value ?: "Error")
        }
        buttonFunc3.setOnClickListener{
            sendfun?.OnButtonClickListener(dbLoader.fetchFunctionButton(3)?.value ?: "Error")
        }
        buttonFunc4.setOnClickListener{
            sendfun?.OnButtonClickListener(dbLoader.fetchFunctionButton(4)?.value ?: "Error")
        }
        buttonForward.setOnClickListener{
            sendfun?.OnButtonClickListener("Forward")
        }
        buttonBrake.setOnClickListener{
            sendfun?.OnButtonClickListener("Brake")
        }
        buttonLeft.setOnClickListener{
            sendfun?.OnButtonClickListener("Left")
        }
        buttonRight.setOnClickListener{
            sendfun?.OnButtonClickListener("Right")
        }

        buttonFunc1.setOnLongClickListener{
            PopupFunction(buttonFunc1,1)
        }
        buttonFunc2.setOnLongClickListener{
            PopupFunction(buttonFunc2,2)
        }
        buttonFunc3.setOnLongClickListener{
            PopupFunction(buttonFunc3,3)
        }
        buttonFunc4.setOnLongClickListener{
            PopupFunction(buttonFunc4,4)
        }

        return v
    }

    private fun PopupFunction(curerntButton: Button, id: Long): Boolean {
        val popupView = layoutInflater.inflate(R.layout.popup_function, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val setText = "Set Function " + id.toString()
        popupView?.findViewById<TextView>(R.id.textViewfunctioDesc)?.text = setText
        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.rgb(135,206,250)))
        popupWindow.showAtLocation(
            view, Gravity.TOP ,
            0, (view?.height ?: 0)/4
        )
        popupView?.findViewById<Button>(R.id.buttonSetFunc)?.setOnClickListener{
            Log.i("BT2","Set Function")
            val funcname = popupView?.findViewById<TextInputLayout>(R.id.textInputLayoutFuncName)?.editText?.text.toString()
            val funcvalue = popupView?.findViewById<TextInputLayout>(R.id.textInputFuncValue)?.editText?.text.toString()
            curerntButton.text = funcname
            funcButton = Functionbuttons(id=id,title = funcname,value = funcvalue)
            dbLoader.createFunctionButton(funcButton)
            dbLoader.updateFunctionButton(funcButton)
            popupWindow.dismiss()
        }
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ControllerFragment.OnButtonClickListener) {
            sendfun = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnSendButtonClickListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        sendfun = null
    }


    interface OnButtonClickListener {
        fun OnButtonClickListener(message: String)
    }


    companion object {
        fun newInstance(): ControllerFragment = ControllerFragment()
    }
}