/*
 * $Id: IBPageBMPBean.java,v 1.10 2003/11/28 15:49:13 sigtryggur Exp $
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
import java.sql.Timestamp;

import javax.ejb.CreateException;

import com.idega.core.builder.data.*;
import com.idega.core.file.data.ICFile;
import com.idega.core.net.data.ICProtocol;
import com.idega.core.user.data.User;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWUserContext;
import com.idega.util.IWTimestamp;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.3
 */
public class IBPageBMPBean extends com.idega.data.TreeableEntityBMPBean implements com.idega.core.builder.data.ICPage {
	private final static String ENTITY_NAME = "IB_PAGE";
	private final static String FILE_COLUMN = "FILE_ID";
	private final static String NAME_COLUMN = "NAME";
	private final static String TEMPLATE_ID_COLUMN = "TEMPLATE_ID";
	private final static String TYPE_COLUMN = "PAGE_TYPE";
	private final static String SUBTYPE_COLUMN = "PAGE_SUB_TYPE";
	private final static String LOCKED_COLUMN = "LOCKED_BY";
	private final static String DELETED_COLUMN = "DELETED";
	private final static String DELETED_BY_COLUMN = "DELETED_BY";
	private final static String DELETED_WHEN_COLUMN = "DELETED_WHEN";
	private final static String TREE_ORDER = "TREE_ORDER";
	private final static String IS_CATEGORY = "IS_CATEGORY";
	private ICFile _file;

	public final static String PAGE = "P";
	public final static String TEMPLATE = "T";
	public final static String DRAFT = "D";
	public final static String FOLDER = "F";
	public final static String DPT_TEMPLATE = "A";
	public final static String DPT_PAGE = "B";

	public final static String DELETED = "Y";
	public final static String NOT_DELETED = "N";

	/**
	 *
	 */
	public IBPageBMPBean() {
		super();
	}

	/**
	 *
	 */
	public IBPageBMPBean(int id) throws SQLException {
		super(id);
	}

	/**
	 *
	 */
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(getColumnName(), "Nafn", true, true, String.class);
		addAttribute(getColumnFile(), "File", true, true, Integer.class, "many-to-one", ICFile.class);
		addAttribute(getColumnTemplateID(), "Template", true, true, Integer.class, "many-to-one", ICPage.class);
		addAttribute(getColumnType(), "Type", true, true, String.class, 1);
		addAttribute(getColumnSubType(), "Sub type", true, true, String.class);
		addAttribute(getColumnLockedBy(), "Locked by", true, true, Integer.class, "many-to-one", User.class);
		addAttribute(getColumnDeleted(), "Deleted", true, true, String.class, 1);
		addAttribute(getColumnDeletedBy(), "Deleted by", true, true, Integer.class, "many-to-one", User.class);
		addAttribute(getColumnDeletedWhen(), "Deleted when", true, true, Timestamp.class);
		addAttribute(TREE_ORDER, "Ordering of pages in a level in the page tree", true, true, Integer.class);
		addAttribute(IS_CATEGORY, "Is used as a page category", true, true, Boolean.class);
		addManyToManyRelationShip(ICProtocol.class, "ib_page_ic_protocol");
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
		//setColumn("image_id",1);
	}

	/**
	 *
	 */
	public String getName() {
		return (getStringColumnValue(getColumnName()));
	}

	/**
	 *
	 */
	public void setName(String name) {
		setColumn(getColumnName(), name);
	}

	/**
	 *
	 */
	public int getTemplateId() {
		return (getIntColumnValue(getColumnTemplateID()));
	}

	/**
	 *
	 */
	public void setTemplateId(int id) {
		setColumn(getColumnTemplateID(), id);
	}

	/**
	 *
	 */
	public String getType() {
		return (getStringColumnValue(getColumnType()));
	}

	/**
	 *
	 */
	public String getSubType() {
		return (getStringColumnValue(getColumnSubType()));
	}

	/**
	 *
	 */
	public int getLockedBy() {
		return (getIntColumnValue(getColumnLockedBy()));
	}

	/**
	 *
	 */
	public void setLockedBy(int id) {
		setColumn(getColumnLockedBy(), id);
	}

	/**
	 *
	 */
	public boolean getDeleted() {
		String deleted = getStringColumnValue(getColumnDeleted());

		if ((deleted == null) || (deleted.equals(NOT_DELETED)))
			return (false);
		else if (deleted.equals(DELETED))
			return (true);
		else
			return (false);
	}
	
	public boolean isCategory() {
		return getBooleanColumnValue(IS_CATEGORY, false);
	}

	public void setIsCategory(boolean isCategory) {
		setColumn(IS_CATEGORY, isCategory);
	}
	
	/**
	 *
	 */
	public void setDeleted(boolean deleted) {
		if (deleted) {
			setColumn(getColumnDeleted(), DELETED);
			setDeletedWhen(IWTimestamp.getTimestampRightNow());
			//      setDeletedBy(iwc.getUserId());
		} else {
			setColumn(getColumnDeleted(), NOT_DELETED);
			//      setDeletedBy(-1);
			//      setDeletedWhen(null);
		}
	}

	/**
	 *
	 */
	public int getDeletedBy() {
		return (getIntColumnValue(getColumnDeletedBy()));
	}

	/**
	 *
	 */
	private void setDeletedBy(int id) {
		//    if (id == -1)
		//      setColumn(getColumnDeletedBy(),(Object)null);
		//    else
		setColumn(getColumnDeletedBy(), id);
	}

	/**
	 *
	 */
	public Timestamp getDeletedWhen() {
		return ((Timestamp)getColumnValue(getColumnDeletedWhen()));
	}

	/**
	 *
	 */
	private void setDeletedWhen(Timestamp when) {
		setColumn(getColumnDeletedWhen(), when);
	}

	/**
	 *
	 */
	public void setType(String type) {
		if ((type.equals(PAGE)) || (type.equals(TEMPLATE)) || (type.equals(DRAFT)) || (type.equals(DPT_TEMPLATE)) || (type.equals(DPT_PAGE)))
			setColumn(getColumnType(), type);
	}

	/**
	 *
	 */
	public void setSubType(String type) {
		setColumn(getColumnSubType(), type);
	}

	/*
	 *
	 */
	private int getFileID() {
		return (getIntColumnValue(getColumnFile()));
	}

	/**
	 * Gets the file 
	 */
	public ICFile getFile() {
		// if we already have an instance of the file we do not
		// want to loose it, especially not if a filevalue has been
		// written to it, else the filevalue gets lost.
		if(_file==null){
			int fileID = getFileID();
			if ( fileID != -1) {
				_file = (ICFile)getColumnValue(getColumnFile());
			}
		}
		return (_file);
	}

	/**
	 *
	 */
	public void setFile(ICFile file) {
		file.setMimeType(com.idega.core.file.data.ICMimeTypeBMPBean.IC_MIME_TYPE_XML);
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
	public static String getColumnName() {
		return (NAME_COLUMN);
	}

	/**
	 *
	 */
	public static String getColumnTemplateID() {
		return (TEMPLATE_ID_COLUMN);
	}

	/**
	 *
	 */
	public static String getColumnFile() {
		return (FILE_COLUMN);
	}

	/**
	 *
	 */
	public static String getColumnType() {
		return (TYPE_COLUMN);
	}

	/**
	 *
	 */
	public static String getColumnSubType() {
		return (SUBTYPE_COLUMN);
	}

	/**
	 *
	 */
	public static String getColumnLockedBy() {
		return (LOCKED_COLUMN);
	}

	/**
	 *
	 */
	public static String getColumnDeleted() {
		return (DELETED_COLUMN);
	}

	/**
	 *
	 */
	public static String getColumnDeletedBy() {
		return (DELETED_BY_COLUMN);
	}

	/**
	 *
	 */
	public static String getColumnDeletedWhen() {
		return (DELETED_WHEN_COLUMN);
	}

	/**
	 *
	 */
	public synchronized void update() throws SQLException {
		ICFile file = getFile();
		if (file != null) {
			try {
				if (file.getPrimaryKey() == null) {
					file.store();
					file.setName(this.getName());
					file.setMimeType("text/xml");
					setFile(file);
				} else {
					file.setName(this.getName());
					file.setMimeType("text/xml");
					file.store();
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		} else {
			System.out.println("IBPage, file == null in update");
		}
		super.update();
	}

	/**
	 *
	 */
	public void insert() throws SQLException {
		ICFile file = getFile();
		if (file != null) {
			//System.out.println("file != null in insert");
			file.store();
			setFile(file);
		} else {
			//System.out.println("file == null in insert");
		}
		super.insert();
	}

	/**
	 *
	 */
	public void delete() throws SQLException {
		throw new SQLException("Use delete(int userId) instead");
	}

	/**
	 *
	 */
	public void delete(int userId) throws SQLException {
		setColumn(getColumnDeleted(), DELETED);
		setDeletedWhen(IWTimestamp.getTimestampRightNow());
		setDeletedBy(userId);

		super.update();
	}

	/**
	 *
	 */
	public void setIsPage() {
		setType(PAGE);
	}

	/**
	 *
	 */
	public void setIsTemplate() {
		setType(TEMPLATE);
	}

	/**
	 *
	 */
	public void setIsDraft() {
		setType(DRAFT);
	}

	/**
	 *
	 */
	public void setIsFolder() {
		setType(FOLDER);
	}

	/**
	 *
	 */
	public boolean isPage() {
		String type = getType();
		if (type.equals(PAGE))
			return (true);
		else
			return (false);
	}

	/**
	 *
	 */
	public boolean isTemplate() {
		String type = getType();
		if (type.equals(TEMPLATE))
			return (true);
		else
			return (false);
	}

	/**
	 *
	 */
	public boolean isDraft() {
		String type = getType();
		if (type.equals(DRAFT))
			return (true);
		else
			return (false);
	}

	/**
	 *
	 */
	public boolean isFolder() {
		String type = getType();
		if (type.equals(FOLDER))
			return (true);
		else
			return (false);
	}

	/**
	 *
	 */
	public boolean isDynamicTriggeredPage() {
		String type = getType();
		if (type.equals(DPT_PAGE))
			return (true);
		else
			return (false);
	}

	/**
	 *
	 */
	public boolean isDynamicTriggeredTemplate() {
		String type = getType();
		if (type.equals(DPT_TEMPLATE))
			return (true);
		else
			return (false);
	}

	/**
	 *
	 */
	public boolean isLeaf() {
		if (getType().equals(FOLDER))
			return false;
		else
			return true;
	}

	public void setOwner(IWUserContext iwuc) {
		try {
			iwuc.getAccessController().setCurrentUserAsOwner(this, iwuc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setTreeOrder(int order) {
		setColumn(TREE_ORDER, order);
	}

	public void setTreeOrder(Integer order) {
		setColumn(TREE_ORDER, order);
	}

	public int getTreeOrder() {
		return getIntColumnValue(TREE_ORDER);
	}
}