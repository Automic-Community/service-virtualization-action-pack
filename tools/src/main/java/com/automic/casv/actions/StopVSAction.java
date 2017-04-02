package com.automic.casv.actions;

import javax.json.JsonObject;

import com.automic.casv.constants.Constants;
import com.automic.casv.exception.AutomicException;
import com.automic.casv.util.CommonUtil;
import com.automic.casv.util.ConsoleWriter;
import com.automic.casv.validator.CaSvValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * This action stops an already running virtual service
 * 
 * @author shrutinambiar
 *
 */
public class StopVSAction extends AbstractHttpAction {

    /**
     * Virtual service environment
     */
    private String vseName;

    /**
     * virtual service name
     */
    private String vsName;

    public StopVSAction() {
        super(true);
        addOption("vsename", true, "VSE Name");
        addOption("vsname", true, "Virtual Service Name");
    }

    @Override
    protected void executeSpecific() throws AutomicException {
        prepareInputs();

        WebResource webResource = getClient().path("VSEs").path(vseName).path(vsName).path("actions").path("stop");

        ConsoleWriter.writeln("Calling url " + webResource.getURI());

        ClientResponse response = webResource.header(Constants.IGNORE_HTTPERROR, "true")
                .accept(Constants.START_STOP_VS_ACCEPT_TYPE).post(ClientResponse.class);

        JsonObject jObj = CommonUtil.readAndLog(response);
        if (!CommonUtil.isHttpStatusOK(response.getStatus())) {
            ConsoleWriter.writeln("UC4RB_SV_RESPMSG::=" + jObj.getString("message"));
            throw new AutomicException("Stop VS Operation failed");
        }
    }

    /**
     * Prepare input parameter and their validation
     * 
     * @throws AutomicException
     */
    private void prepareInputs() throws AutomicException {
        vseName = getOptionValue("vsename");
        CaSvValidator.checkNotEmpty(vseName, "VSE Name");

        vsName = getOptionValue("vsname");
        CaSvValidator.checkNotEmpty(vsName, "Virtual Service Name");
    }

}