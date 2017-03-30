package com.automic.casv.actions;

import com.automic.casv.exception.AutomicException;
import com.automic.casv.util.CommonUtil;

/**
 * 
 * @author vivekmalhotra
 *
 */
public class AbstractRunTestAction extends AbstractHttpAction {

    // whether test are executed asynchronously
    protected boolean async;

    public AbstractRunTestAction() {
        addOption("async", false, "Asynchronous call");

    }

    @Override
    protected void executeSpecific() throws AutomicException {
        // get async option from commandline
        this.async = CommonUtil.convert2Bool(getOptionValue("async"));
    }
    
    
    /**
     * Parse the xml response to get {@link TestResponse}
     * @param xmlResponse
     * @return
     */
    protected TestResponse getTestResponse(String xmlResponse){
        TestResponse response = null;
        
        // TODO: add logic to read XML to get status and result status
        return response;
    }

    class TestResponse {
        private String status;

        private String resultStatus;

        public TestResponse(String status, String resultStatus) {
            this.status = status;
            this.resultStatus = resultStatus;
        }

        public String getStatus() {
            return this.status;
        }

        public String getResultStatus() {
            return this.resultStatus;
        }

    }
}
