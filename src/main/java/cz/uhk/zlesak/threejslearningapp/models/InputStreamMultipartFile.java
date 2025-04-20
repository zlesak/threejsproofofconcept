package cz.uhk.zlesak.threejslearningapp.models;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.io.IOException;

public class InputStreamMultipartFile implements MultipartFile {

    private final InputStream inputStream;
    private final String fileName;
    private final long size;

    public InputStreamMultipartFile(InputStream inputStream, String fileName, long size) {
        this.inputStream = inputStream;
        this.fileName = fileName;
        this.size = size;
    }

    @Override
    public String getName() {
        return "file";
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
        return size == 0;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return inputStream.readAllBytes();
    }

    @Override
    public InputStream getInputStream(){
        return inputStream;
    }

    @Override
    public void transferTo(java.io.File dest) throws IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

