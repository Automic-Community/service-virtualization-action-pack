package com.automic.casv.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.automic.casv.exception.AutomicException;
import com.automic.casv.util.ConsoleWriter;

/**
 * 
 * This entity represents the Test results after running Tests, Test Suite or Model Archive. call
 * {@link TestResult#getInstance(String)} or {@link TestResult#getInstance(File)} to generate an instance of
 * {@link TestResult}.
 * 
 */
public class TestResult {

    private String invokeStatus;

    private String resultStatus;
    private int passCount;
    private int failCount;
    private int abortCount;
    private int warningCount;
    private int errorCount;

    private boolean async;
    private String callBackId;

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
    public TestResult(String xmlResponse, boolean async) throws AutomicException {
        ConsoleWriter.writeln(xmlResponse);
        this.async = async;
        try {
            InputSource source = new InputSource(new StringReader(xmlResponse));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(source);
            parseXMLDocument(document);

            BufferedReader br = new BufferedReader(new StringReader(xmlResponse));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
            ConsoleWriter.writeln("UC4RB_SV_TEST_RESPONSE::=" + sb.toString());

        } catch (IOException | ParserConfigurationException | SAXException | XPathExpressionException ex) {
            ConsoleWriter.writeln(ex);
            throw new AutomicException("Malformed XML" + ex.getMessage());
        }
    }

    public TestResult(File file) throws AutomicException {
        try (FileInputStream fis = new FileInputStream(file)) {
            InputSource source = new InputSource(fis);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(source);
            ConsoleWriter.writeln("Parsing XML for file " + file.getName());
            parseXMLDocument(document);
        } catch (IOException | ParserConfigurationException | SAXException | XPathExpressionException ex) {
            ConsoleWriter.writeln(ex);
            throw new AutomicException("Malformed XML" + ex.getMessage());
        }
    }

    private void parseXMLDocument(Document document) throws XPathExpressionException, AutomicException {
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        invokeStatus = xpath.evaluate("/invokeResult/status", document);
        if ("OK".equalsIgnoreCase(invokeStatus)) {
            if (async) {
                callBackId = xpath.evaluate("/invokeResult/result/callbackKey", document);
            } else {
                resultStatus = xpath.evaluate("/invokeResult/result/status", document);
                try {
                    passCount = Integer.parseInt(xpath.evaluate("/invokeResult/result/pass/@count", document));
                    failCount = Integer.parseInt(xpath.evaluate("/invokeResult/result/fail/@count", document));
                    abortCount = Integer.parseInt(xpath.evaluate("/invokeResult/result/abort/@count", document));
                    warningCount = Integer.parseInt(xpath.evaluate("/invokeResult/result/warning/@count", document));
                    errorCount = Integer.parseInt(xpath.evaluate("/invokeResult/result/error/@count", document));
                } catch (NumberFormatException nfe) {
                    throw new AutomicException("XML doesnot contain valid count for (pass/fail/abort/warning/error)");
                }
            }
        }
    }

    /**
     * return if Test Result have passed or not
     * 
     * @return true if test passed else false
     */
    public boolean isTestSucceeded() {
        boolean isSuccess = false;
        if ("OK".equalsIgnoreCase(this.invokeStatus)) {
            if (async) {
                isSuccess = true;
            } else {
                isSuccess = "ENDED".equalsIgnoreCase(this.resultStatus);
            }
        }
        return isSuccess;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof TestResult) || async) {
            return false;
        }

        TestResult other = (TestResult) obj;

        return isTestSucceeded() && other.isTestSucceeded() && (async == other.async)
                && (passCount == other.getPassCount()) && (failCount == other.getFailCount())
                && (warningCount == other.getWarningCount()) && (errorCount == other.getAbortCount())
                && (abortCount == other.getAbortCount());
    }

    public String getInvokeStatus() {
        return invokeStatus;
    }

    public String getResultStatus() {
        return resultStatus;
    }

    public int getPassCount() {
        return passCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public int getAbortCount() {
        return abortCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public String getCallBackId() {
        return callBackId;
    }

    public void logInfo() {
        if (callBackId != null) {
            ConsoleWriter.writeln("UC4RB_SV_TEST_CALLBACK::=" + callBackId);
        } else {
            ConsoleWriter.writeln("UC4RB_SV_TEST_PASSCOUNT::=" + passCount);
            ConsoleWriter.writeln("UC4RB_SV_TEST_FAILCOUNT::=" + failCount);
            ConsoleWriter.writeln("UC4RB_SV_TEST_ABORTCOUNT::=" + abortCount);
            ConsoleWriter.writeln("UC4RB_SV_TEST_WARNCOUNT::=" + warningCount);
            ConsoleWriter.writeln("UC4RB_SV_TEST_ERRORCOUNT::=" + errorCount);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TestResult [status=").append(invokeStatus);
        builder.append(", resultStatus=").append(resultStatus);
        builder.append(", resultPass=").append(passCount);
        builder.append(", resultFail=").append(failCount);
        builder.append(", resultAbort=").append(abortCount);
        builder.append(", resultWarning=").append(warningCount);
        builder.append(", resultError=").append(errorCount).append("]");
        return builder.toString();
    }

}
