package com.example.flo

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {
    // 안드로이드에서 액티비티의 기능들을 사용할 수 있도록 만든 클래스
    lateinit var binding : ActivitySongBinding
    lateinit var song: Song
    lateinit var timer: Timer

    private var mediaPlayer: MediaPlayer? = null
    private var gson: Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // xml에 표기된 레이아웃들 메모리에 객체화
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSong()
        setPlayer(song)

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

    // 사용자가 포커스를 잃었을 때 음악이 중지
    override fun onPause() {
        super.onPause()
        setPlayerStatus(false)
        song.second = ((binding.songPlayerStatusSb.progress * song.playTime) ) / 1000
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() // 에디터
        val songJson = gson.toJson(song)
        editor.putString("songData", songJson)
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release() // 미디어 플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null
    }

    private fun initSong(){
        if(intent.hasExtra("title") && intent.hasExtra("singer")){
            song = Song(
                intent.getStringExtra("title")!!,
                intent.getStringExtra("singer")!!,
                intent.getIntExtra("second", 0),
                intent.getIntExtra("playTime", 0),
                intent.getBooleanExtra("isPlaying",false),
                intent.getStringExtra("music")!!
            )
        }
        startTimer()
    }

    private fun setPlayer(song: Song){
        binding.songPlayerTitleTv.text = song.title
        binding.songPlayerSingerTv.text = song.singer
        binding.songPlayerNowTimeTv.text = String.format("%02d:%02d", song.second/60, song.second%60)
        binding.songPlayerEndTimeTv.text = String.format("%02d:%02d", song.playTime/60, song.playTime%60)
        binding.songPlayerStatusSb.progress = (song.second / song.playTime)

        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)

        setPlayerStatus(song.isPlaying)
    }
    private fun setPlayerStatus(isPlaying: Boolean){
        song.isPlaying = isPlaying
        timer.isPlaying = isPlaying
        if(isPlaying){
            binding.songPlayerPlayIv.visibility = View.GONE
            binding.songPlayerPauseIv.visibility = View.VISIBLE
            mediaPlayer?.start()
        } else {
            binding.songPlayerPauseIv.visibility = View.GONE
            binding.songPlayerPlayIv.visibility = View.VISIBLE
            if(mediaPlayer?.isPlaying == true)
                mediaPlayer?.pause()
        }
    }

    private fun startTimer(){
        timer = Timer(song.playTime, song.isPlaying)
        timer.start()
    }
    

    inner class Timer(private val playTime: Int, var isPlaying: Boolean) : Thread(){
        private var second: Int = 0
        private var mills: Float = 0f

        override fun run() {
            super.run()
            while(true){
                try {
                    if (second >= playTime) {
                        break
                    }
                    if (isPlaying) {
                        sleep(50)
                        mills += 50
                        runOnUiThread {
                            binding.songPlayerStatusSb.progress = ((mills / playTime)).toInt()
                        }
                        if (mills % 1000 == 0f) {
                            runOnUiThread {
                                binding.songPlayerNowTimeTv.text =
                                    String.format("%02d:%02d", second / 60, second % 60)
                            }
                            second++
                        }
                    }
                }catch (e : InterruptedException){
                    Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
                }
            }
        }
    }
}