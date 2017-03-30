package com.automic.casv.actions;

import com.automic.casv.exception.AutomicException;
import com.automic.casv.util.ConsoleWriter;
import com.automic.casv.validator.CaSvValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Action to run individual test cases
 * 
 *
 */
public class RunTestAction extends AbstractRunTestAction {

    /**
     * file path of test case document. This is a .tst file
     */
    private String testCaseFile;

    /**
     * full path to the staging document. This is a .stg file
     */
    private String stagingDoc;

    /**
     * full path to the configuration. This is a .config file
     */
    private String configFile;

    /**
     * full path to the coordinator
     */
    private String coordinator;

    public RunTestAction() {
        addOption("testcasefile", true, "Test case file");
        addOption("stagingdoc", false, "Staging doc");
        addOption("config", false, "Configuration file");
        addOption("coordinator", false, "Coordinator server name");
    }

    @Override
    protected void executeSpecific() throws AutomicException {
        super.executeSpecific();
        // initialize
        initAndValidateInputs();

        // request to rest api
        WebResource webResource = getClient().path("lisa-invoke").path("runTest")
                .queryParam("testCasePath", testCaseFile);
        if (this.stagingDoc != null) {
            webResource = webResource.queryParam("stagingDocPath", stagingDoc);
        }

        if (this.configFile != null) {
            webResource = webResource.queryParam("configPath", configFile);
        }

        if (this.coordinator != null) {
            webResource = webResource.queryParam("coordName", coordinator);
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
        // validate test case file
        this.testCaseFile = getOptionValue("testcasefile");
        CaSvValidator.checkNotEmpty(this.testCaseFile, "Test case file");

        // validate staging doc
        this.stagingDoc = getOptionValue("stagingdoc");
        if (this.stagingDoc != null) {
            CaSvValidator.checkNotEmpty(this.stagingDoc, "Staging doc");
        }

        // validate config param
        this.configFile = getOptionValue("config");
        if (this.configFile != null) {
            CaSvValidator.checkNotEmpty(this.configFile, "Configuration file");
        }

        // validate coordinator
        this.coordinator = getOptionValue("coordinator");
        if (this.coordinator != null) {
            CaSvValidator.checkNotEmpty(this.coordinator, "Coordinator server name");
        }

    }
    
    // get test status and result status and print in the job report
    private void prepareOutput(ClientResponse response){
        TestResponse testresponse = getTestResponse(response.getEntity(String.class));
        
        ConsoleWriter.writeln("UC4RB_CASV_TEST_STATUS::=" + testresponse.getStatus());
        ConsoleWriter.writeln("UC4RB_CASV_RESULT_STATUS::=" + testresponse.getResultStatus());
    }

}
