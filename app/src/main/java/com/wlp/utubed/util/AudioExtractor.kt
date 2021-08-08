package com.wlp.utubed.util

import android.media.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.IOException
import java.nio.ByteBuffer

class AudioExtractor {

    private val DEFAULT_BUFFER_SIZE = 1 * 1024 * 1024;
    private val TAG = "AudioExtractorDecoder";

    /**
     * @param srcPath  the path of source video file.
     * @param dstPath  the path of destination video file.
     * @param startMs  starting time in milliseconds for trimming. Set to
     *                 negative if starting from beginning.
     * @param endMs    end time for trimming in milliseconds. Set to negative if
     *                 no trimming at the end.
     * @param useAudio true if keep the audio track from the source.
     * @param useVideo true if keep the video track from the source.
     * @throws IOException
     */

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Throws(IOException::class)
    fun genVideoUsingMuxer(srcPath : String
                           , dstPath : String
                           , startMs : Int
                           , endMs : Int
                           , useAudio : Boolean
                           , useVideo : Boolean)
    {
        // Set up MediaExtractor to read from the source.
        val extractor = MediaExtractor();

        extractor.setDataSource(srcPath);

        val trackCount = extractor.getTrackCount()


        // Set up MediaMuxer for the destination.
        val muxer = MediaMuxer(dstPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        // Set up the tracks and retrieve the max buffer size for selected
        // tracks.

        val indexMap = mutableMapOf<Int, Int>();

        var bufferSize = -1;

        for (i in 0 until trackCount)
        {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME);

            var  selectCurrentTrack = false;

            if (mime.startsWith("audio/") && useAudio) {
                selectCurrentTrack = true;
            }
            if (selectCurrentTrack) {
                extractor.selectTrack(i);
                val dstIndex = muxer.addTrack(format);
                indexMap.put(i, dstIndex);
                if (format.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
                    val newSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)

                    if(newSize > bufferSize) bufferSize = newSize
                    else bufferSize;
                }
            }
        }
        if (bufferSize < 0) {
            bufferSize = DEFAULT_BUFFER_SIZE;
        }
        // Set up the orientation and starting time for extractor.
        val retrieverSrc = MediaMetadataRetriever();
        retrieverSrc.setDataSource(srcPath);
        val degreesString = retrieverSrc.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        if (degreesString != null) {
            val degrees = Integer.parseInt(degreesString);
            if (degrees >= 0) {
                muxer.setOrientationHint(degrees);
            }
        }
        if (startMs > 0) {
            extractor.seekTo((startMs * 1000).toLong(), MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        }
        // Copy the samples from MediaExtractor to MediaMuxer. We will loop
        // for copying each sample and stop when we get to the end of the source
        // file or exceed the end time of the trimming.
        var offset = 0;
        var trackIndex = -1;
        var dstBuf = ByteBuffer.allocate(bufferSize);
        val bufferInfo = MediaCodec.BufferInfo();

        muxer.start();

        while (true) {
            bufferInfo.offset = offset;
            bufferInfo.size = extractor.readSampleData(dstBuf, offset);
            if (bufferInfo.size < 0) {
                bufferInfo.size = 0;
                break;
            } else {
                bufferInfo.presentationTimeUs = extractor.getSampleTime();
                if (endMs > 0 && bufferInfo.presentationTimeUs > (endMs * 1000)) {
                    break;
                } else {
                    bufferInfo.flags = extractor.getSampleFlags();
                    trackIndex = extractor.getSampleTrackIndex();
                    muxer.writeSampleData(indexMap.get(trackIndex)!!, dstBuf, bufferInfo);
                    extractor.advance();
                }
            }
        }

        muxer.stop();
        muxer.release();
        return;
    }
}