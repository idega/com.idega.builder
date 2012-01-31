package com.idega.builder.form.presentation;

import java.io.File;
import java.util.List;

import com.idega.builder.business.BuilderConstants;
import com.idega.builder.form.business.EmailedFormBusiness;
import com.idega.builder.handler.IBGenericFormHandler;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.io.UploadFile;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.ui.BackButton;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.util.SendMail;

/**
 * Title: idegaWeb Builder Description: idegaWeb Builder is a framework for
 * building and rapid development of dynamic web applications Copyright:
 * Copyright (c) 2001 Company: idega
 *
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.0
 */
public class FormEmailer extends Block {

	private IBGenericFormHandler handler;

	private String emailServer;

	private String emailToSendTo;

	private String senderEmail = "idegaweb@idega.com";

	private String senderEmailParameter;

	private boolean displayConfirmation = true;

	private final static String SUBJECT_CONSTANT = "From idegaWeb Builder";

	private String subject = SUBJECT_CONSTANT;

	private String _beginningText;

	private boolean sendReceipt = false;

	private String receiptEmailParameter;

	private String spambot_catch_dummy_parameter;
	private String spambot_catch_time_parameter;
	private Integer spambot_catch_timeup;

	private static String CONFIRM_PARAMETER = "ib_formem_conf";

	private static String TEXT_SESSION_KEY = "IB_FORMEMAILER_TEXT";

	private static String UPLOADED_FILENAME_SESSION_KEY = "IB_FORMEMAILER_FILE";

	//new 20.2.2009 - Save form to table
	private boolean saveForm = false;
	private String formType = null;
	private String fieldList = null;


	public FormEmailer() {
		this.handler = new IBGenericFormHandler();
	}

	@Override
	public void main(IWContext iwc) {

		if(isSpambot(iwc)) {
			add(
				getBundle(iwc).getResourceBundle(iwc).getLocalizedString("formemailer.spambotdetected",
				"Sorry, you're most likely to be a spambot. If that's not the case, please press back and refill the form.")
			);
			return;
		}

		UploadFile uploadFile = iwc.getUploadedFile();
		if (uploadFile != null) {
			String uploadedFileName = uploadFile.getAbsolutePath();
			iwc.setSessionAttribute(UPLOADED_FILENAME_SESSION_KEY, uploadedFileName);
		}
		IWResourceBundle iwrb = super.getBundle(iwc).getResourceBundle(iwc);
		if (this.subject == SUBJECT_CONSTANT) {
			this.subject = iwrb.getLocalizedString("formemailer.defaultsubject", "From idegaWeb Builder");
		}
		if (doDisplayConfirmation(iwc)) {
			try {
				String sentText = this.getSentText(iwc);
				String confirmationText = iwrb.getLocalizedString("formemailer.confirmationtext",
						"Confirm send of supplied information:");
				String sendText = iwrb.getLocalizedString("formemailer.send", "Send");
				Table t = new Table();
				add(t);
				t.add(confirmationText, 1, 1);
				t.add("<pre>" + sentText + "</pre>", 1, 2);
				Form f = new Form();
				t.add(f, 1, 3);
				SubmitButton button = new SubmitButton(FormEmailer.CONFIRM_PARAMETER, sendText);
				f.add(button);
				t.setAlignment(1, 3, com.idega.idegaweb.IWConstants.CENTER_ALIGNMENT);
			}
			catch (Exception e) {
				e.printStackTrace();
				Table t = new Table();
				add(t);
				String errorText = iwrb.getLocalizedString("formemailer.error4",
						"There was an error processing the form, one or more fields may be empty");
				t.add(errorText, 1, 1);
				String buttonText = iwrb.getLocalizedString("formemailer.back", "Back");
				BackButton back = new BackButton(buttonText);
				t.add(back, 1, 2);
			}
		}
		else {
			try {
				sendEmail(iwc);
				if (this.saveForm) {
					List fieldValues = this.handler.processFormToFieldList(iwc);
					if (fieldValues != null && !fieldValues.isEmpty()) {
						getEmailedFormBusiness(iwc).insertFormEntries(this.formType, this.fieldList, fieldValues, uploadFile);
					}
				}

				String successfully = iwrb.getLocalizedString("formemailer.successfully", "Email sent successfully");
				add(successfully);
			}
			catch (Exception e) {
				String error1 = iwrb.getLocalizedString("formemailer.error1", "There was an error sending the mail: ");
				add(error1 + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private boolean doDisplayConfirmation(IWContext iwc) {
		if (this.displayConfirmation) {
			if (iwc.getParameter(CONFIRM_PARAMETER) == null) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	private String getSentText(IWContext iwc) {
		if (iwc.getParameter(FormEmailer.CONFIRM_PARAMETER) == null) {
			String text = this.handler.processPlainTextFormatted(iwc);
			iwc.setSessionAttribute(TEXT_SESSION_KEY, text);
			return text;
		}
		else {
			return (String) iwc.getSessionAttribute(TEXT_SESSION_KEY);
		}
	}

	private void cleanUpFromSession(IWContext iwc) {
		iwc.removeSessionAttribute(TEXT_SESSION_KEY);
		iwc.removeSessionAttribute(UPLOADED_FILENAME_SESSION_KEY);
	}

	public void sendEmail(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = super.getBundle(iwc).getResourceBundle(iwc);
		String formText = getSentText(iwc);
		String bodyText;
		String emailFrom = this.senderEmail;
		if (this.senderEmailParameter != null) {
			emailFrom = iwc.getParameter(this.senderEmailParameter);
		}
		if (this._beginningText == null) {
			bodyText = formText;
		}
		else {
			bodyText = this._beginningText + "\n" + formText;
		}
		if (formText == null) {
			// System.out.println("formText==null");
			formText = iwrb.getLocalizedString("formemailer.error_no_email_body", "<<No email body found>>");
		}

		if (this.emailToSendTo == null) {
			String error3 = iwrb.getLocalizedString("formemailer.error3", "No email to send to");
			throw new Exception(error3);
		}
		// if ()
		// System.out.println("Got email to send to " + emailToSendTo + " from "
		// + emailFrom);
		// System.out.println("Message is: " + bodyText);
		File uploadFile = null;
		try {
			String uploadedFileName = (String) iwc.getSessionAttribute(UPLOADED_FILENAME_SESSION_KEY);
			if (uploadedFileName != null) {
				uploadFile = new File(uploadedFileName);
				if (uploadFile.isDirectory()) {
					uploadFile = null;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		try {
			SendMail.send(emailFrom, this.emailToSendTo, "", "", null, this.emailServer, this.subject, bodyText, false, false, uploadFile);
		} catch (Exception e) {
			e.printStackTrace();
			SendMail.send(this.senderEmail, this.emailToSendTo, "", "", null, this.emailServer, this.subject, bodyText, false, false, uploadFile);
		}
		if (this.sendReceipt) {
			String receiptSubject = iwrb.getLocalizedString("formemailer.receiptSubject",
					"The subject of the receipt email");
			String receiptBody = iwrb.getLocalizedString("formemailer.receiptBody", "The body of the receipt email");
			String receiptSignature = iwrb.getLocalizedString("formemailer.receiptSignature",
					"The signature on the receipt email");
			String emailReceiptTo = this.handler.getParameterValue(iwc, this.receiptEmailParameter);
			if (emailReceiptTo != null) {
				try {
					SendMail.send(this.emailToSendTo, emailReceiptTo, "", "", this.emailServer, receiptSubject,	receiptBody + "\n" + receiptSignature);
				}
				catch (Exception e) {
					try {
						SendMail.send(emailFrom, emailReceiptTo, "", "", this.emailServer, receiptSubject, receiptBody + "\n" + receiptSignature);
					}
					catch (Exception e1) {
						SendMail.send(this.senderEmail, emailReceiptTo, "", "", this.emailServer, receiptSubject, receiptBody + "\n" + receiptSignature);
					}
				}
			}
		}

		cleanUpFromSession(iwc);
	}

	protected boolean isSpambot(IWContext iwc) {

		if(getSpambotCatchDummyParameter() != null) {

			String dsc_par = handler.getParameterValue(iwc, getSpambotCatchDummyParameter());

			if(!"0".equals(dsc_par))
				return true;
		}

		if(getSpambotCatchTimeParameter() != null) {

			String sct_par = handler.getParameterValue(iwc, getSpambotCatchTimeParameter());

			if(sct_par == null)
				return true;

			try {
				int sct = Integer.parseInt(sct_par);

				if(sct < getSpambotCatchTimeup().intValue())
					return true;

			} catch (Exception e) {
				return true;
			}
		}

		return false;
	}

	public void setToAddRecievedParameter(String paramName, String description, String type) {
		if (this.fieldList == null) {
			fieldList = new String(paramName);
		} else {
			StringBuffer buffer = new StringBuffer(fieldList);
			buffer.append(";");
			buffer.append(paramName);

			this.fieldList = buffer.toString();
		}

		this.handler.addProcessedParameter(paramName, description, type);
	}

	public void setTextInBeginningOfMail(String beginningText) {
		this._beginningText = beginningText;
	}

	public void setSubjectOfMail(String subject) {
		this.subject = subject;
	}

	public void setMailServer(String serverName) {
		this.emailServer = serverName;
	}

	public void setSendToAddress(String emailAddress) {
		this.emailToSendTo = emailAddress;
	}

	public void setToDisplayConfirmation(boolean doConfirmation) {
		this.displayConfirmation = doConfirmation;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public void setSenderEmailParameter(String parameterName) {
		this.senderEmailParameter = parameterName;
	}

	@Override
	public Object clone() {
		Object newObject = super.clone();
		FormEmailer newEmailer = (FormEmailer) newObject;
		if (this.handler != null) {
			newEmailer.handler = (IBGenericFormHandler) this.handler.clone();
		}
		return newObject;
	}

	@Override
	public String getBundleIdentifier() {
		return BuilderConstants.IW_BUNDLE_IDENTIFIER;
	}

	public void setSendReceipt(boolean sendReceipt) {
		this.sendReceipt = sendReceipt;
	}

	public void setSaveForm(boolean saveForm) {
		this.saveForm = saveForm;
	}

	public boolean getSaveForm() {
		return this.saveForm;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public String getFormType() {
		return this.formType;
	}

	public boolean getSendReceipt() {
		return this.sendReceipt;
	}

	public void setReceiptEmailParameter(String parameter) {
		this.receiptEmailParameter = parameter;
	}

	public String getReceiptEmailParameter() {
		return this.receiptEmailParameter;
	}

	public String getSpambotCatchDummyParameter() {
		return spambot_catch_dummy_parameter;
	}

	public void setSpambotCatchDummyParameter(
			String spambot_catch_dummy_parameter) {
		this.spambot_catch_dummy_parameter = spambot_catch_dummy_parameter;
	}

	public String getSpambotCatchTimeParameter() {
		return spambot_catch_time_parameter;
	}

	public void setSpambotCatchTimeParameter(String spambot_catch_time_parameter) {
		this.spambot_catch_time_parameter = spambot_catch_time_parameter;
	}

	public Integer getSpambotCatchTimeup() {
		return spambot_catch_timeup == null ? 10000 : spambot_catch_timeup;
	}

	public void setSpambotCatchTimeup(Integer spambot_catch_timeout) {
		this.spambot_catch_timeup = spambot_catch_timeout;
	}

	private EmailedFormBusiness getEmailedFormBusiness(IWContext iwc) throws IBOLookupException {
		return (EmailedFormBusiness) IBOLookup.getServiceInstance(iwc, EmailedFormBusiness.class);
	}

}
