package com.articoding.error;

public class DisabledEntity extends RestError {
    public DisabledEntity(String entity) {
        super(String.format("%s unavailable", entity));
    }
}
