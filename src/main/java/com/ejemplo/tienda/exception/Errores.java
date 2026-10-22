package com.ejemplo.tienda.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class Errores {
    private Errores() { }
    public static ResponseStatusException noExiste(String recurso) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND, recurso + " no encontrado");
    }
    public static ResponseStatusException conflicto(String mensaje) {
        return new ResponseStatusException(HttpStatus.CONFLICT, mensaje);
    }
}