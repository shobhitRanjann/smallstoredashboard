package com.smallstore.dashboard.service;

import com.smallstore.dashboard.entity.Contactus;
import com.smallstore.dashboard.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Map;


@Service
public class ContactService {
    private final ContactRepository contactRepository;


    @Autowired
    public ContactService(ContactRepository contactRepository){
        this.contactRepository = contactRepository;
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
        }catch (Exception e){
            return false;
        }
        return true;
    }


}
