package com.idega.builder.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.idega.builder.data.IBExportMetadata;
import com.idega.business.IBOServiceBean;
import com.idega.core.file.data.ICFile;
import com.idega.io.ICFileWriter;
import com.idega.io.ObjectWriter;
import com.idega.io.Storable;
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
public class FileBusinessBean extends IBOServiceBean  implements  FileBusiness,ObjectWriter {

	private static final String ZIP_EXTENSION = "zip";	
	private static final String ZIP_ELEMENT_DELIMITER = "_";
	

	
	public String getURLForOfferingDownload(Storable storableObject) throws IOException {
		return createContainer(storableObject);
	}
	
	private String createContainer(Storable storableObject) throws IOException {
		ICFileWriter currentWriter = (ICFileWriter) storableObject.write(this);
		return currentWriter.createContainer(); 
	}
	
	public String getURLForOfferingDownload(String name, List data, IBExportMetadata metadata) throws IOException {
		return createContainer(name, data, metadata);
	}
	
	private String createContainer(String name, Collection data, IBExportMetadata metadata) throws IOException {
		ICFileWriter currentWriter = new ICFileWriter(getIWApplicationContext());
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
     writeData(data, metadata, destination);
     currentWriter.close(destination);
     return currentWriter.getURLToFile(name, ZIP_EXTENSION, folderIdentifier);
 	}
	
	private void writeData(Collection data, IBExportMetadata metadata, ZipOutputStream destination) throws IOException {
		int entryNumber = 0;
 		Iterator iterator = data.iterator();
 		while (iterator.hasNext()) {
 			Storable element = (Storable) iterator.next();
 			ICFileWriter currentWriter = (ICFileWriter) element.write(this);
 			String originalName = currentWriter.getName();
 			String zipElementName = createZipElementName(originalName, entryNumber);
 			ZipEntry zipEntry = new ZipEntry(zipElementName);
 			metadata.modifyElementSetNameSetOriginalName(entryNumber++, zipElementName, originalName);
 			destination.putNextEntry(zipEntry);
 			currentWriter.writeData(destination);
 			destination.closeEntry();
 		}
 		// add metadata
 		ICFileWriter currentWriter = (ICFileWriter) metadata.write(this);
		String originalName = currentWriter.getName();
		ZipEntry zipEntry = new ZipEntry(originalName);
		destination.putNextEntry(zipEntry);
		currentWriter.writeData(destination);
		destination.closeEntry();
	}
	
  public Object write(ICFile file) {
		return new ICFileWriter((Storable) file, getIWApplicationContext());
	}
	
  public Object write(XMLData xmlData) {
		return new XMLDataWriter(xmlData, getIWApplicationContext());
	}
  
  private String createZipElementName(String originalName, int entryNumber) {
  	StringBuffer buffer = new StringBuffer();
  	buffer.append(entryNumber).append(ZIP_ELEMENT_DELIMITER).append(originalName);
  	return buffer.toString();
  }
  	

}  	
