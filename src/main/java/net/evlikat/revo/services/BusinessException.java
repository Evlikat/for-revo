package net.evlikat.revo.services;

/**
 * NotEnoughMoneyException.
 *
 * @author Roman Prokhorov
 * @version 1.0
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
