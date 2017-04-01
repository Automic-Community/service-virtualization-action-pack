package com.automic.casv.entity;

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
 * {@link TestResult#getInstance(String)} passing the xml response to generate an instance of {@link TestResult}.
 * 
 */
public class TestResult {

    //
    private String status;

    private String resultStatus;

    private TestResult(String status, String resultStatus) {
        this.status = status;
        this.resultStatus = resultStatus;
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
     * We read the following xPaths
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

    /**
     * return if Test Result have passed or not.
     * 
     * @return true if test is passed and false if not
     */
    public boolean isTestPassed() {
        if (!"OK".equals(this.status)
                || (CommonUtil.checkNotEmpty(this.resultStatus) && !"ENDED".equals(this.resultStatus))) {
            return false;
        }

        return true;
    }

}
