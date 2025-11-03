package uk.gov.justice.laa.crime.orchestration.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.annotation.DefaultHTTPErrorResponse;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.service.orchestration.EvidenceOrchestrationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/internal/v1/orchestration/evidence")
@RequiredArgsConstructor
public class EvidenceController {

    private final EvidenceOrchestrationService evidenceOrchestrationService;

    @PutMapping(value = "/income", produces = APPLICATION_JSON_VALUE)
    @Operation(description = "Update Income Evidence for a means assessment")
    @ApiResponse(
            responseCode = "200",
            content =
                    @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApplicationDTO.class)))
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApplicationDTO> updateIncomeEvidence(@Valid @RequestBody WorkflowRequest workflowRequest) {
        log.info("Received a request to update Income Evidence");
        return ResponseEntity.ok(evidenceOrchestrationService.updateIncomeEvidence(workflowRequest));
    }
}
