package com.example.flo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

class AlbumFragment : Fragment() {
    private val information = arrayListOf("수록곡", "상세정보", "영상")
    lateinit var binding : FragmentAlbumBinding
    private var gson: Gson = Gson()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        val albumJson = arguments?.getString("album")

        val album = gson.fromJson(albumJson, Album::class.java)
        setInit(album)

        binding.albumBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HomeFragment())
                .commitAllowingStateLoss()
        }

        val albumAdapter = AlbumVPAdapter(this)
        binding.albumContentVp.adapter = albumAdapter

        //TabLayout 메뉴 설정과 TabLayout과 ViewPager2 연결
       TabLayoutMediator(binding.albumContentTb, binding.albumContentVp) {
            tab, position -> tab.text = information[position]
        }.attach()
        return binding.root
    }
    private fun setInit(album: Album){
        binding.albumImgIv.setImageResource(album.coverImage!!)
        binding.albumTitleTv.text = album.title
        binding.albumSingerTv.text = album.singer
    }
}