package edu.yu.capstone;

/**
 * Represents a resolution
 *
 * Used by AutoBitrateConfig interface
 */
public record Resolution (String name, /* e.g., "720p" */
                          double bitrate /* bitrate in kilobytes per second */) {
}
