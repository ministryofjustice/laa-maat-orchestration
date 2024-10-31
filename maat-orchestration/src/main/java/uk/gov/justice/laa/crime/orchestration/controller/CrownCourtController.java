package uk.gov.justice.laa.crime.orchestration.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.crime.annotation.DefaultHTTPErrorResponse;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.service.orchestration.CrownCourtOrchestrationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/orchestration/crown-court")
@Tag(name = "Crown Court Orchestration", description = "API for orchestration of MAAT Crown Court flows.")
public class CrownCourtController {

    private final CrownCourtOrchestrationService orchestrationService;

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Update Crown Court")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiUpdateApplicationRequest.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApplicationDTO> updateCrownCourt(
            @Valid @RequestBody WorkflowRequest workflowRequest) {
        log.info("Received request to update crown court");
        return ResponseEntity.ok(orchestrationService.update(workflowRequest));
    }
}
