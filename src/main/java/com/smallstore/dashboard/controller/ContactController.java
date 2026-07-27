package com.smallstore.dashboard.controller;

import com.smallstore.dashboard.dto.ChatMessage;
import com.smallstore.dashboard.service.ContactService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/blogs/api")
@Log4j2
public class ContactController {


    // @Value("${google.recaptcha.secret}")
    private String recaptchaSecret = "6LeNRwsrAAAAAJ62XtXBRacX0c2LojohYAFjnjIV";


    private final ContactService contactService;


    @Autowired
    private ContactController(ContactService contactService){
        this.contactService = contactService;
    }


    @PostMapping("/contact")
    public ResponseEntity<?> handleContactForm(@RequestBody Map<String, Object> request) {
        String captchaToken = (String) request.get("captchaToken");


        if (captchaToken == null || captchaToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing captcha token");
        }


        RestTemplate restTemplate = new RestTemplate();
        String verifyUrl = "https://www.google.com/recaptcha/api/siteverify";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", recaptchaSecret);
        body.add("response", captchaToken);


        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body,headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(verifyUrl, requestEntity, Map.class);




        Map<String, Object> responseBody = response.getBody();
        boolean success = (boolean) responseBody.get("success");
        boolean savedata = contactService.submitContact(request);
        if (success && savedata) {
            // Proceed with form handling
            return ResponseEntity.ok(Collections.singletonMap("message", "Success"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "failed"));
        }
    }

}




