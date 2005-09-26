/*
 * $Id: ObjectReaderBuilder.java,v 1.1 2005/09/26 17:09:57 thomas Exp $
 * Created on Sep 26, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.io;

import java.rmi.RemoteException;
import com.idega.builder.data.IBExportImportData;
import com.idega.io.serialization.ObjectReader;
import com.idega.presentation.IWContext;


public interface ObjectReaderBuilder extends ObjectReader {
	
	Object read(IBExportImportData metadata, IWContext context)  throws RemoteException;
}
