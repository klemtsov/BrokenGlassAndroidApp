package ru.sbrf.zsb.android.exceptions;

/**
 * Created by munk1 on 17.06.2016.
 */
public class UserRegistrationException extends Exception {
    public UserRegistrationException() {
    }

    public UserRegistrationException(String detailMessage) {
        super(detailMessage);
    }
}
