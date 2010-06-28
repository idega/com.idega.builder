/**
 * 
 */
package com.idega.builder.business;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.AuthenticationListener;
import com.idega.core.accesscontrol.business.ServletFilterChainInterruptException;
import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.core.builder.business.BuilderService;
import com.idega.presentation.IWContext;
import com.idega.user.data.bean.User;


/**
 * <p>
 * TODO laddi Describe Type UserLoggedInListener
 * </p>
 *  Last modified: $Date: 2008/11/17 08:42:42 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class UserLoggedInListener implements AuthenticationListener {

	/* (non-Javadoc)
	 * @see com.idega.core.accesscontrol.business.AuthenticationListener#getAuthenticationListenerName()
	 */
	public String getAuthenticationListenerName() {
		return "builder.UserLoggedInListener";
	}

	/* (non-Javadoc)
	 * @see com.idega.core.accesscontrol.business.AuthenticationListener#onLogoff(com.idega.presentation.IWContext, com.idega.user.data.User)
	 */
	public void onLogoff(IWContext iwc, User lastUser) throws ServletFilterChainInterruptException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.idega.core.accesscontrol.business.AuthenticationListener#onLogon(com.idega.presentation.IWContext, com.idega.user.data.User)
	 */
	public void onLogon(IWContext iwc, User currentUser) throws ServletFilterChainInterruptException {
		if (iwc.hasRole(StandardRoles.ROLE_KEY_ADMIN) || iwc.hasRole(StandardRoles.ROLE_KEY_EDITOR)) {
			try {
				BuilderService service = (BuilderService) IBOLookup.getServiceInstance(iwc, BuilderService.class);
				service.startBuilderSession(iwc);
			}
			catch (IBOLookupException ile) {
				throw new IBORuntimeException(ile);
			}
		}
	}

}
