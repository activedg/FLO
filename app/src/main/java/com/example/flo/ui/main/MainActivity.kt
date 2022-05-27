package com.example.flo.ui.main

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.flo.*
import com.example.flo.data.entities.Album
import com.example.flo.data.entities.Song
import com.example.flo.data.local.SongDatabase
import com.example.flo.databinding.ActivityMainBinding
import com.example.flo.ui.main.home.HomeFragment
import com.example.flo.ui.main.locker.LockerFragment
import com.example.flo.ui.main.look.LookFragment
import com.example.flo.ui.main.search.SearchFragment
import com.example.flo.ui.song.SongActivity
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

        inputDummyAlbums()
        initOnClickListener()

        Log.d("MAIN/JWT_TO_SERVER", getJwt().toString())
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

    private fun inputDummyAlbums(){
        val songDB = SongDatabase.getInstance(this)!!
        val albums = songDB.albumDao().getAlbums()

        if (albums.isNotEmpty()) return

        songDB.albumDao().insert(
            Album(
                0,
                "LILAC", "아이유 (IU)", R.drawable.img_album_exp2
            )
        )

        songDB.albumDao().insert(
            Album(
                1,
                "Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp
            )
        )

        songDB.albumDao().insert(
            Album(
                2,
                "Next Level Remixes", "에스파 (AESPA)", R.drawable.img_album_exp3
            )
        )

        songDB.albumDao().insert(
            Album(
                3,
                "PERSONA", "방탄소년단 (BTS)", R.drawable.img_album_exp4
            )
        )

        songDB.albumDao().insert(
            Album(
                4,
                "GREAT!", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5
            )
        )
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

    private fun getJwt(): String? {
        val spf = getSharedPreferences("auth2", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getString("jwt", "")
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