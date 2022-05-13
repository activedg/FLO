package com.example.flo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import com.google.gson.Gson

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var timer: Timer

    private lateinit var songDB: SongDatabase
    private var albumDatas =  ArrayList<Album>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Pannel ViewPager
        initHomePannel()
        // 자동 슬라이드 타이머
        initTimer()

        // 데이터 리스트 생성 더미 데이터
        songDB = SongDatabase.getInstance(requireContext())!!
        albumDatas.addAll(songDB.albumDao().getAlbums())

        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        binding.homeAlbumRv.adapter = albumRVAdapter
        binding.homeAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)

        albumRVAdapter.setMyItemClickListener(object: AlbumRVAdapter.MyItemClickListener{
            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)
            }

            override fun onRemoveAlbum(position: Int) {
                albumRVAdapter.removeItem(position)
            }
        })

        val bannerAdapter = BannerVPAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        return binding.root

    }

    private fun changeAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle()?.apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .commitAllowingStateLoss()
    }

    override fun onPause() {
        super.onPause()
        timer.interrupt()
    }

    private fun initHomePannel(){
        val pannel1 = Pannel(R.drawable.img_pannel1, getString(R.string.pannel1), R.drawable.img_album_exp, "Butter", "BTS"
                        , R.drawable.img_album_exp2, "LILAC", "아이유(IU)")
        val pannel2 = Pannel(R.drawable.img_pannel2, getString(R.string.pannel2), R.drawable.img_album_exp3, "Next Level", "Aespa"
                        , R.drawable.img_album_exp4, "작은 것들을 위한 시", "BTS")
        val pannel3 = Pannel(R.drawable.img_pannel3, getString(R.string.pannel3), R.drawable.img_album_exp5, "BAAM", "모모랜드"
            , R.drawable.img_album_exp6, "Weekend", "태연")
        val pannel4 = Pannel(R.drawable.img_pannel4, getString(R.string.pannel4), R.drawable.img_album_exp, "Butter", "BTS"
            , R.drawable.img_album_exp2, "LILAC", "아이유(IU)")
        val pannel5 = Pannel(R.drawable.img_pannel5, getString(R.string.pannel5), R.drawable.img_album_exp5, "BAAM", "모모랜드"
            , R.drawable.img_album_exp6, "Weekend", "태연")

        val pannelAdapter = PannelVPAdapter(this)
        pannelAdapter.addFragment(PannelFragment(pannel1))
        pannelAdapter.addFragment(PannelFragment(pannel2))
        pannelAdapter.addFragment(PannelFragment(pannel3))
        pannelAdapter.addFragment(PannelFragment(pannel4))
        pannelAdapter.addFragment(PannelFragment(pannel5))

        binding.homePannelVp.adapter = pannelAdapter
        binding.homePannelIndicator.setViewPager(binding.homePannelVp)
    }

    private fun initTimer(){
        timer = Timer()
        timer.start()
    }

    inner class Timer() : Thread(){
        private var second: Int = 0
        override fun run() {
            super.run()
            while (true){
                try {
                    sleep(2000)
                    second++
                    second %= 5
                    activity?.runOnUiThread {
                        binding.homePannelVp.setCurrentItem(second, true)
                    }
                } catch (e: InterruptedException){
                    Log.d("Slide", "Slide Finish")
                }

            }
        }
    }
}