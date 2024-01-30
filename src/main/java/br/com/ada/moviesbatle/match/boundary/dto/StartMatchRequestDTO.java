package br.com.ada.moviesbatle.match.boundary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@AllArgsConstructor
@Data
public class StartMatchRequestDTO {
    @Schema(description = "Username registered", requiredMode = REQUIRED)
    @NotBlank(message = "Username is mandatory.")
    private String username;
    @Schema(description = "Password registered", requiredMode = REQUIRED)
    @NotBlank(message = "Password is mandatory.")
    private String password;
}
