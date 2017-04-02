package com.automic.casv.filter;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import com.automic.casv.constants.Constants;
import com.automic.casv.exception.AutomicRuntimeException;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * check if the authentication has failed or not, returning 200 status code with no content
 * 
 * @author shrutinambiar
 *
 */
public class AuthenticationFilter extends ClientFilter {

    @Override
    public ClientResponse handle(ClientRequest request) throws ClientHandlerException {

        ClientResponse response = getNext().handle(request);

        MultivaluedMap<String, String> responseHeaders = response.getHeaders();
        List<String> contentType = responseHeaders.get("Content-Type");
        List<String> contenLength = responseHeaders.get("Content-Length");

        if (response.getStatus() == Constants.HTTP_SUCCESS_START && contentType == null
                && !(contenLength != null && Integer.parseInt(contenLength.get(0)) > 0)) {
            String responseMsg = "Failed to process the request";
            throw new AutomicRuntimeException(responseMsg);
        }

        return response;
    }

}
