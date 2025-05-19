package com.example.pertamaxify.data.model

/*
[
    {
        "id": 71,
        "title": "Die With A Smile",
        "artist": "Lady Gaga, Bruno Mars",
        "artwork": "https://storage.googleapis.com/mad-public-bucket/cover/Die%20With%20A%20Smile.png",
        "url": "https://storage.googleapis.com/mad-public-bucket/mp3/Lady%20Gaga%2C%20Bruno%20Mars%20-%20Die%20With%20A%20Smile%20(Lyrics).mp3",
        "duration": "4:12", // mm:ss
        "country": "GLOBAL",
        "rank": 1,
        "createdAt": "2025-05-08T02:16:53.192Z",
        "updatedAt": "2025-05-08T02:16:53.192Z"
    },
….
]
 */

data class SongApi(
    val title: String,
    val singer: String,
    val imagePath: String, // Image path
    val audioPath: String, // Audio path
    val addedBy: String? = null,
    val isLiked: Boolean? = false, // is the song liked by user who added it
    val addedTime: String, // When the song was added to the database
    val recentlyPlayed: String? = null, // When the song was last played by the user who added it
    val isDownloaded: Boolean = false, // Is the song downloaded from the server
)
