package com.idega.builder.facelets;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Tidy;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.CachedBuilderPage;
import com.idega.util.CoreConstants;
import com.idega.util.FileUtil;
import com.idega.util.StringHandler;
import com.idega.xml.XMLDocument;
import com.idega.xml.XMLParser;
/**
 * <p>
 * Class to convert one IBXML and HTML page to the new Facelet formats.
 * </p>
 * 
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson </a>
 * 
 * Last modified: $Date: 2009/01/14 15:36:08 $ by $Author: tryggvil $
 * @version $Id: BuilderFaceletConverter.java,v 1.3 2009/01/14 15:36:08 tryggvil Exp $
 */
public class BuilderFaceletConverter {

	//private String pageKey;
	private String formatTo = BuilderLogic.PAGE_FORMAT_FACELET;
	private String formatFrom = BuilderLogic.PAGE_FORMAT_IBXML;

	public String getFormatTo() {
		return formatTo;
	}

	public void setFormatTo(String formatTo) {		
		this.formatTo = formatTo;
		if(isLegacyConvert()){
			//We'll switch to a namespace without a prefix:
			this.builderNamespace=Namespace.getNamespace(sBuilderNamespace);
			this.htmlNamespace=Namespace.getNamespace("html",sHtmlNamespace);
		}
	}

	public String getFormatFrom() {
		return formatFrom;
	}

	public void setFormatFrom(String formatFrom) {
		this.formatFrom = formatFrom;
	}

	private String stringSourceMarkup;
	private XMLParser parser;

	boolean legacyConvert = false;

	public static final String sBuilderNamespace = "http://xmlns.idega.com/com.idega.builder";
	public Namespace builderNamespace = Namespace.getNamespace("b", sBuilderNamespace);

	public static final String sFaceletNamespace = "http://java.sun.com/jsf/facelets";
	public Namespace faceletNamespace = Namespace
			.getNamespace("ui", sFaceletNamespace);

	public static final String sHtmlNamespace = "http://www.w3.org/1999/xhtml";
	public Namespace htmlNamespace = Namespace.getNamespace(sHtmlNamespace);
	
	private CachedBuilderPage page;

	public BuilderFaceletConverter(CachedBuilderPage page, String pageFormatTo,
			String stringSourceMarkup) {
		this.page = page;
		setFormatTo(pageFormatTo);
		setFormatFrom(page.getPageFormat());
		this.stringSourceMarkup = stringSourceMarkup;
	}
	
	public BuilderFaceletConverter(String pageFormatFrom, String pageFormatTo,
			String stringSourceMarkup) {
		setFormatTo(pageFormatTo);
		setFormatFrom(pageFormatFrom);
		this.stringSourceMarkup = stringSourceMarkup;
	}

	public BuilderFaceletConverter(CachedBuilderPage cPage,
			String pageFormatTo) {
		this(cPage,pageFormatTo,cPage.toString());
	}

	public void convert() throws Exception {
		this.parser = new XMLParser(false);
		// XMLDocument doc = page.getXMLDocument();
		Document faceletDoc = new Document();
		// DocType faceletDocType =
		// faceletDoc.setDocType(docType)
		XMLDocument doc = parser
				.parse(new StringReader(this.stringSourceMarkup));
		Object o = doc.getDocument();
		if (o instanceof Document) {
			Document d = (Document) o;
			try {
				Element ibXmlRoot = d.getRootElement();
				if (getFormatFrom().equals(BuilderLogic.PAGE_FORMAT_IBXML)) {
					if (ibXmlRoot.getName().equals("xml")) {
						// ignore the xml top element and go to the page
						// element:
						Element pageElement = ibXmlRoot.getChild("page");
						Element newPageElement = null;
						Element newRootElement = null;
						if (isLegacyConvert()) {
							//newRootElement = new Element("page",builderNamespace);
							newRootElement = new Element("xml", builderNamespace);
							newPageElement = new Element("page",
									builderNamespace);
							newRootElement.addContent(newPageElement);
						} else {
							newRootElement = new Element("html", htmlNamespace);
							newPageElement = new Element("composition",
									faceletNamespace);
							newRootElement.addContent(newPageElement);
						}

						faceletDoc.setRootElement(newRootElement);

						transformElementToFacelet(pageElement, newPageElement);

						XMLOutputter out = new XMLOutputter();
						out.setFormat(Format.getPrettyFormat());
						this.stringSourceMarkup = out.outputString(faceletDoc);
					}
				} else if (getFormatFrom().equals(BuilderLogic.PAGE_FORMAT_HTML)) {
					String text = this.stringSourceMarkup;
					// Use JTidy to clean up the HTML
					Tidy tidy = new Tidy();
					tidy.setXHTML(true);
					tidy.setXmlOut(true);
					tidy.setShowWarnings(false);
					tidy.setCharEncoding(Configuration.UTF8);
					InputStream stream = null;
					ByteArrayOutputStream baos = null;
					try {
						stream = StringHandler.getStreamFromString(text);
						baos = new ByteArrayOutputStream();

						tidy.parse(stream, baos);
						text = baos.toString(CoreConstants.ENCODING_UTF8);
						
						text = text.replaceAll("html xmlns=\"http://www.w3.org/1999/xhtml\"", "html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:ui=\"http://java.sun.com/jsf/facelets\"");
						text = rewriteRegionTags(text);
						
						this.stringSourceMarkup = text;
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						// closeInputStream(stream);
						// closeOutputStream(baos);
						stream.close();
						baos.close();
					}
					// if (StringUtil.isEmpty(text)) {
					// return toPrettify;
					// }
					// text = removeAbsoluteReferences(text);

					// return getOnlyBodyContent(text);
				} else {
					// not old style IBXML, exiting without change
				}

			} catch (Exception e) {
				e.printStackTrace();
				// return false;
			}
		}

	}

	private String rewriteRegionTags(String source) {
		// Process the template regions:
		String[] parts = source.split("<!-- TemplateBeginEditable");
		String newString = parts[0];
		for (int i = 1; i < parts.length; i++) {
			String part = parts[i];
			String[] t = part.split("TemplateEndEditable -->");

			String toParse = t[0];
			String[] a1 = toParse.split("name=\"");
			String[] a2 = a1[1].split("\"");

			String regionId = a2[0];
			String newRegionCode = "<ui:insert name=\""
					+ regionId + "\"/>";

			newString += newRegionCode;
			// int childNumber = Integer.parseInt(t[0]) - 1;

			/*
			 * try{ UIComponent region = getRegion(regionId);
			 * renderChild(ctx,region); } catch(ClassCastException cce){
			 * cce.printStackTrace(); }
			 * 
			 * out.write(t[1]);
			 */
			newString += t[1];
		}
		return newString;
	}

	private void transformElementToFacelet(Element oldElement,
			Element newElement) {

		// String text = oldElement.getText();
		// if(text!=null){
		// newElement.setText(text);
		// }

		List attributes = oldElement.getAttributes();
		for (Iterator iterator = attributes.iterator(); iterator.hasNext();) {
			Attribute attribute = (Attribute) iterator.next();
			String name = attribute.getName();
			String value = attribute.getValue();

			if (oldElement.getName().equals("page") && name.equals("template")&&!isLegacyConvert()) {
				value = FaceletsUtil.getRewrittenTemplateReference(this.page,value);
				Attribute newAttribute = new Attribute(name, value);
				newElement.setAttribute(newAttribute);

			} else if (oldElement.getName().equals("page")
					&& !isLegacyConvert()) {
				// Not add the attributes to the composition element
			} else if (oldElement.getName().equals("region")
					&& !isLegacyConvert()) {
				// Not add the attributes to the composition element other than
				// the name attribute:
				if (name.equals("id")) {
					Attribute newAttribute = new Attribute("name", value);
					newElement.setAttribute(newAttribute);
				}
			} else {
				Attribute newAttribute = new Attribute(name, value);
				newElement.setAttribute(newAttribute);
			}

		}

		List children = oldElement.getContent();
		for (Iterator iterator = children.iterator(); iterator.hasNext();) {
			Object child = iterator.next();
			if (child instanceof Element) {
				Element childElement = (Element) child;
				Element newChildElement = null;
				if (childElement.getName().equals("region")
						&& !isLegacyConvert()) {
					newChildElement = new Element("define", faceletNamespace);
					newElement.addContent(newChildElement);
				} else {
					newChildElement = new Element(childElement.getName(),
							builderNamespace);
					newElement.addContent(newChildElement);
				}

				if (childElement.getName().equals("property")&&!isLegacyConvert()) {
					List nameChildren = getChildren(childElement, "name");
					List valueChildren = getChildren(childElement, "value");
					if (nameChildren.size() == 1) {
						Element nameChild = (Element) nameChildren.get(0);
						Element valueChild = (Element) valueChildren.get(0);

						Attribute newNameAttribute = new Attribute("name",
								sBuilderNamespace);
						newNameAttribute.setValue(nameChild.getText());
						newChildElement.setAttribute(newNameAttribute);

						Attribute newValueAttribute = new Attribute("value",
								sBuilderNamespace);
						newValueAttribute.setValue(valueChild.getText());
						newChildElement.setAttribute(newValueAttribute);
					} else {
						transformElementToFacelet(childElement, newChildElement);
					}

				} else {
					transformElementToFacelet(childElement, newChildElement);
				}

			} else if (child instanceof Text) {
				Text childElement = (Text) child;

				Text newChildElement = new Text(childElement.getText());
				newElement.addContent(newChildElement);
			}
		}

	}

	private List getChildren(Element oldElement, String name) {
		ArrayList list = new ArrayList();
		List children = oldElement.getContent();
		for (Iterator iterator = children.iterator(); iterator.hasNext();) {
			Object child = iterator.next();
			if (child instanceof Element) {
				Element element = (Element) child;
				if (element.getName().equals(name)) {
					list.add(element);
				}
			}
		}
		return list;
	}



	public String getConvertedMarkupString() {
		// TODO Auto-generated method stub
		return stringSourceMarkup;
	}

	public static void main(String args[]) throws Exception {

		// InputStreamReader sourceInput = new InputStreamReader(new
		// FileInputStream(new File("/Users/tryggvil/builderpage.ibxml")));
		String source = FileUtil
		 .getStringFromFile("/Users/tryggvil/builderpage.ibxml");
		BuilderFaceletConverter converter = new BuilderFaceletConverter(BuilderLogic.PAGE_FORMAT_IBXML,
				BuilderLogic.PAGE_FORMAT_FACELET, source);
		//String source = FileUtil
		//		.getStringFromFile("/Users/tryggvil/testhtml.html");
		//BuilderFaceletConverter converter = new BuilderFaceletConverter(BuilderLogic.PAGE_FORMAT_HTML,
		//		BuilderLogic.PAGE_FORMAT_IBXML, source);
		converter.convert();
		String output = converter.getConvertedMarkupString();
		System.out.println("Converted to:\n\n" + output);

	}

	public boolean isLegacyConvert() {
		//return legacyConvert;
		if(getFormatTo().equals(BuilderLogic.PAGE_FORMAT_IBXML2)){
			return true;
		}
		else{
			return false;
		}
	}

	//public void setLegacyConvert(boolean legacyConvert) {
	//	this.legacyConvert = legacyConvert;
	//}
}
