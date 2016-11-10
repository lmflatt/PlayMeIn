package com.theironyard.Controllers;

import com.theironyard.Entities.Loop;
import com.theironyard.Repositories.LoopRepository;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


@Controller
@PropertySource("classpath:application.properties")
public class PlayMeInController {
    @Value("${aws.accessid}")
    private String accessid;

    @Autowired
    private Environment environment;

    private String accesskey = environment.getProperty("AWS_ACCESSKEY");

    @Value("{aws.bucket")
    private String bucket;

    @Autowired
    LoopRepository loops;

    public static final ArrayList<String> VOICES = new ArrayList<String>(Arrays.asList("bass", "melody", "drum", "alt-drum", "harmony", "alt-harmony"));
    public static final String BASICPATH = "/Users/lee/workspace/PlayMeIn/src/main/resources/MusicAssets/";
    public static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12345890";

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String home() {
        return "index";
    }

    @RequestMapping(path = "/new-song", method = RequestMethod.POST)
    public String newSong(Model model, String genre) throws Exception {
        List<Path> paths = new ArrayList<>();
        Random rng = new Random();
        loadMusicAssetsFromS3();
        for (String voice : VOICES) {
            List<Loop> partLoops = loops.FindByGenreAndVoice(genre, voice);
            int loopNumber = rng.nextInt(partLoops.size()) + 1;
            String pathEnd = String.format("%s/%s%d", genre, voice, loopNumber);
            paths.add(Paths.get(BASICPATH, pathEnd));
        }

        String tempName = generateString(rng);
        String tempFileLocation = mergeSoundFiles(paths, tempName);

        model.addAttribute("song", tempFileLocation);

        return "preview";
    }

    private void loadMusicAssetsFromS3(String fileName) throws Exception {
        // pull down files from S3 into ____(/tmp ? directory)
        MinioClient s3Client = new MinioClient("https: //s3.amazonaws.com", accessid, accesskey);
        s3Client.getObject(bucket, fileName);
        }


    public String mergeSoundFiles(List<Path> paths, String tempName) throws Exception {
        List<byte[]> bytesList = new ArrayList<>();
        for (Path p : paths) {
            bytesList.add(Files.readAllBytes(p));
        }
        int byteLength = bytesList.get(0).length;

        // TODO Refactor inner for loop into new method, pass bytesList and byteLength, return value of out

        byte[] out = new byte[byteLength];

        for (int i = 0; i < byteLength; i++) {
            int bytesTotal = 0;
            for (byte[] bytes : bytesList) {
                bytesTotal += (int) bytes[i];
            }
            out[i] = (byte) (bytesTotal >> 2);
        }

        //TODO end section

        ByteArrayInputStream bais = new ByteArrayInputStream(out);
        AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
        AudioInputStream stream = new AudioInputStream(bais, format,
                out.length / format.getFrameSize());

        String fileName = String.format("/tmp/%s.wav", tempName);
        File file = new File(fileName);
        AudioSystem.write(stream, AudioFileFormat.Type.WAVE, file);

        // TODO: Does the created file need to go to S3?
//        storeMusicAssetsToS3(tempName + ".wav", stream);

        return fileName;
    }

//    private void storeMusicAssetsToS3(String fileName, InputStream stream) throws Exception {
//        System.out.println("\nBUCKET = " + bucket + "\nSECRET-ACCESS-ID = " + accessid);
//        MinioClient s3Client = new MinioClient("https: //s3.amazonaws.com", accessid, accesskey);
//        s3Client.putObject(bucket, fileName, stream, stream.available(), "application/octet-stream");
//    }
//      May need to place in RestController

    public static String generateString(Random rng)
    {
        char[] text = new char[12];
        for (int i = 0; i < 12; i++)
        {
            text[i] = CHARS.charAt(rng.nextInt(CHARS.length()));
        }
        return new String(text);
    }
}
