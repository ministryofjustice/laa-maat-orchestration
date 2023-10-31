package uk.gov.justice.laa.maat.orchestration.controller;

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
import uk.gov.justice.laa.maat.orchestration.annotation.DefaultHTTPErrorResponse;
import uk.gov.justice.laa.maat.orchestration.dto.ApplicationDTO;
import uk.gov.justice.laa.maat.orchestration.dto.GetHardshipDTO;
import uk.gov.justice.laa.maat.orchestration.dto.HardshipReviewDTO;

import static uk.gov.justice.laa.crime.commons.common.Constants.LAA_TRANSACTION_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/orchestration/hardship")
@Tag(name = "Crime Hardship Orchestration", description = "Rest API for orchestration MAAT Hardship flows.")
public class HardshipOrchestrationController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Find Hardship review")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = HardshipReviewDTO.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<HardshipReviewDTO> find(
            @Parameter(description = "JSON object containing Hardship information",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GetHardshipDTO.class)
                    )
            ) @Valid @RequestBody GetHardshipDTO getHardshipDTO,
            @Parameter(description = "Used for tracing calls") @RequestHeader(value = LAA_TRANSACTION_ID, required = false) String laaTransactionId) {
        log.info("Received request to find hardship with transaction id - " + laaTransactionId);
        return ResponseEntity.ok(new HardshipReviewDTO());
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
            @Parameter(description = "JSON object containing Hardship information",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApplicationDTO.class)
                    )
            ) @Valid @RequestBody ApplicationDTO applicationDTO,
            @Parameter(description = "Used for tracing calls") @RequestHeader(value = LAA_TRANSACTION_ID, required = false) String laaTransactionId) {
        log.info("Received request to create hardship with transaction id - " + laaTransactionId);
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
            @Parameter(description = "JSON object containing Hardship information",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApplicationDTO.class)
                    )
            ) @Valid @RequestBody ApplicationDTO applicationDTO,
            @Parameter(description = "Used for tracing calls") @RequestHeader(value = LAA_TRANSACTION_ID, required = false) String laaTransactionId) {
        log.info("Received request to update hardship with transaction id - " + laaTransactionId);
        return ResponseEntity.ok(applicationDTO);
    }

}
