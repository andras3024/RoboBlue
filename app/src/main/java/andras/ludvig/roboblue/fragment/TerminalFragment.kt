package andras.ludvig.roboblue.fragment

import andras.ludvig.roboblue.R
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView



class TerminalFragment : Fragment() {

    private lateinit var t: TextView
    private lateinit var v: View
    private lateinit var enteredText:  TextInputLayout
    private var sendfun: OnSendButtonClickListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_terminal, container, false)
        t = v.findViewById<TextView>(R.id.textViewterminal)
        enteredText =  v.findViewById<TextInputLayout>(R.id.textInputLayout)
        val button = v.findViewById<Button>(R.id.buttonSend)
        t.movementMethod = ScrollingMovementMethod()
        button.setOnClickListener{
            Log.i("BT2","Clicked send")
            sendfun?.OnSendButtonClickListener(enteredText.editText?.text.toString())

        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TerminalFragment.OnSendButtonClickListener) {
            sendfun = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnSendButtonClickListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        sendfun = null
    }

    fun changeText(message: String)
    {
        t.append(message)
    }

    interface OnSendButtonClickListener {
        fun OnSendButtonClickListener(message: String)
    }


    companion object {
        fun newInstance(): TerminalFragment = TerminalFragment()
    }
}