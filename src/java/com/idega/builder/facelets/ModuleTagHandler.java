/**
 * Licensed under the Common Development and Distribution License,
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.sun.com/cddl/
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.idega.builder.facelets;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.component.ActionSource;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.presentation.AdminToolbar;
import com.idega.builder.presentation.IBObjectControl;
import com.idega.presentation.IWContext;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.MetaRuleset;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagException;

import org.apache.myfaces.view.facelets.tag.MetaTagHandlerImpl;
import org.apache.myfaces.view.facelets.tag.jsf.ComponentSupport;
import org.apache.myfaces.view.facelets.tag.jsf.EditableValueHolderRule;
import org.apache.myfaces.view.facelets.tag.jsf.core.FacetHandler;

/**
 * <p>
 * Implementation of the "module" tag in the IBXML page format
 * as a Facelets Tag handler
 * </p>
 * 
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson </a>
 * 
 * Last modified: $Date: 2009/01/14 15:35:25 $ by $Author: tryggvil $
 * @version $Id: ModuleTagHandler.java,v 1.2 2009/01/14 15:35:25 tryggvil Exp $
 */
public class ModuleTagHandler extends MetaTagHandlerImpl {
    private final static Logger log = Logger
            .getLogger("facelets.tag.component");
    
    private final TagAttribute binding;
    private final String componentType;
    private final TagAttribute id;
    private String rendererType;
	private TagAttribute componentClass;

    //public ModuleTagHandler(ComponentConfig config) {
    public ModuleTagHandler(TagConfig config){
    	//super(new ModuleComponentConfig(config,this.getAttribute("id").getValue(),this.get));
        super(config);
    	//this.componentType = config.getComponentType();
        //this.rendererType = config.getRendererType();
        this.id = this.getAttribute("id");
        this.binding = this.getAttribute("binding");
        this.componentClass = this.getAttribute("class");
        //bugfix - don't know if fetching "class" attribute is ever used or if it is working anywhere.
        if(this.componentClass == null){
        	this.componentClass = this.getAttribute("componentClass");
        }
        ModuleComponentConfig conf = new ModuleComponentConfig(config,this.id.getValue(),this.componentClass.getValue());
        this.componentType=conf.getComponentType();
    }

    /**
     * Method handles UIComponent tree creation in accordance with the JSF 1.2
     * spec.
     * <ol>
     * <li>First determines this UIComponent's id by calling
     * {@link #getId(FaceletContext) getId(FaceletContext)}.</li>
     * <li>Search the parent for an existing UIComponent of the id we just
     * grabbed</li>
     * <li>If found, {@link #markForDeletion(UIComponent) mark} its children
     * for deletion.</li>
     * <li>If <i>not</i> found, call
     * {@link #createComponent(FaceletContext) createComponent}.
     * <ol>
     * <li>Set the UIComponent's id</li>
     * <li>Set the RendererType of this instance</li>
     * </ol>
     * </li>
     * <li>Now apply the nextHandler, passing the UIComponent we've
     * created/found.</li>
     * <li>Now add the UIComponent to the passed parent</li>
     * <li>Lastly, if the UIComponent already existed (found), then
     * {@link #finalizeForDeletion(UIComponent) finalize} for deletion.</li>
     * </ol>
     * 
     * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
     *      javax.faces.component.UIComponent)
     * 
     * @throws TagException
     *             if the UIComponent parent is null
     */
    public final void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, ELException {
        // make sure our parent is not null
        if (parent == null) {
            throw new TagException(this.tag, "Parent UIComponent was null");
        }
        
        // possible facet scoped
        String facetName = this.getFacetName(ctx, parent);

        // our id
        String id = ctx.generateUniqueId(this.tagId);

        // grab our component
        UIComponent c = ComponentSupport.findChildByTagId(parent, id);
        boolean componentFound = false;
        if (c != null) {
            componentFound = true;
            // mark all children for cleaning
            if (log.isLoggable(Level.FINE)) {
                log.fine(this.tag
                        + " Component["+id+"] Found, marking children for cleanup");
            }
            ComponentSupport.markForDeletion(c);
        } else {
            c = this.createComponent(ctx);
            if (log.isLoggable(Level.FINE)) {
                log.fine(this.tag + " Component["+id+"] Created: "
                        + c.getClass().getName());
            }
            this.setAttributes(ctx, c);
            
            // mark it owned by a facelet instance
            c.getAttributes().put(ComponentSupport.MARK_CREATED, id);
            
            // assign our unique id
            if (this.id != null) {
                c.setId(this.id.getValue(ctx));
            } else {
                UIViewRoot root = ComponentSupport.getViewRoot(ctx, parent);
                if (root != null) {
                    String uid = root.createUniqueId();
                    c.setId(uid);
                }
            }
            
            if (this.rendererType != null) {
                c.setRendererType(this.rendererType);
            }
            
            // hook method
            this.onComponentCreated(ctx, c, parent);
        }

        // first allow c to get populated
        this.applyNextHandler(ctx, c);

        // finish cleaning up orphaned children
        if (componentFound) {
            ComponentSupport.finalizeForDeletion(c);
            
            if (facetName == null) {
            	parent.getChildren().remove(c);
            }
        }
        
        this.onComponentPopulated(ctx, c, parent);

        BuilderLogic builderLogic = BuilderLogic.getInstance();
    	IWContext iwc = IWContext.getIWContext(ctx.getFacesContext());
    	//ViewNode requestedNode = ViewManager.getInstance(iwc.getIWMainApplication()).getViewNodeForContext(iwc);
    	//if(requestedNode instanceof IBXMLPage){
    		//IBXMLPage page = (IBXMLPage)requestedNode;
	    	//if(page.hasEditPermissions(iwc)){
	    	if(iwc.isLoggedOn()){
    		//Page parentPage = PresentationObjectUtil.getParentPage(c);
	    		//String pageKey = page.getPageKey();
	    		String pageKey = "-1";
	    		//if(parentPage!=null){
	    		//	pageKey=Integer.toString(parentPage.getPageID());
	    		//}
	    		Boolean added = (Boolean) iwc.getExternalContext().getRequestMap().get("resourcesAdded");
	    		if(added==null||added==false){
	        		iwc.getExternalContext().getRequestMap().put("resourcesAdded",Boolean.TRUE);
	        		builderLogic.addResourcesForBuilderEditMode(iwc);
	        		parent.getChildren().add(new AdminToolbar());
	    		}
	        	c=new IBObjectControl(c,parent,pageKey,iwc,-1,false,false);
	    	} 
    	//}
    	
        // add to the tree afterwards
        // this allows children to determine if it's
        // been part of the tree or not yet
        if (facetName == null) {
        	parent.getChildren().add(c);
        } else {
        	parent.getFacets().put(facetName, c);
        }
    }
    
    /**
     * Return the Facet name we are scoped in, otherwise null
     * @param ctx
     * @return
     */
    protected final String getFacetName(FaceletContext ctx, UIComponent parent) {
    	return (String) parent.getAttributes().get(FacetHandler.KEY);
    }

    
    /**
     * If the binding attribute was specified, use that in conjuction with our
     * componentType String variable to call createComponent on the Application,
     * otherwise just pass the componentType String.
     * <p />
     * If the binding was used, then set the ValueExpression "binding" on the
     * created UIComponent.
     * 
     * @see Application#createComponent(javax.faces.el.ValueBinding,
     *      javax.faces.context.FacesContext, java.lang.String)
     * @see Application#createComponent(java.lang.String)
     * @param ctx
     *            FaceletContext to use in creating a component
     * @return
     */
    protected UIComponent createComponent(FaceletContext ctx) {
        UIComponent c = null;
        FacesContext faces = ctx.getFacesContext();
        Application app = faces.getApplication();
        if (this.binding != null) {
            ValueExpression ve = this.binding.getValueExpression(ctx,
                    Object.class);
            c = app.createComponent(ve, faces, this.componentType);
            if (c != null) {
                c.setValueExpression("binding", ve);
            }
        } else {
            c = app.createComponent(this.componentType);
        }
        return c;
    }

    /**
     * If the id TagAttribute was specified, get it's value, otherwise generate
     * a unique id from our tagId.
     * 
     * @see TagAttribute#getValue(FaceletContext)
     * @param ctx
     *            FaceletContext to use
     * @return what should be a unique Id
     */
    protected String getId(FaceletContext ctx) {
        if (this.id != null) {
            return this.id.getValue(ctx);
        }
        return ctx.generateUniqueId(this.tagId);
    }

    protected MetaRuleset createMetaRuleset(Class type){
        MetaRuleset m = super.createMetaRuleset(type);
        
        // ignore standard component attributes
        m.ignore("binding").ignore("id");
        
        // add auto wiring for attributes
        Class componentRuleClass;
		try {
			componentRuleClass = Class.forName("org.apache.myfaces.view.facelets.tag.jsf.ComponentRule");
	        MetaRule rule = (MetaRule)componentRuleClass.newInstance();
	        //m.addRule(ComponentRule.Instance);
	        m.addRule(rule);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // if it's an ActionSource
        if (ActionSource.class.isAssignableFrom(type)) {
            //m.addRule(ActionSourceRule.Instance);
        	try{
	            Class acomponentRuleClass = Class.forName("org.apache.myfaces.view.facelets.tag.jsf.ActionSourceRule");
	            MetaRule aRule = (MetaRule)acomponentRuleClass.newInstance();
	            //m.addRule(ComponentRule.Instance);
	            m.addRule(aRule);
        	}
        	catch(Exception e){}
        }
        
        // if it's a ValueHolder
        if (ValueHolder.class.isAssignableFrom(type)) {
            //m.addRule(ValueHolderRule.Instance);
        	try{
	            Class acomponentRuleClass = Class.forName("org.apache.myfaces.view.facelets.tag.jsf.ValueHolderRule");
	            MetaRule aRule = (MetaRule)acomponentRuleClass.newInstance();
	            //m.addRule(ComponentRule.Instance);
	            m.addRule(aRule);
        	}
        	catch(Exception e){}
            
            // if it's an EditableValueHolder
            if (EditableValueHolder.class.isAssignableFrom(type)) {
                m.ignore("submittedValue");
                m.ignore("valid");
                m.addRule(EditableValueHolderRule.Instance);
            }
        }
        
        return m;
    }
    
    /**
     * A hook method for allowing developers to do additional processing once Facelets
     * creates the component.  The 'setAttributes' method is still perferred, but this
     * method will provide the parent UIComponent before it's been added to the tree and
     * before any children have been added to the newly created UIComponent.
     * 
     * @param ctx
     * @param c
     * @param parent
     */
    protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {

    	
        // do nothing
    }

    protected void onComponentPopulated(FaceletContext ctx, UIComponent c, UIComponent parent) {
      
    	// do nothing
    }

    protected void applyNextHandler(FaceletContext ctx, UIComponent c) 
            throws IOException, FacesException, ELException {
        // first allow c to get populated
        this.nextHandler.apply(ctx, c);
    }
}