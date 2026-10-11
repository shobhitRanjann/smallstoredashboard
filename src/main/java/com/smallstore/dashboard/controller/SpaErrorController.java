package com.smallstore.dashboard.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.core.io.ResourceLoader;

@Controller
public class SpaErrorController implements ErrorController {

    private final ResourceLoader resourceLoader;

    public SpaErrorController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                String originalUri = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
                
                if (originalUri != null && !originalUri.equals("/") && !originalUri.equals("/error")) {
                    String candidatePath = originalUri.endsWith("/") ? originalUri + "index.html" : originalUri + "/index.html";
                    if (resourceLoader.getResource("classpath:/static" + candidatePath).exists()) {
                        response.setStatus(HttpStatus.OK.value());
                        return "forward:" + candidatePath;
                    }
                    
                    // Handle Next.js Dynamic Routes mapped to 'dummy'
                    if (originalUri.matches("^/tambola/player/[^/]+/?$")) {
                        response.setStatus(HttpStatus.OK.value());
                        return "forward:/tambola/player/dummy/index.html";
                    } else if (originalUri.matches("^/tambola/[^/]+/?$") && !originalUri.startsWith("/tambola/login")) {
                        response.setStatus(HttpStatus.OK.value());
                        return "forward:/tambola/dummy/index.html";
                    } else if (originalUri.matches("^/blog/[^/]+/[^/]+/?$")) {
                        response.setStatus(HttpStatus.OK.value());
                        return "forward:/blog/dummy/dummy/index.html";
                    } else if (originalUri.matches("^/blogw/[^/]+/[^/]+/?$")) {
                        response.setStatus(HttpStatus.OK.value());
                        return "forward:/blogw/dummy/dummy/index.html";
                    } else if (originalUri.matches("^/resume/[^/]+/?$")) {
                        response.setStatus(HttpStatus.OK.value());
                        return "forward:/resume/dummy/index.html";
                    } else if (originalUri.matches("^/storeside/[^/]+/[^/]+/?$")) {
                        response.setStatus(HttpStatus.OK.value());
                        return "forward:/storeside/dummy/dummy/index.html";
                    }
                }
                
                response.setStatus(HttpStatus.OK.value());
                return "forward:/index.html";
            }
        }
        
        return "error";
    }
}
