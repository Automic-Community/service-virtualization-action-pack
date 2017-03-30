package com.automic.casv.actions;

import java.io.File;

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

    public DeployVSAction() {
        addOption("vsename", true, "VSE Name");
        addOption("marfile", false, "MAR File Path");
        addOption("maruri", false, "MAR File URI");
    }

    @Override
    protected void executeSpecific() throws AutomicException {
        prepareInputs();

        WebResource webResource = getClient().path("VSEs").path(vseName).path("actions").path("deployMar");
        ConsoleWriter.writeln("Calling url " + webResource.getURI());

        FileDataBodyPart fp = new FileDataBodyPart("file", marFile, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        FormDataMultiPart part = new FormDataMultiPart();
        part.bodyPart(fp);
        webResource.accept(Constants.START_STOP_VS_ACCEPT_TYPE).type(part.getMediaType())
                .post(ClientResponse.class, part);
    }

    /**
     * Prepare input parameter and their validation
     * 
     * @throws AutomicException
     */
    private void prepareInputs() throws AutomicException {
        try {
            vseName = getOptionValue("vsename");
            CaSvValidator.checkNotEmpty(vseName, "VSE Name");

            String temp = getOptionValue("marfile");
            if (CommonUtil.checkNotEmpty(temp)) {
                marFile = new File(temp);
                CaSvValidator.checkFileExists(marFile, "Artifact File Path");
            }

            marURI = getOptionValue("maruri");
        } catch (AutomicException e) {
            ConsoleWriter.writeln(e.getMessage());
            throw e;
        }

    }

}
