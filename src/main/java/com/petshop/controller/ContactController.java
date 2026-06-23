package com.petshop.controller;

import com.petshop.model.Contact;
import com.petshop.repository.ContactRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/contacts")
public class ContactController {
    private final ContactRepository contactRepository;

    public ContactController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Contact submitContact(@Valid @RequestBody Contact contact) {
        contact.setStatus("PENDING");
        return contactRepository.save(contact);
    }
}
