package edu.yu.capstone;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;

import java.io.File;

/**
 * Implementations of this class provide methods to retrieve information about a video file.
 */
public class VideoInfoImpl implements VideoInfo {
    /**
     * Retrieves the bitrate of the video in kilobytes per second.
     * @param video The video file.
     * @return The bitrate of the video.
     * @throws RuntimeException if no video stream is found in the file.
     */
    @Override
    public int getVideoBitrate(File video) {
        FFprobeResult probeResult = FFprobe.atPath()
                .setShowStreams(true)
                .setInput(video.toPath())
                .execute();

        return Math.toIntExact(probeResult.getStreams().stream()
                .filter(s -> StreamType.VIDEO.equals(s.getCodecType()))
                .findFirst()
                .map(s -> s.getBitRate() != null ? s.getBitRate() / 1000 : 0)
                .orElseThrow(() -> new RuntimeException("No video stream found in the file: " + video.getAbsolutePath())));
    }

    /**
     * Retrieves the length of the video in seconds.
     * @param video The video file.
     * @return The length of the video in seconds.
     * @throws RuntimeException if unable to determine the length of the video file.
     */
    @Override
    public int getVideoLength(File video) {
        FFprobeResult probeResult = FFprobe.atPath()
                .setShowFormat(true)  // Ensure format information is requested
                .setInput(video.toPath())
                .execute();

        if (probeResult.getFormat() == null || probeResult.getFormat().getDuration() == null) {
            throw new RuntimeException("Unable to determine the length of the video file: " + video.getAbsolutePath());
        }

        // Duration is returned in seconds, rounding it to the nearest second for consistency
        return (int) Math.round(probeResult.getFormat().getDuration());
    }

    /**
     * Retrieves the total bitrate of the video file in kilobytes per second.
     * @param video The video file.
     * @return The total bitrate of the video file.
     * @throws RuntimeException if unable to determine the total bitrate of the video file.
     */
    public int getTotalFileBitrate(File video) {
        FFprobeResult probeResult = FFprobe.atPath()
                .setShowFormat(true)  // Ensure format information is also fetched
                .setInput(video.toPath())
                .execute();

        if (probeResult.getFormat() == null || probeResult.getFormat().getBitRate() == null) {
            throw new RuntimeException("Unable to determine the total bitrate of the video file: " + video.getAbsolutePath());
        }

        // The total bitrate, directly from the format
        return Math.toIntExact(probeResult.getFormat().getBitRate() / 1000);
    }


//    public static void main(String[] args) {
//        File videoFile = new File("10min.mp4");
//        VideoInfoImpl videoInfo = new VideoInfoImpl();
//
//        try {
//            int bitrate = videoInfo.getVideoBitrate(videoFile);
//            int totalBitrate = videoInfo.getTotalFileBitrate(videoFile);
//            int length = videoInfo.getVideoLength(videoFile);
//
//            System.out.println("Video Bitrate: " + bitrate + " kb/s");
//            System.out.println("Total File Bitrate: " + totalBitrate + " kb/s");
//            System.out.println("Video Length: " + length + " seconds");
//        } catch (Exception e) {
//            System.err.println("Error processing video file: " + e.getMessage());
//        }
//    }
}