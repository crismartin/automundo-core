package es.upm.miw.tfm.automundo.infrastructure.mongodb.persistence;

import es.upm.miw.tfm.automundo.domain.exceptions.ConflictException;
import es.upm.miw.tfm.automundo.domain.exceptions.NotFoundException;
import es.upm.miw.tfm.automundo.domain.model.Customer;
import es.upm.miw.tfm.automundo.domain.model.CustomerCreation;
import es.upm.miw.tfm.automundo.domain.model.CustomerUpdate;
import es.upm.miw.tfm.automundo.domain.persistence.CustomerPersistence;
import es.upm.miw.tfm.automundo.infrastructure.mongodb.daos.CustomerReactive;
import es.upm.miw.tfm.automundo.infrastructure.mongodb.entities.CustomerEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public class CustomerPersistenceMongodb implements CustomerPersistence {

    private CustomerReactive customerReactive;

    @Autowired
    public CustomerPersistenceMongodb(CustomerReactive customerReactive) {
        this.customerReactive = customerReactive;
    }

    @Override
    public Flux<Customer> findByIdentificationIdAndNameAndSurNameAndSecondSurNameNullSafe(String identificationId, String name, String surName, String secondSurName) {
        return this.customerReactive.findByIdentificationidAndNameAndSurnameAndSecondsurnameNullSafe(identificationId, name, surName, secondSurName)
                .filter(customerEntity -> customerEntity.getLeaveDate() == null)
                .map(CustomerEntity::toCustomer);
    }

    @Override
    public Mono<Customer> findByIdentificationId(String identification) {
        return this.findByIdentificationIdEntity(identification)
                    .map(CustomerEntity::toCustomer);
    }

    private Mono<CustomerEntity> findByIdentificationIdEntity(String identification) {
        return this.customerReactive.findByIdentificationId(identification)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent customer with identification id: " + identification)));
    }

    @Override
    public Mono<Customer> create(CustomerCreation customerCreation) {
        return this.assertIdentificationIdNotExist(customerCreation.getIdentificationId())
                .then(Mono.just(new CustomerEntity(customerCreation)))
                .flatMap(this.customerReactive::save)
                .map(CustomerEntity::toCustomer);
    }

    @Override
    public Mono<Customer> update(String identification, CustomerUpdate customerUpdate) {
        return this.customerReactive.findByIdentificationId(identification)
                .switchIfEmpty(Mono.error(new NotFoundException("Cannot update. Non existent customer " +
                        "with identification id: " + identification)))
                .map(updatingCustomer -> {
                    BeanUtils.copyProperties(customerUpdate, updatingCustomer);
                    return updatingCustomer;
                }).flatMap(this.customerReactive::save)
                .map(CustomerEntity::toCustomer);
    }

    @Override
    public Mono<Void> deleteLogic(String identification) {
        return this.findByIdentificationIdEntity(identification)
                .flatMap(customerEntity -> {
                    customerEntity.setLeaveDate(LocalDateTime.now());
                    return customerReactive.save(customerEntity);
                })
                .then();
    }

    private Mono<Void> assertIdentificationIdNotExist(String identificationId) {
        return this.customerReactive.findByIdentificationId(identificationId)
                .flatMap(customerEntity -> Mono.error(
                        new ConflictException("Customer identification id already exists : " + identificationId)
                ));
    }
}
