package dev.gilvan.gestao_vagas.modules.candidate.useCases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.gilvan.gestao_vagas.exceptions.JobNotFoundException;
import dev.gilvan.gestao_vagas.exceptions.UserNotFoundException;
import dev.gilvan.gestao_vagas.modules.candidate.CandidateEntity;
import dev.gilvan.gestao_vagas.modules.candidate.CandidateRepository;
import dev.gilvan.gestao_vagas.modules.candidate.entities.ApplyJobEntity;
import dev.gilvan.gestao_vagas.modules.candidate.repository.ApplyJobRepository;
import dev.gilvan.gestao_vagas.modules.company.entities.JobEntity;
import dev.gilvan.gestao_vagas.modules.company.repositories.JobRepository;

@ExtendWith(MockitoExtension.class)
public class ApplyJobCandidateUseCaseTest {

    @InjectMocks
    ApplyJobCandidateUseCase applyJobCandidateUseCase;

    @Mock
    CandidateRepository candidateRepository;

    @Mock
    JobRepository jobRepository;

    @Mock
    ApplyJobRepository applyJobRepository;

    UUID candidateId = UUID.randomUUID();
    UUID jobId = UUID.randomUUID();

    CandidateEntity candidate = new CandidateEntity();
    JobEntity job = new JobEntity();

    @BeforeEach
    public void setUp() {

        candidate.setId(candidateId);

        job.setId(jobId);

    }

    @Test
    @DisplayName("should not be able to apply job if candidate not found")
    public void should_not_be_able_to_apply_job_if_candidate_not_found() {

        try {
            applyJobCandidateUseCase
                    .execute(candidateId, jobId);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(UserNotFoundException.class);
        }

    }

    @Test
    @DisplayName("should not be able to apply job if job not found")
    public void should_not_be_able_to_apply_job_if_job_not_found() {

        if (candidateId != null)
            when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        try {
            applyJobCandidateUseCase
                    .execute(candidateId, jobId);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(JobNotFoundException.class);
        }

    }

    @Test
    @DisplayName("should be able to apply for a job")
    public void should_be_able_to_apply_for_a_job() {

        var applyJob = ApplyJobEntity
                .builder()
                .candidateId(candidateId).jobId(jobId)
                .build();

        var applyJobCreated = ApplyJobEntity.builder().id(UUID.randomUUID()).build();

        if (candidateId != null)
            when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(new CandidateEntity()));

        if (jobId != null)
            when(jobRepository.findById(jobId)).thenReturn(Optional.of(new JobEntity()));

        if (applyJob != null)
            when(applyJobRepository.save(applyJob)).thenReturn(applyJobCreated);

        var result = applyJobCandidateUseCase.execute(candidateId, jobId);

        assertThat(result).hasFieldOrProperty("id");
        assertNotNull(result.getId());

    }

}
