package com.android.launcher3.smartspace

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.util.Log

data class MediaTrackInfo(
    val title: String,
    val artist: String?,
    val isPlaying: Boolean,
    val packageName: String
)

object MediaSmartspaceCard {
    private const val TAG = "MediaSmartspaceCard"

    fun getActiveTrack(context: Context): MediaTrackInfo? {
        return try {
            val sessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
                ?: return null

            val controllers = sessionManager.getActiveSessions(null)
            for (controller in controllers) {
                val playbackState = controller.playbackState
                if (playbackState != null && (playbackState.state == PlaybackState.STATE_PLAYING ||
                            playbackState.state == PlaybackState.STATE_BUFFERING)) {
                    val metadata = controller.metadata
                    if (metadata != null) {
                        val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE)
                            ?: metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
                            ?: ""
                        val artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)
                            ?: metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)

                        if (title.isNotEmpty()) {
                            return MediaTrackInfo(
                                title = title,
                                artist = artist,
                                isPlaying = true,
                                packageName = controller.packageName
                            )
                        }
                    }
                }
            }
            null
        } catch (e: SecurityException) {
            // Needs notification listener / session permission if running as non-system, handled gracefully
            null
        } catch (e: Exception) {
            Log.d(TAG, "Failed to query active media session: ${e.message}")
            null
        }
    }
}
