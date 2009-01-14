package com.idega.builder.facelets;

import com.idega.builder.business.CachedBuilderPage;
import com.idega.util.StringHandler;
import com.idega.xml.XMLNamespace;

public class FaceletsUtil {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	public static final long serialVersionUID = -1115066670523521567L;
	public static final String BUILDERPAGE_PREFIX = "builderpage_";
	public static final int BUILDERPAGE_PREFIX_LENGTH = BUILDERPAGE_PREFIX.length();
	public static final String PAGES_DEFAULT_FOLDER = "facelets";
	public static final String FACELET_PAGE_EXTENSION_WITH_DOT = ".xhtml";
	public static final int FACELET_PAGE_EXTENSION_WITH_DOT_LENGTH = FACELET_PAGE_EXTENSION_WITH_DOT.length();
	
	public static final XMLNamespace BUILDER_NAMESPACE=new XMLNamespace("http://xmlns.idega.com/com.idega.builder");
	
	public static String getRewrittenTemplateReference(CachedBuilderPage page,String templateId) {
		//TODO: Implement conversion based on PageURI
		return "/facelets/builderpage_" + templateId + ".xhtml";
	}
	
	/**
	 * Returns page key if the view id represents a FaceletPage else null. 
	 * 
	 * @param viewId
	 * @return
	 */
	public static String getPageKey(String viewId) {
		// we are looking for something like "/jsps/builderpage_12.jsp"
		// quick check at the beginning
		if (! viewId.endsWith(FaceletsUtil.FACELET_PAGE_EXTENSION_WITH_DOT)) {
			// no jsp page at all
			return null;
		}
		int startIndex = viewId.lastIndexOf(FaceletsUtil.BUILDERPAGE_PREFIX);
		if (startIndex < 0) {
			// jsp page but not a builder page
			return null;
		}
		startIndex += FaceletsUtil.BUILDERPAGE_PREFIX_LENGTH;
		int endIndex = viewId.length() - FaceletsUtil.FACELET_PAGE_EXTENSION_WITH_DOT_LENGTH;
		String key = viewId.substring(startIndex, endIndex);
		if (StringHandler.isNaturalNumber(key)) {
			return key;
		}
		return null;
	}
}
