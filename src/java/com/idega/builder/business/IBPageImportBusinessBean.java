package com.idega.builder.business;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import com.idega.builder.data.IBExportImportData;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOServiceBean;
import com.idega.idegaweb.IWUserContext;
import com.idega.io.UploadFile;
import com.idega.util.datastructures.MessageContainer;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 26, 2004
 */
public class IBPageImportBusinessBean extends IBOServiceBean  implements IBPageImportBusiness {
	
	private FileBusiness fileBusiness = null;
	
	public MessageContainer importPages(UploadFile file, boolean performValidation, int parentPageId, int templatePageId, IWUserContext iwuc) throws RemoteException, IOException {
		FileBusiness fileBusiness = getFileBusiness();
		IBExportImportData exportImportData = fileBusiness.getIBExportImportData(file, performValidation,parentPageId, templatePageId, iwuc);
		if (! exportImportData.isValid() && performValidation) { 
			List missingModules = exportImportData.getMissingModules();
			MessageContainer messageContainer = new MessageContainer();
			Iterator iterator = missingModules.iterator();
			while (iterator.hasNext()) {
				String missingModule = (String) iterator.next();
				messageContainer.addMessage(missingModule);
			}
			return messageContainer;
		}
		return null;
	}


	
	

	private FileBusiness getFileBusiness() throws IBOLookupException {
		if (fileBusiness == null) {
			fileBusiness =  (FileBusiness) IBOLookup.getServiceInstance( getIWApplicationContext(), FileBusiness.class);
		}
		return fileBusiness;
	}
}