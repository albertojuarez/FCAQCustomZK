/******************************************************************************
 * Product: Posterita Ajax UI 												  *
 * Copyright (C) 2007 Posterita Ltd.  All Rights Reserved.                    *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Posterita Ltd., 3, Draper Avenue, Quatre Bornes, Mauritius                 *
 * or via info@posterita.org or http://www.posterita.org/                     *
 *****************************************************************************/

package org.adempiere.webui.session;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.adempiere.webui.IWebClient;
import org.adempiere.webui.desktop.IDesktop;
import org.compiere.util.Env;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;

/**
 * 
 * @author <a href="mailto:agramdass@gmail.com">Ashley G Ramdass</a>
 * @date Feb 25, 2007
 * @version $Revision: 0.10 $
 */
public class SessionManager
{
    public static final String SESSION_APPLICATION = "SessionApplication";
    
    public static boolean isUserLoggedIn(Properties ctx)
    {
        String adUserId = Env.getContext(ctx, "#AD_User_ID");
        String adRoleId = Env.getContext(ctx, "#AD_Role_ID");
        String adClientId = Env.getContext(ctx, "#AD_Client_ID");
        String adOrgId = Env.getContext(ctx, "#AD_Org_ID");

        return (!"".equals(adUserId) && !"".equals(adRoleId)
                && !"".equals(adClientId) && !"".equals(adOrgId));
    }
    
    public static Session getSession()
    {
        return  Executions.getCurrent().getDesktop().getSession();
    }
    
    public static void setSessionApplication(IWebClient app)
    {
        Session session = getSession();
        session.setAttribute(SESSION_APPLICATION, app);
    }
    
    public static IDesktop getAppDesktop()
    {
    	return getSessionApplication().getAppDeskop();
    }
    
    public static IWebClient getSessionApplication()
    {
    	
    	String displaytype = ""; // W - Window, P - Process
    	int desktop_id= -1;
    	try
    	{
    		Map map = Executions.getCurrent().getParameterMap();
    
    		Set set = map.entrySet();
    		Iterator parameters = set.iterator();
    				
    		while (parameters.hasNext())
    		{
    			Map.Entry m =(Map.Entry)parameters.next();
    			String name = (String)m.getKey();
    			if("displaytype".equals(name))
					displaytype = Executions.getCurrent().getParameter("displaytype");
				else if("desktop_id".equals(name))
					desktop_id = Integer.parseInt(Executions.getCurrent().getParameter("desktop_id"));
    		}
    	}
    	catch(Exception e)
    	{
    		displaytype="";
    		desktop_id = -1;
    	}
    	
        Session session = getSession();
        IWebClient app = null; 
        if(desktop_id>0)
        	 app = (IWebClient)session.getAttribute(SESSION_APPLICATION + displaytype + desktop_id);
        else
        	 app = (IWebClient)session.getAttribute(SESSION_APPLICATION);
        return app;
    }
    
    public static void clearSession()
    {
        Env.getCtx().clear();
        Session session = getSession();
        session.removeAttribute(SessionContextListener.SESSION_CTX);
        session.invalidate();
    }
    
    public static void logoutSession()
    {
        getSessionApplication().logout();
    }
    
    
    // Controlling multiple desktops by same session
    
    
    public static boolean isDefaultDesktop()
    {
    	 Session session = getSession();
         Boolean isdefault = (Boolean)session.getAttribute("IsDefaultDesktop");
         return isdefault;    
    }
    
    public static void setIsDefaultDesktop( boolean isDefault)
    {
    	Session session = getSession();
        session.setAttribute("IsDefaultDesktop", isDefault);
    }
    
    public static void setUniqueDesktop(IDesktop desktop)
    {
    	Session session = getSession();
        session.setAttribute("UniqueDesktop", desktop);
    }
    public static IDesktop getUniqueDesktop()
    {
    	Session session = getSession();
        IDesktop desktop = (IDesktop)session.getAttribute("UniqueDesktop");
        return desktop; 
    }
    
    public static void setSessionApplication(IWebClient app, String attributeName)
    {
        Session session = getSession();
        session.setAttribute(attributeName, app);
    }
    
}
