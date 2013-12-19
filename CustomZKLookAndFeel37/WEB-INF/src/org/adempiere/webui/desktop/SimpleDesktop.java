
package org.adempiere.webui.desktop;

import java.io.Serializable;
import java.util.List;

import org.adempiere.webui.component.ToolBarButton;
import org.adempiere.webui.event.MenuListener;
import org.adempiere.webui.util.IServerPushCallback;
import org.adempiere.webui.util.ServerPushTemplate;
import org.compiere.util.CLogger;
import org.zkoss.zk.ui.Component;
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
public class SimpleDesktop extends TabbedDesktop implements MenuListener, Serializable, EventListener, IServerPushCallback
{
	/**
	 * generated serial version ID 
	 */
	private static final long serialVersionUID = -8203958978173990301L;

	private static final CLogger logger = CLogger.getCLogger(SimpleDesktop.class);

    private Center windowArea;

	private Borderlayout layout;

	private Thread dashboardThread;

    public SimpleDesktop()
    {
    	super();
    }

    protected Component doCreatePart(Component parent)
    {
        layout = new Borderlayout();
        if (parent != null)
        {
        	layout.setParent(parent);
        	layout.setWidth("100%");
        	layout.setHeight("100%");
        	layout.setStyle("position: absolute");
        }
        else
        	layout.setPage(page);

        windowArea = new Center();
        windowArea.setParent(layout);
        windowArea.setFlex(true);

        windowContainer.createPart(windowArea);
        
        return layout;
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

	/**
	 *
	 * @param page
	 */
	public void setPage(Page page) {
		if (this.page != page) {
			layout.setPage(page);
			this.page = page;
		}
	}

	/**
	 * Get the root component
	 * @return Component
	 */
	public Component getComponent() {
		return layout;
	}

	public void logout() {
		if (dashboardThread != null && dashboardThread.isAlive()) {
			dashboardThread.interrupt();
		}
	}

	public void updateUI() {
		
	}

	@Override
	protected void preOpenNewTab() 
	{
	}	
}
