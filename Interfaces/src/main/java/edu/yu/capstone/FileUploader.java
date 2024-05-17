package edu.yu.capstone;

import java.io.File;

/**
 * Interface for a component responsible for uploading files to a remote location.
 */
public interface FileUploader {
    /**
     * Upload a local file to a remote location.
     * @param file Local file to upload.
     */
    void upload(File file, String uploadFileName);
}
