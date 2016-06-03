package edu.pitt.dbmi.deid.comparison.explanation;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

public class FontBuilder {
	
		public Font getAFont() {
			// initial strings of desired fonts
			String[] desiredFonts = { "French Script", "FrenchScript", "Script" };

			String[] existingFamilyNames = null; // installed fonts
			String fontName = null; // font we'll use

			// Search for all installed font families. The first
			// call may take a while on some systems with hundreds of
			// installed fonts, so if possible execute it in idle time,
			// and certainly not in a place that delays painting of
			// the UI (for example, when bringing up a menu).
			//
			// In systems with malformed fonts, this code might cause
			// serious problems; use the latest JRE in this case. (You'll
			// see the same problems if you use Swing's HTML support or
			// anything else that searches for all fonts.) If this call
			// causes problems for you under the latest JRE, please let
			// us know.
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			if (ge != null) {
				existingFamilyNames = ge.getAvailableFontFamilyNames();
			}

			// See if there's one we like.
			if ((existingFamilyNames != null) && (desiredFonts != null)) {
				int i = 0;
				while ((fontName == null) && (i < desiredFonts.length)) {

					// Look for a font whose name starts with desiredFonts[i].
					int j = 0;
					while ((fontName == null) && (j < existingFamilyNames.length)) {
						if (existingFamilyNames[j].startsWith(desiredFonts[i])) {

							// We've found a match. Test whether it can display
							// the Latin character 'A'. (You might test for
							// a different character if you're using a different
							// language.)
							Font f = new Font(existingFamilyNames[j], Font.PLAIN, 1);
							if (f.canDisplay('A')) {
								fontName = existingFamilyNames[j];
								System.out.println("Using font: " + fontName);
							}
						}

						j++; // Look at next existing font name.
					}
					i++; // Look for next desired font.
				}
			}

			// Return a valid Font.
			if (fontName != null) {
				return new Font(fontName, Font.PLAIN, 36);
			} else {
				return new Font("Serif", Font.ITALIC, 36);
			}
		}
	}
