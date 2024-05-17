package edu.yu.capstone;

/**
 * This class provides methods to manage the naming convention for partitions of videos.
 */
public class VideoNameManager implements NameManager {
    private int jobId; // The ID of the job associated with the video
    private Resolution res; // The resolution of the video

    /**
     * Constructs a VideoNameManager object with the given job ID and resolution.
     * @param jobId The ID of the job associated with the video.
     * @param res The resolution of the video.
     */
    public VideoNameManager(int jobId, Resolution res) {
        this.jobId = jobId;
        this.res = res;
    }

    /**
     * Generates the name for a partition based on the given partition ID.
     * @param partitionId The ID of the partition.
     * @return The name of the partition.
     */
    public String getPartitionName(int partitionId) {
        return this.jobId + "." + res.name() + "." + partitionId;
    }
}
