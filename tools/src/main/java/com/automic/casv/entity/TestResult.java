package com.automic.casv.entity;

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

import com.automic.casv.exception.AutomicRuntimeException;

public class TestResult {

    private String status;

    private String resultStatus;

    public static TestResult getInstance(String xmlResponse) {
        try {
            InputSource source = new InputSource(new StringReader(xmlResponse));

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(source);

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            String status = xpath.evaluate("/invokeResult/status", document);
            String resultStatus = xpath.evaluate("/invokeResult/result/status", document);

            return new TestResult(status, resultStatus);

        } catch (IOException | ParserConfigurationException | SAXException | XPathExpressionException ex) {
            throw new AutomicRuntimeException(ex.getMessage());
        }

    }

    private TestResult(String status, String resultStatus) {
        this.status = status;
        this.resultStatus = resultStatus;
    }

    /**
     * return if Test Result have passed or not
     * 
     * @return
     */
    public boolean isTestPassed() {
        if (!"OK".equals(this.status) || (!"".equals(this.resultStatus) && !"ENDED".equals(this.resultStatus))) {
            return false;
        }

        return true;
    }

}
