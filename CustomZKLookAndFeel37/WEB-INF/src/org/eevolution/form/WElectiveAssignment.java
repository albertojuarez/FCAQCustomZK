package org.eevolution.form;

import java.util.Vector;

import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListHead;
import org.adempiere.webui.component.ListHeader;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.panel.StatusBarPanel;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zhtml.Span;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vbox;

public class WElectiveAssignment extends ElectiveAssignment
	implements IFormController, EventListener, WTableModelListener, ValueChangeListener
{

	// Layout components
	private CustomForm form = new CustomForm();
	private Borderlayout mainLayout = new Borderlayout();
	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();
	private Panel southPanel = new Panel();
	private StatusBarPanel statusBar = new StatusBarPanel();
	private WListbox electiveTable = ListboxFactory.newDataTable();
	private WListbox assignmentTable = ListboxFactory.newDataTable();

	private Panel mainPanel = new Panel();
	private Grid buttonLayout = GridFactory.newGridLayout();
	private Button bPut = new Button();
	private Button bRemove = new Button();
	private Button bOk = new Button();
	private Button bCancel = new Button();
	private Hbox hbox;
	
	// Form Components
	private Label lStudent = null;
	private WSearchEditor fStudent = null;
	
	
	public WElectiveAssignment()
	{
		try
		{
			dynInit();
			zkInit();
			loadElectiveTable();
			loadAssignmentTable();
			southPanel.appendChild(new Separator());
			southPanel.appendChild(statusBar);
		}
		catch(Exception e)
		{
			//log.log(Level.SEVERE, "", e);
		}
	}
	    
	private void loadAssignmentTable() {
		Vector<Vector<Object>> data = getAssignmentData();
		Vector<String> columnNames = getColumnNamesAssignment();
		
		assignmentTable.clear();
		assignmentTable.getModel().removeTableModelListener(this);
		
		ListModelTable modelP = new ListModelTable(data);
		modelP.addTableModelListener(this);
		assignmentTable.setData(modelP, columnNames);
		
		assignmentTable.setColumnClass(0, String.class, true);			//  0-ElectiveAssignment & SubjectMatter
	}

	private void loadElectiveTable() {
		Vector<Vector<Object>> data = getElectiveData();
		Vector<String> columnNames = getColumnNamesElective();
		
		electiveTable.clear();
		electiveTable.getModel().removeTableModelListener(this);
		
		ListModelTable modelP = new ListModelTable(data);
		modelP.addTableModelListener(this);
		electiveTable.setData(modelP, columnNames);
		
		electiveTable.setColumnClass(0, String.class, true);			//  0-Group/Course & SubjectMatter
	}

	private void zkInit() {
		form.appendChild(mainLayout);
		mainLayout.setWidth("99%");
		mainLayout.setHeight("100%");
		
		parameterPanel.appendChild(parameterLayout);

		lStudent = new Label();
		lStudent.setText(Msg.getMsg(Env.getCtx(), "Student"));		
		bPut.setLabel(Msg.getMsg(Env.getCtx(), ">>"));
		bRemove.setLabel(Msg.getMsg(Env.getCtx(), "<<"));
		bOk.setImage("images/Ok24.png");
    	bOk.setTooltiptext(Msg.getMsg(Env.getCtx(), "Ok"));
    	LayoutUtils.addSclass("action-button", bOk);
    	bCancel.setImage("images/Cancel24.png");
    	bCancel.setTooltiptext(Msg.getMsg(Env.getCtx(), "Cancel"));
    	LayoutUtils.addSclass("action-button", bCancel);
    	
		North north = new North();
		north.setStyle("border: none");
		mainLayout.appendChild(north);
		north.appendChild(parameterPanel);
		Rows rows = null;
		Row row = null;
		parameterLayout.setWidth("800px");
		rows = parameterLayout.newRows();
		
		row = rows.newRow();
		row.appendChild(lStudent);
		row.appendChild(fStudent.getComponent());
		
		
		Center center = new Center();
		mainLayout.appendChild(center);
		
		mainPanel.setWidth("100%");
		mainPanel.setHeight("100%");
		
		center.appendChild(mainPanel);
		
		electiveTable.setWidth("100%");
		electiveTable.setHeight("100%");
		
		
		
		Span span = new Span();
		span.setParent(mainPanel);
		span.setStyle("height: 99%; display: inline-block; width: 40%;");
		span.appendChild(electiveTable);
		
		
		Vbox vbox = new Vbox();

		vbox.setWidth("100%");
		vbox.appendChild(bPut);
		bPut.setWidth("99%");
		vbox.appendChild(bRemove);
		bRemove.setWidth("99%");
		vbox.setAlign("center");
		span = new Span();
		span.setParent(mainPanel);
		span.setStyle("height: 99%; display: inline-block; width: 10%");
		span.appendChild(vbox);
		
		span = new Span();
		span.setParent(mainPanel);
		span.setStyle("height: 99%; display: inline-block; width: 40%;");
		span.appendChild(assignmentTable);
		
	
		
		
		
		South south = new South();
		south.setStyle("border: none");
		
		
        mainLayout.appendChild(south);
		Panel southPanel = new Panel();
        south.appendChild(southPanel);

        Panel pnlBtnRight = new Panel();
        pnlBtnRight.setAlign("right");
        pnlBtnRight.appendChild(bCancel);
        pnlBtnRight.appendChild(bOk);

        Hbox hboxBtnRight = new Hbox();
        hboxBtnRight.appendChild(pnlBtnRight);
        hboxBtnRight.setWidth("100%");
        hboxBtnRight.setStyle("text-align:right");

        southPanel.appendChild(hboxBtnRight);
        southPanel.setWidth("100%");
        
	}

	private void dynInit() {
		int AD_Column_ID = 2893;        //  C_BPartner.C_BPartner_ID
		MLookup lookupBP = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, AD_Column_ID, DisplayType.Search);
		fStudent = new WSearchEditor("C_BPartner_ID", true, false, true, lookupBP);
		fStudent.addValueChangeListener(this);
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tableChanged(WTableModelEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		bPut.addActionListener(this);
		bRemove.addActionListener(this);
		bOk.addActionListener(this);
		bCancel.addActionListener(this);
	}

	@Override
	public ADForm getForm() {
		return form;
	}
	
	
}
