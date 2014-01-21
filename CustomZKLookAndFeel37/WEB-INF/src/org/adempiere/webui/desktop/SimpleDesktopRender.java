package org.adempiere.webui.desktop;

import org.adempiere.webui.component.Tab;
import org.adempiere.webui.component.Tabbox;
import org.adempiere.webui.component.Tabpanel;
import org.adempiere.webui.component.Tabpanels;
import org.adempiere.webui.component.Tabs;
import org.adempiere.webui.part.AbstractUIPart;
import org.adempiere.webui.part.ITabOnSelectHandler;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;

public class SimpleDesktopRender extends AbstractUIPart {




	public SimpleDesktopRender() {
		super();
	}

	@Override
	public Component getComponent() {
		return tabbox;
	}
	
	private static final int MAX_TITLE_LENGTH = 30;

    private Tabbox           tabbox;
    
    private Center windowArea;

	private Borderlayout layout;
	
	


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
        
        
		
		tabbox = new Tabbox();

		Tabpanels tabpanels = new Tabpanels();
		Tabs tabs = new Tabs();

		tabbox.appendChild(tabs);
		tabbox.appendChild(tabpanels);
		tabbox.setWidth("100%");
		tabbox.setHeight("100%");

		tabbox.setParent(windowArea);

		return layout;
	}

	   /**
     * 
     * @param comp
     * @param title
     * @param closeable
     */
    public void addWindow(Component comp, String title, boolean closeable)
    {
        addWindow(comp, title, closeable, true);
    }
    
    /**
     * 
     * @param comp
     * @param title
     * @param closeable
     * @param enable
     */
    public void addWindow(Component comp, String title, boolean closeable, boolean enable) 
    {
    	insertBefore(null, comp, title, closeable, enable);
    }
    
    /**
     * 
     * @param refTab
     * @param comp
     * @param title
     * @param closeable
     * @param enable
     */
    public void insertBefore(Tab refTab, Component comp, String title, boolean closeable, boolean enable)
    {
    	
        Tab tab = new Tab();
        title = title.replaceAll("[&]", "");
        if (title.length() <= MAX_TITLE_LENGTH) 
        {
        	tab.setLabel(title);
        }
        else
        {
        	tab.setTooltiptext(title);
        	title = title.substring(0, 27) + "...";
        	tab.setLabel(title);
        }
        tab.setClosable(closeable);
        
        // fix scroll position lost coming back into a grid view tab
        tab.addEventListener(Events.ON_SELECT, new EventListener() {
			public void onEvent(Event event) throws Exception {
				Tab tab = (Tab)event.getTarget();
				org.zkoss.zul.Tabpanel panel = tab.getLinkedPanel();
				Component component = panel.getFirstChild();
				if (component != null && component.getAttribute(ITabOnSelectHandler.ATTRIBUTE_KEY) instanceof ITabOnSelectHandler)
				{
					ITabOnSelectHandler handler = (ITabOnSelectHandler) component.getAttribute(ITabOnSelectHandler.ATTRIBUTE_KEY);
					handler.onSelect();
				}
			}
		});

        Tabpanel tabpanel = null;
        if (comp instanceof Tabpanel) {
        	tabpanel = (Tabpanel) comp;
        } else {
        	tabpanel = new Tabpanel();
        	tabpanel.appendChild(comp);
        }                
        tabpanel.setHeight("100%");
        tabpanel.setWidth("100%");
        tabpanel.setZclass("desktop-tabpanel");
        tabpanel.setStyle("position: absolute;");
        
        if (refTab == null)  
        {
        	tabbox.getTabs().appendChild(tab);
        	tabbox.getTabpanels().appendChild(tabpanel);
        }
        else
        {
        	org.zkoss.zul.Tabpanel refpanel = refTab.getLinkedPanel();
        	tabbox.getTabs().insertBefore(tab, refTab);
        	tabbox.getTabpanels().insertBefore(tabpanel, refpanel);
        }

        if (enable)
        	setSelectedTab(tab);
        
    }
    
    public Tab getSelectedTab() {
    	return (Tab) tabbox.getSelectedTab();
    }
    
    public void setSelectedTab(Tab tab)
    {
    	tabbox.setSelectedTab(tab);
    }

}
