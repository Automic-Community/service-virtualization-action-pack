package com.automic.casv.actions;

import com.automic.casv.exception.AutomicException;
import com.automic.casv.validator.CaSvValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Action to run a Mar of Mari file
 * 
 *
 */
public class RunMarAction extends AbstractHttpAction {

    /**
     * file path of Mar. This is a .mar or .mari file
     */
    private String marOrMariFile;

    public RunMarAction() {
        addOption("marormari", true, "Mar or Mari file");
    }

    @Override
    protected void executeSpecific() throws AutomicException {
        // initialize
        initAndValidateInputs();

        // request to rest api
        WebResource webResource = getClient().path("lisa-invoke").path("runMar")
                .queryParam("marOrMariPath", marOrMariFile);

        // submit the request
        webResource.get(ClientResponse.class);

    }

    // initialize and validate the inputs
    private void initAndValidateInputs() throws AutomicException {
        // validate test suite file
        this.marOrMariFile = getOptionValue("marormari");
        CaSvValidator.checkNotEmpty(this.marOrMariFile, "Mar or Mari file");

    }

}
