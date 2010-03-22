package com.idega.builder.form.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;

import javax.ejb.CreateException;

import com.idega.builder.form.data.EmailedForm;
import com.idega.builder.form.data.EmailedFormHome;
import com.idega.builder.form.data.Field;
import com.idega.business.IBOServiceBean;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.util.IWTimestamp;

public class EmailedFormBusinessBean extends IBOServiceBean implements EmailedFormBusiness {
	public boolean insertFormEntries(String type, String fieldList, List entries, File uploadFile) {
		try {
			EmailedForm form = getEmailedFormHome().create();
			form.setType(type);
			form.setFieldList(fieldList);
			if (entries != null && !entries.isEmpty()) {
				Iterator it = entries.iterator();
				while (it.hasNext()) {
					Field field = (Field) it.next();
					form.setMetaData(field.getName(), field.getValue());
				}
			}
			
			if (uploadFile != null && uploadFile.isFile()) {
				try {
					ICFile file = ((ICFileHome) IDOLookup.getHome(ICFile.class))
					.create();
					file.setFileValue(new FileInputStream(uploadFile));
					file.setName(uploadFile.getName());
					file.store();
					form.setAttachment(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			
			form.setFormEntryDate(IWTimestamp.getTimestampRightNow());
			
			form.store();
		} catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}

		return false;
	}
	
	private EmailedFormHome getEmailedFormHome() throws IDOLookupException {
		return (EmailedFormHome) IDOLookup.getHome(EmailedForm.class);
	}
}
