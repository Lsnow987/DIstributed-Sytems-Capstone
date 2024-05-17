package edu.yu.capstone;

import java.io.File;

/**
 * Implementations of this class provide utility methods to get information about a particular video file
 */

public interface VideoInfo {
    // Returns the bitrate of the video in kilobytes per second
    public int getVideoBitrate(File video);

    // Returns the length of the video in seconds
    public int getVideoLength(File video);
}
