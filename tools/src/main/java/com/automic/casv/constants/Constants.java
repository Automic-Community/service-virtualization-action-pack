package com.automic.casv.constants;

/**
 * Constants class contains all the constants.
 *
 */
public class Constants {

	public static final String ACTION = "action";
	public static final String HELP = "help";

	public static final String ENV_CONNECTION_TIMEOUT = "ENV_CONNECTION_TIMEOUT";
	public static final String ENV_READ_TIMEOUT = "ENV_READ_TIMEOUT";

	public static final int CONNECTION_TIMEOUT = 30000;
	public static final int READ_TIMEOUT = 60000;

	public static final int HTTP_SUCCESS_START = 200;
	public static final int HTTP_SUCCESS_END = 299;
	public static final int HTTP_NOT_FOUND = 404;

	public static final String BASE_URL = "baseurl";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "UC4_DECRYPTED_PWD";
	public static final String HTTPS = "https";
	public static final String SKIP_CERT_VALIDATION = "ssl";

	public static final int MINUS_ONE = -1;
	public static final int ZERO = 0;

	public static final String YES = "YES";
	public static final String TRUE = "TRUE";
	public static final String ONE = "1";

	// Accept type for the enpoints
	public static final String START_STOP_VS_ACCEPT_TYPE = "application/vnd.ca.lisaInvoke.virtualService+json";
	public static final String SERVICE_ALREADY_EXIST = "There is already a service with the name";

	public static final String TEST_XML = "testXML";

	public static final String BASELINE_XML = "baselineXML";

	public static final String IGNORE_DEPLOY_FAILURE = "IgnoreDeployFailure";

	private Constants() {
	}
}
