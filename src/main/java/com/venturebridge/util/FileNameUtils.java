package com.venturebridge.util;

import java.text.Normalizer;

public final class FileNameUtils {

    private FileNameUtils() {
    }

    public static String sanitize(String value) {
        String normalized = Normalizer.normalize(value == null ? "file" : value, Normalizer.Form.NFD);
        return normalized.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}