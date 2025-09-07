package cz.uhk.zlesak.threejslearningapp.application.files;

import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * A simple implementation of MultipartFile backed by in-memory bytes.
 * Used to handle file uploads for model or texture files.
 * Stores the file name and a display name (fallbacks to fileName).
 */
public class InputStreamMultipartFile implements MultipartFile {
    private final byte[] content;
    private final String fileName;
    private String displayName;

    /**
     * Constructor for InputStreamMultipartFile.
     *
     * @param inputStream the InputStream of the file (will be fully read and then closed)
     * @param fileName    the name of the file
     * @param displayName the display name of the file, if null, it will be set to fileName
     */
    @Builder
    public InputStreamMultipartFile(InputStream inputStream, String fileName, String displayName) {
        byte[] bytes;
        if (inputStream == null) {
            bytes = new byte[0];
        } else {
            try (InputStream is = inputStream) {
                bytes = is.readAllBytes();
            } catch (IOException e) {
                throw new IllegalStateException("Failed to read input stream", e);
            }
        }
        this.content = bytes;
        this.fileName = fileName;
        this.displayName = displayName != null ? displayName : fileName;
    }

    /**
     * Override of method for the name getter
     * @return FILE NAME!
     */
    @NotNull
    @Override
    public String getName() {
        return fileName;
    }

    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName != null ? displayName : fileName;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return "application/octet-stream";
    }

    @Override
    public boolean isEmpty() {
        return content == null || content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @NotNull
    @Override
    public byte[] getBytes() {
        return content.clone();
    }

    @NotNull
    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(@NotNull File dest) throws IllegalStateException {
        throw new UnsupportedOperationException("Not supported.");
    }
}
