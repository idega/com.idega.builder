/*
 * $Id: IBObjectLibraryBMPBean.java,v 1.6 2003/10/03 01:41:59 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.data;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import javax.ejb.CreateException;

import com.idega.core.file.data.ICFile;
import com.idega.core.user.data.User;
import com.idega.data.IDOLookupException;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBObjectLibraryBMPBean extends com.idega.data.TreeableEntityBMPBean implements com.idega.builder.data.IBObjectLibrary {
	private final static String ENTITY_NAME = "ib_library";
	private final static String FILE_COLUMN = "file_id";
	private final static String OWNER_COLUMN = "user_id";

	private ICFile _file;

	/**
	 *
	 */
	public IBObjectLibraryBMPBean() {
		super();
	}

	/**
	 *
	 */
	public IBObjectLibraryBMPBean(int id) throws SQLException {
		super(id);
	}

	/**
	 *
	 */
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(getColumnFile(), "File", true, true, Integer.class, com.idega.data.GenericEntity.MANY_TO_ONE, ICFile.class);
		addAttribute(getColumnOwner(), "Owner", true, true, Integer.class, com.idega.data.GenericEntity.MANY_TO_ONE, User.class);
	}

	/**
	 *
	 */
	public void insertStartData() throws Exception {
	}

	/**
	 *
	 */
	public String getEntityName() {
		return (ENTITY_NAME);
	}

	/**
	 *
	 */
	public void setDefaultValues() {
	}

	/*
	 *
	 */
	private int getFileID() {
		return (getIntColumnValue(getColumnFile()));
	}

	/**
	 *
	 */
	public ICFile getFile() {
		int fileID = getFileID();
		if (fileID != -1) {
			_file = (ICFile)getColumnValue(getColumnFile());
		}
		return (_file);
	}

	/**
	 *
	 */
	public void setFile(ICFile file) {
		setColumn(getColumnFile(), file);
		_file = file;
	}

	/**
	 *
	 */
	public void setPageValue(InputStream stream) {
		ICFile file = getFile();
		if (file == null) {
			try {
				file = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).create();
				setFile(file);
			} catch (IDOLookupException e) {
				e.printStackTrace();
			} catch (CreateException e) {
				e.printStackTrace();
			}
		}
		file.setFileValue(stream);
	}

	/**
	 *
	 */
	public InputStream getPageValue() {
		try {
			ICFile file = getFile();
			if (file != null) {
				return (file.getFileValue());
			}
		} catch (Exception e) {
		}

		return (null);
	}

	/**
	 *
	 */
	public OutputStream getPageValueForWrite() {
		ICFile file = getFile();
		if (file == null) {
			try {
				file = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).create();
				setFile(file);
			} catch (IDOLookupException e) {
				e.printStackTrace();
			} catch (CreateException e) {
				e.printStackTrace();
			}
			
		}
		OutputStream theReturn = file.getFileValueForWrite();

		return (theReturn);
	}

	/**
	 *
	 */
	public void update() throws SQLException {
		ICFile file = getFile();
		if (file != null) {
			try {
				if (file.getPrimaryKey() == null) {
					file.store();
					setFile(file);
				} else {
					file.store();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.update();
	}

	/**
	 *
	 */
	public void insert() throws SQLException {
		ICFile file = getFile();
		if (file != null) {
			file.store();
			setFile(file);
		}
		super.insert();
	}

	/**
	 *
	 */
	public void delete() throws SQLException {
		ICFile file = getFile();
		if (file != null) {
			try {
				file.delete();
			} catch (SQLException e) {
			}
		}
		super.delete();
	}

	/**
	 *
	 */
	public String getColumnFile() {
		return (FILE_COLUMN);
	}

	/**
	 *
	 */
	public String getColumnOwner() {
		return (OWNER_COLUMN);
	}

	/**
	 *
	 */
	public void setOwnerId(int id) {
		setColumn(getColumnOwner(), id);
	}

	/**
	 *
	 */
	public int getOwnerId() {
		return (getIntColumnValue(getColumnOwner()));
	}
}
