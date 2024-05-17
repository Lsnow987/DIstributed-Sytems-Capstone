package edu.yu.capstone;

/**
 * A class to calculate video partitioning based on video properties and desired outcomes.
 * It calculates the number of partitions, segments per partition, duration of partitions, 
 * and the total number of segments, with support for calculations based on both source and target bitrates.
 */

public class PartitionCalculator implements UtilityCalculations {
    private final double sourceBitrate; // The source bitrate of the video in kbps.
    private final int videoDuration; // The total duration of the video in seconds.
    private final int segmentDuration; // The desired duration of each video segment in seconds.
    private final int targetPartitionSize; // The target size of each partition in megabytes (MB).
    private final int totalSegments; // The total number of segments in the video.

    /**
     * Constructs a PartitionCalculator with specified video parameters.
     *
     * @param sourceBitrate The bitrate of the source video in kbps.
     * @param videoDuration The duration of the video in seconds.
     * @param segmentDuration The desired duration of each segment in seconds.
     * @param targetPartitionSize The target size for each partition in MB.
     */
    public PartitionCalculator(double sourceBitrate, int videoDuration, int segmentDuration, int targetPartitionSize) {
        this.sourceBitrate = sourceBitrate;
        this.videoDuration = videoDuration;
        this.segmentDuration = segmentDuration;
        this.targetPartitionSize = targetPartitionSize;
        this.totalSegments = (int) Math.ceil((double) videoDuration / segmentDuration);
    }

    // Methods using source bitrate
    public int numberOfPartitions() {
        return numberOfPartitions(this.sourceBitrate);
    }

    public int segmentsPerPartition() {
        return segmentsPerPartition(this.sourceBitrate);
    }

    public int partitionDuration() {
        return partitionDuration(this.sourceBitrate);
    }

    public int numberOfSegments() {
        return totalSegments;
    }

    // Overloaded methods for target bitrate calculations
    public int numberOfPartitions(double targetBitrate) {
        int segmentsPerPartition = segmentsPerPartition(targetBitrate);
        return (int) Math.ceil((double) totalSegments / segmentsPerPartition);
    }

    public int segmentsPerPartition(double targetBitrate) {
        double segmentSizeMB = (segmentDuration * targetBitrate) / (8 * 1024); // Convert bitrate to size in MB.
        return (int) Math.floor((double)targetPartitionSize / segmentSizeMB);
    }

    public int partitionDuration(double targetBitrate) {
        return segmentsPerPartition(targetBitrate) * segmentDuration;
    }

    /**
     * Determines the partition number for a given segment number.
     *
     * @param segmentNumber The segment number for which the partition number is requested.
     * @return The partition number in which the given segment falls.
     * @throws IllegalArgumentException If the segment number is out of the valid range.
     */
    public int getPartitionBySegment(int segmentNumber) {
        if (segmentNumber < 1 || segmentNumber > totalSegments) {
            throw new IllegalArgumentException("Segment number is out of range.");
        }

        int segmentsPerPartition = segmentsPerPartition();
        int partitionNumber = (segmentNumber - 1) / segmentsPerPartition + 1;

        return partitionNumber;
    }
}