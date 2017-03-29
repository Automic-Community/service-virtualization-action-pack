package com.automic.casv.filter;

import java.util.List;

import javax.json.JsonObject;
import javax.ws.rs.core.MultivaluedMap;

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

    private static final int HTTP_SUCCESS_START = 200;
    private static final int HTTP_SUCCESS_END = 299;

    private static final String RESPONSE_CODE = "Response Code [%s]";
    private static final String RESPONSE_MSG = RESPONSE_CODE + " Message : [%s]";

    @Override
    public ClientResponse handle(ClientRequest request) {

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

        // print json or xml depending on its content-type
        MultivaluedMap<String, String> responseHeaders = response.getHeaders();
        List<String> contentType = responseHeaders.get("Content-Type");
        if (contentType != null && contentType.get(0).toLowerCase().contains("json")) {
            // pretty print of json response on console
            JsonObject jsonResponse = CommonUtil.jsonObjectResponse(response.getEntityInputStream());
            ConsoleWriter.writeln(CommonUtil.jsonPrettyPrinting(jsonResponse));
        } else if (contentType != null && contentType.get(0).toLowerCase().contains("xml")) {
            ConsoleWriter.writeln(response.getEntity(String.class));
        } else if (response.getStatus() == 200) {
            // check if authentication has failed
            String responseMsg = "Authentication failed";
            throw new AutomicRuntimeException(responseMsg);
        }

        if (!(response.getStatus() >= HTTP_SUCCESS_START && response.getStatus() <= HTTP_SUCCESS_END)) {
            String responseMsg = response.getEntity(String.class);
            throw new AutomicRuntimeException(responseMsg);
        }

        return response;
    }
}
