package com.idega.builder.business;

import java.io.IOException;

import com.idega.builder.data.IBExportImportData;
import com.idega.business.IBOServiceBean;
import com.idega.core.file.data.ICFile;
import com.idega.io.ICFileWriter;
import com.idega.io.ObjectWriter;
import com.idega.io.Storable;
import com.idega.io.UploadFile;
import com.idega.io.WriterToFile;
import com.idega.io.XMLDataWriter;
import com.idega.io.IBExportImportDataWriter;
import com.idega.io.IBExportImportDataReader;
import com.idega.util.xml.XMLData;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 15, 2004
 */
public class FileBusinessBean extends IBOServiceBean  implements  FileBusiness,ObjectWriter {
	
	public IBExportImportData getIBExportImportData(UploadFile uploadFile) throws IOException {
		IBExportImportDataReader exportImportDataReader = new IBExportImportDataReader ();
		return exportImportDataReader.getData(uploadFile);
	}
		
	public String getURLForOfferingDownload(Storable storableObject) throws IOException {
		return createContainer(storableObject);
	}
	
	private String createContainer(Storable storableObject) throws IOException {
		WriterToFile currentWriter = (WriterToFile) storableObject.write(this);
		return currentWriter.createContainer(); 
	}
	
  public Object write(ICFile file) {
		return new ICFileWriter((Storable) file, getIWApplicationContext());
	}
	
  public Object write(XMLData xmlData) {
		return new XMLDataWriter(xmlData, getIWApplicationContext());
	}
  
  public Object write(IBExportImportData metadata) {
  	return new IBExportImportDataWriter(metadata, getIWApplicationContext());
  }
  

}  	
