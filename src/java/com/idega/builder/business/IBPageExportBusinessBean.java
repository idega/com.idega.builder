package com.idega.builder.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOServiceBean;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.file.data.ICFile;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 12, 2004
 */
public class IBPageExportBusinessBean extends IBOServiceBean implements IBPageExportBusiness {
	
	private ICPageHome pageHome = null;
	private FileBusiness fileBusiness = null;
	
	public String exportPages(List pageIds) throws IOException, FinderException  {
  	if (pageIds == null) {
  		throw new IOException("List of page ids is empty");
  	}
  	List files = new ArrayList();
//  	XMLData exportXML = XMLData.getInstanceWithoutExistingFile("export");
//  	XMLDocument exportDocument = exportXML.getDocument();
//  	XMLElement exportRoot = exportDocument.getRootElement();
//  	exportRoot.setName(XMLConstants.PAGES);
  	Iterator pageIterator = pageIds.iterator();
  	while (pageIterator.hasNext()) {
  		Integer pageId = (Integer) pageIterator.next();
  		ICPageHome pageHome = getPageHome();
  		ICPage page = pageHome.findByPrimaryKey(pageId.intValue());
  		ICFile file = page.getFile();
  		files.add(file);
//  		XMLData xmlData = XMLData.getInstanceForFile(file);
//  		XMLDocument pageXML = xmlData.getDocument();
//  		XMLElement pageRoot = pageXML.getRootElement().getChild(XMLConstants.PAGE_STRING);
//  		pageRoot.detach();
//  		exportRoot.addContent(pageRoot);
  	}	
  	FileBusiness fileBusiness = getFileBusiness();
  	return fileBusiness.getURLForOfferingDownload("export", files);
  	//return fileBusiness.getURLForOfferingDownload(exportXML);
  }
 		
	private ICPageHome getPageHome() throws IDOLookupException {
		if (pageHome == null)  {
			pageHome = (ICPageHome) IDOLookup.getHome(ICPage.class);
		}
		return pageHome;
	}
	
	private FileBusiness getFileBusiness() throws IBOLookupException {
		if (fileBusiness == null) {
			fileBusiness =  (FileBusiness) IBOLookup.getServiceInstance( getIWApplicationContext(), FileBusiness.class);
		}
		return fileBusiness;
	}

	
}
