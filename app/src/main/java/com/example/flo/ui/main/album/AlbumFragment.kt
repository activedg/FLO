package com.example.flo.ui.main.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flo.R
import com.example.flo.ui.main.MainActivity
import com.example.flo.data.entities.Album
import com.example.flo.data.entities.Like
import com.example.flo.data.local.SongDatabase
import com.example.flo.databinding.FragmentAlbumBinding
import com.example.flo.ui.main.home.HomeFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

class AlbumFragment : Fragment() {
    private val information = arrayListOf("수록곡", "상세정보", "영상")
    lateinit var binding : FragmentAlbumBinding
    private var gson: Gson = Gson()

    private var isLiked: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)


        val albumJson = arguments?.getString("album")

        val album = gson.fromJson(albumJson, Album::class.java)
        isLiked = isLikedAlbum(album.id)
        setInit(album)


        setOnClickListeners(album)

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

        if(isLiked){
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else{
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }

    }

    private fun getJwt(): Int{
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getInt("jwt", 0)
    }

    private fun likeAlbum(userId: Int, albumId: Int){
        val songDB = SongDatabase.getInstance(requireContext())!!
        val like = Like(userId, albumId)

        songDB.albumDao().likeAlbum(like)
    }

    private fun isLikedAlbum(albumId: Int): Boolean{
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getJwt()

        val likeId: Int? = songDB.albumDao().isLikedAlbum(userId, albumId)

        return likeId != null
    }

    private fun disLikedAlbum(albumId: Int){
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getJwt()

        songDB.albumDao().dislikeAlbum(userId, albumId)
    }

    private fun setOnClickListeners(album: Album){
        val userId = getJwt()
        binding.albumLikeIv.setOnClickListener {
            if (isLiked){
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
                disLikedAlbum(album.id)
            } else{
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
                likeAlbum(userId, album.id)
            }
            isLiked = !isLiked
        }
    }

}