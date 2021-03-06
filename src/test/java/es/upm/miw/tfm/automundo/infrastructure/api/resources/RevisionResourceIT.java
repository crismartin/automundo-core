package es.upm.miw.tfm.automundo.infrastructure.api.resources;

import es.upm.miw.tfm.automundo.domain.exceptions.NotFoundException;
import es.upm.miw.tfm.automundo.domain.model.Revision;
import es.upm.miw.tfm.automundo.infrastructure.api.RestClientTestService;
import es.upm.miw.tfm.automundo.domain.model.*;
import es.upm.miw.tfm.automundo.infrastructure.api.dtos.*;
import es.upm.miw.tfm.automundo.infrastructure.enums.StatusRevision;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static es.upm.miw.tfm.automundo.infrastructure.api.resources.RevisionResource.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RestTestConfig
class RevisionResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testFindVehiclesByIdCustomerOk() {
        String reference = "ref-1001";
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(REVISIONS + VEHICLE_REFERENCE, reference)
                .exchange()
                .expectBodyList(RevisionLineDto.class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testFindVehiclesByIdCustomerUnknow() {
        String reference = "ref-unknow-rest";
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(REVISIONS + VEHICLE_REFERENCE, reference)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void testCreateOk() {
        TechnicianLineDto technician = TechnicianLineDto.builder()
                .identificationId("11111111-T")
                .build();

        RevisionNewDto revisionNewDto = RevisionNewDto.builder()
                .vehicleReference("ref-2002")
                .diagnostic("DIAGNOSTIC RESOURCE")
                .registerDate(LocalDateTime.now())
                .initialKilometers(1000)
                .workedHours(10)
                .workDescription("DESCRIPTION RESOURCE TEST")
                .technician(technician)
                .build();

        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(REVISIONS)
                .body(Mono.just(revisionNewDto), RevisionNewDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Revision.class)
                .value(Assertions::assertNotNull)
                .value(revisionCreated -> {
                    assertNotNull(revisionCreated);
                    assertNotNull(revisionCreated.getReference());

                    assertEquals(StatusRevision.POR_CONFIRMAR, revisionCreated.getStatus());
                    assertEquals(revisionCreated.getTechnicianIdentification(), revisionNewDto.getTechnicianIdentification());
                    assertEquals(revisionCreated.getVehicleReference(), revisionNewDto.getVehicleReference());
                }).returnResult().getResponseBody();
    }

    @Test
    void testCreateErrorByTechnicianUnknown() {
        TechnicianLineDto technician = TechnicianLineDto.builder()
                .identificationId("TECHNICIAN_UNKNOWN")
                .build();

        RevisionNewDto revisionNewDto = RevisionNewDto.builder()
                .vehicleReference("ref-2002")
                .diagnostic("DIAGNOSTIC RESOURCE")
                .registerDate(LocalDateTime.now())
                .initialKilometers(1000)
                .workedHours(10)
                .workDescription("DESCRIPTION RESOURCE TEST")
                .technician(technician)
                .build();

        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(REVISIONS)
                .body(Mono.just(revisionNewDto), RevisionNewDto.class)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void testCreateErrorByVehicleUnknown() {
        TechnicianLineDto technician = TechnicianLineDto.builder()
                .identificationId("11111111-T")
                .build();

        RevisionNewDto revisionNewDto = RevisionNewDto.builder()
                .vehicleReference("VEHICLE_REF_UNKNOWN")
                .diagnostic("DIAGNOSTIC RESOURCE")
                .registerDate(LocalDateTime.now())
                .initialKilometers(1000)
                .workedHours(10)
                .workDescription("DESCRIPTION RESOURCE TEST")
                .technician(technician)
                .build();

        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(REVISIONS)
                .body(Mono.just(revisionNewDto), RevisionNewDto.class)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    private void compareReplacements(ReplacementUsed replacementUsedExpected, ReplacementUsed replacementUsedActual){
        assertEquals(replacementUsedExpected.getDiscount(), replacementUsedActual.getDiscount());
        assertEquals(replacementUsedExpected.getQuantity(), replacementUsedActual.getQuantity());
        assertEquals(replacementUsedExpected.getOwn(), replacementUsedActual.getOwn());
        assertEquals(replacementUsedExpected.getPrice(), replacementUsedActual.getPrice());
        assertEquals(replacementUsedExpected.getDiscount(), replacementUsedActual.getDiscount());
    }

    @Test
    void testCreateReplacementsOk() {
        BigDecimal priceReplacementA = BigDecimal.valueOf(20.00);
        BigDecimal priceReplacementB = BigDecimal.valueOf(20.00);

        ReplacementUsed[] replacementsUsed = {
                ReplacementUsed.builder()
                        .quantity(2).discount(20).own(false).price(priceReplacementA)
                        .replacement(Replacement.builder().reference("11111111").build())
                        .build(),
                ReplacementUsed.builder()
                        .quantity(2).discount(20).own(false).price(priceReplacementB)
                        .replacement(Replacement.builder().reference("22222222").build())
                        .build()
        };

        ReplacementsUsedNewDto replacementsUsedNewDto = ReplacementsUsedNewDto.builder()
                .revisionReference("rev-2").replacementsUsed(List.of(replacementsUsed))
                .build();

        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(REVISIONS + REPLACEMENTS_USED)
                .body(Mono.just(replacementsUsedNewDto), ReplacementsUsedNewDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Revision.class)
                .value(Assertions::assertNotNull)
                .value(revisionUpdated -> {
                    assertNotNull(revisionUpdated.getReference());
                    assertNotNull(revisionUpdated.getReplacementsUsed());

                    assertEquals(replacementsUsedNewDto.getReplacementsUsed().size(), revisionUpdated.getReplacementsUsed().size());

                    for(int i = 0 ; i < revisionUpdated.getReplacementsUsed().size(); i++){
                        assertNotNull(revisionUpdated.getReplacementsUsed().get(i).getReference());
                        compareReplacements(replacementsUsedNewDto.getReplacementsUsed().get(i), revisionUpdated.getReplacementsUsed().get(i));
                    }
                    assertEquals(priceReplacementA.add(priceReplacementB), revisionUpdated.getCost());
                });
    }

    @Test
    void testCreateReplacementsErrorByRevisionUnknow() {
        ReplacementUsed[] replacementsUsed = {
                ReplacementUsed.builder()
                        .quantity(2).discount(20).own(false).price(BigDecimal.valueOf(20.00))
                        .replacement(Replacement.builder().reference("11111111").build())
                        .build(),
                ReplacementUsed.builder()
                        .quantity(2).discount(20).own(false).price(BigDecimal.valueOf(20.00))
                        .replacement(Replacement.builder().reference("22222222").build())
                        .build()
        };

        ReplacementsUsedNewDto replacementsUsedNewDto = ReplacementsUsedNewDto.builder()
                .revisionReference("rev-unknown").replacementsUsed(List.of(replacementsUsed))
                .build();

        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(REVISIONS + REPLACEMENTS_USED)
                .body(Mono.just(replacementsUsedNewDto), ReplacementsUsedNewDto.class)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void testCreateReplacementsErrorByReplacementUnknow() {
        ReplacementUsed[] replacementsUsed = {
                ReplacementUsed.builder()
                        .quantity(2).discount(20).own(false).price(BigDecimal.valueOf(20.00))
                        .replacement(Replacement.builder().reference("ref-unknown").build())
                        .build(),
                ReplacementUsed.builder()
                        .quantity(2).discount(20).own(false).price(BigDecimal.valueOf(20.00))
                        .replacement(Replacement.builder().reference("22222222").build())
                        .build()
        };

        ReplacementsUsedNewDto replacementsUsedNewDto = ReplacementsUsedNewDto.builder()
                .revisionReference("rev-2").replacementsUsed(List.of(replacementsUsed))
                .build();

        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(REVISIONS + REPLACEMENTS_USED)
                .body(Mono.just(replacementsUsedNewDto), ReplacementsUsedNewDto.class)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void testFindRevisionByReferenceOk() {
        String reference = "rev-1";
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(REVISIONS + REFERENCE, reference)
                .exchange()
                .expectBody(Revision.class)
                .value(Assertions::assertNotNull)
                .value(revision -> {
                    assertNotNull(revision);
                    assertNotNull(revision.getReference());

                    assertEquals(reference, revision.getReference());
                }).returnResult().getResponseBody();
    }

    @Test
    void testFindRevisionErrorByReferenceUnknown() {
        String reference = "rev-unknown";
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(REVISIONS + REFERENCE, reference)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void testUpdateOk() {
        Technician technician = Technician.builder()
                .identificationId("11111111-T")
                .build();

        RevisionUpdateDto revisionUpdateDto = RevisionUpdateDto.builder()
                .reference("rev-1")
                .diagnostic("DIAGNOSTIC RESOURCE")
                .registerDate(LocalDateTime.now())
                .initialKilometers(1000)
                .workedHours(10)
                .workDescription("DESCRIPTION RESOURCE TEST")
                .technician(technician)
                .status(StatusRevision.POR_CONFIRMAR)
                .build();

        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(REVISIONS)
                .body(Mono.just(revisionUpdateDto), RevisionUpdateDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Revision.class)
                .value(Assertions::assertNotNull)
                .value(revisionCreated -> {
                    assertNotNull(revisionCreated);
                    assertNotNull(revisionCreated.getReference());

                    assertEquals(StatusRevision.POR_CONFIRMAR, revisionCreated.getStatus());
                    assertEquals(revisionCreated.getTechnicianIdentification(), revisionUpdateDto.getTechnicianIdentification());
                }).returnResult().getResponseBody();
    }

    @Test
    void testUpdateErrorByReferenceUnknown() {
        Technician technician = Technician.builder()
                .identificationId("11111111-T")
                .build();

        RevisionUpdateDto revisionUpdateDto = RevisionUpdateDto.builder()
                .reference("rev-unknown")
                .diagnostic("DIAGNOSTIC RESOURCE")
                .registerDate(LocalDateTime.now())
                .initialKilometers(1000)
                .workedHours(10)
                .workDescription("DESCRIPTION RESOURCE TEST")
                .technician(technician)
                .status(StatusRevision.POR_CONFIRMAR)
                .build();

        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(REVISIONS)
                .body(Mono.just(revisionUpdateDto), RevisionUpdateDto.class)
                .exchange()
                .expectStatus().is4xxClientError();
    }


    @Test
    void testPrintByReference() {
        String revisionReference = "rev-1";
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(REVISIONS + PRINT)
                        .queryParam("reference", revisionReference)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testPrintByReferenceErrorByReferenceUnknown() {
        String revisionReference = "invc_N_NOTEXIST";
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(REVISIONS + PRINT)
                        .queryParam("reference", revisionReference)
                        .build())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(NotFoundException.class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testDeleteLogicRevisionOk() {
        String reference = "rev-1";
        this.restClientTestService.loginAdmin(webTestClient)
                .delete()
                .uri(REVISIONS + REFERENCE, reference)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void testDeleteLogicRevisionErrorByReferenceUnknown() {
        String reference = "rev-unknown";
        this.restClientTestService.loginAdmin(webTestClient)
                .delete()
                .uri(REVISIONS + REFERENCE, reference)
                .exchange()
                .expectStatus().is4xxClientError();
    }
}
