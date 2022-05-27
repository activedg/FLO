package com.example.flo.ui.song

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.R
import com.example.flo.data.entities.Song
import com.example.flo.data.local.SongDatabase
import com.example.flo.databinding.ActivitySongBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {
    // 안드로이드에서 액티비티의 기능들을 사용할 수 있도록 만든 클래스
    lateinit var binding : ActivitySongBinding
    lateinit var timer: Timer

    private var mediaPlayer: MediaPlayer? = null
    private var gson: Gson = Gson()

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // xml에 표기된 레이아웃들 메모리에 객체화
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPlayList()
        initSong()

        initClickListener()


    }
    private fun initClickListener(){
        binding.songPlayerDownIv.setOnClickListener {
            finish()
        }
        binding.songPlayerPlayIv.setOnClickListener {
            setPlayerStatus(true)
        }
        binding.songPlayerPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }

        binding.songPlayerPreviousIv.setOnClickListener {
            moveSong(-1)
        }

        binding.songPlayerNextIv.setOnClickListener {
            moveSong(1)
        }

        binding.songPlayerLikeIv.setOnClickListener {
            setLike(songs[nowPos].isLike)
        }
    }
    // 사용자가 포커스를 잃었을 때 음악이 중지
    override fun onPause() {
        super.onPause()
        setPlayerStatus(false)
        songs[nowPos].second = ((binding.songPlayerStatusSb.progress * songs[nowPos].playTime) ) / 1000
        songs[nowPos].pos = mediaPlayer?.currentPosition!!
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() // 에디터
        editor.putInt("songId", songs[nowPos].id)
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release() // 미디어 플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null
    }

    private fun initPlayList(){
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun initSong(){
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)

        nowPos = getPlayingSong(songId)
        Log.d("now Song ID", songs[nowPos].id.toString())
        startTimer()
        setPlayer(songs[nowPos])
    }

    private fun getPlayingSong(songId: Int): Int{
        for (i in 0..songs.size){
            if (songs[i].id == songId)
                return i
        }
        return 0
    }

    private fun setPlayer(song: Song){
        binding.songPlayerTitleTv.text = song.title
        binding.songPlayerSingerTv.text = song.singer
        binding.songPlayerNowTimeTv.text = String.format("%02d:%02d", song.second/60, song.second%60)
        binding.songPlayerEndTimeTv.text = String.format("%02d:%02d", song.playTime/60, song.playTime%60)
        binding.songPlayerAlbumIv.setImageResource(song.coverImage!!)
        binding.songPlayerStatusSb.progress = (song.second / song.playTime)

        if (song.isLike){
            binding.songPlayerLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else{
            binding.songPlayerLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }

        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
        //mediaPlayer?.seekTo(song.pos)
        setPlayerStatus(song.isPlaying)
    }
    private fun setPlayerStatus(isPlaying: Boolean){
        songs[nowPos].isPlaying = isPlaying
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

    private fun setLike(isLike: Boolean){
        songs[nowPos].isLike = !isLike
        songDB.songDao().updateIsLikeById(!isLike, songs[nowPos].id)

        if (!isLike){
            binding.songPlayerLikeIv.setImageResource(R.drawable.ic_my_like_on)
            Snackbar.make(binding.songCoordiLayout, "좋아요 한 곡에 담겼습니다.", Snackbar.LENGTH_LONG).show()
        } else{
            binding.songPlayerLikeIv.setImageResource(R.drawable.ic_my_like_off)
            Snackbar.make(binding.songCoordiLayout, "좋아요 한 곡이 취소되었습니다.", Snackbar.LENGTH_LONG).show()
        }
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

        timer.interrupt()
        startTimer()

        mediaPlayer?.release()
        mediaPlayer = null

        setPlayer(songs[nowPos])
    }

    private fun startTimer(){
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
        timer.start()
    }
    

    inner class Timer(private val playTime: Int, var isPlaying: Boolean) : Thread(){
        private var second: Int = 0
        private var mills: Float = 0f

        override fun run() {
            super.run()
            Log.d("song_timer", songs[nowPos].title)
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
                    isPlaying = false;
                    Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
                }
            }
        }

    }
}