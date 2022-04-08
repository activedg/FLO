package com.example.flo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PannelVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val pannelList: ArrayList<Fragment> = ArrayList()

    override fun getItemCount(): Int = pannelList.size

    override fun createFragment(position: Int): Fragment = pannelList[position]

    fun addFragment(fragment: Fragment){
        pannelList.add(fragment)
        notifyItemChanged(pannelList.size - 1)
    }
}