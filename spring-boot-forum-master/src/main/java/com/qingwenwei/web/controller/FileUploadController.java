package com.qingwenwei.web.controller;

import com.qingwenwei.persistence.model.User;
import com.qingwenwei.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cglib.proxy.CallbackGenerator;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * 参考：  https://blog.csdn.net/a625013/article/details/52414470
 */

@Controller
public class FileUploadController {

    @Autowired
    private UserService userService;

    Logger logger = LogManager.getLogger(FileUploadController.class);
//    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);
    @Value("${resource.staticResourceLocation}")
    public String ROOT;

    private final ResourceLoader resourceLoader;

    @Autowired
    public FileUploadController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/redirect")
    public String provideUploadInfo(Model model) throws IOException {

        model.addAttribute("files", Files.walk(Paths.get(ROOT))
                .filter(path -> !path.equals(Paths.get(ROOT)))
                .map(path -> Paths.get(ROOT).relativize(path))
              //  .map(path -> linkTo(methodOn(FileUploadController.class).getFile(path.toString())).withRel(path.toString()))
                .collect(Collectors.toList()));

        return "uploadForm";
    }
    //显示图片的方法关键 匹配路径像 localhost:8080/b7c76eb3-5a67-4d41-ae5c-1642af3f8746.png
    @RequestMapping(method = RequestMethod.GET, value = "/avatar/*/*.*")
    @ResponseBody
    public ResponseEntity<?> getFile() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        logger.debug(username);
        User byUsername = this.userService.findByUsername(username);
        String avatarLocation = byUsername.getAvatarLocation();
        logger.debug(avatarLocation);
         String[] split = avatarLocation.split("/");
        for (int i = 0; i < split.length-1; i++) {
            ROOT = ROOT + split[i];
        }

        String filename=split[split.length-1];
        logger.debug(ROOT);
        logger.debug(filename);
        try {
            return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(ROOT, filename).toString()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
//    //显示图片的方法关键 匹配路径像 localhost:8080/b7c76eb3-5a67-4d41-ae5c-1642af3f8746.png
//    @RequestMapping(method = RequestMethod.GET, value = "/avatar/*")
//    @ResponseBody
//    public ResponseEntity<?> getFile(@PathVariable String filename) {
//        logger.debug(filename);
//        String[] split = filename.split("/");
//        for (int i = 0; i < split.length-1; i++) {
//            ROOT = ROOT + split[i];
//        }
//        filename = split[split.length];
//        try {
//            return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(ROOT, filename).toString()));
//        } catch (Exception e) {
//            return ResponseEntity.notFound().build();
//        }
//    }

//    //显示图片的方法关键 匹配路径像 localhost:8080/b7c76eb3-5a67-4d41-ae5c-1642af3f8746.png
//    @RequestMapping(method = RequestMethod.GET, value = "/avatar/{filename:.+}")
//    @ResponseBody
//    public ResponseEntity<?> getFile(@PathVariable String filename) {
//
//        String[] split = filename.split("/");
//        for(String s:split)
//            try {
//                return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(ROOT, filename).toString()));
//            } catch (Exception e) {
//                return ResponseEntity.notFound().build();
//            }
//    }

    //上传的方法
    @RequestMapping(method = RequestMethod.POST, value = "/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes, HttpServletRequest request) {
//        System.out.println(request.getParameter("member"));
        if (!file.isEmpty()) {

            try {
                Files.copy(file.getInputStream(), Paths.get(ROOT, file.getOriginalFilename()));
                redirectAttributes.addFlashAttribute("message",
                        "You successfully uploaded " + file.getOriginalFilename() + "!");
            } catch (IOException|RuntimeException e) {
                redirectAttributes.addFlashAttribute("message", "Failued to upload " + file.getOriginalFilename() + " => " + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "Failed to upload " + file.getOriginalFilename() + " because it was empty");
        }

        return "redirect:/redirect";
    }

}