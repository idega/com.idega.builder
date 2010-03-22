package com.idega.builder.form.data;

import java.sql.Timestamp;

import com.idega.core.file.data.ICFile;
import com.idega.data.GenericEntity;
import com.idega.data.MetaDataCapable;
import com.idega.util.IWTimestamp;

public class EmailedFormBMPBean extends GenericEntity implements EmailedForm, MetaDataCapable {
	private static final String ENTITY_NAME = "fe_emailed_form";
	private static final String COLUMN_TYPE = "type";
	private static final String COLUMN_FIELD_LIST = "field_list";
	private static final String COLUMN_ATTACHMENT = "attachment";
	private static final String COLUMN_FORM_ENTRY_DATE = "form_entry_date";
	
	public String getEntityName() {
		return ENTITY_NAME;
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(COLUMN_TYPE, "Type", String.class);
		addAttribute(COLUMN_FIELD_LIST, "List of the fields in metadata/form", String.class, 4000);
		addManyToOneRelationship(COLUMN_ATTACHMENT, ICFile.class);
		addAttribute(COLUMN_FORM_ENTRY_DATE, "Form entry date", Timestamp.class);
		
		addMetaDataRelationship();
	}
	
	public void setDefaultValues() {
		setFormEntryDate(IWTimestamp.getTimestampRightNow());
	}
	
	//getters
	public String getType() {
		return getStringColumnValue(COLUMN_TYPE);
	}
	
	public String getFieldList() {
		return getStringColumnValue(COLUMN_FIELD_LIST);
	}
	
	public ICFile getAttachment() {
		return (ICFile) getColumnValue(COLUMN_ATTACHMENT);
	}
	
	public Timestamp getFormEntryDate() {
		return getTimestampColumnValue(COLUMN_FORM_ENTRY_DATE);
	}
	
	//setters
	public void setType(String type) {
		setColumn(COLUMN_TYPE, type);
	}
	
	public void setFieldList(String fieldList) {
		setColumn(COLUMN_FIELD_LIST, fieldList);
	}
	
	public void setAttachment(ICFile file) {
		setColumn(COLUMN_ATTACHMENT, file);
	}
	
	public void setFormEntryDate(Timestamp entryDate) {
		setColumn(COLUMN_FORM_ENTRY_DATE, entryDate);
	}
}