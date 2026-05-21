package com.medicluz.usuario.dto;

import jakarta.validation.constraints.*;

public record SolicitudUsuario(

    @NotBlank(message = "El nombre es obligatorio") @Size(max = 100)
    String primerNombre,

    @NotBlank(message = "El apellido es obligatorio") @Size(max = 100)
    String apellido,

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    String correo,

    /** Opcional en actualización (null = no cambiar) */
    @Size(min = 8, message = "Mínimo 8 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{8,}$",
        message = "Debe contener mayúscula, minúscula, número y carácter especial"
    )
    String contrasena,

    @NotNull(message = "El ID de rol es obligatorio")
    Long idRol
) {}
