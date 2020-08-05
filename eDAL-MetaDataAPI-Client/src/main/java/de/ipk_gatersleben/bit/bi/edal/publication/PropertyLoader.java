/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 *
 * We have chosen to apply the GNU General Public License (GPL) Version 3 (https://www.gnu.org/licenses/gpl-3.0.html)
 * to the copyrightable parts of e!DAL, which are the source code, the executable software, the training and
 * documentation material. This means, you must give appropriate credit, provide a link to the license, and indicate
 * if changes were made. You are free to copy and redistribute e!DAL in any medium or format. You are also free to
 * adapt, remix, transform, and build upon e!DAL for any purpose, even commercially.
 *
 *  Contributors:
 *       Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany
 */
package de.ipk_gatersleben.bit.bi.edal.publication;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel.AuthorsPanel;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;

/**
 * Central class to load and define graphical properties and parameters for the
 * data submission and publication tool
 * 
 * @author arendd
 *
 */
public class PropertyLoader {

	public static String PROGRAM_NAME = "IPK Data Publication System";

	public static final String AGREEMENT_PANEL_PROPERTY = "AGREEMENT_PANEL";
	public static final String AGREEMENT_PDF_PROPERTY = "AGREEMENT_PDF";

	private static Path userAttributeFile = Paths.get(System.getProperty("user.home"), ".eDAL", "attributes.txt");

	private static String propertyFile = null;

	public static URL PGP_CONTRACT_URL = null;

	public static URL ADOBE_GET_URL = null;
	
	public static URL INSTITUTE_LOGO_URL = null;

	public static Image EDAL_LOGO = null;

	public static Image EDAL_ICON = null;

	public static ImageIcon PDF_ICON = null;

	public static ImageIcon ADOBE_ICON = null;

	public static Properties props;

	public static Properties userValues;

	public static Path RUNNING_PATH = null;

	public static final Color HEADER_FOOTER_COLOR = new Color(210, 210, 210);

	public static final Color MAIN_BACKGROUND_COLOR = Color.WHITE;

	public static final Color LABEL_COLOR = new Color(0, 132, 184);

	public static final Color MAIN_FONT_COLOR = Color.BLACK;

	public static final Color DISABLED_FONT_COLOR = Color.LIGHT_GRAY;

	public static final Color OPEN_ACCESS_COLOR = new Color(0, 128, 0);

	public static final Color TABLE_HAS_VALUE_BACKGROUND_COLOR = (Color) UIManager.getDefaults()
			.get("ComboBox.selectionBackground");

	public static final Font OPEN_ACCESS_FONT = new Font(Font.SANS_SERIF, Font.BOLD, PropertyLoader.DEFAULT_FONT_SIZE);

	public static final int DEFAULT_FONT_SIZE = 12;

	public static final int ATTRIBUTE_LABEL_FONT_SIZE = 13;

	public static final int SMALL_BUTTON_FONT_SIZE = 11;

	public static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, PropertyLoader.DEFAULT_FONT_SIZE);

	public static final Font AGREEMENT_PANEL_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 12);

	public static final int TITLE_PANEL_HEIGHT = 2 * DEFAULT_FONT_SIZE;

	public static final int SUBJECTS_PANEL_HEIGHT = 4 * DEFAULT_FONT_SIZE;

	public static final int PUBLISHER_PANEL_HEIGHT = 5 * DEFAULT_FONT_SIZE;

	public static final int AUTHOR_PANEL_HEIGHT = 5 * DEFAULT_FONT_SIZE;

	public static final int EMBARGO_PANEL_HEIGHT = 11 * DEFAULT_FONT_SIZE;

	public static final int LICENSE_PANEL_HEIGHT = 14 * DEFAULT_FONT_SIZE;

	public static final int DESCRIPTION_PANEL_HEIGHT = 4 * DEFAULT_FONT_SIZE;

	public static final int TWO_LINE_HEIGHT = 2 * DEFAULT_FONT_SIZE;

	public static final Dimension MINIMUM_DIM_PUBLICATION_FRAME = new Dimension(1024, 800);

	public static final int ATTRIBUTE_PANEL_WIDTH = 680;
	public static final int ATTRIBUTE_LABEL_WIDTH = 120;

	public static List<String> SUPPORTED_LICENSES = null;
	public static List<String> SUPPORTED_LICENSES_LEGAL_CODE_URL = null;
	public static List<String> SUPPORTED_LICENSES_HUMAN_READABLE_URL = null;

	public static List<String> RESOURCE_TYPES = null;

	public static AttributeLabel UPLOADPATH_LABEL, TITLE_LABEL, DESCRIPTION_LABEL, AUTHORS_LABEL, SUBJECTS_LABEL,
			LANGUAGE_LABEL, LICENSE_LABEL, PUBLISHER_LABEL, EMBARGO_LABEL, RESOURCE_LABEL;

	private static void initGraphicComponents() {
		UPLOADPATH_LABEL = new AttributeLabel(PropertyLoader.props.getProperty("UPLOAD_LABEL"),
				PropertyLoader.props.getProperty("UPLOAD_TOOLTIP"));
		TITLE_LABEL = new AttributeLabel(PropertyLoader.props.getProperty("TITLE_LABEL"),
				PropertyLoader.props.getProperty("TITLE_TOOLTIP"));
		DESCRIPTION_LABEL = new AttributeLabel(PropertyLoader.props.getProperty("DESCRIPTION_LABEL"),
				PropertyLoader.props.getProperty("DESCRIPTION_TOOLTIP"));
		AUTHORS_LABEL = new AttributeLabel(PropertyLoader.props.getProperty("AUTHORS_LABEL"),
				PropertyLoader.props.getProperty("AUTHORS_TOOLTIP"));
		SUBJECTS_LABEL = new AttributeLabel(PropertyLoader.props.getProperty("SUBJECTS_LABEL"),
				PropertyLoader.props.getProperty("SUBJECTS_TOOLTIP"));
		LANGUAGE_LABEL = new AttributeLabel(PropertyLoader.props.getProperty("LANGUAGE_LABEL"),
				PropertyLoader.props.getProperty("LANGUAGE_TOOLTIP"));
		LICENSE_LABEL = new AttributeLabel(PropertyLoader.props.getProperty("LICENSE_LABEL"),
				PropertyLoader.props.getProperty("LICENSE_TOOLTIP"));
		PUBLISHER_LABEL = new AttributeLabel(PropertyLoader.props.getProperty("PUBLISHER_LABEL"),
				PropertyLoader.props.getProperty("PUBLISHER_TOOLTIP"));
		EMBARGO_LABEL = new AttributeLabel(PropertyLoader.props.getProperty("EMBARGO_LABEL"),
				PropertyLoader.props.getProperty("EMBARGO_TOOLTIP"));
		RESOURCE_LABEL = new AttributeLabel(PropertyLoader.props.getProperty("RESOURCE_LABEL"),
				PropertyLoader.props.getProperty("RESOURCE_TOOLTIP"));
	}

	private static void loadEdalLogos() {
		
		INSTITUTE_LOGO_URL = PropertyLoader.class.getResource(PropertyLoader.props.getProperty("INSTITUTE_LOGO"));
		EDAL_LOGO = new ImageIcon(PropertyLoader.class.getResource("edal_scaled.png")).getImage();
		EDAL_ICON = new ImageIcon(PropertyLoader.class.getResource("edal_icon.png")).getImage();
		PDF_ICON = new ImageIcon(PropertyLoader.class.getResource("pdf_icon.png"));
		Image image = PDF_ICON.getImage();
		Image newimg = image.getScaledInstance(15, 20, java.awt.Image.SCALE_SMOOTH);
		PDF_ICON = new ImageIcon(newimg);
		ADOBE_ICON = new ImageIcon(PropertyLoader.class.getResource("adobe.png"));
		Image a_image = ADOBE_ICON.getImage();
		Image new_a_img = a_image.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
		ADOBE_ICON = new ImageIcon(new_a_img);
	}

	private static void loadProperties() {

		props = new Properties();

		try {
			props.load(new InputStreamReader(PropertyLoader.class.getResourceAsStream(propertyFile), "UTF-8"));
		} catch (IOException | NullPointerException e) {

			JOptionPane.showMessageDialog(null,
					"Unable to load property file '" + propertyFile + "' : " + e.getMessage(), "ERROR",
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		SUPPORTED_LICENSES = new ArrayList<String>();
		SUPPORTED_LICENSES_LEGAL_CODE_URL = new ArrayList<String>();
		SUPPORTED_LICENSES_HUMAN_READABLE_URL = new ArrayList<String>();
		int i = 1;

		while (PropertyLoader.props.getProperty("LICENSE_" + i) != null) {
			SUPPORTED_LICENSES.add(PropertyLoader.props.getProperty("LICENSE_" + i));
			SUPPORTED_LICENSES_LEGAL_CODE_URL
					.add(PropertyLoader.props.getProperty("SUPPORTED_LICENSES_LEGAL_CODE_URL_" + i));
			SUPPORTED_LICENSES_HUMAN_READABLE_URL
					.add(PropertyLoader.props.getProperty("SUPPORTED_LICENSES_HUMAN_READABLE_URL_" + i));
			i++;
		}

		RESOURCE_TYPES = new ArrayList<String>();

		i = 1;
		while (PropertyLoader.props.getProperty("RESOURCE_TYPE_" + i) != null) {
			RESOURCE_TYPES.add(PropertyLoader.props.getProperty("RESOURCE_TYPE_" + i));
			i++;
		}
		
		PROGRAM_NAME = PropertyLoader.props.getProperty("PROGRAM_NAME");

	}

	private static void loadRunnigPath() {

		try {
			RUNNING_PATH = Paths.get(PropertyLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (Exception e) {
			RUNNING_PATH = Paths.get(System.getProperty("user.home"));
		}
	}

	private static void loadUserValues() {

		userValues = new Properties();

		try {
			userValues.load(new FileInputStream(userAttributeFile.toFile()));
		} catch (IOException | NullPointerException e) {
			try {
				Files.createFile(userAttributeFile);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null,
						"Unable to load/create user file '" + userAttributeFile + "' : " + e.getMessage(), "ERROR",
						JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		}

	}

	public static void initialize(String propsFile) {

		propertyFile = propsFile;

		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			ClientDataManager.logger.info("Use " + UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				ClientDataManager.logger.info("Use " + UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e1) {
				e1.printStackTrace();
			}

		}

		Locale.setDefault(Locale.ENGLISH);

		try {
			PGP_CONTRACT_URL = new URL("http://edal-pgp.ipk-gatersleben.de/document/PGP-contract.pdf");
			ADOBE_GET_URL = new URL("https://get.adobe.com/de/reader/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		loadProperties();
		loadUserValues();
		loadRunnigPath();
		loadEdalLogos();
		initGraphicComponents();

		ToolTipManager.sharedInstance().setInitialDelay(0);
	}

	private static void saveUserValue() {

		try {
			OutputStream out = new FileOutputStream(userAttributeFile.toFile());
			userValues.store(out, "Update");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String loadSubjectsString() {

		String subjects = PropertyLoader.userValues.getProperty("SUBJECTS");

		if (subjects == null || subjects.isEmpty()) {
			return PropertyLoader.props.getProperty("DEFAULT_SUBJECTS_STRING");
		} else
			return subjects;

	}

	public static String loadPublisherString() {

		String publisherString = PropertyLoader.userValues.getProperty("PUBLISHER");

		if (publisherString == null || publisherString.isEmpty()) {
			return PropertyLoader.props.getProperty("DEFAULT_PUBLISHER_STRING");
		} else {

			if (publisherString.equals(PropertyLoader.props.getProperty("DEFAULT_PUBLISHER_STRING"))) {

				StringBuffer buffer = new StringBuffer();

				String[] publisher = publisherString.split(";");

				for (int i = 0; i < publisher.length; i++) {

					buffer.append(publisher[i]);

					if (i != publisher.length - 1) {
						buffer.append(", ");
					}
				}
				return buffer.toString();
			}
			else {
				return PropertyLoader.props.getProperty("DEFAULT_PUBLISHER_STRING");
			}
		}
	}

	public static String loadAuthorsString() {
		String authorsString = PropertyLoader.userValues.getProperty("AUTHORS");

		if (authorsString == null || authorsString.isEmpty()) {
			return PropertyLoader.props.getProperty("DEFAULT_AUTHORS_STRING");
		} else {

			StringBuffer buffer = new StringBuffer();

			String[] authors = authorsString.split(";");

			for (int i = 0; i < authors.length; i++) {

				String[] author = authors[i].split("@");
				if (author[0].equals(AuthorsPanel.CREATOR) || author[0].equals(AuthorsPanel.CONTRIBUTOR)) {

					/* author is a NaturalPerson */
					if (!author[2].isEmpty() && !author[3].isEmpty()) {

						buffer.append(author[3]);
						buffer.append(", ");
						buffer.append(author[2]);
						if (i != authors.length - 1) {
							buffer.append("; ");
						}
					}
					/* author is a LegalPerson */
					else if (!author[4].isEmpty()) {
						buffer.append(author[4]);
						if (i != authors.length - 1) {
							buffer.append("; ");
						}
					}
				} else {
					return PropertyLoader.props.getProperty("DEFAULT_AUTHORS_STRING");
				}
			}
			return buffer.toString();
		}

	}

	public static void setUserValue(String key, String value) {
		PropertyLoader.userValues.setProperty(key, value);
		PropertyLoader.saveUserValue();

	}

	public static String loadLanguageString() {
		String language = PropertyLoader.userValues.getProperty("LANGUAGE");

		if (language == null || language.isEmpty()) {
			return PropertyLoader.props.getProperty("DEFAULT_LANGUAGE_STRING");
		} else
			return language;
	}

	public static String loadResourceString() {
		String resourceType = PropertyLoader.userValues.getProperty("RESOURCE_TYPE");

		if (resourceType == null || resourceType.isEmpty()) {
			return PropertyLoader.props.getProperty("DEFAULT_RESOURCE_STRING");
		} else
			return resourceType;
	}

	public static String loadUploadPathString() {
		String uploadPath = PropertyLoader.userValues.getProperty("UPLOAD_PATH");

		if (uploadPath == null || uploadPath.isEmpty()) {
			return PropertyLoader.props.getProperty("DEFAULT_UPLOAD_PATH_STRING");
		} else if (Files.exists(Paths.get(uploadPath))) {
			return uploadPath;
		} else {
			setUserValue("UPLOAD_PATH", PropertyLoader.props.getProperty("DEFAULT_UPLOAD_PATH_STRING"));
			return PropertyLoader.props.getProperty("DEFAULT_UPLOAD_PATH_STRING");
		}
	}
}