package edu.yu.capstone;

import java.io.File;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResult;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Implementation of the VideoTranscoder interface that provides methods for creating keyframes, partitioning videos, and transcoding videos to different resolutions.
 */
public class VideoTranscoderImpl implements VideoTranscoder {
    private String bucketPushname;
    private TranscodingTask task;

    /**
     * Constructor to initialize the VideoTranscoderImpl object with the bucket push name and transcoding task.
     * @param bucketPushname The name of the bucket where the transcoded video will be pushed.
     * @param task The transcoding task associated with the video.
     */
    public VideoTranscoderImpl(String bucketPushname, TranscodingTask task) {
        this.bucketPushname = bucketPushname;
        this.task = task;
    }

    @Override
    public void createKeyFrames(File video, int seconds) {
        // Obtain the path of the input video file
        Path videoPath = video.toPath();
        // Determine the output directory for keyframed video
        Path outputDirectory = videoPath.getParent() != null ? videoPath.getParent() : Paths.get("");
        // Specify the output path for keyframed video
        Path outputPath = outputDirectory.resolve("keyframed_" + videoPath.getFileName().toString());
        // Building the expression for key frame placement
        String keyFrameExpression = "expr:gte(t,n_forced*" + seconds + ")";

        // Execute FFmpeg command to create keyframes
        FFmpegResult result = FFmpeg.atPath()
                .addInput(UrlInput.fromPath(videoPath))
                .addOutput(UrlOutput.toPath(outputPath)
                        .addArguments("-c:v", "libx264") // Specify the video codec
                        .addArguments("-force_key_frames", keyFrameExpression)) // Set keyframe expression
                .execute();
    }

    @Override
    public void partitionVideo(File video, int partitionDuration) {
        // Obtain the path of the input video file
        Path videoPath = video.toPath();
        // Extract file extension
        String fileName = videoPath.getFileName().toString();
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        // Specify the output directory and pattern for segmented video
        Path outputDir = videoPath.getParent() != null ? videoPath.getParent() : Paths.get("");
        String outputPattern = outputDir.resolve("segment_%03d." + extension).toString();
        // Specify the path for the manifest file
        String manifestFileName = "manifest.m3u8";
        Path manifestFilePath = outputDir.resolve(manifestFileName);

        // Execute FFmpeg command to partition video
        FFmpegResult result = FFmpeg.atPath()
                .addInput(UrlInput.fromPath(videoPath))
                .addOutput(UrlOutput.toPath(Paths.get(outputPattern))
                        .addArguments("-f", "segment")
                        .addArguments("-segment_time", String.valueOf(partitionDuration))
                        .addArguments("-reset_timestamps", "1") // Reset timestamps for each segment
                        .addArguments("-c", "copy")) // Copy codecs to prevent re-encoding
                .addArguments("-segment_list", manifestFilePath.toString())
                .addArguments("-segment_list_type", "m3u8") // Specify the playlist type
                .execute();

        // Upload the generated manifest file to S3
        S3FileUploader fileUploader = new S3FileUploader(this.bucketPushname, task);
        fileUploader.upload(new File("manifest.m3u8"), manifestFileName);
    }

    // Method to transcode video with different bitrates and resolutions
    @Override
    public void transcodeVideo(String resolution, String inputPath, String outputPath) {
        // replace extension for output path from mp4 to ts
        outputPath = outputPath.replace(".mp4", ".ts");
        // Create directory for transcoding if it does not exist
        String dir = "/home/lsnow/transcode/";
        File directory = new File(dir);
        outputPath = dir + outputPath;
        if (!directory.exists()) {
//            System.out.println("Creating directory for transcoding: " + directory);
            directory.mkdirs();
        }

        // Check if the output file already exists and delete it if necessary
//        File outputFile = new File(outputPath);
//        if (outputFile.exists()) {
//            boolean deleted = outputFile.delete();
//            if (!deleted) {
//                // If the file couldn't be deleted, throw an error or exit
//                throw new IllegalStateException("Failed to delete existing output file: " + outputPath);
//            }
//        }

        // Retrieve transcoding parameters from environment variables
        String getResString = resolution + "_RESOLUTION";
        String resolutionValue = System.getenv(getResString);
        String getBitrateString = resolution + "_BITRATE";
        int bitrateValue = Integer.parseInt(System.getenv(getBitrateString));
        String codecValue = System.getenv("VIDEO_CODEC");
        String audioCodecValue = System.getenv("AUDIO_CODEC");
        String audioBitrateValue = System.getenv("AUDIO_BITRATE");
        String formatValue = System.getenv("VIDEO_FORMAT");
        String threadsValue = System.getenv("THREAD_COUNT_FOR_FFMPEG");

        // Perform transcoding using FFmpeg
        FFmpeg.atPath()
                .addInput(UrlInput.fromPath(Paths.get(inputPath)))
                .addOutput(UrlOutput.toPath(Paths.get(outputPath))
                        .addArguments("-c:v", codecValue) // Video codec
                        .addArguments("-vf", "scale=" + resolutionValue) // Video scaling
                        .addArguments("-b:v", bitrateValue + "k") // Video bitrate
                        .addArguments("-c:a", audioCodecValue) // Audio codec
                        .addArguments("-b:a", audioBitrateValue + "k") // Audio bitrate
                        .addArguments("-f", formatValue) // Output format
                        .addArguments("-threads", threadsValue) // Number of threads for transcoding
                )
                .execute();

        // Log success message
        System.out.println("Successfully transcoded " + inputPath + " to " + outputPath);
//        print all file names stored in transcode directory
//        System.out.println("Files in directory: ");
//        File[] files = directory.listFiles();
//        if (files != null) {
//            for (File file : files) {
//                System.out.println(file.getName());
//            }
//        }


        // Delete input file after successful transcoding
        File inputFileToDelete = new File(inputPath);
        if (inputFileToDelete.delete()) {
//            System.out.println("Deleted input file: " + inputPath);
        } else {
//            System.err.println("Failed to delete input file: " + inputPath);
        }
    }

//    public static void main(String[] args) {
////        File videoFile = new File("input.mp4");
////        int keyFrameIntervalSeconds = 5; // For example, insert a keyframe every 5 seconds
////
//        VideoTranscoderImpl transcoder = new VideoTranscoderImpl();
////        transcoder.createKeyFrames(videoFile, keyFrameIntervalSeconds);
////
////        System.out.println("Keyframe insertion complete.");
////        int partitionDuration = 300; // For example, partition the video into 1-minute segments
////
////        transcoder.partitionVideo(videoFile, partitionDuration);
////        System.out.println("Video partitioning completed.");
//
////        example transcoding
//        String resolution = "720p";
//        String inputPath = "input.mp4";
//        String outputPath = "output.mp4";
//        transcoder.transcodeVideo(resolution, inputPath, outputPath);
//        System.out.println("Video transcoding completed.");
//    }
}