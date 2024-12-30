package io.openfuture.openmessenger.service.impl

import com.xuggle.mediatool.IMediaListener
import com.xuggle.mediatool.event.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class XuggleAudioListener(outputFilename: String) : IMediaListener {

    private var outputStream: FileOutputStream? = null

    init {
        try {
            outputStream = FileOutputStream(File(outputFilename))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onAudioSamples(event: IAudioSamplesEvent) {
        val samples = event.audioSamples
        try {
            outputStream!!.write(samples.data.getByteArray(0, samples.size))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onOpen(p0: IOpenEvent?) {}
    override fun onClose(p0: ICloseEvent?) {}
    override fun onAddStream(event: IAddStreamEvent) {}
    override fun onCloseCoder(event: ICloseCoderEvent) {}
    override fun onFlush(event: IFlushEvent) {}
    override fun onWriteTrailer(p0: IWriteTrailerEvent?) {}
    override fun onOpenCoder(event: IOpenCoderEvent) {}
    override fun onReadPacket(event: IReadPacketEvent) {}
    override fun onWritePacket(p0: IWritePacketEvent?) {}
    override fun onWriteHeader(p0: IWriteHeaderEvent?) {}
    override fun onVideoPicture(event: IVideoPictureEvent) {}
}