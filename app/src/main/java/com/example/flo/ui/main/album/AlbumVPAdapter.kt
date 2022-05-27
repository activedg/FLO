package com.example.flo.ui.main.album

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.flo.ui.song.*

class AlbumVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    // 반복된 화면이 아닌 각각의 화면(수록곡, 상세정보, 영상)
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> SongFragment()
            1 -> DetailFragment()
            else -> VideoFragment()
        }
    }
}