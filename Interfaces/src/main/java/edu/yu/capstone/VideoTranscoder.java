package edu.yu.capstone;

import java.io.File;

/**
 * Implementations of this interface provide methods for transcoding and processing videos.
 */
public interface VideoTranscoder {

    /**
     * Creates keyframes for the given video at specified intervals.
     * @param video The video file for which keyframes are to be created.
     * @param seconds The interval, in seconds, between keyframes.
     */
    public void createKeyFrames(File video, int seconds);

    /**
     * Partitions the given video into segments of a specified duration.
     * @param video The video file to be partitioned.
     * @param partitionDuration The duration, in seconds, of each partition.
     */
    public void partitionVideo(File video, int partitionDuration);

    /**
     * Transcodes the given video to the specified resolution.
     * @param resolution The target resolution for transcoding.
     * @param inputPath The path of the input video file.
     * @param outputPath The path where the transcoded video will be saved.
     */
    public void transcodeVideo(String resolution, String inputPath, String outputPath);
}
