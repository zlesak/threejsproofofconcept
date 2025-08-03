package cz.uhk.zlesak.threejslearningapp.data.files;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamMultipartFile implements MultipartFile {

    private final InputStream inputStream;
    private final String fileName;

    public InputStreamMultipartFile(InputStream inputStream, String fileName) {
        this.inputStream = inputStream;
        this.fileName = fileName;
    }

    @NotNull
    @Override
    public String getName() {
        return fileName;
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

    @Override
    public void transferTo(@NotNull File dest) throws IllegalStateException {
        throw new UnsupportedOperationException("Not supported.");
    }
}

