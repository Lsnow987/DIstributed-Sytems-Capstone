package edu.yu.capstone;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Interface for a component responsible for pulling files from a remote location.
 */
public interface FilePuller {
    /**
     * Download files specified in the segment to partition mapping.
     * @param map Mapping of segment names to corresponding partition names.
     * @return List of downloaded files.
     */
    List<File> download(Map<String,String> map);
}
