package com.automic.casv.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.automic.casv.exception.AutomicException;
import com.automic.casv.util.CommonUtil;
import com.automic.casv.util.ConsoleWriter;

/**
 * 
 * This entity represents the Test results after running Tests, Test Suite or Model Archive. call
 * {@link TestResult#getInstance(String)} or {@link TestResult#getInstance(File)} to generate an instance of
 * {@link TestResult}.
 * 
 */
public class TestResult {

    private String status;

    private String resultStatus;

    private Integer resultPass;
    private Integer resultFail;
    private Integer resultAbort;
    private Integer resultWarning;
    private Integer resultError;

    private TestResult(String status, String resultStatus, Integer resultPass, Integer resultFail, Integer resultAbort,
            Integer resultWarning, Integer resultError) {
        this.status = status;
        this.resultStatus = resultStatus;
        this.resultPass = resultPass;
        this.resultFail = resultFail;
        this.resultAbort = resultAbort;
        this.resultWarning = resultWarning;
        this.resultError = resultError;
    }

    private TestResult(String status, String resultStatus) {
        this(status, resultStatus, null, null, null, null, null);
    }

    public static TestResult getInstance(File file) throws AutomicException {
        try {
            InputSource source = new InputSource(new FileInputStream(file));

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(source);

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            String status = xpath.evaluate("/invokeResult/status", document);
            String resultStatus = xpath.evaluate("/invokeResult/result/status", document);
            Integer resultPass = new Integer(xpath.evaluate("/invokeResult/result/pass/@count", document));
            Integer resultFail = new Integer(xpath.evaluate("/invokeResult/result/fail/@count", document));
            Integer resultAbort = new Integer(xpath.evaluate("/invokeResult/result/abort/@count", document));
            Integer resultWarning = new Integer(xpath.evaluate("/invokeResult/result/warning/@count", document));
            Integer resultError = new Integer(xpath.evaluate("/invokeResult/result/error/@count", document));

            return new TestResult(status, resultStatus, resultPass, resultFail, resultAbort, resultWarning, resultError);

        } catch (IOException | ParserConfigurationException | SAXException | XPathExpressionException ex) {
            ConsoleWriter.writeln(ex);
            throw new AutomicException(ex.getMessage());
        }

    }

    /**
     * return if Test Result have passed or not
     * 
     * @return true if test passed else false
     */
    public boolean isTestPassed() {
        if (!"OK".equals(this.status)
                || (CommonUtil.checkNotEmpty(this.resultStatus) && !"ENDED".equals(this.resultStatus))) {
            return false;
        }

        return true;
    }

    /**
     * This method converts an XML response String to instance of {@link TestResult}. A sample response xml is of format
     * 
     * <pre>
     *  {@code
     *  <?xml version="1.0" encoding="UTF-8"?>
     *  <invokeResult>
     *    <method name="RunMar">
     *      <params>
     *        <param name="marOrMariPath" value="C:/jira_get_issue.mari" />
     *        <param name="callbackKey" value="788582F3160611E7ACE1005056BD12C1" />
     *      </params>
     *    </method>
     *    <status>OK</status>
     *    <result>
     *      <status>ENDED</status>
     *      <reportUrl><![CDATA[<monitor_url>]]></reportUrl>
     *      <runId>823C3D23160611E7BA97005056BD12C1</runId>
     *      <pass count="1" />
     *      <fail count="0" />
     *      <abort count="0" />
     *      <warning count="0" />
     *      <error count="0" />
     *      <message>jira_get_issue,Run1User1Cycle</message>
     *    </result>
     *  </invokeResult>
     * }
     * </pre>
     * 
     * We read the xPaths /invokeResult/status and /invokeResult/result/status
     * 
     * @param xmlResponse
     * @return an instance
     * @throws AutomicException
     */
    public static TestResult getInstance(String xmlResponse, boolean async) throws AutomicException {
        try {
            InputSource source = new InputSource(new StringReader(xmlResponse));
            String status = null;
            String resultStatus = null;

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(source);

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            XPathExpression resultExpr = xpath.compile("/invokeResult");
            Node invokeResultNode = (Node) resultExpr.evaluate(document, XPathConstants.NODE);
            if (invokeResultNode != null) {
                XPathExpression statusExpr = xpath.compile("./status");
                status = (String) statusExpr.evaluate(invokeResultNode, XPathConstants.STRING);
                if ("OK".equals(status) & !async) {
                    XPathExpression resultStatusExpr = xpath.compile("./result/status");
                    resultStatus = (String) resultStatusExpr.evaluate(invokeResultNode, XPathConstants.STRING);
                }
            }

            return new TestResult(status, resultStatus);

        } catch (IOException | ParserConfigurationException | SAXException | XPathExpressionException ex) {
            ConsoleWriter.writeln(ex);
            throw new AutomicException(ex.getMessage());
        }

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        TestResult anotherObj = (TestResult) obj;
        if (!(this.status.equals("OK") && (this.status.equals(anotherObj.status)))) {
            return false;
        }
        if (!this.resultStatus.equals(anotherObj.resultStatus)) {
            return false;
        }
        if (!this.resultPass.equals(anotherObj.resultPass)) {
            return false;
        }
        if (!this.resultFail.equals(anotherObj.resultFail)) {
            return false;
        }
        if (!this.resultAbort.equals(anotherObj.resultAbort)) {
            return false;
        }
        if (!this.resultWarning.equals(anotherObj.resultWarning)) {
            return false;
        }
        if (!this.resultError.equals(anotherObj.resultError)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TestResult [status=");
        builder.append(status);
        builder.append(", resultStatus=");
        builder.append(resultStatus);
        builder.append(", resultPass=");
        builder.append(resultPass);
        builder.append(", resultFail=");
        builder.append(resultFail);
        builder.append(", resultAbort=");
        builder.append(resultAbort);
        builder.append(", resultWarning=");
        builder.append(resultWarning);
        builder.append(", resultError=");
        builder.append(resultError);
        builder.append("]");
        return builder.toString();
    }

}
