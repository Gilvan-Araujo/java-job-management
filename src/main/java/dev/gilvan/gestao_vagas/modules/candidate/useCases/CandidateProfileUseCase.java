package dev.gilvan.gestao_vagas.modules.candidate.useCases;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.gilvan.gestao_vagas.exceptions.UserNotFoundException;
import dev.gilvan.gestao_vagas.modules.candidate.CandidateRepository;
import dev.gilvan.gestao_vagas.modules.candidate.dto.CandidateProfileResponseDTO;
import lombok.NonNull;

@Service
public class CandidateProfileUseCase {

    @Autowired
    private CandidateRepository candidateRepository;

    public CandidateProfileResponseDTO execute(@NonNull UUID candidateId) {
        var candidate = this.candidateRepository.findById(candidateId)
                .orElseThrow(() -> new UserNotFoundException());

        var candidateResponse = CandidateProfileResponseDTO.builder()
                .name(candidate.getName())
                .id(candidate.getId())
                .email(candidate.getEmail())
                .username(candidate.getUsername())
                .description(candidate.getDescription())
                .build();

        return candidateResponse;
    }

}
