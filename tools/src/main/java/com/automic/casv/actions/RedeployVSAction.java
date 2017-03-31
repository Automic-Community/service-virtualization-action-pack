package com.automic.casv.actions;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;

import com.automic.casv.constants.Constants;
import com.automic.casv.exception.AutomicException;
import com.automic.casv.exception.AutomicRuntimeException;
import com.automic.casv.util.CommonUtil;
import com.automic.casv.util.ConsoleWriter;
import com.automic.casv.validator.CaSvValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;

/**
 * This action takes a posted mar and redeploys it to the VSE as virtual service.
 * 
 * @author Anurag Upadhyay
 *
 */
public class RedeployVSAction extends AbstractHttpAction {

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

    public RedeployVSAction() {
        addOption("vsename", true, "VSE Name");
        addOption("marfile", false, "MAR File Path");
        addOption("maruri", false, "MAR File URI");
    }

    @Override
    protected void executeSpecific() throws AutomicException {
        prepareInputs();
        ConsoleWriter.writeln("************** Deploying Virtual Service ******************");
        ConsoleWriter.newLine();
        String vsName = deployService(true);
        if (vsName != null) {
            ConsoleWriter.newLine();
            ConsoleWriter.writeln("************** Deleting Virtual Service ******************");
            ConsoleWriter.newLine();
            deleteService(vsName);
            ConsoleWriter.newLine();
            ConsoleWriter.writeln("************** Redeploying Virtual Service ******************");
            ConsoleWriter.newLine();
            deployService(false);
        }

    }

    private String deployService(boolean flag) throws AutomicException {
        String serviceName = null;
        WebResource webResource = getClient().path("VSEs").path(vseName).path("actions").path("deployMar");
        ConsoleWriter.writeln("Calling url " + webResource.getURI());

        FormDataMultiPart part = new FormDataMultiPart();
        if (marFile != null) {
            FileDataBodyPart fp = new FileDataBodyPart("file", marFile, MediaType.APPLICATION_OCTET_STREAM_TYPE);
            part.bodyPart(fp);
        } else {
            part.field("fileURI", marURI);
        }

        ClientResponse response = webResource.accept(Constants.START_STOP_VS_ACCEPT_TYPE)
                .header("IgnoreDeployFailure", flag).type(part.getMediaType()).post(ClientResponse.class, part);

        JsonObject jsonObjectResponse = CommonUtil.jsonObjectResponse(response.getEntityInputStream());
        ConsoleWriter.writeln(CommonUtil.jsonPrettyPrinting(jsonObjectResponse));
        if (!(response.getStatus() >= Constants.HTTP_SUCCESS_START && response.getStatus() <= Constants.HTTP_SUCCESS_END)) {
            String msg = jsonObjectResponse.getString("message");

            if (containsIgnoreCase(msg, Constants.SERVICE_ALREADY_EXIST)) {
                serviceName = msg.replaceFirst("There is already a service with the name", "").trim();
            } else {
                String responseMsg = response.getEntity(String.class);
                throw new AutomicRuntimeException(responseMsg);
            }

        }

        return serviceName;
    }

    private void deleteService(String vsName) throws AutomicException {
        WebResource webResource = getClient().path("VSEs").path(vseName).path(vsName);
        ConsoleWriter.writeln("Calling url " + webResource.getURI());
        webResource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
    }

    /**
     * Prepare input parameter and their validation
     * 
     * @throws AutomicException
     */
    private void prepareInputs() throws AutomicException {
        vseName = getOptionValue("vsename");
        CaSvValidator.checkNotEmpty(vseName, "VSE Name");

        String temp = getOptionValue("marfile");
        if (CommonUtil.checkNotEmpty(temp)) {
            marFile = new File(temp);
            CaSvValidator.checkFileExists(marFile, "Artifact File Path");
        }

        marURI = getOptionValue("maruri");
    }

    public boolean containsIgnoreCase(String source, String key) {

        if (!CommonUtil.checkNotEmpty(source))
            return false;

        Pattern p = Pattern.compile(key, Pattern.CASE_INSENSITIVE + Pattern.LITERAL);
        Matcher m = p.matcher(source);
        return m.find();
    }

}
