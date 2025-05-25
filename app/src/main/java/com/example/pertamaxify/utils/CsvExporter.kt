import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.example.pertamaxify.data.model.MonthlyStats
import java.io.File
import java.io.IOException

object CsvExporter {

    fun exportMonthlyStats(
        context: Context,
        stats: List<MonthlyStats>,
        fileName: String = "sound_capsule.csv"
    ): Uri? {
        val csvHeader = listOf(
            "Month", "Minutes Listened", "Top Song", "Top Artist",
            "Streak Days", "Streak Song", "Streak Start", "Streak End"
        ).joinToString(",")

        val csvRows = stats.map {
            listOf(
                it.monthYear,
                it.timesListened.toString(),
                it.topSong,
                it.topArtist,
                it.streakDay?.toString() ?: "-",
                it.streakSong?.title ?: "-",
                it.streakStartDate?.toString() ?: "-",
                it.streakEndDate?.toString() ?: "-"
            ).joinToString(",") { field -> field.replace(",", " ") } // Avoid breaking CSV
        }

        val content = buildString {
            appendLine(csvHeader)
            csvRows.forEach { appendLine(it) }
        }

        return try {
            val file = File(context.getExternalFilesDir(null), fileName)
            file.writeText(content)

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        } catch (e: IOException) {
            Log.e("CsvExporter", "Failed to write CSV: ${e.message}")
            null
        }
    }

    fun writeCsvToUri(context: Context, uri: Uri, stats: List<MonthlyStats>) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { output ->
                val writer = output.bufferedWriter()

                val header = listOf(
                    "Month", "Minutes Listened", "Top Song", "Top Artist",
                    "Streak Days", "Streak Song", "Streak Start", "Streak End"
                )
                writer.appendLine(header.joinToString(","))

                for (item in stats) {
                    val row = listOf(
                        item.monthYear,
                        item.timesListened.toString(),
                        item.topSong,
                        item.topArtist,
                        item.streakDay?.toString() ?: "-",
                        item.streakSong?.title ?: "-",
                        item.streakStartDate?.toString() ?: "-",
                        item.streakEndDate?.toString() ?: "-"
                    ).joinToString(",") { it.replace(",", " ") } // Sanitize commas
                    writer.appendLine(row)
                }

                writer.flush()
            }
        } catch (e: Exception) {
            Log.e("CSV_WRITE", "Error writing CSV: ${e.message}")
        }
    }

}
