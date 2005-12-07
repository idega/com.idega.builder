<jsp:root version="1.2" 
	xmlns:f="http://java.sun.com/jsf/core" 
	xmlns:h="http://java.sun.com/jsf/html" 
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:ws="http://xmlns.idega.com/com.idega.workspace"
	xmlns:wf="http://xmlns.idega.com/com.idega.webface"
	xmlns:ic="http://xmlns.idega.com/com.idega.core">
    <jsp:directive.page contentType="text/html" /><!--;charset=UTF-8" pageEncoding="UTF-8"-->
    <f:view>
		<ic:page id="builderinitialsetuppage" styleClass="ws_body">
		<h:form id="builderinitialsetupform"><!-- acceptCharset="UTF-8" -->
			<wf:wfblock id="builderinitialsetupblock" title="#{localizedStrings['com.idega.builder']['initialsetup']}">
			<wf:container styleClass="wf_formitem" >
				<h:outputLabel for="mainDomainName" id="mainDomainNameLabel" value="#{localizedStrings['com.idega.builder']['mainDomainName']}"/>
				<h:inputText value="#{BuilderInitialSetup.domainName}" id="mainDomainName"/>
			</wf:container>
		
			<wf:container styleClass="wf_formitem" >
				<h:outputLabel for="frontPageName" id="frontPageNameLabel" value="#{localizedStrings['com.idega.builder']['frontPageName']}"/>
				<h:inputText value="#{BuilderInitialSetup.frontPageName}" id="frontPageName"/>
			</wf:container>
			
			<h:commandButton id="builderinitialsetup_store" action="#{BuilderInitialSetup.store}" value="#{localizedStrings['com.idega.builder']['save']}"/>
			</wf:wfblock>
		</h:form>
		</ic:page>
    </f:view>
</jsp:root>
