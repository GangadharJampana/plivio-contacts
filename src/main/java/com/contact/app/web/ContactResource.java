package com.contact.app.web;

import com.contact.app.domain.Contact;
import com.contact.app.repo.ContactRepository;
import com.contact.app.specification.ContactSpecification;
import com.contact.app.specification.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
public class ContactResource {

    @Autowired
    private ContactRepository contactRepository;

    @GetMapping(value = "/")
    public ResponseEntity index(@RequestParam(value = "page", required = false) Integer page,
                                @RequestParam(value = "size", required = false) Integer size) {
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page.intValue(), size.intValue());
        return ResponseEntity.ok(contactRepository.findAll(pageable));
    }

    @GetMapping(value = "/{search}")
    public ResponseEntity getContact(@PathVariable(value = "search") String search,
                                     @RequestParam(value = "page", required = false) Integer page,
                                     @RequestParam(value = "size", required = false) Integer size) {

        if (search == null || search.isEmpty()) {
            return ResponseEntity.badRequest().body("Search String is empty");
        }
        ContactSpecification nameSpec = new ContactSpecification(new SearchCriteria("name", ":", search));
        ContactSpecification emailSpec = new ContactSpecification(new SearchCriteria("email", ":", search));
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page.intValue(), size.intValue());
        Page<Contact> results = contactRepository.findAll(Specification.where(nameSpec).or(emailSpec), pageable);
        return ResponseEntity.ok(results);
    }

    @PostMapping(value = "/")
    public ResponseEntity addToContactList(@Valid @RequestBody Contact contact) {
        if (contact.getEmail() == null) {
            return ResponseEntity.badRequest().body("email is required");
        }
        Optional<Contact> foundContact = contactRepository.findOneByEmail(contact.getEmail());
        if (foundContact.isPresent()) {
            return ResponseEntity.badRequest().body("contact already exits for this  email : " + contact.getEmail());
        }
        return ResponseEntity.ok(contactRepository.save(contact));
    }


    @PutMapping(value = "/")
    public ResponseEntity updateContactList(@Valid @RequestBody Contact contact) {
        if (contact.getId() == null) {
            return addToContactList(contact);
        }
        Optional<Contact> foundContact = contactRepository.findOneByEmail(contact.getEmail());
        if (!foundContact.isPresent()) {
            return ResponseEntity.badRequest().body("contact with this  email : " + contact.getEmail() + "doesn't exist");
        } else if (contact.getId().equals(foundContact.get().getId())) {
            return ResponseEntity.ok(contactRepository.save(contact));
        }
        return ResponseEntity.badRequest().body("No contact exists  with this details. Please try with correct ones");

    }

    @DeleteMapping(value = "/")
    public ResponseEntity removeContact(@RequestParam(value = "id") Long id) {
        Contact contact = contactRepository.getOne(id);
        contactRepository.delete(contact);
        return ResponseEntity.noContent().build();
    }
}
