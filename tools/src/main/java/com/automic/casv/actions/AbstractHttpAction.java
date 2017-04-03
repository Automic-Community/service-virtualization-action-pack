package com.automic.casv.actions;

import java.net.URI;
import java.net.URISyntaxException;

import com.automic.casv.config.HttpClientConfig;
import com.automic.casv.constants.Constants;
import com.automic.casv.constants.ExceptionConstants;
import com.automic.casv.exception.AutomicException;
import com.automic.casv.filter.AuthenticationErrorFilter;
import com.automic.casv.filter.GenericResponseFilter;
import com.automic.casv.util.CommonUtil;
import com.automic.casv.util.ConsoleWriter;
import com.automic.casv.validator.CaSvValidator;
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
     * Option to skip validation
     */
    private boolean skipCertValidation;

    private Client client;

    private boolean authRequired;

    public AbstractHttpAction(boolean authRequired) {
        this.authRequired = authRequired;
        addOption(Constants.BASE_URL, true, "Base API URL");
        addOption(Constants.SKIP_CERT_VALIDATION, false, "Skip SSL validation");
        if (authRequired) {
            addOption(Constants.USERNAME, true, "Username");
        }
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
        if (authRequired) {
            this.username = getOptionValue(Constants.USERNAME);
            CaSvValidator.checkNotEmpty(username, "User");            
        }
        this.skipCertValidation = CommonUtil.convert2Bool(getOptionValue(Constants.SKIP_CERT_VALIDATION));
        try {
            this.baseUrl = new URI(temp);
        } catch (URISyntaxException e) {
            ConsoleWriter.writeln(e);
            String msg = String.format(ExceptionConstants.INVALID_INPUT_PARAMETER, "Base API URL", temp);
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
            if (authRequired) {
                client.addFilter(new HTTPBasicAuthFilter(username, System.getenv(Constants.PASSWORD)));
                client.addFilter(new AuthenticationErrorFilter());
            }
            client.addFilter(new GenericResponseFilter());
        }
        return client.resource(baseUrl);
    }

}