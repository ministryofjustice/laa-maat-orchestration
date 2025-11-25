package uk.gov.justice.laa.crime.orchestration.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.justice.laa.crime.annotation.DefaultHTTPErrorResponse;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;
import uk.gov.justice.laa.crime.orchestration.service.orchestration.IojAppealsOrchestrationService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/orchestration/ioj-appeals")
@Tag(name = "IoJ Appeal Orchestration", description = "Rest API for orchestrating IoJ Appeal flows.")
public class IojAppealController {

    private final IojAppealsOrchestrationService orchestrationService;

    @GetMapping(value = "/{appealId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Find IoJ Appeal")
    @ApiResponse(
            responseCode = "200",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = IOJAppealDTO.class)))
    @DefaultHTTPErrorResponse
    public ResponseEntity<IOJAppealDTO> find(@PathVariable int appealId) {
        log.info("Received request to find IoJ appeal by appeal ID - {}", appealId);
        return ResponseEntity.ok(orchestrationService.find(appealId));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Create IoJ Appeal")
    @ApiResponse(
        responseCode = "200",
        content =
        @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ApplicationDTO.class)))
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApplicationDTO> create(@Valid @RequestBody WorkflowRequest workflowRequest) {
        return ResponseEntity.ok(orchestrationService.create(workflowRequest));
    }
}
