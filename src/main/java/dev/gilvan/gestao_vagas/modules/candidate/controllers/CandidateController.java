package dev.gilvan.gestao_vagas.modules.candidate.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.gilvan.gestao_vagas.modules.candidate.CandidateEntity;
import dev.gilvan.gestao_vagas.modules.candidate.dto.CandidateProfileResponseDTO;
import dev.gilvan.gestao_vagas.modules.candidate.useCases.ApplyJobCandidateUseCase;
import dev.gilvan.gestao_vagas.modules.candidate.useCases.CandidateProfileUseCase;
import dev.gilvan.gestao_vagas.modules.candidate.useCases.CreateCandidateUseCase;
import dev.gilvan.gestao_vagas.modules.candidate.useCases.ListAllJobsByFilterUseCase;
import dev.gilvan.gestao_vagas.modules.company.entities.JobEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/candidate")
@Tag(name = "Candidate", description = "Candidate information")
public class CandidateController {

    @Autowired
    private CreateCandidateUseCase createCandidateUseCase;

    @Autowired
    private CandidateProfileUseCase candidateProfileUseCase;

    @Autowired
    private ListAllJobsByFilterUseCase listAllJobsByFilterUseCase;

    @Autowired
    private ApplyJobCandidateUseCase applyJobCandidateUseCase;

    @PostMapping("/")
    public ResponseEntity<Object> create(@Valid @RequestBody CandidateEntity candidate) {
        try {
            var result = this.createCandidateUseCase.execute(candidate);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping("/")
    @Operation(summary = "Get candidate profile information", description = "This function returns the candidate profile information")
    @SecurityRequirement(name = "jwt_auth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get candidate profile", content = {
                    @Content(schema = @Schema(implementation = CandidateProfileResponseDTO.class)),
            }),
            @ApiResponse(responseCode = "400", description = "User not found", content = {
                    @Content(schema = @Schema(implementation = String.class)),
            })
    })
    public ResponseEntity<Object> get(HttpServletRequest request) {
        var candidateId = request.getAttribute("candidate_id");

        try {
            var profile = this.candidateProfileUseCase
                    .execute(UUID.fromString(candidateId.toString()));
            return ResponseEntity.ok().body(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping("/job")
    @Operation(summary = "List all jobs by filter", description = "This function lists all available job positions by a string filter")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of jobs", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = JobEntity.class))),
            })
    })

    @SecurityRequirement(name = "jwt_auth")
    public List<JobEntity> findJobBySearchText(@RequestParam String filter) {

        return this.listAllJobsByFilterUseCase.execute(filter);

    }

    @PostMapping("/job/apply")
    @PreAuthorize("hasRole('CANDIDATE')")
    @SecurityRequirement(name = "jwt_auth")
    @Operation(summary = "Allow candidate to apply for a job", description = "This function allows a candidate to apply for a job")

    public ResponseEntity<Object> applyJob(HttpServletRequest request, @RequestBody UUID jobId) {

        var candidateId = request.getAttribute("candidate_id");

        try {
            var result = this.applyJobCandidateUseCase.execute(UUID.fromString(candidateId.toString()), jobId);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
