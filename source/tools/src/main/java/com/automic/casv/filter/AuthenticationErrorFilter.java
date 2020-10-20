package com.automic.casv.filter;

import org.apache.http.HttpStatus;

import com.automic.casv.exception.AutomicRuntimeException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * This class acts as a filter and intercept the response to make sure that there is no authentication error.
 *
 */

public class AuthenticationErrorFilter extends ClientFilter {

    @Override
    public ClientResponse handle(ClientRequest request) {
        ClientResponse response = getNext().handle(request);
        if (response.getStatus() == HttpStatus.SC_OK && response.getLength() == 0) {
            throw new AutomicRuntimeException("Request is not authenticated.");
        }
        return response;
    }
}
