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
import uk.gov.justice.laa.crime.orchestration.annotation.DefaultHTTPErrorResponse;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.service.MeansAssessmentOrchestrationService;

import static uk.gov.justice.laa.crime.commons.common.Constants.LAA_TRANSACTION_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/orchestration/cma")
@Tag(name = "Crime Means Assessment Orchestration", description = "Rest API for orchestrating Crime Means assessment flows.")
public class MeansAssessmentController {

    private final MeansAssessmentOrchestrationService assessmentOrchestrationService;

    @GetMapping(value = "/{financialAssessmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Find Crime Means Assessment")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FinancialAssessmentDTO.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<FinancialAssessmentDTO> find(
            @PathVariable int financialAssessmentId,
            @Parameter(description = "Used for tracing calls") @RequestHeader(value = LAA_TRANSACTION_ID, required = false) String laaTransactionId) {
        log.info("Received request to find crime means assessment with transaction id - {}", laaTransactionId);
        return ResponseEntity.ok(assessmentOrchestrationService.find(financialAssessmentId));
    }


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Create Crime Means Assessment")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApplicationDTO.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApplicationDTO> create(
            @Valid @RequestBody WorkflowRequest workflowRequest,
            @Parameter(description = "Used for tracing calls") @RequestHeader(value = LAA_TRANSACTION_ID, required = false) String laaTransactionId) {
        log.info("Received request to create hardship with transaction id - {}", laaTransactionId);
        return ResponseEntity.ok(assessmentOrchestrationService.create(workflowRequest));
    }


    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Update Crime Means Assessment")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApplicationDTO.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApplicationDTO> update(
            @Valid @RequestBody WorkflowRequest workflowRequest,
            @Parameter(description = "Used for tracing calls") @RequestHeader(value = LAA_TRANSACTION_ID, required = false) String laaTransactionId) {
        log.info("Received request to update hardship with transaction id - {}", laaTransactionId);
        return ResponseEntity.ok(assessmentOrchestrationService.update(workflowRequest));
    }

}
