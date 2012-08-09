package org.eevolution.form;

import java.sql.Timestamp;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.editor.WDateEditor;
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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Separator;

public class WStudentAssistance extends StudentAssistance
implements IFormController, EventListener, WTableModelListener, ValueChangeListener{

	private CustomForm form = new CustomForm();
	private Borderlayout mainLayout = new Borderlayout();
	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();
	private Panel southPanel = new Panel();
	private StatusBarPanel statusBar = new StatusBarPanel();
	private WListbox studentTable = ListboxFactory.newDataTable();
	private Button bSendAssistance = new Button();
	
	
	private Label lSubject = null;
	private Label lDate = null;
	private Label lGroup = null;
	
	private WSearchEditor fSubject = null;
	private WDateEditor fDate = null;
	private WSearchEditor fCourse = null;
	
	public WStudentAssistance()
	{
		try
		{
			dynInit();
			zkInit();
			loadCandidateTable();
			southPanel.appendChild(new Separator());
			southPanel.appendChild(statusBar);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "", e);
		}
	}
	
	// Init Components
		private void zkInit() {
			
			
			form.appendChild(mainLayout);
			mainLayout.setWidth("99%");
			mainLayout.setHeight("100%");
			
			parameterPanel.appendChild(parameterLayout);

			
			
			lSubject = new Label();
			lSubject.setText(Msg.getMsg(Env.getCtx(), "Subject"));

			lDate = new Label();
			lDate.setText(Msg.getMsg(Env.getCtx(), "Date"));
			fDate = new WDateEditor();
			
			
			lGroup = new Label();
			lGroup.setText(Msg.getMsg(Env.getCtx(), "AcademicGroup"));
			
			bSendAssistance.setLabel(Msg.getMsg(Env.getCtx(), "SendAssistance"));
			
			
			
			
			North north = new North();
			north.setStyle("border: none");
			mainLayout.appendChild(north);
			north.appendChild(parameterPanel);
			Rows rows = null;
			Row row = null;
			parameterLayout.setWidth("800px");
			rows = parameterLayout.newRows();
			row = rows.newRow();
			
			row.appendChild(lGroup);
			row.appendChild(fCourse.getComponent());
			
			row.appendChild(lSubject);
			row.appendChild(fSubject.getComponent());
			
			row = rows.newRow();
			
			row.appendChild(lDate);
			row.appendChild(fDate.getComponent());
			
			row = rows.newRow();
			
			
			
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
			row.appendChild(bSendAssistance);
			mainLayout.appendChild(south);
			
		}
	
		// Init Search Editors
		private void dynInit() {
			
				int AD_Column_ID = 2893;        //  C_BPartner.C_BPartner_ID
				MLookup subjectLk = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, AD_Column_ID, DisplayType.Search);
				fSubject = new WSearchEditor("CA_SubjectMatter_ID", true, false, true, subjectLk);
				fSubject.addValueChangeListener(this);
				
				AD_Column_ID = 2893;        //  C_BPartner.C_BPartner_ID
				MLookup groupLk = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, AD_Column_ID, DisplayType.Search);
				fCourse= new WSearchEditor("CA_CourseDef_ID", true, false, true, subjectLk);
				fCourse.addValueChangeListener(this);

		}
		
		
		private void loadCandidateTable() {
			Vector<Vector<Object>> data = getAssistanceData(fCourse.getValue(), fSubject.getValue(), fDate.getValue());
			Vector<String> columnNames = getColumnNames();
			
			studentTable.clear();
			studentTable.getModel().removeTableModelListener(this);
			
			ListModelTable modelP = new ListModelTable(data);
			modelP.addTableModelListener(this);
			studentTable.setData(modelP, columnNames);
			
			studentTable.setColumnClass(0, String.class, true);         //  0-Name
			studentTable.setColumnClass(1, Combobox.class, false);        //  1-Birthday
			studentTable.setColumnClass(2, String.class, false);          // Group
					
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
		
	}

	@Override
	public ADForm getForm() {
		return form;
	}
	
	
	
}
