package com.automic.casv.validator;

import java.io.File;

import com.automic.casv.constants.ExceptionConstants;
import com.automic.casv.exception.AutomicException;
import com.automic.casv.util.CommonUtil;

/**
 * This class provides common validations as required by action(s).
 *
 */

public final class CaSvValidator {

    private CaSvValidator() {
    }

    public static final void checkNotEmpty(String parameter, String parameterName) throws AutomicException {
        if (!CommonUtil.checkNotEmpty(parameter)) {
            throw new AutomicException(String.format(ExceptionConstants.INVALID_INPUT_PARAMETER, parameterName,
                    parameter));
        }
    }

    public static final void checkNotNull(Object parameter, String parameterName) throws AutomicException {
        if (parameter != null) {
            throw new AutomicException(String.format(ExceptionConstants.INVALID_INPUT_PARAMETER, parameterName,
                    parameter));
        }
    }

    public static void lessThan(int value, int lessThan, String parameterName) throws AutomicException {
        if (value < lessThan) {
            String errMsg = String.format(ExceptionConstants.INVALID_INPUT_PARAMETER, parameterName, value);
            throw new AutomicException(errMsg);
        }
    }

    public static void checkFileExists(File file, String parameterName) throws AutomicException {
        if (!(file.exists() && file.isFile())) {
            throw new AutomicException(String.format(ExceptionConstants.INVALID_INPUT_PARAMETER, parameterName, file));
        }
    }

}
