package cz.uhk.zlesak.threejslearningapp.data.files;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * A simple implementation of MultipartFile that wraps an InputStream.
 * Used to handle file uploads as for the model or texture files.
 * Wraps the file name and display name, which is bettter suited for displaying in UI components.
 */
public class InputStreamMultipartFile implements MultipartFile {

    private final InputStream inputStream;
    private final String fileName;
    private final String displayName;

    /**
     * Constructor for InputStreamMultipartFile.
     *
     * @param inputStream the InputStream of the file
     * @param fileName    the name of the file
     * @param displayName the display name of the file, if null, it will be set to fileName
     */
    public InputStreamMultipartFile(InputStream inputStream, String fileName, String displayName) {
        this.inputStream = inputStream;
        this.fileName = fileName;
        this.displayName = displayName != null ? displayName : fileName;
    }

    //region Getters
    @NotNull
    @Override
    public String getName() {
        return fileName;
    }

    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return "application/octet-stream";
    }

    @SneakyThrows
    @Override
    public boolean isEmpty() {
        return inputStream.available() == 0;
    }

    @SneakyThrows
    @Override
    public long getSize() {
        return inputStream.available();
    }

    @NotNull
    @Override
    public byte[] getBytes() throws IOException {
        return inputStream.readAllBytes();
    }

    @NotNull
    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    //end region
    @Override
    public void transferTo(@NotNull File dest) throws IllegalStateException {
        throw new UnsupportedOperationException("Not supported.");
    }
}

