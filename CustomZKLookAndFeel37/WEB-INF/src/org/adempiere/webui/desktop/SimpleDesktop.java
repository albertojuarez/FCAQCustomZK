
package org.adempiere.webui.desktop;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.adempiere.webui.AdempiereWebUI;
import org.adempiere.webui.IWebClient;
import org.adempiere.webui.apps.ProcessDialog;
import org.adempiere.webui.component.DesktopTabpanel;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Tabpanel;
import org.adempiere.webui.component.ToolBarButton;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.event.MenuListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.part.AbstractUIPart;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.util.IServerPushCallback;
import org.adempiere.webui.util.ServerPushTemplate;
import org.adempiere.webui.window.ADWindow;
import org.compiere.model.MQuery;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.WebDoc;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;

/**
 *
 * Default desktop implementation.
 * @author <a href="mailto:alberto.juarez@e-evolution.com">Alberto Juarez</a>
 * @date Ago 9, 2011
 * @version $Revision: 0.10 $
 */
public class SimpleDesktop extends AbstractDesktop implements MenuListener, Serializable, EventListener, IServerPushCallback
{
	/**
	 * generated serial version ID 
	 */
	private static final long serialVersionUID = -8203958978173990301L;

	private static final CLogger logger = CLogger.getCLogger(SimpleDesktop.class);



	private Thread dashboardThread;

	public SimpleDesktop()
	{
		super();
		createContainer();
	}

	protected void createContainer()
	{

		if(getUIContainer().getRenderPart()!=null )
			return;

		SimpleDesktopRender renderPart = new SimpleDesktopRender();

		renderPart.createPart(getUIContainer().getPage());

		getUIContainer().setRenderPart(renderPart);


		return;
	}



	public void onEvent(Event event)
	{
		Component comp = event.getTarget();
		String eventName = event.getName();

		if(eventName.equals(Events.ON_CLICK))
		{
			if(comp instanceof ToolBarButton)
			{
				ToolBarButton btn = (ToolBarButton) comp;

				int menuId = 0;
				try
				{
					menuId = Integer.valueOf(btn.getName());
				}
				catch (Exception e) {

				}

				if(menuId > 0) onMenuSelected(menuId);
			}
		}
	}

	public void onServerPush(ServerPushTemplate template)
	{
		template.execute(this);
	}


	/*
	/**
	 *
	 * @param page

	public void setPage(Page page) {

		Borderlayout layout = getUIContainer().getLayout();

		if (this.page != page) {
			layout.setPage(page);
			this.page = page;
		}
	}

	/**
	 * Get the root component
	 * @return Component

	public Component getComponent() {
		return getUIContainer().getLayout();
	}*/

	public void logout() {
		if (dashboardThread != null && dashboardThread.isAlive()) {
			dashboardThread.interrupt();
		}
	}

	public void updateUI() {

	}

	protected void preOpenNewTab() 
	{

	}

	@Override
	public boolean closeActiveWindow() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean closeWindow(int windowNo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void showURL(String url, boolean closeable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showURL(WebDoc doc, String string, boolean closeable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showZoomWindow(int window_ID, MQuery query) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showWindow(int window_ID, MQuery query) {
		// TODO Auto-generated method stub

	}

	@Override
	public ProcessDialog openProcessDialog(int processId, boolean soTrx) {
		createContainer();

		ProcessDialog pd = new ProcessDialog (processId, soTrx);
		if (pd.isValid()) {
			DesktopTabpanel tabPanel = new DesktopTabpanel();
			pd.setParent(tabPanel);
			String title = pd.getTitle();
			pd.setTitle(null);
			preOpenNewTab();
			getUIContainer().getRenderPart().addWindow(tabPanel, title, true);
		}
		return pd;
	}

	@Override
	public ADForm openForm(int formId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void openBrowse(int AD_Browse_ID) {
		// TODO Auto-generated method stub

	}

	@Override
	public ADWindow openWindow(int windowId) {

		createContainer();

		ADWindow adWindow = new ADWindow(Env.getCtx(), windowId);

		DesktopTabpanel panel = new DesktopTabpanel();

		if (adWindow.createPart(panel) != null) {
			preOpenNewTab();
			getUIContainer().getRenderPart().addWindow(panel, adWindow.getTitle(), true); 
			return adWindow;
		} else {
			//user cancel
			return null;
		}
	}

	@Override
	public ADWindow openWindow(int windowId, MQuery query) {

		createContainer(); 

		ADWindow adWindow = new ADWindow(Env.getCtx(), windowId, query);

		DesktopTabpanel panel = new DesktopTabpanel();

		if (adWindow.createPart(panel) != null) {
			preOpenNewTab();
			getUIContainer().getRenderPart().addWindow(panel, adWindow.getTitle(), true); 
			return adWindow;
		} else {
			//user cancel
			return null;
		}
	}

	@Override
	public void openTask(int task_ID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void openWorkflow(int workflow_ID) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void showEmbedded(Window window) {


		Tabpanel tabPanel = new Tabpanel();
		window.setParent(tabPanel);
		String title = window.getTitle();
		window.setTitle(null);
		preOpenNewTab();


		Map attributes = SessionManager.getSession().getAttributes();

		Set set = attributes.entrySet();
		Iterator parameters = set.iterator();

		boolean added=false;
		
		while (parameters.hasNext() && !added)
		{
			Map.Entry m =(Map.Entry)parameters.next();
			String name = (String)m.getKey();

			try{
				if(name.contains("SessionApplication"))
				{

					AdempiereWebUI app = null; 
					app = (AdempiereWebUI)SessionManager.getSession().getAttribute(name);
					final Desktop dt = app.getDesktop();
					if (dt != null && dt != Executions.getCurrent().getDesktop())
					{
						System.out.println("Not valid Desktop " + dt.toString() + ", " + Executions.getCurrent().getDesktop());
					}
					else
					{
						app.getRenderPart().addWindow(tabPanel, title, true);
						added=true;
					}

				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public AdempiereWebUI getUIContainer() {
		return (AdempiereWebUI)SessionManager.getSessionApplication();
	}

	@Override
	protected Component doCreatePart(Component parent) {
		return null;
	}

	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPage(Page page) {
		// TODO Auto-generated method stub

	}	
}
