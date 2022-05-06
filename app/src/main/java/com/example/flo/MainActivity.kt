package com.example.flo

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.flo.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var mainTimer: MainTimer
    private var mediaPlayer: MediaPlayer? = null
    private var songs = ArrayList<Song>()
    private var nowPos: Int = 0
    private var gson: Gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initOnClickListener()
    }
    private fun initOnClickListener(){
        binding.mainPlayerCl.setOnClickListener {
            // startActivity(Intent(this, SongActivity::class.java))
            songs[nowPos].second = ((binding.mainMiniplayerSb.progress * songs[nowPos].playTime) ) / 1000
            songs[nowPos].pos = mediaPlayer?.currentPosition!!

            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", songs[nowPos].id)
            editor.apply()

            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)
        }

        initBottomNavigation()

        binding.mainMiniplayerBtn.setOnClickListener {
            setMiniPlayerStatus(true)
        }
        binding.mainPauseBtn.setOnClickListener {
            setMiniPlayerStatus(false)
        }

        binding.mainPreviouosBtn.setOnClickListener {
            moveSong(-1)
        }
        binding.mainNextBtn.setOnClickListener {
            moveSong(1)
        }
    }

    private fun setMiniPlayer(song: Song){
        binding.mainMiniplayerTitle.text = song.title
        binding.mainMiniplayerSinger.text = song.singer
        binding.mainMiniplayerSb.progress = (song.second * 1000) / song.playTime


        val music = resources.getIdentifier(songs[nowPos].music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
        mediaPlayer?.seekTo(songs[nowPos].pos)
        startMainTimer()

        setMiniPlayerStatus(song.isPlaying)
    }

    private fun setMiniPlayerStatus(isPlaying: Boolean){
        songs[nowPos].isPlaying = isPlaying
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


    private fun inputDummySongs(){
        val songDB = SongDatabase.getInstance(this)!!
        val songs = songDB.songDao().getSongs()

        if (songs.isNotEmpty()) return;

        songDB.songDao().insert(
           Song(
               "Next Level",
               "Aespa",
               0,
               221,
               false,
               "next_level",
               R.drawable.img_album_exp3,
               false)
        )

        songDB.songDao().insert(
            Song(
                "Lilac",
                "IU(아이유)",
                0,
                217,
                false,
                "lilac",
                R.drawable.img_album_exp2,
                false)
        )
        songDB.songDao().insert(
            Song(
                "Butter",
                "BTS",
                0,
                222,
                false,
                "butter",
                R.drawable.img_album_exp,
                false)
        )
        val _songs = songDB.songDao().getSongs()
        Log.d("DB Data", _songs.toString())
    }



    private fun getPlayingSong(songId: Int): Int{
        for (i in 0..songs.size){
            if (songs[i].id == songId)
                return i
        }
        return 0
    }

    private fun moveSong(direct: Int){
        if (nowPos + direct < 0){
            Toast.makeText(this, "First Song", Toast.LENGTH_SHORT).show()
            return
        }
        if (nowPos + direct >= songs.size){
            Toast.makeText(this, "Last Song", Toast.LENGTH_SHORT).show()
            return
        }
        nowPos += direct

        mainTimer.interrupt()
        startMainTimer()

        mediaPlayer?.release()
        mediaPlayer = null

        setMiniPlayer(songs[nowPos])
    }

    override fun onStart() {
        super.onStart()
        inputDummySongs()

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)
        val songDB = SongDatabase.getInstance(this)!!

        songs.addAll(songDB.songDao().getSongs())

        nowPos = if (songId == 0) 1 else{
            getPlayingSong(songId)
        }
        Log.d("songId", songId.toString())
        setMiniPlayer(songs[nowPos])



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
        mainTimer = MainTimer(songs[nowPos].playTime, songs[nowPos].isPlaying)
        mainTimer.start()
    }

    inner class MainTimer(private val playTime: Int, var isPlaying: Boolean) : Thread(){
        private var second: Int = songs[nowPos].second
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
                    isPlaying = false
                    Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
                }
            }
        }
    }
}