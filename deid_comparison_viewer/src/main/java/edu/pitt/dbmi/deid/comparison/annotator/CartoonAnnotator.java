package edu.pitt.dbmi.deid.comparison.annotator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class CartoonAnnotator {
	
	private final List<Annotation> annotations = new ArrayList<>();
	
	private Pattern pattern;
	private Matcher matcher;

	public static void main(String[] args) {
		CartoonAnnotator cartoonAnnotator = new CartoonAnnotator();
		cartoonAnnotator.annotate("Chilly Willy attended Monsters University.");
		for (Annotation annotation : cartoonAnnotator.getAnnotations()) {
			System.out.println(annotation);
		}
	}
	
	public CartoonAnnotator() {
		try {
			derivePatternFromNameSet();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void annotate(String content) {
		annotations.clear();
		matcher = pattern.matcher(content);
		while (matcher.find()) {
			Annotation annotation = new Annotation();
			annotation.setAnnotationKind("Cartoon");
			annotation.setsPos((long)matcher.start());
			annotation.setePos((long)matcher.end());
			annotation.setSpannedText(matcher.group());
			annotations.add(annotation);
		}
	}
	
	private void derivePatternFromNameSet() throws IOException {
		final List<String> allCartoonNames = new ArrayList<String>();
		allCartoonNames.addAll(IOUtils.readLines(getClass().getResourceAsStream("DeID-institutions.txt")));	
		allCartoonNames.addAll(IOUtils.readLines(getClass().getResourceAsStream("DeID-names.txt")));	
		allCartoonNames.addAll(IOUtils.readLines(getClass().getResourceAsStream("DeID-places.txt")));	
		StringBuilder sb = new StringBuilder();
		for (String cartoonName : allCartoonNames) {
			String trimmedCartoonName = StringUtils.trimToEmpty(cartoonName);
			if (trimmedCartoonName.length()>0) {
				if (sb.length()>0) {
					sb.append("|");
				}
				sb.append("\\b" + trimmedCartoonName + "\\b");
			}
		}
		int patternFlags = Pattern.DOTALL;
		patternFlags |= Pattern.MULTILINE;
		pattern = Pattern.compile(sb.toString(),patternFlags);
		
	}

	public List<Annotation> getAnnotations() {
		return annotations;
	}


}
