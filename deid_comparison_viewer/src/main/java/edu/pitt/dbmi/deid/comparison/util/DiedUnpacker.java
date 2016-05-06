package edu.pitt.dbmi.deid.comparison.util;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class DiedUnpacker {
	
	public static final String PARAM_INPUT_FILE_PATH = "InputDeIdFilePath";

	public static void main(String[] args) {
		DiedUnpacker unPacker = new DiedUnpacker();
		unPacker.execute();
	}

	private void execute() {
		final String deidFormattedFilePath = System.getProperty(PARAM_INPUT_FILE_PATH);
		System.out.println("Unpacking " + deidFormattedFilePath);
		File deidFile = new File(deidFormattedFilePath);
		try {
			String deidContent = FileUtils.readFileToString(deidFile);
			final String[] reports = deidContent.split("E_O_R");
			int reportIdx = 0;
			File reportsDirectory = deidFile.getParentFile();
			for (String report : reports) {
				if (report.trim().length() > 0) {
					Pattern pattern = Pattern.compile("6\\.24\\.5\\.1\\]\\s+");
					Matcher matcher = pattern.matcher(report);
					if (matcher.find()) {
						String content = report.substring(matcher.end());
						String reportName = "report" + StringUtils.leftPad(reportIdx+"", 5, "0") + ".txt";
						File reportFile = new File(reportsDirectory, reportName);
						FileUtils.writeStringToFile(reportFile, content);
					}
					reportIdx++;
				}				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
