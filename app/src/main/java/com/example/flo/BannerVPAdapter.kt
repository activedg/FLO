package com.example.flo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BannerVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment){
    // fragment에 이미지 파일 넣어 viewpager 전환
    private val fragmentList : ArrayList<Fragment> = ArrayList()

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position]

    fun addFragment(fragment: Fragment){
        fragmentList.add(fragment)
        // ViewPager 에게 새로운 값 추가되었다고 알려줌
        notifyItemChanged(fragmentList.size - 1)
    }
}