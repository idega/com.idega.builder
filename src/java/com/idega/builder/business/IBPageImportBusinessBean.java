package com.idega.builder.business;

import java.io.IOException;
import java.rmi.RemoteException;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOServiceBean;
import com.idega.io.UploadFile;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 26, 2004
 */
public class IBPageImportBusinessBean extends IBOServiceBean implements IBPageImportBusiness{
	
	private FileBusiness fileBusiness = null;
	
	public void importPages(UploadFile file, int parentPageId, int templatePageId) throws RemoteException, IOException {
		FileBusiness fileBusiness = getFileBusiness();
		fileBusiness.getIBExportImportData(file, parentPageId, templatePageId);
		
	}


	
	

	private FileBusiness getFileBusiness() throws IBOLookupException {
		if (fileBusiness == null) {
			fileBusiness =  (FileBusiness) IBOLookup.getServiceInstance( getIWApplicationContext(), FileBusiness.class);
		}
		return fileBusiness;
	}
}