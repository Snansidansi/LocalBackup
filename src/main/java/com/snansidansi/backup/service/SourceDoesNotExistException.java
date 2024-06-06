package com.snansidansi.backup.service;

public class SourceDoesNotExistException extends Exception {
    public SourceDoesNotExistException() {
        super();
    }

    public SourceDoesNotExistException(String message) {
        super(message);
    }
}
