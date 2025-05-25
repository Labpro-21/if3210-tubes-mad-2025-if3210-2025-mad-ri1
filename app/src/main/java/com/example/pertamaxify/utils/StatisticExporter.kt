package com.example.pertamaxify.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import com.example.pertamaxify.data.model.MonthlyStats

object StatisticExporter {
    fun writeCsvToUri(context: Context, uri: Uri, stats: List<MonthlyStats>) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { output ->
                val writer = output.bufferedWriter()

                val header = listOf(
                    "Month",
                    "Minutes Listened",
                    "Top Song",
                    "Top Artist",
                    "Streak Days",
                    "Streak Song",
                    "Streak Start",
                    "Streak End"
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
                    ).joinToString(",") { it.replace(",", " ") }
                    writer.appendLine(row)
                }

                writer.flush()
            }
        } catch (e: Exception) {
            Log.e("CSV_WRITE", "Error writing CSV: ${e.message}")
        }
    }

    fun writePdfToUri(context: Context, uri: Uri, stats: List<MonthlyStats>) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val document = PdfDocument()
                val paint = Paint()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val page = document.startPage(pageInfo)

                val canvas = page.canvas
                var yPosition = 40
                paint.textSize = 16f
                canvas.drawText("Your Sound Capsule Summary", 40f, yPosition.toFloat(), paint)
                yPosition += 30

                paint.textSize = 12f

                stats.forEach { item ->
                    val streak =
                        if (item.streakDay != null && item.streakSong != null) "${item.streakDay} days of '${item.streakSong.title}'"
                        else "No streak"

                    val lines = listOf(
                        "Month: ${item.monthYear}",
                        "Minutes Listened: ${item.timesListened}",
                        "Top Song: ${item.topSong}",
                        "Top Artist: ${item.topArtist}",
                        "Streak: $streak",
                        "Date: ${item.streakStartDate ?: "-"} to ${item.streakEndDate ?: "-"}",
                        ""
                    )

                    lines.forEach { line ->
                        if (yPosition > 800) {
                            document.finishPage(page)
                            yPosition = 40
                        }
                        canvas.drawText(line, 40f, yPosition.toFloat(), paint)
                        yPosition += 20
                    }
                }

                document.finishPage(page)
                document.writeTo(outputStream)
                document.close()
            }
        } catch (e: Exception) {
            Log.e("PDF_WRITE", "Error writing PDF: ${e.message}")
        }
    }


}
