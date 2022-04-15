package com.example.flo

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.flo.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var mainTimer: MainTimer
    private var mediaPlayer: MediaPlayer? = null
    private var song: Song = Song()
    private var gson: Gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setJson()
        startMainTimer()

        binding.mainPlayerCl.setOnClickListener {
            // startActivity(Intent(this, SongActivity::class.java))
            song.second = ((binding.mainMiniplayerSb.progress * song.playTime) ) / 1000
            song.pos = mediaPlayer?.currentPosition!!
            val intent = Intent(this, SongActivity::class.java)
            intent.putExtra("title", song.title)
            intent.putExtra("singer", song.singer)
            intent.putExtra("second", song.second)
            intent.putExtra("playTime", song.playTime)
            intent.putExtra("isPlaying", song.isPlaying)
            intent.putExtra("music", song.music)
            intent.putExtra("currentPos", song.pos)
            startActivity(intent)
        }

        initBottomNavigation()

        binding.mainMiniplayerBtn.setOnClickListener {
            setMiniPlayerStatus(true)
        }
        binding.mainPauseBtn.setOnClickListener {
            setMiniPlayerStatus(false)
        }

        Log.d("Song", song.title + song.singer)
    }

    private fun setMiniPlayer(song: Song){
        binding.mainMiniplayerTitle.text = song.title
        binding.mainMiniplayerSinger.text = song.singer
        binding.mainMiniplayerSb.progress = (song.second * 1000) / song.playTime
        setMiniPlayerStatus(song.isPlaying)
    }

    private fun setMiniPlayerStatus(isPlaying: Boolean){
        song.isPlaying = isPlaying
        mainTimer.isPlaying = isPlaying
        if(isPlaying) {
            binding.mainPauseBtn.visibility = View.VISIBLE
            binding.mainMiniplayerBtn.visibility = View.GONE
            mediaPlayer?.start()
            Log.d("mainTimer", mainTimer.isPlaying.toString())
        }
        else {
            binding.mainMiniplayerBtn.visibility = View.VISIBLE
            binding.mainPauseBtn.visibility = View.GONE
            if (mediaPlayer?.isPlaying == true)
                mediaPlayer?.pause()
        }
    }

    private fun initBottomNavigation(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) {

                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun setJson(){
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val songJson = sharedPreferences.getString("songData", null)
        song = if (songJson == null) {
            Song("Next Level", "Aespa", 0, 221, false, "next_level")
        } else{
            gson.fromJson(songJson, song::class.java)
        }
    }

    override fun onStart() {
        super.onStart()
        setJson()
        setMiniPlayer(song)

        startMainTimer()
        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
        mediaPlayer?.seekTo(song.pos)
    }

    override fun onPause() {
        super.onPause()
        mainTimer.isPlaying = false
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        mainTimer.interrupt()
    }

    private fun startMainTimer(){
        mainTimer = MainTimer(song.playTime, song.isPlaying)
        mainTimer.start()
    }

    inner class MainTimer(private val playTime: Int, var isPlaying: Boolean) : Thread(){
        private var second: Int = song.second
        private var mills: Float = (second * 1000).toFloat()

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
                            binding.mainMiniplayerSb.progress = ((mills / playTime)).toInt()
                        }
                        if (mills % 1000 == 0f)
                            second++
                    }
                }catch (e : InterruptedException){
                    Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
                }
            }
        }
    }
}