package com.theironyard.controllers;

import com.theironyard.entities.Loop;
import com.theironyard.services.LoopRepository;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by lee on 11/3/16.
 */
@RestController
public class PlayMeInRestController {
    @Value("${aws.accessid}")
    private String accessid;

    @Value("${aws.bucket}")
    private String bucket;

    private Map<String, String> env = System.getenv();

    private String accesskey = env.get("AWS_ACCESSKEY");

    @Autowired
    LoopRepository loops;

    // RECEIVING String genre, String voice, and Multipart-File music
    @CrossOrigin
    @RequestMapping("/upload")
    public Loop upload(
            HttpServletResponse response,
            String genre,
            String voice,
            MultipartFile sample
    ) throws Exception {
        int partNumber = loops.findByGenreAndVoice(genre, voice).size() + 1;
        String filePrefix = genre + voice + partNumber;

        File wavFile = File.createTempFile(filePrefix, ".wav", new File("/tmp/"));
        FileOutputStream fos = new FileOutputStream(wavFile);
        fos.write(sample.getBytes());

        FileInputStream fis = new FileInputStream(wavFile);

        storeMusicAssetsToS3(filePrefix + ".wav", fis);

        Loop loop = new Loop(genre, voice, partNumber);
        loops.save(loop);

        response.sendRedirect("/");
        return loop;
    }

    private void storeMusicAssetsToS3(String fileName, InputStream stream) throws Exception {
        System.out.println("\nBUCKET = " + bucket + "\nSECRET-ACCESS-ID = " + accessid);
        MinioClient s3Client = new MinioClient("https: //s3.amazonaws.com", accessid, accesskey);
        s3Client.putObject(bucket, fileName, stream, stream.available(), "application/octet-stream");
    }
}
