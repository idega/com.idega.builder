package com.idega.builder.facelets;

import java.util.Iterator;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.CachedBuilderPage;
import com.idega.builder.business.PageCacher;

/**
 * <p>
 * Class to convert the Whole Builder Tree to the new Facelet formats
 * </p>
 *
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson </a>
 *
 * Last modified: $Date: 2009/01/14 15:35:24 $ by $Author: tryggvil $
 * @version $Id: BuilderBatchFaceletConverter.java,v 1.3 2009/01/14 15:35:24 tryggvil Exp $
 */
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Service(BuilderBatchFaceletConverter.beanIdentifier)
public class BuilderBatchFaceletConverter {

	public static final String beanIdentifier = "builderBatchFaceletConverter";

	public void convertAllPagesToFaceletsLegacy(){
		PageCacher cacher = BuilderLogic.getInstance().getPageCacher();
		Iterator<CachedBuilderPage> iter = cacher.getAllPages();
		while(iter.hasNext()){
			CachedBuilderPage page = iter.next();
			try {
				if(page.getPageFormat().equals(BuilderLogic.PAGE_FORMAT_IBXML)){
					String pageKey = page.getPageKey();
					BuilderFaceletConverter converter = new BuilderFaceletConverter(page,BuilderLogic.PAGE_FORMAT_IBXML2);
					converter.convert();
					String convertedContent = converter.getConvertedMarkupString();
					cacher.storePage(pageKey, BuilderLogic.PAGE_FORMAT_IBXML2, convertedContent);
					cacher.flagPageInvalid(pageKey);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void convertAllPagesToFaceletsCompliant(){
		PageCacher cacher = BuilderLogic.getInstance().getPageCacher();
		Iterator<CachedBuilderPage> iter = cacher.getAllPages();
		while(iter.hasNext()){
			CachedBuilderPage page = iter.next();
			try {
				if(page.getPageFormat().equals(BuilderLogic.PAGE_FORMAT_IBXML)||page.getPageFormat().equals(BuilderLogic.PAGE_FORMAT_HTML)){
					String pageKey = page.getPageKey();
					BuilderFaceletConverter converter = new BuilderFaceletConverter(page,BuilderLogic.PAGE_FORMAT_FACELET);
					converter.convert();
					String convertedContent = converter.getConvertedMarkupString();
					cacher.storePage(pageKey, BuilderLogic.PAGE_FORMAT_FACELET, convertedContent);
					cacher.flagPageInvalid(pageKey);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
