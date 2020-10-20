package com.automic.casv.filter;

import com.automic.casv.constants.Constants;
import com.automic.casv.exception.AutomicRuntimeException;
import com.automic.casv.util.CommonUtil;
import com.automic.casv.util.ConsoleWriter;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * This class acts as a filter and intercept the response to validate it.
 *
 */

public class GenericResponseFilter extends ClientFilter {

    private static final String RESPONSE_CODE = "Response Code [%s]";
    private static final String RESPONSE_MSG = RESPONSE_CODE + " Message : [%s]";

    @Override
    public ClientResponse handle(ClientRequest request) {
        boolean ignoreHttpError = (request.getHeaders().remove(Constants.IGNORE_HTTPERROR) != null);
        ClientResponse response = getNext().handle(request);
        String msg = null;
        if (response.getClientResponseStatus() != null
                && CommonUtil.checkNotEmpty(response.getClientResponseStatus().getReasonPhrase())) {
            msg = String.format(RESPONSE_MSG, response.getStatus(), response.getClientResponseStatus()
                    .getReasonPhrase());
        } else {
            msg = String.format(RESPONSE_CODE, response.getStatus());
        }

        // printing response code and message on console
        ConsoleWriter.writeln(msg);
        if (!ignoreHttpError && !CommonUtil.isHttpStatusOK(response.getStatus())) {
            ConsoleWriter.writeln(response.getEntity(String.class));
            throw new AutomicRuntimeException("Requested Operation failed");
        }
        return response;
    }
}
