package com.example.pertamaxify.db

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object DatabaseSeeder {
    private const val PREF_KEY = "db_seeded"

    fun seedSong(context: Context, database: AppDatabase, onComplete: () -> Unit) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val seeded = prefs.getBoolean(PREF_KEY, false)

        if (seeded) {
            Log.d("DatabaseSeeder", "Already seeded.")
            onComplete()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            Log.d("DatabaseSeeder", "Seeding database...")

            // Force open database by call random func
            database.songDao().getAllSong()

//            val seedSongs = listOf(
//                Song(
//                    "Starboy",
//                    "The Weeknd, Daft Punk",
//                    "content://media/external/file/1000000033",
//                    "content:l//media/external/file/1000000035"
//                ),
//                Song(
//                    "Here Comes The Sun",
//                    "The Beatles",
//                    "content://media/external/file/1000000034",
//                    "..."
//                ),
//                Song("Midnight Pretenders", "Tomoko Aran", "Sickboy Chainsmoker.png", "..."),
//                Song("Violent Crimes", "Kanye West", "Sickboy Chainsmoker.png", "...")
//            )

//            seedSongs.forEach { database.songDao().upsertSong(it) }

            prefs.edit() { putBoolean(PREF_KEY, true) }
            Log.d("DatabaseSeeder", "Seeding complete.")

            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }
}
