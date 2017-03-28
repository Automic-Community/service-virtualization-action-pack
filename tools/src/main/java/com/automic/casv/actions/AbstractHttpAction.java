package com.automic.casv.actions;

import java.net.URI;
import java.net.URISyntaxException;

import com.automic.casv.config.HttpClientConfig;
import com.automic.casv.constants.Constants;
import com.automic.casv.constants.ExceptionConstants;
import com.automic.casv.exception.AutomicException;
import com.automic.casv.filter.GenericResponseFilter;
import com.automic.casv.util.CommonUtil;
import com.automic.casv.util.ConsoleWriter;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * This class defines the execution of any action.It provides some initializations and validations on common inputs .The
 * child actions will implement its executeSpecific() method as per their own need.
 */
public abstract class AbstractHttpAction extends AbstractAction {

    /**
     * Service end point
     */
    protected URI baseUrl;

    /**
     * Username to connect to CA SV
     */
    protected String username;

    /**
     * Password for the username
     */
    private String password;

    /**
     * Option to skip validation
     */
    private boolean skipCertValidation;

    private Client client;

    public AbstractHttpAction() {
        addOption(Constants.BASE_URL, true, "BlazeMeter URL");
        addOption(Constants.USERNAME, true, "CASV URL");
        addOption(Constants.SKIP_CERT_VALIDATION, false, "Skip SSL validation");
    }

    /**
     * This method initializes the arguments and calls the execute method.
     *
     * @throws AutomicException
     *             exception while executing an action
     */
    public final void execute() throws AutomicException {
        prepareCommonInputs();
        try {
            executeSpecific();
        } finally {
            if (client != null) {
                client.destroy();
            }
        }
    }

    private void prepareCommonInputs() throws AutomicException {
        String temp = getOptionValue(Constants.BASE_URL);
        this.password = System.getenv(Constants.PASSWORD);
        this.skipCertValidation = CommonUtil.convert2Bool(getOptionValue(Constants.SKIP_CERT_VALIDATION));
        try {
            this.baseUrl = new URI(temp);
        } catch (URISyntaxException e) {
            ConsoleWriter.writeln(e);
            String msg = String.format(ExceptionConstants.INVALID_INPUT_PARAMETER, "URL", temp);
            throw new AutomicException(msg);
        }
    }

    /**
     * Method to execute the action.
     *
     * @throws AutomicException
     */
    protected abstract void executeSpecific() throws AutomicException;

    /**
     * Method to initialize Client instance.
     *
     * @throws AutomicException
     *
     */
    protected WebResource getClient() throws AutomicException {
        if (client == null) {
            client = HttpClientConfig.getClient(baseUrl.getScheme(), this.skipCertValidation);
            client.addFilter(new HTTPBasicAuthFilter(username, password));
            client.addFilter(new GenericResponseFilter());
        }
        return client.resource(baseUrl);
    }

}