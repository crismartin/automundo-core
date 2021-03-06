package es.upm.miw.tfm.automundo.infrastructure.api.resources;

import es.upm.miw.tfm.automundo.domain.model.Customer;
import es.upm.miw.tfm.automundo.domain.model.CustomerCreation;
import es.upm.miw.tfm.automundo.domain.model.CustomerUpdate;
import es.upm.miw.tfm.automundo.domain.services.CustomerService;
import es.upm.miw.tfm.automundo.infrastructure.api.dtos.CustomerLineDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(CustomerResource.CUSTOMERS)
public class CustomerResource {
    public static final String CUSTOMERS = "/customers";
    public static final String SEARCH = "/search";
    public static final String IDENTIFICATION_ID = "/{identification}";

    private CustomerService customerService;


    @Autowired
    public CustomerResource(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping(SEARCH)
    public Flux<CustomerLineDto> findByIdentificationIdAndNameAndSurNameAndSecondSurNameNullSafe(
            @RequestParam(required = false) String identificationId, @RequestParam(required = false) String name,
            @RequestParam(required = false) String surName, @RequestParam(required = false) String secondSurName) {
        return this.customerService.findByIdentificationIdAndNameAndSurNameAndSecondSurNameNullSafe(
                identificationId, name, surName, secondSurName)
                .map(CustomerLineDto::new);
    }

    @GetMapping(IDENTIFICATION_ID)
    public Mono<Customer> read(@PathVariable String identification) {
        return this.customerService.read(identification);
    }

    @PostMapping(produces = {"application/json"})
    public Mono<Customer> create(@Valid @RequestBody CustomerCreation customerCreation) {
        return this.customerService.create(customerCreation);
    }

    @PutMapping(IDENTIFICATION_ID)
    public Mono<Customer> update(@PathVariable String identification, @Valid @RequestBody CustomerUpdate customerUpdate) {
        return this.customerService.update(identification, customerUpdate);
    }

    @DeleteMapping(IDENTIFICATION_ID)
    public Mono<Void> delete(@PathVariable String identification) {
        return this.customerService.delete(identification);
    }

}
