package com.example.flo.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentPannelBinding

class PannelFragment(val pannel: Pannel) : Fragment(){
    lateinit var binding: FragmentPannelBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPannelBinding.inflate(inflater, container, false)
        initPannel()
        return binding.root
    }

    private fun initPannel() {
        binding.homePannelBackgroundIv.setImageResource(pannel.bgImgRes)
        binding.homePannelTitleTv.text = pannel.title
        binding.homePannelAlbumImgIv.setImageResource(pannel.album1ImgRes)
        binding.homePannelAlbumTitleTv.text = pannel.album1Title
        binding.homePannelAlbumSingerTv.text = pannel.album1Singer
        binding.homePannelAlbum2ImgIv.setImageResource(pannel.album2ImgRes)
        binding.homePannelAlbum2TitleTv.text = pannel.album2Title
        binding.homePannelAlbum2SingerTv.text = pannel.album2Singer
    }
}