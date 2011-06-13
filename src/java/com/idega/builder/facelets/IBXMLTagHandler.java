package com.idega.builder.facelets;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletException;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

/**
 * <p>
 * Class handle the root tag in IBXML
 * </p>
 * 
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson </a>
 * 
 * Last modified: $Date: 2009/01/14 15:35:25 $ by $Author: tryggvil $
 * @version $Id: IBXMLTagHandler.java,v 1.2 2009/01/14 15:35:25 tryggvil Exp $
 */
public class IBXMLTagHandler extends TagHandler{

	public IBXMLTagHandler(TagConfig config) {
		super(config);
	}

	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {
		//Does nothing except forward to the sub handlers
		this.nextHandler.apply(ctx, parent);
	}

}
