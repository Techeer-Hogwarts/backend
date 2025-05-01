package backend.techeerzip.infra.s3;

import java.util.Set;

public final class ImageExtensionHandler {

    private static final Set<String> VALID_EXTENSIONS =
            Set.of(
                    "jpg", "jpeg", "png", "gif", "svg", "webp", "bmp", "tiff", "ico", "heic",
                    "heif", "raw", "psd");

    private ImageExtensionHandler() {}

    public static void isValid(String ext) {
        if (!VALID_EXTENSIONS.contains(ext.toLowerCase())) {
            throw new IllegalArgumentException();
        }
    }

    public static String extractExtension(String name) {
        if (name == null || !name.contains(".")) {
            throw new IllegalArgumentException();
        }
        final String ext = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
        isValid(ext);
        return ext;
    }
}
