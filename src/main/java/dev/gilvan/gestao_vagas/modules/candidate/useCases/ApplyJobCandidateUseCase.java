package dev.gilvan.gestao_vagas.modules.candidate.useCases;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.gilvan.gestao_vagas.exceptions.JobNotFoundException;
import dev.gilvan.gestao_vagas.exceptions.UserNotFoundException;
import dev.gilvan.gestao_vagas.modules.candidate.CandidateRepository;
import dev.gilvan.gestao_vagas.modules.candidate.entities.ApplyJobEntity;
import dev.gilvan.gestao_vagas.modules.candidate.repository.ApplyJobRepository;
import dev.gilvan.gestao_vagas.modules.company.repositories.JobRepository;
import jakarta.validation.constraints.NotNull;

@Service
public class ApplyJobCandidateUseCase {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplyJobRepository applyJobRepository;

    public ApplyJobEntity execute(@NotNull UUID candidateId, @NotNull UUID jobId) {
        if (candidateId == null || jobId == null)
            throw new IllegalArgumentException();

        this.candidateRepository.findById(candidateId)
                .orElseThrow(() -> {
                    throw new UserNotFoundException();
                });

        this.jobRepository.findById(jobId).orElseThrow(
                () -> {
                    throw new JobNotFoundException();
                });

        var applyJob = ApplyJobEntity.builder().candidateId(candidateId).jobId(jobId).build();

        if (applyJob != null)
            applyJob = this.applyJobRepository.save(applyJob);

        return applyJob;

    }
}
