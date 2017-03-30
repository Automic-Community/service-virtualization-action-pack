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
public class RunMarAction extends AbstractHttpAction {

    // file path of Mar. This is a .mar or .mari file
    private String marOrMariFile;

    // whether test are executed asynchronously
    private boolean async;

    public RunMarAction() {
        addOption("marormari", true, "Mar or Mari Path");
        addOption("async", false, "Asynchronous call");
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

        // submit the request
        ClientResponse response = webResource.get(ClientResponse.class);
        prepareOutput(response);
    }

    // initialize and validate the inputs
    private void initAndValidateInputs() throws AutomicException {
        // validate test suite file
        this.marOrMariFile = getOptionValue("marormari");
        CaSvValidator.checkNotEmpty(this.marOrMariFile, "Mar or Mari Path");

        // get async option from commandline
        this.async = CommonUtil.convert2Bool(getOptionValue("async"));

    }

    // get test result and print in the job report whether test passed or not
    private void prepareOutput(ClientResponse response) {
        TestResult testResult = TestResult.getInstance(response.getEntity(String.class));

        ConsoleWriter.writeln("UC4RB_SV_TEST_RESULT::=" + testResult.isTestPassed());

    }
}
