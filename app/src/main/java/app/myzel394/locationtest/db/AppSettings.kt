package app.myzel394.locationtest.db

import android.media.MediaRecorder
import android.os.Build
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@Serializable
data class AppSettings(
    val audioRecorderSettings: AudioRecorderSettings = AudioRecorderSettings(),
    val showAdvancedSettings: Boolean = false,
) {
    fun setShowAdvancedSettings(showAdvancedSettings: Boolean): AppSettings {
        return copy(showAdvancedSettings = showAdvancedSettings)
    }

    fun setAudioRecorderSettings(audioRecorderSettings: AudioRecorderSettings): AppSettings {
        return copy(audioRecorderSettings = audioRecorderSettings)
    }

    companion object {
        fun getDefaultInstance(): AppSettings = AppSettings()
    }
}

@Serializable
data class AudioRecorderSettings(
    // 60 seconds
    val intervalDuration: Long = 60 * 1000L,
    // 320 Kbps
    val bitRate: Int = 320000,
    val samplingRate: Int? = null,
    val outputFormat: Int? = null,
    val encoder: Int? = null,
) {
    fun getOutputFormat(): Int = outputFormat ?:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            MediaRecorder.OutputFormat.AAC_ADTS
        else
            MediaRecorder.OutputFormat.THREE_GPP

    fun getSamplingRate(): Int = samplingRate ?: when(getOutputFormat()) {
        MediaRecorder.OutputFormat.AAC_ADTS -> 96000
        MediaRecorder.OutputFormat.THREE_GPP -> 44100
        else -> throw Exception("Unknown output format")
    }

    fun getEncoder(): Int = encoder ?:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            MediaRecorder.AudioEncoder.AAC
        else
            MediaRecorder.AudioEncoder.AMR_NB

    fun getFileExtensions(): String =
        when(getOutputFormat()) {
            MediaRecorder.OutputFormat.AAC_ADTS -> "aac"
            MediaRecorder.OutputFormat.THREE_GPP -> "3gp"
            else -> throw Exception("Unknown output format")
        }

    fun setIntervalDuration(duration: Long): AudioRecorderSettings {
        if (duration < 30 * 1000L || duration > 60 * 60 * 1000L) {
            throw Exception("Interval duration must be between 30 seconds and 1 hour")
        }

        return copy(intervalDuration = duration)
    }

    fun setBitRate(bitRate: Int): AudioRecorderSettings {
        println("bitRate: $bitRate")
        if (bitRate !in 1000..320000) {
            throw Exception("Bit rate must be between 1000 and 320000")
        }

        return copy(bitRate = bitRate)
    }

    fun setSamplingRate(samplingRate: Int?): AudioRecorderSettings {
        if (samplingRate != null && samplingRate < 1000) {
            throw Exception("Sampling rate must be at least 1000")
        }

        return copy(samplingRate = samplingRate)
    }

    fun setOutputFormat(outputFormat: Int): AudioRecorderSettings {
        if (outputFormat < 0 || outputFormat > 11) {
            throw Exception("OutputFormat is not a MediaRecorder.OutputFormat constant")
        }

        return copy(outputFormat = outputFormat)
    }

    fun setEncoder(encoder: Int): AudioRecorderSettings {
        if (encoder < 0 || encoder > 7) {
            throw Exception("Encoder is not a MediaRecorder.AudioEncoder constant")
        }

        return copy(encoder = encoder)
    }

    companion object {
        val EXAMPLE_DURATION_TIMES = listOf(
            60 * 1000L,
            60 * 5 * 1000L,
            60 * 10 * 1000L,
            60 * 15 * 1000L,
            60 * 30 * 1000L,
            60 * 60 * 1000L,
        )
        val EXAMPLE_BITRATE_VALUES = listOf(
            96 * 1000,
            128 * 1000,
            160 * 1000,
            192 * 1000,
            256 * 1000,
            320 * 1000,
        )
        val EXAMPLE_SAMPLING_RATE = listOf(
            null,
            8000,
            16000,
            22050,
            44100,
            48000,
            96000,
        )
    }
}
