package org.example.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 @RestController
This tells Spring that this class is a Component of type RestController and capable of handling HTTP requests.

 @RequestMapping("/cashcards")
This is a companion to @RestController that indicates which address requests must have to access this Controller.
 */
@RestController
@RequestMapping("/cashcards")
class CashCardController {

    /*
    @GetMapping marks a method as a handler method.
    GET requests that match cashcards/{requestedID} will be handled by this method.
     */
    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById() {
        CashCard cashCard = new CashCard(1000L, 0.0);
        return ResponseEntity.ok(cashCard);
    }
}