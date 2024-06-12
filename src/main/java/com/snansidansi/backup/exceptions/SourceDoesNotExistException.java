package com.snansidansi.backup.exceptions;

public class SourceDoesNotExistException extends Exception {
    public SourceDoesNotExistException() {
        super();
    }

    public SourceDoesNotExistException(String message) {
        super(message);
    }
}
