package com.shoothzj.fdb.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Controller
public class DownloadController {

    private static final String path = System.getProperty("user.dir");

    @GetMapping("/download")
    public void downloadFile(HttpServletResponse response) throws IOException {
        File file = new File(path + "/src/main/resources/static/hello.txt");

        String mimeType = Files.probeContentType(file.toPath());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);

        String filename = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        try (
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                FileChannel inputChannel = randomAccessFile.getChannel();
                WritableByteChannel outputChannel = Channels.newChannel(response.getOutputStream())
        ) {
            long position = 0;
            long count = inputChannel.size();
            while (position < count) {
                position += inputChannel.transferTo(position, count - position, outputChannel);
            }
        }
    }

}
