package es.upm.miw.tfm.automundo.domain.persistence;

import es.upm.miw.tfm.automundo.domain.model.VehicleType;
import es.upm.miw.tfm.automundo.domain.model.VehicleTypeCreation;
import es.upm.miw.tfm.automundo.domain.model.VehicleTypeUpdate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface VehicleTypePersistence {
    Flux<VehicleType> findByReferenceAndNameAndDescriptionAndActiveNullSafe(
            String reference, String name, String description, Boolean active);

    Mono<VehicleType> findByReference(String reference);

    Mono<VehicleType> create(VehicleTypeCreation vehicleTypeCreation);

    Mono<VehicleType> update(String reference, VehicleTypeUpdate vehicleTypeUpdate);

    Flux<VehicleType> findAllActive();
}
