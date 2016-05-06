package edu.pitt.dbmi.deid.comparison.viewer;

import java.awt.Color;
import java.util.Random;

public class RedactedContentMerger {

	private String contentOne;
	private String contentTwo;
	private String contentMerged;

	private Color[] colorArray;

	public void merge() {
		
		Random random = new Random();
		Color colorOneOnly = generateRandomColor(random, Color.WHITE);
		Color colorTwoOnly = generateRandomColor(random, Color.WHITE);
		Color colorMerged = generateRandomColor(random, Color.WHITE);
		colorOneOnly = Color.cyan;
		colorTwoOnly = Color.magenta;
		colorMerged = Color.orange;
		
		if (contentOne.length() == contentTwo.length()) {
			final char[] contentOneArray = contentOne.toCharArray();
			final char[] contentTwoArray = contentTwo.toCharArray();
			final char[] contentMergedArray = new char[contentOne.length()];
			colorArray = new Color[contentOneArray.length];
			for (int idx = 0; idx < contentOne.length(); idx++) {
				if (contentOneArray[idx] == '\u25A0' &&
					contentTwoArray[idx] == '\u25A0') {
					contentMergedArray[idx] = '\u25A0';
					colorArray[idx] = colorMerged;
				}
				else if (contentOneArray[idx] == '\u25A0') { 
					contentMergedArray[idx] = '\u25A0';
					colorArray[idx] = colorOneOnly;
				}
				else if (contentTwoArray[idx] == '\u25A0') { 
					contentMergedArray[idx] = '\u25A0';
					colorArray[idx] = colorTwoOnly;
				}
				else {
					contentMergedArray[idx] = contentOneArray[idx];
					colorArray[idx] = Color.WHITE;
				}
			}
			contentMerged = new String(contentMergedArray);
		}
		else {
			System.err.println("Content lengths must be the same here");
		}
	}

	public Color generateRandomColor(Random random, Color mix) {

		int red = random.nextInt(256);
		int green = random.nextInt(256);
		int blue = random.nextInt(256);

		// mix the color
		if (mix != null) {
			red = (red + mix.getRed()) / 2;
			green = (green + mix.getGreen()) / 2;
			blue = (blue + mix.getBlue()) / 2;
		}

		Color color = new Color(red, green, blue);
		return color;
	}

	public String getContentOne() {
		return contentOne;
	}

	public String getContentTwo() {
		return contentTwo;
	}

	public String getContentMerged() {
		return contentMerged;
	}

	public void setContentOne(String contentOne) {
		this.contentOne = contentOne;
	}

	public void setContentTwo(String contentTwo) {
		this.contentTwo = contentTwo;
	}

	public void setContentMerged(String contentMerged) {
		this.contentMerged = contentMerged;
	}

	public Color[] getColorArray() {
		return colorArray;
	}

	public void setColorArray(Color[] colorArray) {
		this.colorArray = colorArray;
	}

}
