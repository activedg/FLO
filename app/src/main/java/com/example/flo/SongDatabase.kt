package com.example.flo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Song::class], version = 1)
abstract class SongDatabase: RoomDatabase() {
    abstract fun songDao(): SongDao

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