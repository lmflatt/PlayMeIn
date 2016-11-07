package com.theironyard.Controllers;

import com.theironyard.Entities.Loop;
import com.theironyard.Repositories.LoopRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


@Controller
public class PlayMeInController {
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

    public String mergeSoundFiles(List<Path> paths, String tempName) throws IOException {
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

        return fileName;
    }

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
