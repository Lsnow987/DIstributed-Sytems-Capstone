package edu.yu.capstone;

/**
 * Implementations of this class streamline the process of keeping track of partition files by giving
 * them a set name
 */

public interface NameManager {
    // Return name of partition
    public String getPartitionName(int partitionId);
}
