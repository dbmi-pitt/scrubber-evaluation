package edu.pitt.dbmi.deid.comparison.viewer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;

import edu.pitt.dbmi.deid.comparison.annotator.Annotation;
import edu.pitt.dbmi.deid.comparison.explanation.Explanation;
import edu.pitt.dbmi.deid.comparison.util.ConversionUtils;
import edu.pitt.dbmi.deid.comparison.util.JdomUtils;

public class ExplanationData {

	private final Map<String, Explanation> explanationMap = new HashMap<String, Explanation>();
	private final Map<String, Annotation> annotationMap = new HashMap<String, Annotation>();
	private File lastExplanation = null;
	private int maxRevision = Integer.MIN_VALUE;
	private final Pattern pattern = Pattern.compile("\\d\\d\\d\\d\\d");

	public static void main(String[] args) {
		ExplanationData explanationData = new ExplanationData();
		try {
			explanationData.initialize();
			Explanation explanationToAdd = new Explanation("A new reason", "A new reason to explain");
			explanationData.getExplanationMap().put(explanationToAdd.getHeader(), explanationToAdd);
			explanationData.saveState();
			
			explanationData.getAnnotationMap().clear();
			explanationData.getExplanationMap().clear();
			explanationData.setMaxRevision(Integer.MIN_VALUE);
			explanationData.initialize();
			explanationToAdd = new Explanation("A second new reason", "A second new reason to explain");
			explanationData.getExplanationMap().put(explanationToAdd.getHeader(), explanationToAdd);
			explanationData.saveState();	
			
		} catch (IOException e) {
			e.printStackTrace();
		};
	}

	public ExplanationData() {
	}
	
	public ExplanationData(Explanation[] explanationArray) {
		for (Explanation eplanationData : explanationArray) {
			explanationMap.put(eplanationData.getHeader(), eplanationData);
		}
	}

	public void initialize() throws IOException {
		lastExplanation = findLastExplanation();
		if (lastExplanation == null) {
			System.err.println("Failed to find lastExplanation File.");
			System.exit(1);
		}
		else {
			readState(lastExplanation);
		}
	}

	private File findLastExplanation() throws IOException {
		File lastExplanation = null;
		String userHome = System.getProperty("user.home");
		File explanationsDirectory = new File(userHome);
		explanationsDirectory = new File(explanationsDirectory, ".scrubber_evalutation/explanations");
		FileUtils.forceMkdir(explanationsDirectory);
		if (explanationsDirectory.exists() && explanationsDirectory.isDirectory()) {
			if (explanationsDirectory.listFiles().length == 0) {
				lastExplanation = storeEmptyExplanationsFile(explanationsDirectory);
				maxRevision = 0;
			}
			else {
				for (File lastExplanationCandidate : explanationsDirectory.listFiles()) {
					boolean isPotentialMaxRevision = true;
					isPotentialMaxRevision = isPotentialMaxRevision && lastExplanationCandidate.exists();
					isPotentialMaxRevision = isPotentialMaxRevision && lastExplanationCandidate.isFile();
					isPotentialMaxRevision = isPotentialMaxRevision
							&& lastExplanationCandidate.getName().matches("explanations_\\d\\d\\d\\d\\d\\.xml");
					if (isPotentialMaxRevision) {
						Matcher matcher = pattern.matcher(lastExplanationCandidate.getName());
						if (matcher.find()) {
							int maxRevisionCandidate = new Integer(matcher.group());
							if (maxRevisionCandidate > maxRevision) {
								lastExplanation = lastExplanationCandidate;
								maxRevision = maxRevisionCandidate;
							}
						}
					}
				}
			}
		}
		return lastExplanation;
	}
	
	private File storeEmptyExplanationsFile(File explanationsDirectory) throws IOException {
		File lastExplanation = new File(explanationsDirectory, "explanations_00000.xml");
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<Data>\n");
		sb.append("   <Annotations/>\n");
		sb.append("   <Explanations/>\n");
		sb.append("</Data>\n");
		FileUtils.write(lastExplanation, sb.toString());
		return lastExplanation;
}

	@SuppressWarnings("unused")
	private File findLastExplanationUsingClassPath() {
		File lastExplanation = null;
		
		final URL explanationsDirectoryUrl = getClass().getResource("explanations");
		String explanationsDirectoryLocation = explanationsDirectoryUrl.getFile();
		explanationsDirectoryLocation = explanationsDirectoryLocation.replaceAll("target/classes", "src/main/resources");
		final File explanationsDirectory = new File(explanationsDirectoryLocation);
		if (explanationsDirectory.exists() && explanationsDirectory.isDirectory()) {
			for (File lastExplanationCandidate : explanationsDirectory.listFiles()) {
				boolean isPotentialMaxRevision = true;
				isPotentialMaxRevision = isPotentialMaxRevision && lastExplanationCandidate.exists();
				isPotentialMaxRevision = isPotentialMaxRevision && lastExplanationCandidate.isFile();
				isPotentialMaxRevision = isPotentialMaxRevision
						&& lastExplanationCandidate.getName().matches("explanations_\\d\\d\\d\\d\\d\\.xml");
				if (isPotentialMaxRevision) {
					Matcher matcher = pattern.matcher(lastExplanationCandidate.getName());
					if (matcher.find()) {
						int maxRevisionCandidate = new Integer(matcher.group());
						if (maxRevisionCandidate > maxRevision) {
							lastExplanation = lastExplanationCandidate;
							maxRevision = maxRevisionCandidate;
						}
					}
				}
			}
		}
		return lastExplanation;
	}

	public Annotation materializeAnnotation(Annotation requestAnnotation) {
		Annotation responseAnnotation = null;
		for (Annotation candidateAnnotation : annotationMap.values()) {			
			if (candidateAnnotation.getKey().equals(requestAnnotation.getKey())) {
				responseAnnotation = candidateAnnotation;
				break;
			}
		}
		if (responseAnnotation == null) {
			responseAnnotation = requestAnnotation;
			annotationMap.put(requestAnnotation.getKey(), requestAnnotation);
		}
		return responseAnnotation;
	}

	private void readState(File lastExplanationFile) throws IOException {
		String lastExplanationXml = FileUtils.readFileToString(lastExplanationFile);
		Document lastExplanationDocument = JdomUtils
				.convertXmlToDocument(lastExplanationXml);
		Element dataElement = lastExplanationDocument.getRootElement();
		for (Element explanationElement : dataElement.getChild("Explanations").getChildren("Explanation")) {
			String header = explanationElement.getAttributeValue("id");
			String content = explanationElement.getText();
			Explanation explanation = new Explanation(header, content);
			explanationMap.put(explanation.getHeader(), explanation);
		}
		for (Element annotationElement : dataElement.getChild("Annotations").getChildren("Annotation")) {
			String reportBaseName = annotationElement.getAttributeValue("report");
			String sPosAsString = annotationElement.getAttributeValue("sPos");
			String ePosAsString = annotationElement.getAttributeValue("ePos");
			String redactor = annotationElement.getAttributeValue("redactor");
			String explanationKey = annotationElement.getAttributeValue("explanation");
			String spannedText = annotationElement.getText();
			Annotation annotation = new Annotation();
			annotation.setReport(reportBaseName);
			annotation.setAnnotationKind("NA");
			annotation.setsPos(new Long(sPosAsString));
			annotation.setePos(new Long(ePosAsString));
			annotation.setRedactor(redactor);
			annotation.setSpannedText(spannedText);
			if (explanationKey != null) {
				Explanation explanation = explanationMap.get(explanationKey);
				annotation.setExplanation(explanation);
				explanation.getAnnotations().add(annotation);
				annotationMap.put(annotation.getKey(), annotation);
			}
		}
	}

	public void saveState() throws IOException {
		Element dataElement = new Element("Data");
		Document doc = new Document(dataElement);
		Element annotationsElement = new Element("Annotations");
		dataElement.addContent(annotationsElement);
		for (Annotation annot : annotationMap.values()) {
			Element annotElement = new Element("Annotation");
			annotElement.setAttribute("report", annot.getReport());
			annotElement.setAttribute("redactor", annot.getRedactor());
			annotElement.setAttribute("sPos", ConversionUtils.formatLongAsString(annot.getsPos()));
			annotElement.setAttribute("ePos", ConversionUtils.formatLongAsString(annot.getePos()));
			if (annot.getExplanation() != null) {
				annotElement.setAttribute("explanation", annot.getExplanation().getHeader());
			} else {
				annotElement.setAttribute("explanation", "TBD");
			}
			annotationsElement.addContent(annotElement);
		}
		Element explanationsElement = new Element("Explanations");
		dataElement.addContent(explanationsElement);
		for (Explanation explanation : explanationMap.values()) {
			Element explanationElement = new Element("Explanation");
			explanationElement.setAttribute("id", explanation.getHeader());
			explanationElement.setText(explanation.getContent());
			explanationsElement.addContent(explanationElement);
		}
//		String lastRevisionAsString = StringUtils.leftPad(maxRevision + "", 5, "0");
		String nextRevisionAsString = StringUtils.leftPad(maxRevision + 1 + "", 5, "0");
		maxRevision++;
		File nextExplanationsFile = new File(lastExplanation.getParent(),
				"explanations_" + nextRevisionAsString + ".xml");
		String xml = JdomUtils.convertDocumentToString(doc, Format.getPrettyFormat());
		FileUtils.write(nextExplanationsFile, xml);
	}
	
	public Explanation[] asExplanationArray() {
		final Explanation[] dataAsArray = new Explanation[explanationMap.size()];
		int nextExplanation = 0;
		for (Explanation explanation : explanationMap.values()) {
			dataAsArray[nextExplanation] = explanation;
			nextExplanation++;
		}
		return dataAsArray;
	}

	public Map<String, Explanation> getExplanationMap() {
		return explanationMap;
	}

	public Map<String, Annotation> getAnnotationMap() {
		return annotationMap;
	}
	
	public int getMaxRevision() {
		return maxRevision;
	}
	
	public void setMaxRevision(int maxRevision) {
		this.maxRevision = maxRevision;
	}

	

}
