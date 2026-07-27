package com.smallstore.dashboard.controller;

import com.smallstore.dashboard.dto.BlogRequest;
import com.smallstore.dashboard.dto.ChatMessage;
import com.smallstore.dashboard.entity.Blog;
import com.smallstore.dashboard.service.BlogService;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.UUID;

@RestController
@Log4j2
//@CrossOrigin(origins = "https://smallstore.in/", allowedHeaders = "*", allowCredentials = "true")
@RequestMapping("/blogs")
public class HomeController {
    private final BlogService blogService;
    private static final String UPLOAD_DIR = "/home/ranjan/Desktop/smallstorebackend/smallstoredashboard/uploads/blogs/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".png");

    @Autowired
    public HomeController(BlogService blogService) {
        this.blogService = blogService;
    }

    @PostMapping
    public ResponseEntity<Blog> createBlog(@RequestBody BlogRequest request) {
        Blog blog = blogService.createBlog(request);


        return ResponseEntity.ok(blog);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Blog>> getAllBlog(){
        log.info("fetching all blog");
        List<Blog> blog = blogService.getAllBlogs();
        return ResponseEntity.ok(blog);
    }
    @GetMapping("/{id}/{title}")
    public ResponseEntity<Blog> getBlogById(@PathVariable("id") String id, @PathVariable("title") String title){
//        List<Blog> blog = blogService.getAllBlogs();
        log.info(id + "    "+title );
        if(id!=null && title!=null){
            Blog blog = blogService.findByBlogId(id);
            return ResponseEntity.ok(blog);
        }
        return ResponseEntity.ok(null);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImages(@RequestParam("files") List<MultipartFile> files) {
       // List<String> uploadedFilePaths = new ArrayList<>();
        List<String> uploadedFileName = new ArrayList<>();

        try {
            // Ensure the upload directory exists
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            for (MultipartFile file : files) {
                // Validate file size
                if (file.getSize() > MAX_FILE_SIZE) {
                    return ResponseEntity.badRequest().body("File " + file.getOriginalFilename() + " exceeds max size of 5MB");
                }

                // Validate file extension
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
                if (!ALLOWED_EXTENSIONS.contains(extension)) {
                    return ResponseEntity.badRequest().body("File " + originalFilename + " is not a valid image type. Only JPG and PNG are allowed.");
                }

                // Generate a unique filename
                String uniqueFilename = UUID.randomUUID().toString() + extension;

                // Save the file
                Path filePath = Paths.get(UPLOAD_DIR, uniqueFilename);
                Files.write(filePath, file.getBytes());

                // Store file path
              //  uploadedFilePaths.add("/" + UPLOAD_DIR + uniqueFilename);
                uploadedFileName.add(uniqueFilename);
            }

            // Return file paths
           // return ResponseEntity.ok(uploadedFilePaths);
            return ResponseEntity.ok(uploadedFileName);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error saving files: " + e.getMessage());
        }
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            log.info("getting image of > "+filename);
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @DeleteMapping("/{filename}")
    public ResponseEntity<?> deleteImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
            File file = filePath.toFile();

            if (file.exists()) {
                if (file.delete()) {
                    return ResponseEntity.ok("File deleted successfully: " + filename);
                } else {
                    return ResponseEntity.status(500).body("Failed to delete file: " + filename);
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting file: " + e.getMessage());
        }
    }

}
