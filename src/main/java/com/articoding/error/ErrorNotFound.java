package com.articoding.error;

public class ErrorNotFound extends RestError {
    public ErrorNotFound(String entity, Long id) {
        super(String.format("%s with ID %d does not exist", entity, id));
    }
}
