package com.idega.builder.business;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.idega.builder.data.IBExportImportData;
import com.idega.business.IBOServiceBean;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOStoreException;
import com.idega.idegaweb.IWCacheManager;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.io.ICFileWriter;
import com.idega.io.ObjectWriter;
import com.idega.io.Storable;
import com.idega.io.UploadFile;
import com.idega.io.WriterToFile;
import com.idega.io.XMLDataWriter;
import com.idega.io.IBExportImportDataWriter;
import com.idega.io.IBExportImportDataReader;
import com.idega.util.FileUtil;
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
	
	public final static String AUXILIARY_FOLDER = "auxiliaryDataFolder";
	public final static String AUXILIARY_FILE = "auxililary_data_file_";
	
	public IBExportImportData getIBExportImportData(UploadFile uploadFile, boolean performValidation, int parentPageId, int templatePageId, IWUserContext iwuc) throws IOException {
		IBExportImportData exportImportData = new IBExportImportData();
		IBExportImportDataReader reader = new IBExportImportDataReader(exportImportData,performValidation, getIWApplicationContext(), iwuc);
		reader.setParentPageForImportedPages(parentPageId);
		reader.setParentTemplateForImportedTemplates(templatePageId);
		reader.openContainer(uploadFile);
		return exportImportData;
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
  
  /** inputStream is not closed by this method */
	public ICFile createFileFromInputStream(InputStream inputStream, String name, String mimeType) throws IOException {
    // create or fetch existing ICFile
    ICFile file = getNewFile();
    file.setMimeType(mimeType);
    file.setName(name);
    try {
      file.store();
    }
    catch (IDOStoreException ex)  {
      logError("[ICFileReader] problem storing ICFile Message is: "+ex.getMessage());
      throw new IOException("file could not be stored");
    }
    Integer fileId = (Integer) file.getPrimaryKey();
    // To avoid problems with databases (e.g. MySQL) 
    // we do not write directly to the ICFile object but
    // create an auxiliary file on the hard disk and write the xml file to that file.
    // After that we read the file on the hard disk an write it to the ICFile object.
    // Finally we delete the auxiliary file.
    
    // write the output first to a file object  
    // get the output stream      
    String separator = FileUtil.getFileSeparator();
    IWMainApplication mainApp = getIWMainApplication();
    StringBuffer path = new StringBuffer(mainApp.getApplicationRealPath());
           
    path.append(IWCacheManager.IW_ROOT_CACHE_DIRECTORY)
      .append(separator)
      .append(AUXILIARY_FOLDER);
    // check if the folder exists create it if necessary
    // usually the folder should be already be there.
    // the folder is never deleted by this class
    FileUtil.createFolder(path.toString());
    // set name of auxiliary file
    path.append(separator).append(AUXILIARY_FILE).append(fileId);
    BufferedOutputStream outputStream = null;
    File auxiliaryFile = null;
    try {
      auxiliaryFile = new File(path.toString());
      outputStream = new BufferedOutputStream(new FileOutputStream(auxiliaryFile));
    }
    catch (FileNotFoundException ex)  {
      logError("FileBusiness] problem creating file. Message is: "+ex.getMessage());
      throw new IOException("xml file could not be stored");
    }
    // now we have an output stream of the auxiliary file
    // write to the file
    try {
    	writeFromStreamToStream(inputStream, outputStream);
    }
    finally {
    	close(outputStream);
    }
    // writing finished
    // get size of the file
    int size = (int) auxiliaryFile.length();
    // get the input stream of the auxiliary file
    BufferedInputStream auxInputStream = null;
    try {
      auxInputStream = new BufferedInputStream(new FileInputStream(auxiliaryFile));
        }
    catch (FileNotFoundException ex)  {
      logError("[XMLData] problem reading file. Message is: "+ex.getMessage());
      throw new IOException("xml file could not be stored");
    }
    // now we have an input stream of the auxiliary file
    // write to the ICFile object
    file.setFileSize(size);
    try {
    	file.setFileValue(auxInputStream);
    }
    finally {
    	close(auxInputStream);
    }
//    try {
      //xmlFile.update();
    file.store();
//    }
//    catch (SQLException ex)  {
//      System.err.println("[XMLData] problem storing ICFile Message is: "+ex.getMessage());
//      ex.printStackTrace(System.err);
//      throw new IOException("xml file could not be stored");
//    }
    // reading finished
    // delete file
    auxiliaryFile.delete();
    return file;
  }

	private ICFile getNewFile()  {
    try {
      ICFileHome home = (ICFileHome) IDOLookup.getHome(ICFile.class);
      ICFile xmlFile = home.create();
      return xmlFile;
    }
    // FinderException, RemoteException
    catch (Exception ex)  {
      throw new RuntimeException("[XMLData]: Message was: " + ex.getMessage());
    }
 }	
	
	private void writeFromStreamToStream(InputStream source, OutputStream destination) throws IOException { 
		// parts of this method  were copied from "Java in a nutshell" by David Flanagan
    byte[] buffer = new byte[4096];  // A buffer to hold file contents
    int bytesRead;                       
    while((bytesRead = source.read(buffer)) != -1)  {  // Read bytes until EOF
      destination.write(buffer, 0, bytesRead);    
    }
	}
	
  private void close(InputStream input) {
  	try {
			if (input != null) {
				input.close();
			}
		}
		// do not hide an existing exception
		catch (IOException io) {
		}
  }		
  
  private void close(OutputStream output) {
  	try {
  		if (output != null) {
  			output.close();
  		}
  	}
  	// do not hide an existing exception
  	catch (IOException io) {
  	}
  }
	
	
}  	
