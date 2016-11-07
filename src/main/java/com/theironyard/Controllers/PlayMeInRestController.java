package com.theironyard.Controllers;

import com.theironyard.Repositories.LoopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lee on 11/3/16.
 */
@RestController
public class PlayMeInRestController {
    @Autowired
    LoopRepository loops;

    // RECEIVING String genre, String voice, and Multipart-File music
//    @RequestMapping("/upload")
//    public Photo upload(
//            HttpSession session,
//            HttpServletResponse response,
//            String genre,
//            String voice,
//            MultipartFile sample
//    ) throws Exception {
//        String userName = (String) session.getAttribute("userName");
//        if (userName == null) {
//            throw new Exception("not logged in.");
//        }
//        User sender = users.findFirstByName(userName);
//        User receiverUser = users.findFirstByName(receiver);
//
//        if (receiverUser == null) {
//            throw new Exception("Receiver does not exist.");
//        }
//
//        if (! photo.getContentType().startsWith("image")) {
//            throw new Exception("Only images are allowed.");
//        }
//
//        File photoFile = File.createTempFile("photo", photo.getOriginalFilename(), new File("public"));
//        FileOutputStream fos = new FileOutputStream(photoFile);
//        fos.write(photo.getBytes());
//
//        if(seconds <= 0) {
//            seconds = 10;
//        }
//        Photo p = new Photo();
//        p.setSender(sender);
//        p.setReceiver(receiverUser);
//        p.setFilename(photoFile.getName());
//        p.setSecondsToDelete(seconds);
//        if (isPublic) {
//            p.setpublic(true);
//        }
//        photos.save(p);
//
//        response.sendRedirect("/");
//        return p;
//    }
}
