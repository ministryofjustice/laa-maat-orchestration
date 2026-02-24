package uk.gov.justice.laa.crime.orchestration.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.crime.annotation.DefaultHTTPErrorResponse;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.service.orchestration.PassportAssessmentOrchestrationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/orchestration/passport")
@Tag(name = "Passport Assessment Orchestration", description = "Rest API for orchestrating Passport Assessment flows.")
public class PassportAssessmentController {

    private final PassportAssessmentOrchestrationService orchestrationService;

    @GetMapping(value = "/{assessmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Find Passport Assessment")
    @ApiResponse(
            responseCode = "200",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PassportedDTO.class)))
    @DefaultHTTPErrorResponse
    public ResponseEntity<PassportedDTO> find(@PathVariable int assessmentId) {
        return ResponseEntity.ok(orchestrationService.find(assessmentId));
    }

}
