package com.idega.builder.form.presentation;

import java.io.File;
import com.idega.builder.handler.IBGenericFormHandler;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.io.UploadFile;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.ui.BackButton;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;

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

	private static final String BUILDER_BUNDLE_IDENTIFIER = "com.idega.builder";

	private String emailServer;

	private String emailToSendTo;

	private String senderEmail = "idegaweb@idega.com";

	private String senderEmailParameter;

	private boolean displayConfirmation = true;

	private final static String SUBJECT_CONSTANT = "From idegaWeb Builder";

	private String subject = SUBJECT_CONSTANT;

	private String _beginningText;

	private boolean sendReceipt = true;

	private String receiptEmailParameter;

	private static String CONFIRM_PARAMETER = "ib_formem_conf";

	private static String TEXT_SESSION_KEY = "IB_FORMEMAILER_TEXT";

	private static String UPLOADED_FILENAME_SESSION_KEY = "IB_FORMEMAILER_FILE";

	public FormEmailer() {
		handler = new IBGenericFormHandler();
	}

	public void main(IWContext iwc) {
		UploadFile uploadFile = iwc.getUploadedFile();
		if (uploadFile != null) {
			String uploadedFileName = uploadFile.getAbsolutePath();
			iwc.setSessionAttribute(UPLOADED_FILENAME_SESSION_KEY, uploadedFileName);
		}
		IWResourceBundle iwrb = super.getBundle(iwc).getResourceBundle(iwc);
		if (subject == SUBJECT_CONSTANT) {
			subject = iwrb.getLocalizedString("formemailer.defaultsubject", "From idegaWeb Builder");
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
				SubmitButton button = new SubmitButton(this.CONFIRM_PARAMETER, sendText);
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
		if (iwc.getParameter(this.CONFIRM_PARAMETER) == null) {
			String text = handler.processPlainTextFormatted(iwc);
			iwc.setSessionAttribute(TEXT_SESSION_KEY, text);
			return text;
		}
		else {
			return (String) iwc.getSessionAttribute(TEXT_SESSION_KEY);
		}
	}

	private void cleanUpFromSession(IWContext iwc) {
		iwc.removeSessionAttribute(TEXT_SESSION_KEY);
	}

	public void sendEmail(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = super.getBundle(iwc).getResourceBundle(iwc);
		String formText = getSentText(iwc);
		String bodyText;
		String emailFrom = senderEmail;
		if (senderEmailParameter != null) {
			emailFrom = iwc.getParameter(senderEmailParameter);
		}
		if (_beginningText == null) {
			bodyText = formText;
		}
		else {
			bodyText = _beginningText + "\n" + formText;
		}
		if (formText == null) {
			// System.out.println("formText==null");
			formText = iwrb.getLocalizedString("formemailer.error_no_email_body", "<<No email body found>>");
		}
		if (emailServer == null) {
			String error2 = iwrb.getLocalizedString("formemailer.error2", "Email Server not specified");
			throw new Exception(error2);
		}
		if (emailToSendTo == null) {
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
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		try {
			com.idega.util.SendMail.send(emailFrom, emailToSendTo, "", "", emailServer, subject, bodyText, uploadFile);
		}
		catch (Exception e) {
			e.printStackTrace();
			com.idega.util.SendMail.send(senderEmail, emailToSendTo, "", "", emailServer, subject, bodyText, uploadFile);
		}
		if (sendReceipt) {
			String receiptSubject = iwrb.getLocalizedString("formemailer.receiptSubject",
					"The subject of the receipt email");
			String receiptBody = iwrb.getLocalizedString("formemailer.receiptBody", "The body of the receipt email");
			String receiptSignature = iwrb.getLocalizedString("formemailer.receiptSignature",
					"The signature on the receipt email");
			String emailReceiptTo = handler.getParameterValue(iwc, receiptEmailParameter);
			if (emailReceiptTo != null) {
				try {
					com.idega.util.SendMail.send(emailToSendTo, emailReceiptTo, "", "", emailServer, receiptSubject,
							receiptBody + "\n" + receiptSignature);
				}
				catch (Exception e) {
					try {
						com.idega.util.SendMail.send(emailFrom, emailReceiptTo, "", "", emailServer, receiptSubject,
								receiptBody + "\n" + receiptSignature);
					}
					catch (Exception e1) {
						com.idega.util.SendMail.send(senderEmail, emailReceiptTo, "", "", emailServer, receiptSubject,
								receiptBody + "\n" + receiptSignature);
					}
				}
			}
		}
		cleanUpFromSession(iwc);
	}

	public void setToAddRecievedParameter(String paramName, String description, String type) {
		handler.addProcessedParameter(paramName, description, type);
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

	public Object clone() {
		Object newObject = super.clone();
		FormEmailer newEmailer = (FormEmailer) newObject;
		if (this.handler != null) {
			newEmailer.handler = (IBGenericFormHandler) this.handler.clone();
		}
		return newObject;
	}

	public String getBundleIdentifier() {
		return BUILDER_BUNDLE_IDENTIFIER;
	}

	public void setSendReceipt(boolean sendReceipt) {
		this.sendReceipt = sendReceipt;
	}

	public boolean getSendReceipt() {
		return sendReceipt;
	}

	public void setReceiptEmailParameter(String parameter) {
		receiptEmailParameter = parameter;
	}

	public String getReceiptEmailParameter() {
		return receiptEmailParameter;
	}
}