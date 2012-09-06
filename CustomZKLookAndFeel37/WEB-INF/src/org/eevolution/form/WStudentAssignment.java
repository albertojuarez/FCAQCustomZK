package org.eevolution.form;

import java.sql.Timestamp;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.editor.WTableDirEditor;
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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;

public class WStudentAssignment extends StudentAssignment 
	implements IFormController, EventListener, WTableModelListener, ValueChangeListener
{
	
	// Layout components
	private CustomForm form = new CustomForm();
	private Borderlayout mainLayout = new Borderlayout();
	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();
	private Panel southPanel = new Panel();
	private StatusBarPanel statusBar = new StatusBarPanel();
	private WListbox studentTable = ListboxFactory.newDataTable();
	private Button bAssign = new Button();
	private Button bFindStudents = new Button();
	
	// Form Components
	
	private Label lCourseDef = null;
	private WTableDirEditor fCourseDef = null;


	public WStudentAssignment()
	{
		try
		{
			dynInit();
			zkInit();
			loadStudentTable();
			southPanel.appendChild(new Separator());
			southPanel.appendChild(statusBar);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "", e);
		}
	}
	


	private void loadStudentTable() {
		Vector<Vector<Object>> data = getStudentData(fCourseDef.getValue());
		Vector<String> columnNames = getColumnNames();
		
		studentTable.clear();
		studentTable.getModel().removeTableModelListener(this);
		
		ListModelTable modelP = new ListModelTable(data);
		modelP.addTableModelListener(this);
		studentTable.setData(modelP, columnNames);

		studentTable.setColumnClass(0, Boolean.class, false);			//  0-Selection
		studentTable.setColumnClass(1, String.class, true);         	//  1-Name
		studentTable.setColumnClass(2, Timestamp.class, true);        //  2-Birthday
		studentTable.setColumnClass(3, String.class, true);			//  3-Group
			
	}


	// Init Components
	private void zkInit() {
		
		
		form.appendChild(mainLayout);
		mainLayout.setWidth("99%");
		mainLayout.setHeight("100%");
		
		parameterPanel.appendChild(parameterLayout);

		lCourseDef = new Label();
		lCourseDef.setText(Msg.getMsg(Env.getCtx(), "Group"));
		
		bFindStudents.setLabel(Msg.getMsg(Env.getCtx(), "FindStudents"));
		
		bAssign.setLabel(Msg.getMsg(Env.getCtx(), "Assign"));
		
		
		
		North north = new North();
		north.setStyle("border: none");
		mainLayout.appendChild(north);
		north.appendChild(parameterPanel);
		Rows rows = null;
		Row row = null;
		parameterLayout.setWidth("800px");
		rows = parameterLayout.newRows();
		
		row = rows.newRow();
		row.appendChild(lCourseDef);
		row.appendChild(fCourseDef.getComponent());
		row.appendChild(new Space());
		row.appendChild(bFindStudents);
		
		Center center = new Center();
		center.setFlex(true);
		mainLayout.appendChild(center);
		
		center.appendChild(studentTable);
		studentTable.setWidth("99%");
		studentTable.setHeight("99%");
		center.setStyle("border: none");
		
		
		South south = new South();
		south.setStyle("border: none");
		Panel southPanel = new Panel();
		Grid southLayout= GridFactory.newGridLayout();
		southPanel.appendChild(southLayout);
		south.appendChild(southPanel);
		rows = new Rows();
		southLayout.appendChild(rows);
		row = rows.newRow();
		row.appendChild(bAssign);
		mainLayout.appendChild(south);
		
	}

	// Init Search Editors
	private void dynInit() {
		
			int AD_Column_ID = 1000734;        //  C_CourseDef.C_CourseDef_ID
			MLookup lookupBP = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
			fCourseDef = new WTableDirEditor("C_BPartner_ID", true, false, true, lookupBP);
			fCourseDef.addValueChangeListener(this);
	}




	@Override
	public void valueChange(ValueChangeEvent evt) {

	}

	@Override
	public void tableChanged(WTableModelEvent event) {

	}

	@Override
	public void onEvent(Event event) throws Exception {

	}

	@Override
	public ADForm getForm() {
		return form;
	}

}
