package com.automic.casv.actions;

import com.automic.casv.exception.AutomicException;
import com.automic.casv.validator.CaSvValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Action to run test suite
 * 
 *
 */
public class RunTestSuiteAction extends AbstractHttpAction {

    /**
     * file path of test suite. This is a .ste file
     */
    private String testSuiteFile;

    /**
     * full path to the configuration. This is a .config file
     */
    private String configFile;

    public RunTestSuiteAction() {
        addOption("testsuitefile", true, "Test suite file");
        addOption("config", false, "Configuration file");
    }

    @Override
    protected void executeSpecific() throws AutomicException {
        // initialize
        initAndValidateInputs();

        // request to rest api
        WebResource webResource = getClient().path("lisa-invoke").path("runTestSuite")
                .queryParam("suitePath", testSuiteFile);

        if (this.configFile != null) {
            webResource = webResource.queryParam("configPath", configFile);
        }

        // submit the request
        webResource.get(ClientResponse.class);

    }

    // initialize and validate the inputs
    private void initAndValidateInputs() throws AutomicException {
        // validate test suite file
        this.testSuiteFile = getOptionValue("testsuitefile");
        CaSvValidator.checkNotEmpty(this.testSuiteFile, "Test suite file");

        // validate config param
        this.configFile = getOptionValue("config");
        if (this.configFile != null) {
            CaSvValidator.checkNotEmpty(this.configFile, "Configuration file");
        }

    }

}
