package dev.gilvan.gestao_vagas.modules.candidate.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CandidateProfileResponseDTO {

    @Schema(example = "Java developer")
    public String description;

    @Schema(example = "joe_valentine")
    public String username;

    @Schema(example = "joe_valentine@gmail.com")
    public String email;
    public UUID id;

    @Schema(example = "Joseph Valentine")
    public String name;
}
