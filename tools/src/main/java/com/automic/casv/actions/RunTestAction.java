package com.automic.casv.actions;

import com.automic.casv.entity.TestResult;
import com.automic.casv.exception.AutomicException;
import com.automic.casv.util.CommonUtil;
import com.automic.casv.util.ConsoleWriter;
import com.automic.casv.validator.CaSvValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Action to run individual test cases
 * 
 *
 */
public class RunTestAction extends AbstractHttpAction {

    // whether test are executed asynchronously
    private boolean async;

    // file path of test case document. This is a .tst file
    private String testCaseFile;

    // full path to the staging document. This is a .stg file
    private String stagingDoc;

    // full path to the configuration. This is a .config file
    private String configFile;

    /**
     * full path to the coordinator
     */
    private String coordinator;

    public RunTestAction() {
        super(false);
        addOption("testcasefile", true, "Test Case Path");
        addOption("stagingdoc", false, "Staging Doc Path");
        addOption("config", false, "Config Path");
        addOption("coordinator", false, "Coordinator Server");
        addOption("async", false, "Run Asynchronously");
    }

    @Override
    protected void executeSpecific() throws AutomicException {
        // initialize
        initAndValidateInputs();

        // request to rest api
        WebResource webResource = getClient().path("lisa-invoke").path("runTest")
                .queryParam("testCasePath", testCaseFile);
        if (CommonUtil.checkNotEmpty(this.stagingDoc)) {
            webResource = webResource.queryParam("stagingDocPath", stagingDoc);
        }

        if (CommonUtil.checkNotEmpty(this.configFile)) {
            webResource = webResource.queryParam("configPath", configFile);
        }

        if (CommonUtil.checkNotEmpty(this.coordinator)) {
            webResource = webResource.queryParam("coordName", coordinator);
        }

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
        // validate test case file
        this.testCaseFile = getOptionValue("testcasefile");
        CaSvValidator.checkNotEmpty(this.testCaseFile, "Test Case Path");

        // initialize staging doc
        this.stagingDoc = getOptionValue("stagingdoc");

        // initialize config param
        this.configFile = getOptionValue("config");

        // initialize coordinator
        this.coordinator = getOptionValue("coordinator");

        // get async option from commandline
        this.async = CommonUtil.convert2Bool(getOptionValue("async"));

    }

    // get test result and print in the job report whether test passed or not
    private void prepareOutput(ClientResponse response) throws AutomicException {
        String xmlResponse = response.getEntity(String.class);
        TestResult testResult = new TestResult(xmlResponse, this.async);
        if (!testResult.isTestSucceeded()) {
            throw new AutomicException("Run Test operation failed.");
        } 
        testResult.logInfo();        
    }
}
