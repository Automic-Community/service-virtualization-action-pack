package com.automic.casv.actions;

import com.automic.casv.entity.TestResult;
import com.automic.casv.exception.AutomicException;
import com.automic.casv.util.CommonUtil;
import com.automic.casv.util.ConsoleWriter;
import com.automic.casv.validator.CaSvValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Action to run test suite
 * 
 *
 */
public class RunTestSuiteAction extends AbstractHttpAction {

    // file path of test suite. This is a .ste file
    private String testSuiteFile;

    // full path to the configuration. This is a .config file
    private String configFile;

    // whether test are executed asynchronously
    private boolean async;

    public RunTestSuiteAction() {
        addOption("testsuitefile", true, "Test suite file");
        addOption("config", false, "Configuration file");
        addOption("async", false, "Asynchronous call");
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
        this.testSuiteFile = getOptionValue("testsuitefile");
        CaSvValidator.checkNotEmpty(this.testSuiteFile, "Test Suite Path");

        // validate config param
        this.configFile = getOptionValue("config");
        if (this.configFile != null) {
            CaSvValidator.checkNotEmpty(this.configFile, "Config Path");
        }

        // get async option from commandline
        this.async = CommonUtil.convert2Bool(getOptionValue("async"));

    }

    // get test result and print in the job report whether test passed or not
    private void prepareOutput(ClientResponse response) {
        String xmlResponse = response.getEntity(String.class);
        ConsoleWriter.writeln(xmlResponse);
        TestResult testResult = TestResult.getInstance(xmlResponse);

        ConsoleWriter.writeln("UC4RB_SV_TEST_RESULT::=" + testResult.isTestPassed());

    }

}
