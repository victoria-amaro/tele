package com.vic.devocional

import org.junit.Assert.*
import org.junit.Test

class YouTubeChoiceTest {
    @Test fun usesSmartTubeAfterItIsInstalledEvenIfOfficialWasPreviouslySaved() {
        assertEquals("org.smarttube.stable", YouTubeChoice.preferred(setOf("com.google.android.youtube.tv", "org.smarttube.stable"), "com.google.android.youtube.tv"))
    }
    @Test fun respectsExplicitChoiceOfSmartTubeBeta() {
        assertEquals("org.smarttube.beta", YouTubeChoice.preferred(setOf("org.smarttube.stable", "org.smarttube.beta"), "org.smarttube.beta"))
    }
    @Test fun fallsBackWhenSavedAppWasUninstalled() {
        assertEquals("org.smarttube.stable", YouTubeChoice.preferred(setOf("org.smarttube.stable"), "org.smarttube.beta"))
    }
    @Test fun identifiesOfficialAppWhenThereIsNoAlternative() {
        assertEquals("com.google.android.youtube.tv", YouTubeChoice.preferred(setOf("com.google.android.youtube.tv"), null))
    }
    @Test fun doesNotInventAnInstalledPlayer() {
        assertNull(YouTubeChoice.preferred(emptySet(), null))
    }
}
