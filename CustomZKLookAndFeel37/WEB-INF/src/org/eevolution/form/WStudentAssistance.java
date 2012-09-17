package org.eevolution.form;

import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.panel.StatusBarPanel;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.MRefList;
import org.compiere.util.DB;
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
	
	private WTableDirEditor fSubject = null;
	private WDateEditor fDate = null;
	private WTableDirEditor fCourse = null;
	
	
	public WStudentAssistance()
	{
		try
		{
			loadStartData();
			
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
			lSubject.setText(Msg.getMsg(Env.getCtx(), "SubjectMatter"));

			lDate = new Label();
			lDate.setText(Msg.getMsg(Env.getCtx(), "Date"));
			fDate = new WDateEditor();
			fDate.setValue(new Timestamp(System.currentTimeMillis()));
			fDate.setReadWrite(false);
			
			
			lGroup = new Label();
			lGroup.setText(Msg.getMsg(Env.getCtx(), "Group"));
			
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
			
			fSubject = new WTableDirEditor("CA_SubjectMatter_ID", true, false, true, getSubjectMatter(form.getWindowNo()));
			fSubject.addValueChangeListener(this);
				
			fCourse = new WTableDirEditor("CA_CourseDef_ID", true, false, true, getCourseDef(form.getWindowNo()));
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
			
			studentTable.setColumnClass(0, String.class, true);			//  0-Name
			studentTable.setColumnClass(1, Checkbox.class, false);		//  1-Assistance
			studentTable.setColumnClass(2, Checkbox.class, false);		//  2-Absence
			studentTable.setColumnClass(3, Checkbox.class, false); 		//  3-Dealy
			studentTable.setColumnClass(4, Combobox.class, false);		//  4-Motive
			studentTable.setColumnClass(5, Combobox.class, false);		//  5-Comment
			studentTable.setColumnClass(6, String.class, false);		// 6-Observations
			
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
	
	@Override
	public void showErrorMessage(String message){
		FDialog.error(form.getWindowNo(), Msg.translate(Env.getCtx(), message));
	}
	
	@Override
	public void dispose(){
		form.dispose();
	}

	@Override
	public Object getComments() {
		Combobox commentBox = new Combobox();
		commentBox.setSclass("ipadcombobox");
		
		String name = null;
		for (MRefList comment : comments) {
			name = DB.getSQLValueString(
					null,
					"SELECT NAME FROM AD_Ref_List_Trl WHERE AD_Ref_List_ID = "
							+ comment.get_ID() + " AND AD_Language = '"
							+ Env.getContext(Env.getCtx(), "#AD_Language")
							+ "'");

			if (name == "" || name == null)
				commentBox.appendItem(comment.getName(), comment.getValue());
			else
				commentBox.appendItem(name, comment.getValue());
		}
		return commentBox;
	}
	
	@Override
	public Object getMotive() {
		Combobox motiveBox = new Combobox();
		motiveBox.setSclass("ipadcombobox");
		motiveBox.appendItem(Msg.translate(Env.getCtx(), "Justified"), "J");
		motiveBox.appendItem(Msg.translate(Env.getCtx(), "Unjustified"), "I");
		return motiveBox;
	}
	
}
