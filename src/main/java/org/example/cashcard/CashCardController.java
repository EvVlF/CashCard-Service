package org.example.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.net.URI;
import java.security.Principal;
import java.util.List;
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
    /*
     principal.getName() will return the username provided from Basic Auth.
     */
    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
        Optional<CashCard> cashCardOptional = Optional.ofNullable(cashCardRepository.findByIdAndOwner(requestedId, principal.getName()));
        if (cashCardOptional.isPresent()) {
            return ResponseEntity.ok(cashCardOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /*
    createCashCard(@RequestBody CashCard newCashCardRequest, ...)
    the POST expects a request "body". This contains the data submitted to the API.
    Spring Web will deserialize the data into a CashCard for us.
     */
    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb, Principal principal) {
        CashCard cashCardWithOwner = new CashCard(null, newCashCardRequest.amount(), principal.getName());

        /*
        it saves a new CashCard for us, and returns the saved object with a unique id provided by the database
         */
        CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);

        /*
        This is constructing a URI to the newly created CashCard.
        This is the URI that the caller can then use to GET the newly-created CashCard.
        Note: savedCashCard.id is used as the identifier,
        which matches the GET endpoint's specification of cashcards/<CashCard.id>

        We were able to add UriComponentsBuilder ucb as a method argument to this POST handler method and
        it was automatically passed in with injected from Spring's IoC Container.
         */
        URI locationOfNewCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    /*
    Repository, CashCardRepository, will automatically return all CashCard records
    from the database when findAll() is invoked
     */
    @GetMapping
    /*
    findAll(Pageable pageable): Pageable is yet another object that Spring Web provides for us.
    Since we specified the URI parameters of page=0&size=1, pageable will contain the values we need.
    PageRequest is a basic Java Bean implementation of Pageable.
    Things that want paging and sorting implementation often support this, such as some types of Spring Data Repositories.
     */
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

    @PutMapping("/{requestedId}")
    //added the Principal as a method argument, provided automatically by Spring Security
    private ResponseEntity<Void> putCashCard(@PathVariable Long requestedId, @RequestBody CashCard cashCardUpdate, Principal principal) {
        /*scope retrieval of the CashCard to the submitted requestedId and Principal (provided by Spring Security)
        to ensure only the authenticated, authorized owner may update this CashCard
         */
        CashCard cashCard = cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
        if (cashCard != null) {
            //build a CashCard with updated values and save it
            CashCard updatedCashCard = new CashCard(cashCard.id(), cashCardUpdate.amount(), principal.getName());
            cashCardRepository.save(updatedCashCard);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}