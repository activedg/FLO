package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity() {
    // 안드로이드에서 액티비티의 기능들을 사용할 수 있도록 만든 클래스
    lateinit var binding : ActivitySongBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // xml에 표기된 레이아웃들 메모리에 객체화
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.songPlayerDownIv.setOnClickListener {
            finish()
        }
        binding.songPlayerPlayIv.setOnClickListener {
            setPlayerStatus(true)
        }
        binding.songPlayerPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }

        if(intent.hasExtra("title") && intent.hasExtra("singer")){
            binding.songPlayerTitleTv.text = intent.getStringExtra("title")
            binding.songPlayerSingerTv.text = intent.getStringExtra("singer")
        }
    }
    private fun setPlayerStatus(isPlaying: Boolean){
        if(isPlaying){
            binding.songPlayerPlayIv.visibility = View.GONE
            binding.songPlayerPauseIv.visibility = View.VISIBLE
        } else {
            binding.songPlayerPauseIv.visibility = View.GONE
            binding.songPlayerPlayIv.visibility = View.VISIBLE
        }
    }
}