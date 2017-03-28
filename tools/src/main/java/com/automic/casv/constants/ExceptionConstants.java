package com.automic.casv.constants;

/**
 * Exception constants used in the application
 * 
 * @author shrutinambiar
 *
 */
public class ExceptionConstants {

    public static final String UNABLE_TO_FLUSH_STREAM = "Error while flushing stream";
    public static final String INVALID_ARGS = "Improper Args. Possible cause : %s";

    public static final String INVALID_INPUT_PARAMETER = "Invalid value for parameter [%s] : [%s]";

    public static final String GENERIC_ERROR_MSG = "System Error occured.";

    public static final String ERROR_SKIPPING_CERT = "Error skipping the certificate validation";

    private ExceptionConstants() {
    }
}
