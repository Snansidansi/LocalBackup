package com.snansidansi.backup.util;

/**
 * Record calls to hold two string that are intended to be a source path and a destination path.
 *
 * @param srcPath  Source path as string.
 * @param destPath Destination path as string.
 */
public record SrcDestPair(String srcPath, String destPath) {
}
