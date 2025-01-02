package io.openfuture.openmessenger.service.impl

import com.xuggle.mediatool.IMediaReader
import com.xuggle.mediatool.ToolFactory
import io.openfuture.openmessenger.service.VideoService
import org.springframework.stereotype.Service


@Service
class VideoServiceImpl: VideoService {

    override fun convertToAudio() {
        val inputFilename = "videofiles/meeting.mp4"
        val outputFilename = "audiofiles/meeting.mp3"

        val mediaReader: IMediaReader = ToolFactory.makeReader(inputFilename)

        mediaReader.addListener(XuggleAudioListener(outputFilename))

        while (mediaReader.readPacket() == null) {

        }

    }

}
