package br.com.ada.moviesbatle.match.boundary;

import br.com.ada.moviesbatle.security.service.TokenBlackListService;
import br.com.ada.moviesbatle.match.boundary.dto.StartMatchRequestDTO;
import br.com.ada.moviesbatle.match.boundary.dto.StartMatchResponseDTO;
import br.com.ada.moviesbatle.security.service.JwtTokenService;
import br.com.ada.moviesbatle.match.control.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/match")
@Slf4j
public class MatchController {

    private final JwtTokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final TokenBlackListService tokenBlackListService;
    private final MatchService matchService;

    public MatchController(JwtTokenService tokenService,
                           AuthenticationManager authenticationManager,
                           TokenBlackListService tokenBlackListService,
                           MatchService matchService) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
        this.tokenBlackListService = tokenBlackListService;
        this.matchService = matchService;
    }

    @Operation(summary = "Obtain an access token that must be used in all Battle Movies requests, " +
            "as well as starting a match and associating the generated token with that match.")
    @ApiResponse(responseCode  = "200", description = "Create token and start a new match",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = StartMatchResponseDTO.class))})
    @ApiResponse(responseCode  = "401", description = "Invalid access token.", content = { @Content(mediaType ="application/json")})
    @PostMapping("/start")
    public ResponseEntity<StartMatchResponseDTO> startMatch(@Valid @RequestBody StartMatchRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = tokenService.generateToken(loginRequestDTO.getUsername());
        var tokenId = tokenService.saveToken(loginRequestDTO.getUsername(), jwtToken);

        matchService.createMatch(loginRequestDTO.getUsername(), tokenId);

        return ResponseEntity.ok(new StartMatchResponseDTO(jwtToken));
    }

    @Operation(summary = "Invalidates token and ends the match associated with that token.")
    @ApiResponse(responseCode  = "200", description = "Invalidate token and end match",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = StartMatchResponseDTO.class))})
    @ApiResponse(responseCode  = "401", description = "Invalid access token.", content = { @Content(mediaType ="application/json")})
    @PostMapping("/finish")
    public ResponseEntity<String> finishMatch(HttpServletRequest request) {
        var token = tokenService.extractJwtFromRequest(request);
        tokenBlackListService.addToBlacklist(token);

        matchService.finishMatch(token);

        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout completed successfully.");
    }
}
