package edu.pitt.dbmi.deid.comparison.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class DiedXmlUnpacker {
	
	public static final String PARAM_INPUT_FILE_PATH = "InputDeIdFilePath";
	public static final String PARAM_OUTPUT_DIRECTORY_PATH = "OutputDirectoryPath";
	
	public static void main(String[] args) {
		DiedXmlUnpacker unPacker = new DiedXmlUnpacker();
		try {
			unPacker.execute();
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
	}

	private void execute() throws JDOMException, IOException {
		final String deidFormattedFilePath = System.getProperty(PARAM_INPUT_FILE_PATH);
		final String outputDirectoryPath = System.getProperty(PARAM_OUTPUT_DIRECTORY_PATH);
		File deidFile = new File(deidFormattedFilePath);		
		File outputDirectory = new File(outputDirectoryPath);
		SAXBuilder saxBuilder = new SAXBuilder();
		Document document = saxBuilder.build(deidFile);
		Element dataSetElement = document.getRootElement();
		int reportIdx = 0;
		for (Element reportElement : dataSetElement.getChildren("Report")) {
			Element reportTextElement = reportElement.getChild("Report_Text");
			String reportContent = reportTextElement.getText();
			String reportName = "report" + StringUtils.leftPad(reportIdx+"", 5, "0") + ".txt";
			File reportFile = new File(outputDirectory, reportName);
			FileUtils.writeStringToFile(reportFile, reportContent);
			reportIdx++;
		}
	}

}
