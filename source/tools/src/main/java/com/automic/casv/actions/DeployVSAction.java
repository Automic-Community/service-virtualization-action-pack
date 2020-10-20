package com.automic.casv.actions;

import java.io.File;

import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;

import com.automic.casv.constants.Constants;
import com.automic.casv.exception.AutomicException;
import com.automic.casv.util.CommonUtil;
import com.automic.casv.util.ConsoleWriter;
import com.automic.casv.validator.CaSvValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;

/**
 * This action takes a posted mar and deploys it to the VSE as virtual service.
 * 
 * @author shrutinambiar
 *
 */
public class DeployVSAction extends AbstractHttpAction {

    /**
     * Virtual service environment
     */
    private String vseName;

    /**
     * MAR file for deployment
     */
    private File marFile;

    /**
     * URI to MAR file for deployment
     */
    private String marURI;

    private boolean reDeploy;

    public DeployVSAction() {
        super(true);
        addOption("vsename", true, "VSE Name");
        addOption("mar", true, "MAR Location");
        addOption("deployoption", true, "Deploy using (File | Uri)");
        addOption("redeploy", false, "Redeploy (True | False)");
    }

    @Override
    protected void executeSpecific() throws AutomicException {
        prepareInputs();

        WebResource webResource = getClient().path("VSEs").path(vseName).path("actions").path("deployMar");
        ConsoleWriter.writeln("Calling url " + webResource.getURI());

        FormDataMultiPart part = new FormDataMultiPart();
        if (marFile != null) {
            FileDataBodyPart fp = new FileDataBodyPart("file", marFile, MediaType.APPLICATION_OCTET_STREAM_TYPE);
            part.bodyPart(fp);
        } else {
            part.field("fileURI", marURI);
        }

        WebResource.Builder builder = webResource.getRequestBuilder();
        if (reDeploy) {
            builder = builder.header(Constants.IGNORE_HTTPERROR, "true");
        }

        ClientResponse response = builder.accept(Constants.START_STOP_VS_ACCEPT_TYPE).type(part.getMediaType())
                .post(ClientResponse.class, part);

        JsonObject jObj = CommonUtil.readAndLog(response);
        if (!CommonUtil.isHttpStatusOK(response.getStatus())) {
            String msg = jObj.getString("message");
            if (msg != null && msg.contains(Constants.SERVICE_ALREADY_EXIST)) {
                ConsoleWriter.writeln("Deploy operation failed. Attempting to delete and deploy.");
                String serviceName = msg.replaceFirst(Constants.SERVICE_ALREADY_EXIST, "").trim();
                WebResource temp = getClient().path("VSEs").path(vseName).path(serviceName);
                ConsoleWriter.writeln("Calling url " + temp.getURI());
                temp.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
                reDeploy = false;
                executeSpecific();
            } else {
                throw new AutomicException("ReDeploy Operation failed");
            }
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

        String temp = getOptionValue("mar");
        CaSvValidator.checkNotEmpty(temp, "MAR");

        String deployOption = getOptionValue("deployoption");
        CaSvValidator.checkNotEmpty(deployOption, "Deploy using (File | Uri)");

        if ("File".equalsIgnoreCase(deployOption)) {
            marFile = new File(temp);
            CaSvValidator.checkFileExists(marFile, "MAR Location");
        } else if ("Uri".equalsIgnoreCase(deployOption)) {
            marURI = temp;
        } else {
            throw new AutomicException("Invalid Deploy using.");
        }

        reDeploy = CommonUtil.convert2Bool(getOptionValue("redeploy"));
    }

}
