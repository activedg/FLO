package com.example.flo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flo.data.entities.Album
import com.example.flo.data.entities.Like
import com.example.flo.data.entities.Song
import com.example.flo.data.entities.User

@Database(entities = [Song::class, User::class, Like::class, Album::class], version = 1)
abstract class SongDatabase: RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun userDao(): UserDao
    abstract fun albumDao(): AlbumDao

    // Singleton 으로 만들기
    companion object{
        private var instance: SongDatabase? = null

        @Synchronized
        fun getInstance(context: Context) : SongDatabase?{
            if (instance == null){
                synchronized(SongDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SongDatabase::class.java,
                        "song-database"
                    // 원래는 따로 스레드 생성해서 액티비티에서 비동기적으로 해야함/ allowMainThreadQueries 메서드 없이 build만
                    ).allowMainThreadQueries().build()
                }
            }
            return instance
        }
    }
}