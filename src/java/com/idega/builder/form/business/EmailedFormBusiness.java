package com.idega.builder.form.business;


import com.idega.business.IBOService;
import java.io.File;
import java.util.List;
import java.rmi.RemoteException;

public interface EmailedFormBusiness extends IBOService {
	/**
	 * @see com.idega.builder.form.business.EmailedFormBusinessBean#insertFormEntries
	 */
	public boolean insertFormEntries(String type, String fieldList,
			List entries, File uploadFile) throws RemoteException;
}