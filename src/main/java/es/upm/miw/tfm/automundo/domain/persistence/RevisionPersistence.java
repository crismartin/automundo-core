package es.upm.miw.tfm.automundo.domain.persistence;

import es.upm.miw.tfm.automundo.domain.model.Revision;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RevisionPersistence {
    Flux<Revision> findAllByVehicleReference(String reference);

    Mono<Revision> create(Revision revision);
}
