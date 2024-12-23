package com.example.taskManagerWithLogin.exceptions;

public class DuplicateUsernameException extends Throwable {
    public DuplicateUsernameException(String s) {
        super(s);
    }
}
