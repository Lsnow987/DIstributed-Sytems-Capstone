package edu.yu.capstone;

/**
 * Implementations of this interface perform useful calculations pertaining to segments and partitions.
 */
public interface UtilityCalculations {
    /**
     * Calculates the total number of partitions.
     * @return The total number of partitions.
     */
    public int numberOfPartitions();

    /**
     * Calculates the number of segments per partition.
     * @return The number of segments per partition.
     */
    public int segmentsPerPartition();

    /**
     * Calculates the duration of each partition.
     * @return The duration of each partition.
     */
    public int partitionDuration();

    /**
     * Calculates the total number of segments.
     * @return The total number of segments.
     */
    public int numberOfSegments();

    /**
     * Gets the partition number for a given segment number.
     * @param segmentNumber The segment number.
     * @return The partition number containing the given segment.
     */
    public int getPartitionBySegment(int segmentNumber);
}
