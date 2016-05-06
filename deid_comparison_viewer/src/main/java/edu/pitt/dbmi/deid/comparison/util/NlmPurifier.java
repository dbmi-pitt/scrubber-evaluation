package edu.pitt.dbmi.deid.comparison.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class NlmPurifier {

	public static final String PARAM_INPUT_FILE_PATH = "InputDirectoryPath";

	public static void main(String[] args) {
		NlmPurifier purifier = new NlmPurifier();
		try {
			purifier.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void execute() throws IOException {
		final String inputDirectoryPath = System.getProperty(PARAM_INPUT_FILE_PATH);
		File inputDirectory = new File(inputDirectoryPath);
		File[] inputFiles = inputDirectory.listFiles();
		for (File inputFile : inputFiles) {
			if (inputFile.isFile() && inputFile.getName().matches("report\\d+\\.nphi\\.txt")) {
				List<String> fileLines = FileUtils.readLines(inputFile, "UTF-8");
				FileUtils.writeLines(inputFile, fileLines, "\n");
				System.out.println("Purified " + inputFile.getName());
			}
		
		}
	}

}
