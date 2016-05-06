package edu.pitt.dbmi.deid.comparison.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class DeidInputFileBuilder {
	public static void main(String[] args) {
		DeidInputFileBuilder deidInputFileBuilder = new DeidInputFileBuilder();
		try {
			deidInputFileBuilder.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void execute() throws IOException {
		Element dataSetElement = new Element("Dataset");
		Document document = new Document(dataSetElement);
		File phiDirectory = new File("E:\\work\\deid\\corpora\\deepphe_reidentified\\cartoon");
		File[] reportFiles = phiDirectory.listFiles();
		for (File reportFile : reportFiles) {
			if (reportFile.getName().matches("report\\d+\\.txt")) {
				System.out.println("Processing " + reportFile.getAbsolutePath());
				Element reportElement = new Element("Report");
				Element patientIdElement = new Element("Patient_ID");
				patientIdElement.setText(reportFile.getName());
				Element reportTextElement = new Element("Report_Text");
				reportTextElement.setText(FileUtils.readFileToString(reportFile, "UTF-8"));
				reportElement.addContent(patientIdElement);
				reportElement.addContent(reportTextElement);
				dataSetElement.addContent(reportElement);
			}
		}
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
//		xmlOutput.output(document, System.out);
		xmlOutput.output(document,
				new FileWriter("E:\\work\\deid\\corpora\\deepphe_reidentified\\cartoon_deid_input.xml"));
	}

}
