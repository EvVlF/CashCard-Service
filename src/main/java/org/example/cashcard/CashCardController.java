package org.example.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/*
 @RestController
This tells Spring that this class is a Component of type RestController and capable of handling HTTP requests.

 @RequestMapping("/cashcards")
This is a companion to @RestController that indicates which address requests must have to access this Controller.
 */
@RestController
@RequestMapping("/cashcards")
class CashCardController {
    private final CashCardRepository cashCardRepository;

    private CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    /*
    @GetMapping marks a method as a handler method.
    GET requests that match cashcards/{requestedID} will be handled by this method.
     */
    /*
    CrudRepository.findById, which returns an Optional. Might or might not contain the CashCard
     */
    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);
        if (cashCardOptional.isPresent()) {
            return ResponseEntity.ok(cashCardOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}