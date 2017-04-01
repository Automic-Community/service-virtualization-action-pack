package com.automic.casv.filter;

import java.util.List;

import javax.json.JsonObject;
import javax.ws.rs.core.MultivaluedMap;

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
        boolean ignoreHttpError = (request.getHeaders().remove(Constants.IGNORE_DEPLOY_FAILURE) != null);
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
        // Redeploy service when package already deployed
        if (ignoreHttpError && response.getStatus() == Constants.HTTP_NOT_FOUND) {
            return response;
        }
        // print json or xml depending on its content-type
        MultivaluedMap<String, String> responseHeaders = response.getHeaders();
        List<String> contentType = responseHeaders.get("Content-Type");
        List<String> contenLength = responseHeaders.get("Content-Length");

        if (contentType != null) {
            if (!(response.getStatus() >= Constants.HTTP_SUCCESS_START && response.getStatus() <= Constants.HTTP_SUCCESS_END)) {
                if (contentType.get(0).toLowerCase().contains("json")) {
                    JsonObject jsonResponse = CommonUtil.jsonObjectResponse(response.getEntityInputStream());
                    ConsoleWriter.writeln(CommonUtil.jsonPrettyPrinting(jsonResponse));
                } else if (contentType.get(0).toLowerCase().contains("xml")) {
                    ConsoleWriter.writeln(response.getEntity(String.class));
                }

                String responseMsg = response.getEntity(String.class);
                throw new AutomicRuntimeException(responseMsg);
            }
        } else if (!(contenLength != null && Integer.parseInt(contenLength.get(0)) > 0)) {
            String responseMsg = "Failed to process the request";
            throw new AutomicRuntimeException(responseMsg);
        }

        return response;
    }
}
