package es.upm.miw.tfm.automundo.infrastructure.api.resources;

import es.upm.miw.tfm.automundo.domain.model.Revision;
import es.upm.miw.tfm.automundo.domain.services.RevisionService;
import es.upm.miw.tfm.automundo.infrastructure.api.dtos.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;


@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(RevisionResource.REVISIONS)
public class RevisionResource {
    public static final String REVISIONS = "/revisions";
    public static final String VEHICLE_REFERENCE = "/vehicle/{reference}";
    public static final String REPLACEMENTS_USED = "/replacements-used";
    public static final String REFERENCE = "/{reference}";
    public static final String PRINT = "/print";

    private RevisionService revisionService;

    @Autowired
    public RevisionResource(RevisionService revisionService){
        this.revisionService = revisionService;
    }

    @GetMapping(VEHICLE_REFERENCE)
    public Flux<RevisionLineDto> findAllByVehicleReference(@PathVariable String reference) {
        return this.revisionService.findAllByVehicleReference(reference)
                .map(RevisionLineDto::new);
    }

    @PostMapping(produces = {"application/json"})
    public Mono<Revision> create(@Valid @RequestBody RevisionNewDto revisionCreate){
        Revision revision = new Revision(revisionCreate);
        return this.revisionService.create(revision);
    }

    @PostMapping(path = REPLACEMENTS_USED, produces = {"application/json"})
    public Mono<Revision> createReplacementsUsed(@Valid @RequestBody ReplacementsUsedNewDto replacementsUsed) {
        Revision revision = Revision.builder()
                .reference(replacementsUsed.getRevisionReference())
                .replacementsUsed(replacementsUsed.getReplacementsUsed())
                .build();
        return revisionService.createReplacementsUsed(revision);
    }

    @GetMapping(REFERENCE)
    public Mono<Revision> findByReference(@PathVariable String reference) {
        return revisionService.findByReference(reference);
    }

    @PutMapping()
    public Mono<Revision> update(@Valid @RequestBody RevisionUpdateDto revisionUpdate) {
        Revision revision = new Revision(revisionUpdate);
        return revisionService.update(revision);
    }

    @GetMapping(value = PRINT, produces = {"application/pdf"})
    public Mono<byte[]> printByReference(@RequestParam String reference) {
        return this.revisionService.printByReference(reference);
    }

    @DeleteMapping(REFERENCE)
    public Mono<Void> deleteLogic(@PathVariable String reference) {
        return this.revisionService.deleteLogic(reference);
    }
}
