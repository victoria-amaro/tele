package com.vic.devocional

/** A previous official-app selection must not hide a newly installed SmartTube. */
object YouTubeChoice {
    const val OFFICIAL = "com.google.android.youtube.tv"
    private val smartTube = listOf(
        "org.smarttube.stable", "org.smarttube.beta", "app.smarttube.fdroid",
        "com.teamsmart.videomanager.tv"
    )

    fun preferred(available: Set<String>, saved: String?): String? {
        if (saved != null && saved != OFFICIAL && saved in available) return saved
        return smartTube.firstOrNull { it in available } ?: OFFICIAL.takeIf { it in available }
    }
}
