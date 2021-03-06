package com.automic.casv.actions;

import com.automic.casv.entity.TestResult;
import com.automic.casv.exception.AutomicException;
import com.automic.casv.util.CommonUtil;
import com.automic.casv.util.ConsoleWriter;
import com.automic.casv.validator.CaSvValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Action to run a Mar of Mari file
 * 
 *
 */
public class RunModelArchiveAction extends AbstractHttpAction {

    // file path of Mar. This is a .mar or .mari file
    private String marOrMariFile;

    // whether test are executed asynchronously
    private boolean async;

    public RunModelArchiveAction() {
        super(false);
        addOption("marormari", true, "MAR or MARI Path");
        addOption("async", false, "Run Asynchronously");
    }

    @Override
    protected void executeSpecific() throws AutomicException {
        // initialize
        initAndValidateInputs();

        // request to rest api
        WebResource webResource = getClient().path("lisa-invoke").path("runMar")
                .queryParam("marOrMariPath", marOrMariFile);

        if (this.async) {
            webResource = webResource.queryParam("async", "true");
        }
        ConsoleWriter.writeln("Calling url " + webResource.getURI());
        // submit the request
        ClientResponse response = webResource.get(ClientResponse.class);
        prepareOutput(response);
    }

    // initialize and validate the inputs
    private void initAndValidateInputs() throws AutomicException {
        // validate test suite file
        this.marOrMariFile = getOptionValue("marormari");
        CaSvValidator.checkNotEmpty(this.marOrMariFile, "MAR or MARI Path");

        // get async option from commandline
        this.async = CommonUtil.convert2Bool(getOptionValue("async"));
    }

    // get test result and print in the job report whether test passed or not
    private void prepareOutput(ClientResponse response) throws AutomicException {
        String xmlResponse = response.getEntity(String.class);
        TestResult testResult = new TestResult(xmlResponse, this.async);
        if (!testResult.isTestSucceeded()) {
            throw new AutomicException("Run Model Archive operation failed.");
        }
        testResult.logInfo();
    }
}
