package com.idega.builder.form.data;


import com.idega.core.file.data.ICFile;
import com.idega.data.MetaDataCapable;
import java.sql.Timestamp;
import com.idega.data.IDOEntity;

public interface EmailedForm extends IDOEntity, MetaDataCapable {
	/**
	 * @see com.idega.builder.form.data.EmailedFormBMPBean#getType
	 */
	public String getType();

	/**
	 * @see com.idega.builder.form.data.EmailedFormBMPBean#getFieldList
	 */
	public String getFieldList();

	/**
	 * @see com.idega.builder.form.data.EmailedFormBMPBean#getAttachment
	 */
	public ICFile getAttachment();

	/**
	 * @see com.idega.builder.form.data.EmailedFormBMPBean#getFormEntryDate
	 */
	public Timestamp getFormEntryDate();

	/**
	 * @see com.idega.builder.form.data.EmailedFormBMPBean#setType
	 */
	public void setType(String type);

	/**
	 * @see com.idega.builder.form.data.EmailedFormBMPBean#setFieldList
	 */
	public void setFieldList(String fieldList);

	/**
	 * @see com.idega.builder.form.data.EmailedFormBMPBean#setAttachment
	 */
	public void setAttachment(ICFile file);

	/**
	 * @see com.idega.builder.form.data.EmailedFormBMPBean#setFormEntryDate
	 */
	public void setFormEntryDate(Timestamp entryDate);
}