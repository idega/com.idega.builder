package com.idega.builder.form.presentation;

import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;

import com.idega.idegaweb.IWGenericFormHandler;

/**
 * Title:        idegaWeb Builder
 * Description:  idegaWeb Builder is a framework for building and rapid development of dynamic web applications
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class FormEmailer extends Block {

  private IWGenericFormHandler handler;

  private String emailServer;
  private String emailToSendTo;
  private boolean displayConfirmation=false;
  private String subject = "From idegaWeb Builder";
  private String _beginningText;


  private static String CONFIRM_PARAMETER = "ib_formem_conf";
  private static String TEXT_SESSION_KEY = "IB_FORMEMAILER_TEXT";


  public FormEmailer(){
    handler=new IWGenericFormHandler();
  }

  public void main(IWContext iwc){

    if(doDisplayConfirmation(iwc)){
      String sentText=this.getSentText(iwc);
      String confirmationText = "Confirm send of supplied information:";
      String sendText="Send";
      Table t = new Table();
      add(t);
      t.add(confirmationText,1,1);
      t.add("<pre>"+sentText+"</pre>",1,2);
      Form f = new Form();
      t.add(f,1,3);
      SubmitButton button = new SubmitButton(this.CONFIRM_PARAMETER,sendText);
      f.add(button);
      t.setAlignment(1,3,com.idega.idegaweb.IWConstants.CENTER_ALIGNMENT);;
    }
    else{
      try{
        sendEmail(iwc);
        add("Email sent successfully");
      }
      catch(Exception e){
        add("There was an error sending the mail: "+e.getMessage());
        e.printStackTrace();
      }
    }
  }

  private boolean doDisplayConfirmation(IWContext iwc){
    if(this.displayConfirmation){
      if(iwc.getParameter(CONFIRM_PARAMETER)==null){
        return true;
      }
      else{
        return false;
      }
    }
    else{
      return false;
    }
  }

  private String getSentText(IWContext iwc){
    if(iwc.getParameter(this.CONFIRM_PARAMETER)==null){
      String text = handler.processPlainTextFormatted(iwc);
      iwc.setSessionAttribute(TEXT_SESSION_KEY,text);
      return text;
    }
    else{
      return (String)iwc.getSessionAttribute(TEXT_SESSION_KEY);
    }
  }

  private void cleanUpFromSession(IWContext iwc){
    iwc.removeSessionAttribute(TEXT_SESSION_KEY);
  }

  public void sendEmail(IWContext iwc)throws Exception{
    String formText = getSentText(iwc);
    String bodyText;
    if(_beginningText==null){
      bodyText=formText;
    }
    else{
      bodyText=_beginningText+"\n"+formText;
    }

    if(formText==null){
      System.out.println("formText==null");
      formText="Ekkert";
    }
    if(emailServer==null){
      throw new Exception("Email Server not specified");
    }
    if(emailToSendTo==null){
      throw new Exception("No email to send to");
    }
    //com.idega.util.SendMail.send("idega@idega.is",emailToSendTo,"","",emailServer,subject,bodyText);
    cleanUpFromSession(iwc);
  }

  public void addRecievedParameter(String paramName,String description,String type){
    handler.addProcessedParameter(paramName,description,type);
  }


  public void setTextInBeginningOfMail(String beginningText){
    this._beginningText=beginningText;
  }

  public void setSubjectOfMail(String subject){
    this.subject=subject;
  }

  public void setMailServer(String serverName){
    this.emailServer=serverName;
  }

  public void setSendToAddress(String emailAddress){
    this.emailToSendTo=emailAddress;
  }

  public void setToDisplayConfirmation(boolean doConfirmation){
    this.displayConfirmation=doConfirmation;
  }


  public Object clone(){
    Object newObject = super.clone();
    FormEmailer newEmailer = (FormEmailer)newObject;
    if(this.handler!=null){
      newEmailer.handler=(IWGenericFormHandler)this.handler.clone();
    }
    return newObject;
  }


}