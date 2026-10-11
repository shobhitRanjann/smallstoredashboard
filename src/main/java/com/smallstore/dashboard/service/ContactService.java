package com.smallstore.dashboard.service;

import com.smallstore.dashboard.entity.Contactus;
import com.smallstore.dashboard.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


import java.util.Map;


@Service
public class ContactService {
    private final ContactRepository contactRepository;
    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final String toEmail;


    public ContactService(ContactRepository contactRepository,
                          JavaMailSender mailSender,
                          @Value("${contact.mail.from}") String fromEmail,
                          @Value("${contact.mail.to}") String toEmail) {
        this.contactRepository = contactRepository;
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.toEmail = toEmail;
    }


    public boolean submitContact(Map<String, Object> request){
        String name = (String) request.get("name");
        String email = (String) request.get("email");
        String phone = (String) request.get("phone");
        String projectType = (String) request.get("projectType");
        String message = (String) request.get("message");


        Contactus contactus = Contactus.builder()
                .name(name)
                .emailid(email)
                .phone(phone)
                .projectType(projectType)
                .comment(message)
                .build();
        try {
            contactRepository.save(contactus);
            sendContactEmail(contactus);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    private void sendContactEmail(Contactus contactus) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromEmail);
        mailMessage.setTo(toEmail);
        mailMessage.setReplyTo(contactus.getEmailid());
        mailMessage.setSubject("New contact form submission");
        mailMessage.setText(
                "Name: " + contactus.getName() + "\n"
                        + "Email: " + contactus.getEmailid() + "\n"
                        + "Phone: " + contactus.getPhone() + "\n"
                        + "Project type: " + contactus.getProjectType() + "\n"
                        + "Message: " + contactus.getComment()
        );
        mailSender.send(mailMessage);
    }


}
