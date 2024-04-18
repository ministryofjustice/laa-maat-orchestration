package uk.gov.justice.laa.crime.orchestration.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.justice.laa.crime.annotation.DefaultHTTPErrorResponse;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipReviewDTO;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.exception.MaatOrchestrationException;
import uk.gov.justice.laa.crime.orchestration.service.orchestration.HardshipOrchestrationService;

import static uk.gov.justice.laa.crime.commons.common.Constants.LAA_TRANSACTION_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/orchestration/hardship")
@Tag(name = "Crime Hardship Orchestration", description = "Rest API for orchestration MAAT Hardship flows.")
public class HardshipController {

    private final HardshipOrchestrationService orchestrationService;
    private static final int REQUEST_ROLLED_BACK = 555;

    @GetMapping(value = "/{hardshipReviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Find Hardship review")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = HardshipReviewDTO.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<HardshipReviewDTO> find(
            @PathVariable int hardshipReviewId,
            @Parameter(description = "Used for tracing calls") @RequestHeader(value = LAA_TRANSACTION_ID, required = false) String laaTransactionId) {
        log.info("Received request to find hardship with transaction id - " + laaTransactionId);
        return ResponseEntity.ok(orchestrationService.find(hardshipReviewId));
    }


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Create Hardship review")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApplicationDTO.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApplicationDTO> create(
            @Valid @RequestBody WorkflowRequest workflowRequest,
            @Parameter(description = "Used for tracing calls") @RequestHeader(value = LAA_TRANSACTION_ID, required = false) String laaTransactionId) {
        log.info("Received request to create hardship with transaction id - " + laaTransactionId);
        ApplicationDTO applicationDTO;
        try {
            applicationDTO = orchestrationService.create(workflowRequest);
        } catch (MaatOrchestrationException ex) {
            return ResponseEntity.status(REQUEST_ROLLED_BACK).body(ex.getApplicationDTO());
        }
        return ResponseEntity.ok(applicationDTO);
    }


    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Update Hardship review")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApplicationDTO.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApplicationDTO> update(
            @Valid @RequestBody WorkflowRequest workflowRequest,
            @Parameter(description = "Used for tracing calls") @RequestHeader(value = LAA_TRANSACTION_ID, required = false) String laaTransactionId) {
        log.info("Received request to update hardship with transaction id - " + laaTransactionId);
        ApplicationDTO applicationDTO;
        try {
            applicationDTO = orchestrationService.update(workflowRequest);
        } catch (MaatOrchestrationException ex) {
            return ResponseEntity.status(REQUEST_ROLLED_BACK).body(ex.getApplicationDTO());
        }
        return ResponseEntity.ok(applicationDTO);
    }

}
