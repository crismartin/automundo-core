package es.upm.miw.tfm.automundo.domain.services;

import es.upm.miw.tfm.automundo.domain.model.Vehicle;
import es.upm.miw.tfm.automundo.domain.persistence.RevisionPersistence;
import es.upm.miw.tfm.automundo.domain.persistence.VehiclePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class VehicleService {

    private VehiclePersistence vehiclePersistence;
    private RevisionPersistence revisionPersistence;

    @Autowired
    public VehicleService(VehiclePersistence vehiclePersistence, RevisionPersistence revisionPersistence){
        this.vehiclePersistence = vehiclePersistence;
        this.revisionPersistence = revisionPersistence;
    }

    public Flux<Vehicle> findVehiclesByIdCustomer(String identificationId) {
        return vehiclePersistence.findVehiclesByIdCustomer(identificationId);
    }

    public Mono<Vehicle> findByReference(String reference) {
        return vehiclePersistence.findByReference(reference);
    }

    public Mono<Vehicle> create(Vehicle vehicle) {
        return vehiclePersistence.create(vehicle);
    }

    public Mono<Vehicle> update(Vehicle vehicle) {
        return vehiclePersistence.update(vehicle);
    }

    public Mono<Void> deleteLogic(String reference) {
        return vehiclePersistence.deleteLogic(reference)
                .then(revisionPersistence.deleteByVehicleReference(reference));
    }

    public Flux<Vehicle> findByPlateAndBinAndCustomerNullSafe(Vehicle filterParams) {
        return vehiclePersistence.findByPlateAndBinAndCustomerNullSafe(filterParams);
    }
}
