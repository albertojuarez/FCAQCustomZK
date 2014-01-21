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

package org.adempiere.webui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.CWindowToolbar;
import org.adempiere.webui.component.DrillCommand;
import org.adempiere.webui.component.TokenCommand;
import org.adempiere.webui.component.ZoomCommand;
import org.adempiere.webui.desktop.DefaultDesktop;
import org.adempiere.webui.desktop.IDesktop;
import org.adempiere.webui.desktop.SimpleDesktop;
import org.adempiere.webui.desktop.SimpleDesktopRender;
import org.adempiere.webui.event.TokenEvent;
import org.adempiere.webui.session.SessionContextListener;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.BrowserToken;
import org.adempiere.webui.util.UserPreference;
import org.adempiere.webui.window.ADWindow;
import org.compiere.model.MQuery;
import org.compiere.model.MRole;
import org.compiere.model.MSession;
import org.compiere.model.MSysConfig;
import org.compiere.model.MSystem;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.model.MWindow;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.SecureEngine;
import org.zkoss.zk.au.Command;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.impl.ExecutionCarryOver;
import org.zkoss.zk.ui.sys.DesktopCache;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.Visualizer;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Window;
import org.zkoss.zkex.zul.Borderlayout;


/**
 *
 * @author  <a href="mailto:agramdass@gmail.com">Ashley G Ramdass</a>
 * @date    Feb 25, 2007
 * @version $Revision: 0.10 $
 *
 * @author hengsin
 */
public class AdempiereWebUI extends Window implements EventListener, IWebClient
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3744725245132180915L;

	public static final String APP_NAME = "Adempiere";

    public static final String UID          = "3.5";

    private WLogin             loginDesktop;

    private IDesktop           appDesktop;

    private ClientInfo		   clientInfo;

	private String langSession;

	private UserPreference userPreference;

	private static final CLogger logger = CLogger.getCLogger(AdempiereWebUI.class);

	public static final String EXECUTION_CARRYOVER_SESSION_KEY = "execution.carryover";

	public static final String ZK_DESKTOP_SESSION_KEY = "zk.desktop";

    public AdempiereWebUI()
    {
    	this.addEventListener(Events.ON_CLIENT_INFO, this);
    	this.setVisible(false);

    	userPreference = new UserPreference();
    }
    
    private boolean isDefaultDesktop = true; 
    private String desktopClass = ";"; 
    private String username = ""; 
    private String password = "";
	private String role_id  = "";
	private int client_id= 0;
	private int org_id   = 0;
	private String displaytype = "W"; // W - Window, P - Process
	private int desktop_id= 0;
	private int  record_id = 0;
	private String pLanguaje = "es_EC";

	
	
	Hashtable<String, IDesktop> desktops = new Hashtable<String, IDesktop>();


	

    public void onCreate()
    {
    	// BEGIN AJC 11 dic 2013
    	
    	
    	isDefaultDesktop=true;
		Properties ctx = Env.getCtx();

		Map map = getDesktop().getExecution().getParameterMap();
		Set set = map.entrySet();
		Iterator parameters = set.iterator();
				
		while (parameters.hasNext())
		{
			Map.Entry m =(Map.Entry)parameters.next();
			String name = (String)m.getKey();
			
			try{
			
				if("iDesktopClass".equals(name))
				{
					isDefaultDesktop=false;
					desktopClass = getDesktop().getExecution().getParameter("iDesktopClass");		
				}
				else if("username".equals(name))
				{
					username = getDesktop().getExecution().getParameter("username");	
					username = SecureEngine.decrypt(username);
				}
				else if("password".equals(name))
				{
					password = getDesktop().getExecution().getParameter("password");
					password = SecureEngine.decrypt(password);
				}
				else if("role_id".equals(name))
					role_id = getDesktop().getExecution().getParameter("role_id");
				else if("client_id".equals(name))
					client_id = Integer.parseInt(getDesktop().getExecution().getParameter("client_id"));
				else if("org_id".equals(name))
					org_id = Integer.parseInt(getDesktop().getExecution().getParameter("org_id"));
				else if("displaytype".equals(name))
					displaytype = getDesktop().getExecution().getParameter("displaytype");
				else if("desktop_id".equals(name))
					desktop_id = Integer.parseInt(getDesktop().getExecution().getParameter("desktop_id"));
				else if("record_id".equals(name))
					record_id = Integer.parseInt(getDesktop().getExecution().getParameter("record_id"));
				else if(name.startsWith("_"))
				{
					String value = getDesktop().getExecution().getParameter(name);
					name = name.substring(name.indexOf("_")+1) + displaytype + desktop_id;
					Env.setContext(ctx, 0, name, value);
					value = Env.getContext(ctx, 0 ,  name);
					System.out.println("To Context: " + name + "=" + value);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
//		Language
		String langName = pLanguaje;
		Language language = Language.getLanguage(langName);
		Language.setLoginLanguage(language);
		Env.verifyLanguage (ctx, language);
		Env.setContext(ctx, Env.LANGUAGE, language.getAD_Language());
    	// END AJC 11 dic 2013
		
		
    	
        this.getPage().setTitle(ThemeManager.getBrowserTitle());
        
        
        langSession = Env.getContext(ctx, Env.LANGUAGE);
        
        SessionManager.setIsDefaultDesktop(isDefaultDesktop);
        
        if(SessionManager.isDefaultDesktop())
        	SessionManager.setSessionApplication(this);
        else
        {
        	SessionManager.setSessionApplication(this, "SessionApplication" + displaytype + desktop_id);
        	//if(SessionManager.getSessionApplication()==null)
            SessionManager.setSessionApplication(this);

        }
        
        Session session = Executions.getCurrent().getDesktop().getSession();
        if (session.getAttribute(SessionContextListener.SESSION_CTX) == null || !SessionManager.isUserLoggedIn(ctx))
        {
        	//BEGIN AJC 8 AGO 2011
			if(username!=null && username.length()>0 && !(SessionManager.isDefaultDesktop()))
			{
				validateLogin();
			}
			else
			{
				loginDesktop = new WLogin(this);
				loginDesktop.createPart(this.getPage());
			}
			//END AJC 8 AGO 2011

        }
        else
        {
            loginCompleted();
        }
    }
    
    
	//BEGIN AJC 8 AGO 2011
	public void validateLogin()
	{
		
		String rolename = DB.getSQLValueString(null, "Select name from AD_Role where AD_Role_ID=?", role_id);
		String clientname = DB.getSQLValueString(null, "Select name from AD_Client where AD_Client_ID=?", client_id);
		String orgname = DB.getSQLValueString(null, "Select name from AD_Org where AD_Org_ID=?", org_id);
		
		if (org.compiere.Adempiere.getVersion().length() != 0 && !(SessionManager.isDefaultDesktop())) {
			org.compiere.Adempiere.startup(true);
			org.compiere.util.Ini.setProperty(org.compiere.util.Ini.P_UID,      username);
			org.compiere.util.Ini.setProperty(org.compiere.util.Ini.P_PWD,      password);
			org.compiere.util.Ini.setProperty(org.compiere.util.Ini.P_ROLE,     rolename);
			org.compiere.util.Ini.setProperty(org.compiere.util.Ini.P_CLIENT,   clientname);
			org.compiere.util.Ini.setProperty(org.compiere.util.Ini.P_ORG,		orgname);
			//org.compiere.util.Ini.setProperty(org.compiere.util.Ini.P_WAREHOUSE, pWarehouse);
			//org.compiere.util.Ini.setProperty(org.compiere.util.Ini.P_LANGUAGE, "es_MX");
			org.compiere.util.Ini.setProperty(org.compiere.util.Ini.P_LANGUAGE, pLanguaje);
			org.compiere.util.Login login = new org.compiere.util.Login(Env.getCtx());
			
			if (login.batchLogin()) {
				Session currSess = Executions.getCurrent().getDesktop().getSession();
				currSess.setAttribute("Check_AD_User_ID", Env.getAD_User_ID(Env.getCtx()));
				loginCompleted();
			}
			else
			{
				throw new WrongValueException("User Id or Password invalid!!!");
			}
		}
	}
	//END AJC 8 AGO 2011
    
    //Begin AJC 26 dic 2013
	
	SimpleDesktopRender windowArea;
	
	public void setRenderPart(SimpleDesktopRender windowArea)
	{
		this.windowArea = windowArea;
	}
	
	public SimpleDesktopRender getRenderPart()
	{
		return windowArea;
	}
	
	
	//end AJC 26 dic 2013
	
	
    public void onOk()
    {
    }

    public void onCancel()
    {
    }

    /* (non-Javadoc)
	 * @see org.adempiere.webui.IWebClient#loginCompleted()
	 */
    public void loginCompleted()
    {
    	if (loginDesktop != null)
    	{
    		loginDesktop.detach();
    		loginDesktop = null;
    	}

        Properties ctx = Env.getCtx();
        String langLogin = Env.getContext(ctx, Env.LANGUAGE);
        if (langLogin == null || langLogin.length() <= 0)
        {
        	langLogin = langSession;
        	Env.setContext(ctx, Env.LANGUAGE, langSession);
        }

        // Validate language
		Language language = Language.getLanguage(langLogin);
		String locale = Env.getContext(ctx, AEnv.LOCALE);
		if (locale != null && locale.length() > 0 && !language.getLocale().toString().equals(locale))
		{
			String adLanguage = language.getAD_Language();
			Language tmp = Language.getLanguage(locale);
			language = new Language(tmp.getName(), adLanguage, tmp.getLocale(), tmp.isDecimalPoint(),
	    			tmp.getDateFormat().toPattern(), tmp.getMediaSize());
		}
		else
		{
			Language tmp = language;
			language = new Language(tmp.getName(), tmp.getAD_Language(), tmp.getLocale(), tmp.isDecimalPoint(),
	    			tmp.getDateFormat().toPattern(), tmp.getMediaSize());
		}
    	Env.verifyLanguage(ctx, language);
    	Env.setContext(ctx, Env.LANGUAGE, language.getAD_Language()); //Bug

		//	Create adempiere Session - user id in ctx
        Session currSess = Executions.getCurrent().getDesktop().getSession();
        HttpSession httpSess = (HttpSession) currSess.getNativeSession();

		MSession mSession = MSession.get (ctx, currSess.getRemoteAddr(),
			currSess.getRemoteHost(), httpSess.getId() );

		//enable full interface, relook into this when doing preference
		Env.setContext(ctx, "#ShowTrl", true);
		Env.setContext(ctx, "#ShowAcct", MRole.getDefault().isShowAcct());
		Env.setContext(ctx, "#ShowAdvanced", true);

		// to reload preferences when the user refresh the browser
		userPreference = loadUserPreference(Env.getAD_User_ID(ctx));
		
		//auto commit user preference
		String autoCommit = userPreference.getProperty(UserPreference.P_AUTO_COMMIT);
		Env.setAutoCommit(ctx, "true".equalsIgnoreCase(autoCommit) || "y".equalsIgnoreCase(autoCommit));

		//auto new user preference
		String autoNew = userPreference.getProperty(UserPreference.P_AUTO_NEW);
		Env.setAutoNew(ctx, "true".equalsIgnoreCase(autoNew) || "y".equalsIgnoreCase(autoNew));
		
		
		IDesktop d = null;
		d = (IDesktop) currSess.getAttribute("application.desktop");

		
		//BEGIN AJC 9 AGO 2011
		if(d instanceof SimpleDesktop)
		{
			d=null;
			appDesktop=null;
		}
		//END AJC 9 AGO 2011

		if (d != null && d instanceof IDesktop)
		{
			ExecutionCarryOver eco = (ExecutionCarryOver) currSess.getAttribute(EXECUTION_CARRYOVER_SESSION_KEY);
			if (eco != null) {
				//try restore
				try {
					appDesktop = (IDesktop) d;

					ExecutionCarryOver current = new ExecutionCarryOver(this.getPage().getDesktop());
					ExecutionCtrl ctrl = ExecutionsCtrl.getCurrentCtrl();
					Visualizer vi = ctrl.getVisualizer();
					eco.carryOver();
					Collection<Component> rootComponents = new ArrayList<Component>();
					try {
						ctrl = ExecutionsCtrl.getCurrentCtrl();
						((DesktopCtrl)Executions.getCurrent().getDesktop()).setVisualizer(vi);

						//detach root component from old page
						Page page = appDesktop.getComponent().getPage();
						Collection<?> collection = page.getRoots();
						Object[] objects = new Object[0];
						objects = collection.toArray(objects);
						for(Object obj : objects) {
							if (obj instanceof Component) {
								((Component)obj).detach();
								rootComponents.add((Component) obj);
							}
						}
						appDesktop.getComponent().detach();
						DesktopCache desktopCache = ((SessionCtrl)currSess).getDesktopCache();
						if (desktopCache != null)
							desktopCache.removeDesktop(Executions.getCurrent().getDesktop());
					} catch (Exception e) {
						appDesktop = null;
					} finally {
						eco.cleanup();
						current.carryOver();
					}

					if (appDesktop != null) {
						//re-attach root components
						for (Component component : rootComponents) {
							try {
								component.setPage(this.getPage());
							} catch (UiException e) {
								// e.printStackTrace();
								// an exception is thrown here when refreshing the page, it seems is harmless to catch and ignore it
								// i.e.: org.zkoss.zk.ui.UiException: Not unique in the ID space of [Page z_kg_0]: zk_comp_2
							}
						}
						appDesktop.setPage(this.getPage());
						currSess.setAttribute(EXECUTION_CARRYOVER_SESSION_KEY, current);
					}
					
					currSess.setAttribute(ZK_DESKTOP_SESSION_KEY, this.getPage().getDesktop());
				} catch (Throwable t) {
					//restore fail
					appDesktop = null;
				}

			}
		}

		
			//create new desktop
			if(SessionManager.isDefaultDesktop() )
			{
				if (appDesktop == null)
				{
					createDesktop();
					appDesktop.setClientInfo(clientInfo);
					appDesktop.createPart(this.getPage());
					currSess.setAttribute("application.desktop", appDesktop);
					ExecutionCarryOver eco = new ExecutionCarryOver(this.getPage().getDesktop());
					currSess.setAttribute(EXECUTION_CARRYOVER_SESSION_KEY, eco);
					currSess.setAttribute(ZK_DESKTOP_SESSION_KEY, this.getPage().getDesktop());
				}
			}
			else
			{
				if(SessionManager.getUniqueDesktop()==null)
				{
					createDesktop();
					appDesktop.setClientInfo(clientInfo);
					SessionManager.setUniqueDesktop(appDesktop);
					// TODO: Missing set current UI as Draw Canvas
					ExecutionCarryOver eco = new ExecutionCarryOver(this.getPage().getDesktop());
					currSess.setAttribute(EXECUTION_CARRYOVER_SESSION_KEY, eco);
					currSess.setAttribute(ZK_DESKTOP_SESSION_KEY, this.getPage().getDesktop());
				}
				else
				{
					appDesktop = SessionManager.getUniqueDesktop();
				}
					
			}
			
			
		
		//BEGIN AJC 12 DIC 2013
		if(appDesktop instanceof SimpleDesktop && !(SessionManager.isDefaultDesktop()))
		{			
			try{
				appDesktop.closeActiveWindow();
			}
			catch(Exception ex)
			{
				//No hay ninguna ventana abierta
			}
			
			if("W".equals(displaytype) && record_id == 0) // Open Window
			{
				ADWindow window = appDesktop.openWindow(desktop_id);
				//window.getADWindowPanel().onNew();
				CWindowToolbar toolbar = window.getADWindowPanel().getToolbar();
				toolbar.setVisible(false);
			}
			else if("W".equals(displaytype) && record_id > 0)
			{
				MWindow window = new MWindow(Env.getCtx(), desktop_id, null);

				
				if(window!=null)
				{
					int AD_Table_ID=0;
					MTable table = null;
					
					
					for(MTab tab : window.getTabs(true, null))
					{
						AD_Table_ID = tab.getAD_Table_ID();
						table = (MTable)tab.getAD_Table();
				
						break;
					}
					if(AD_Table_ID!=0)
					{
						MQuery query = new MQuery(AD_Table_ID);
						query.addRestriction(table.getTableName() + "_ID", MQuery.EQUAL, record_id);
						query.setRecordCount(1);
						AEnv.zoom(desktop_id, query);
					}
				}
			}
			else if("P".equals(displaytype))
			{
				appDesktop.openProcessDialog(desktop_id, true);
			}
			
			
			
			
		}
		
		
		if ("Y".equalsIgnoreCase(Env.getContext(ctx, BrowserToken.REMEMBER_ME)) && MSystem.isZKRememberUserAllowed())
		{
			MUser user = MUser.get(ctx);
			BrowserToken.save(mSession, user);
		}
		else
		{
			BrowserToken.remove();
		}
    }

    private void createDesktop()
    {
    	appDesktop = null;
		String className = "";

    	if(!(SessionManager.isDefaultDesktop()))
			className=desktopClass;
		else
			className= MSysConfig.getValue(IDesktop.CLASS_NAME_KEY);
    	
    	
    	if ( className != null && className.trim().length() > 0)
		{
			try
			{
				Class<?> clazz = this.getClass().getClassLoader().loadClass(className);
				appDesktop = (IDesktop) clazz.newInstance();
			}
			catch (Throwable t)
			{
				logger.warning("Failed to instantiate desktop. Class=" + className);
			}
		}
		//fallback to default
		if (appDesktop == null)
			appDesktop = new DefaultDesktop();
	}

	/* (non-Javadoc)
	 * @see org.adempiere.webui.IWebClient#logout()
	 */
    public void logout()
    {
    	appDesktop.logout();
    	Executions.getCurrent().getDesktop().getSession().getAttributes().clear();

    	MSession mSession = MSession.get(Env.getCtx(), false);
    	if (mSession != null) {
    		mSession.logout();
    	}

        SessionManager.clearSession();
        super.getChildren().clear();
        Page page = this.getPage();
        page.removeComponents();
        Executions.sendRedirect("index.zul");
    }

    /**
     * @return IDesktop
     */
    public IDesktop getAppDeskop()
    {
    	if(SessionManager.isDefaultDesktop())
    		return appDesktop;
    	else
    		return SessionManager.getUniqueDesktop();
    }

	public void onEvent(Event event) {
		if (event instanceof ClientInfoEvent) {
			ClientInfoEvent c = (ClientInfoEvent)event;
			clientInfo = new ClientInfo();
			clientInfo.colorDepth = c.getColorDepth();
			clientInfo.desktopHeight = c.getDesktopHeight();
			clientInfo.desktopWidth = c.getDesktopWidth();
			clientInfo.desktopXOffset = c.getDesktopXOffset();
			clientInfo.desktopYOffset = c.getDesktopYOffset();
			clientInfo.timeZone = c.getTimeZone();
			if (appDesktop != null)
				appDesktop.setClientInfo(clientInfo);
		}

	}

	/**
	 * @param userId
	 * @return UserPreference
	 */
	public UserPreference loadUserPreference(int userId) {
		userPreference.loadPreference(userId);
		return userPreference;
	}

	/**
	 * @return UserPrerence
	 */
	public UserPreference getUserPreference() {
		return userPreference;
	}
	
	//global command
	static {
		new ZoomCommand("onZoom", Command.IGNORE_OLD_EQUIV);
		new DrillCommand("onDrillAcross", Command.IGNORE_OLD_EQUIV);
		new DrillCommand("onDrillDown", Command.IGNORE_OLD_EQUIV);
		new TokenCommand(TokenEvent.ON_USER_TOKEN, Command.IGNORE_OLD_EQUIV);
	}

	@Override
	public boolean isDefaultDesktop() {
		return SessionManager.isDefaultDesktop();
	}
}
