package com.automic.casv.actions;

import javax.ws.rs.core.MediaType;

import com.automic.casv.exception.AutomicException;
import com.automic.casv.util.ConsoleWriter;
import com.automic.casv.validator.CaSvValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * This action deletes an already deployed virtual service
 * 
 * @author shrutinambiar
 *
 */
public class DeleteVSAction extends AbstractHttpAction {

    /**
     * Virtual service environment
     */
    private String vseName;

    /**
     * virtual service name
     */
    private String vsName;

    public DeleteVSAction() {
        addOption("vsename", true, "VSE Name");
        addOption("vsname", true, "Virtual Service Name");
    }

    @Override
    protected void executeSpecific() throws AutomicException {
        prepareInputs();

        WebResource webResource = getClient().path("VSEs").path(vseName).path(vsName);

        ConsoleWriter.writeln("Calling url " + webResource.getURI());

        webResource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);

    }

    /**
     * Prepare input parameter and their validation
     * 
     * @throws AutomicException
     */
    private void prepareInputs() throws AutomicException {
        try {
            vseName = getOptionValue("vsename");
            CaSvValidator.checkNotEmpty(vseName, "VSE Name");

            vsName = getOptionValue("vsname");
            CaSvValidator.checkNotEmpty(vsName, "Virtual Service Name");
        } catch (AutomicException e) {
            ConsoleWriter.writeln(e.getMessage());
            throw e;
        }

    }

}