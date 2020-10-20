package com.automic.casv.actions;

import java.io.File;

import com.automic.casv.constants.Constants;
import com.automic.casv.entity.TestResult;
import com.automic.casv.exception.AutomicException;
import com.automic.casv.util.ConsoleWriter;
import com.automic.casv.validator.CaSvValidator;

/**
 * This class is used to compare two test results.
 *
 */
public class CompareTestResultsAction extends AbstractAction {

    private File baselineXML;

    private File testXML;

    public CompareTestResultsAction() {
        addOption(Constants.BASELINE_XML, true, "Baseline XML file");
        addOption(Constants.TEST_XML, true, "Test XML file");

    }

    @Override
    protected void execute() throws AutomicException {
        prepareInputs();

        TestResult baselineTestResult = new TestResult(baselineXML);
        TestResult otherTestResult = new TestResult(testXML);
        ConsoleWriter.writeln("Baseline XML - " + baselineTestResult);
        ConsoleWriter.writeln("Test XML " + otherTestResult);
        ConsoleWriter.writeln("UC4RB_SV_COMPARE_RESULT::="
                + Boolean.toString(baselineTestResult.equals(otherTestResult)).toUpperCase());
    }

    private void prepareInputs() throws AutomicException {
        String temp = getOptionValue(Constants.BASELINE_XML);
        CaSvValidator.checkNotEmpty(temp, "Baseline XML");
        baselineXML = new File(temp);
        CaSvValidator.checkFileExists(baselineXML, "Baseline XML temp file path");

        temp = getOptionValue(Constants.TEST_XML);
        CaSvValidator.checkNotEmpty(temp, "Test XML");
        testXML = new File(temp);
        CaSvValidator.checkFileExists(testXML, "Test XML temp file path");
    }

}
