package com.autopilot.ludvig.roboblue.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.autopilot.ludvig.roboblue.fragment.BluetoothFragment
import com.autopilot.ludvig.roboblue.fragment.ControllerFragment
import com.autopilot.ludvig.roboblue.fragment.TerminalFragment

class BTPagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {

    companion object {
        private const val NUM_PAGES = 3
    }

    override fun getCount(): Int = NUM_PAGES

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> BluetoothFragment()
            1 -> TerminalFragment()
            2 -> ControllerFragment()
            else -> throw IllegalArgumentException("No such page!")
        }
    }

}