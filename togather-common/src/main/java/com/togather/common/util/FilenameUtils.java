package com.togather.common.util;

import java.util.Optional;

public class FilenameUtils {
    public static String getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                .orElse("");
    }

    public static String addExtension(String fileName, String extension) {
        return fileName + "." + extension;
    }
}
