package com.idega.builder.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.idega.business.IBOServiceBean;
import com.idega.core.file.data.ICFile;
import com.idega.io.ICFileWriter;
import com.idega.io.Storable;
import com.idega.io.Writer;
import com.idega.io.XMLDataWriter;
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
public class FileBusinessBean extends IBOServiceBean  implements FileBusiness, Writer {

	private static final String ZIP_EXTENSION = "zip";	
	
	private ICFileWriter fileWriter = null;
	private XMLDataWriter xmlDataWriter = null;
	
	public String getURLForOfferingDownload(Storable storableObject) throws IOException {
		return createContainer(storableObject);
	}
	
	private String createContainer(Storable storableObject) throws IOException {
		ICFileWriter currentWriter = (ICFileWriter) storableObject.write(this);
		return currentWriter.createContainer(storableObject); 
	}
	
	public String getURLForOfferingDownload(String name, List data) throws IOException {
		return createContainer(name, data);
	}
	
	private String createContainer(String name, List data) throws IOException {
		ICFileWriter currentWriter = getICFileWriter();
		long folderIdentifier = System.currentTimeMillis();
 		String path = currentWriter.getRealPathToFile(name, ZIP_EXTENSION, folderIdentifier);
     File auxiliaryFile = null;
     ZipOutputStream destination = null;
     try {
       auxiliaryFile = new File(path);
       destination = new ZipOutputStream(new FileOutputStream(auxiliaryFile));
     }
     catch (FileNotFoundException ex)  {
//     	logError("[XMLData] problem creating file.");
//     	log(ex);
     	throw new IOException("xml file could not be stored");
     }
     writeData(data, destination);
     currentWriter.close(destination);
     return currentWriter.getURLToFile(name, ZIP_EXTENSION, folderIdentifier);
 	}
	
	private void writeData(List data, ZipOutputStream destination) throws IOException {
 		Iterator iterator = data.iterator();
 		while (iterator.hasNext()) {
 			Storable element = (Storable) iterator.next();
 			ICFileWriter currentWriter = (ICFileWriter) element.write(this);
 			String name = currentWriter.getName(element);
 			ZipEntry zipEntry = new ZipEntry(name);
 			destination.putNextEntry(zipEntry);
 			fileWriter.writeData(element, destination);
 			destination.closeEntry();
 		}
	}
	
	private ICFileWriter getICFileWriter() {
		if (fileWriter == null) {
			fileWriter = new ICFileWriter(getIWApplicationContext());
		}
		return fileWriter;
	}
	
	private XMLDataWriter getXMLDataWriter() {
		if (xmlDataWriter == null) {
			xmlDataWriter = new XMLDataWriter(getIWApplicationContext());
		}
		return xmlDataWriter;
	}
	
  
  public Object write(ICFile file) {
  	return getICFileWriter();
  }
  
  public Object write(XMLData xmlData) {
  	return getXMLDataWriter();
  }
 
 
}  	
