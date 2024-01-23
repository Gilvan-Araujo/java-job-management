package dev.gilvan.gestao_vagas.modules.candidate.useCases;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import dev.gilvan.gestao_vagas.modules.candidate.CandidateRepository;
import dev.gilvan.gestao_vagas.modules.candidate.dto.AuthCandidateRequestDTO;
import dev.gilvan.gestao_vagas.modules.candidate.dto.AuthCandidateResponseDTO;

@Service
public class AuthCandidateUseCase {

        @Autowired
        private CandidateRepository candidateRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Value("${security.token.secret.candidate}")
        private String secret;

        public AuthCandidateResponseDTO execute(AuthCandidateRequestDTO authCandidateRequestDTO)
                        throws AuthenticationException {
                var candidate = candidateRepository.findByUsername(authCandidateRequestDTO.username())
                                .orElseThrow(() -> new UsernameNotFoundException("Username/password incorrect"));

                var passwordsMatch = passwordEncoder.matches(authCandidateRequestDTO.password(),
                                candidate.getPassword());

                if (!passwordsMatch) {
                        throw new AuthenticationException("Username/password incorrect");
                }

                Algorithm algorithm = Algorithm.HMAC256(secret);
                var expiresAt = Instant.now().plus(Duration.ofHours(24));
                var token = JWT.create()
                                .withIssuer("javagas")
                                .withSubject(candidate.getId().toString())
                                .withExpiresAt(expiresAt)
                                .withClaim("roles", Arrays.asList("CANDIDATE"))
                                .sign(algorithm);

                var authCandidateResponse = AuthCandidateResponseDTO
                                .builder()
                                .accessToken(token)
                                .expiresAt(expiresAt.toEpochMilli())
                                .build();

                return authCandidateResponse;
        }
}
