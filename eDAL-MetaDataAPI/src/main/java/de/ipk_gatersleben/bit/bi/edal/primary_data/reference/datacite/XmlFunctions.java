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
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;

/**
 * Usefull function to handle XML files.
 * 
 * @author arendd
 */
public class XmlFunctions {

	/**
	 * Parse a string containing XML.
	 * 
	 * @param xmlString
	 *            the XML string
	 * @return XML DOM document
	 */
	public static Document parse(final String xmlString) {
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setNamespaceAware(true);
		dbf.setCoalescing(true);
		dbf.setIgnoringElementContentWhitespace(true);
		dbf.setIgnoringComments(true);

		/** define inputs */
		final StringReader stringReader = new StringReader(xmlString);
		final InputSource inputSource = new InputSource(stringReader);
		/** parse inputs */

		DocumentBuilder builder;
		Document document = null;
		try {
			builder = dbf.newDocumentBuilder();

			document = builder.parse(inputSource);

			document.normalizeDocument();
		} catch (final ParserConfigurationException e) {
			DataManager.getImplProv().getLogger().error(e.getMessage());
		} catch (final SAXException e) {
			DataManager.getImplProv().getLogger().error(e.getMessage());
		} catch (final IOException e) {
			DataManager.getImplProv().getLogger().error(e.getMessage());
		}

		return document;
	}

	/**
	 * <p>
	 * parse.
	 * </p>
	 *
	 * @param xmlFile
	 *            a {@link java.io.File} object.
	 * @return a {@link org.w3c.dom.Document} object.
	 */
	public static Document parse(File xmlFile) {
		try {
			return parse(getFileAsString(xmlFile));
		} catch (IOException e) {
			DataManager.getImplProv().getLogger().error(e.getMessage());
		}
		return null;
	}

	/**
	 *
	 * reads a the content of a file and returns is as a String
	 *
	 * @param filename
	 *            the filename to read
	 * @return the String containing the text of the file
	 * @throws java.io.IOException
	 *             if any.
	 */
	private static String getFileAsString(File filename) throws IOException {
		try {
			StringBuffer content = new StringBuffer();
			String line, lineFeed = System.getProperty("line.separator");
			BufferedReader in = new BufferedReader(new FileReader(filename));
			while ((line = in.readLine()) != null)
				content.append(line).append(lineFeed);
			in.close();
			return content.toString();
		} catch (IOException e) {
			throw new IOException("unable to read from file: " + filename);
		}
	}

	/**
	 * Convert XML DOM document to a string.
	 * 
	 * @param document
	 *            XML DOM document
	 * @return XML string
	 */
	public static String toString(final Document document) {
		final StringWriter stringWriter = new StringWriter();
		final StreamResult streamResult = new StreamResult(stringWriter);
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();

		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.transform(new DOMSource(document.getDocumentElement()), streamResult);
		} catch (final TransformerConfigurationException e) {
			DataManager.getImplProv().getLogger().error(e.getMessage());
		} catch (final TransformerException e) {
			DataManager.getImplProv().getLogger().error(e.getMessage());
		}

		return stringWriter.toString();
	}

	/**
	 * Validate a XML-{@link Document} against a XSD-SchemaFile.
	 * 
	 * @param schemaFile
	 *            the XSD-{@link URL}.
	 * @param xmlDocument
	 *            the XML-{@link Document}.
	 * @throws SAXException
	 *             if there is an validation error.
	 * @throws IOException
	 *             if unable to access the files.
	 */
	public static void validate(final URL schemaFile, final Document xmlDocument) throws SAXException, IOException {

		/** create a SchemaFactory capable of understanding WXS schemas */
		final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		/** load a WXS schema, represented by a Schema instance */
		final Schema schema = factory.newSchema(schemaFile);

		/** validate the DOM tree */
		schema.newValidator().validate(new DOMSource(xmlDocument));
	}
}