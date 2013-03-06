package org.eevolution.form;

import java.sql.Timestamp;
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
import org.adempiere.webui.component.Textbox;
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
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.window.FDialog;
import org.compiere.minigrid.IDColumn;
import org.compiere.model.MRefList;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.fcaq.model.MCAPeriodClass;
import org.fcaq.model.X_CA_CourseDef;
import org.fcaq.model.X_CA_MatterAssignment;
import org.fcaq.model.X_CA_SubjectMatter;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Separator;


public class WStudentAssistance extends StudentAssistance
implements IFormController, EventListener, WTableModelListener, ValueChangeListener{

	private CustomForm form = new CustomForm();
	private Borderlayout mainLayout = new Borderlayout();
	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();
	private Panel southPanel = new Panel();
	private Hbox hboxBtnRight;
	private Panel pnlBtnRight;
	private StatusBarPanel statusBar = new StatusBarPanel();
	private WListbox studentTable = ListboxFactory.newDataTable();
	private Button bSendAssistance = new Button();
	private Button bRefresh = new Button();

	private Label lDay = null;
	private Label lDayNo = null;
	private Label lSubject = null;
	private Label lDate = null;
	private Label lCourse = null;
	private Label lPeriod = null;

	private WTableDirEditor fSubject = null;
	private WDateEditor fDate = null;
	private WTableDirEditor fCourse = null;
	private WTableDirEditor fPeriod = null;

	private boolean setting = false;


	public WStudentAssistance()
	{
		try
		{
			loadStartData();

			dynInit();
			zkInit();
			refreshHeader();
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

		lDay = new Label();
		lDay.setText(Msg.getMsg(m_ctx, "Day"));
		lDayNo = new Label();
		lDayNo.setText("" + dayNo);
		
		lSubject = new Label();
		lSubject.setText(Msg.getMsg(m_ctx, "SubjectMatter"));

		lDate = new Label();
		lDate.setText(Msg.getMsg(m_ctx, "Date"));
		fDate = new WDateEditor();
		fDate.setValue(new Timestamp(System.currentTimeMillis()));
		fDate.setReadWrite(false);
		
		lCourse = new Label();
		lCourse.setText(Msg.getMsg(m_ctx, "Group"));

		lPeriod = new Label();
		lPeriod.setText(Msg.getMsg(m_ctx, "PeriodClass"));
		
		bSendAssistance.setLabel(Msg.getMsg(m_ctx, "SendAssistance"));
		
		bRefresh.setLabel(Msg.getMsg(m_ctx, "Refresh"));


		North north = new North();
		north.setStyle("border: none");
		mainLayout.appendChild(north);
		north.appendChild(parameterPanel);
		Rows rows = null;
		Row row = null;
		parameterLayout.setWidth("99%");
		rows = parameterLayout.newRows();

		row = rows.newRow();
		row.appendChild(lDay);
		row.appendChild(lDayNo);
		
		row = rows.newRow();
		row.appendChild(lDate);
		row.appendChild(fDate.getComponent());
		row.appendChild(lPeriod);
		fPeriod.getComponent().setWidth("90%");
		row.appendChild(fPeriod.getComponent());

		row = rows.newRow();
		row.appendChild(lCourse);
		fCourse.getComponent().setWidth("90%");
		row.appendChild(fCourse.getComponent());
		row.appendChild(lSubject);
		fSubject.getComponent().setWidth("90%");
		row.appendChild(fSubject.getComponent());

		
		Center center = new Center();
		center.setFlex(true);
		mainLayout.appendChild(center);

		center.appendChild(studentTable);
		studentTable.setWidth("99%");
		studentTable.setHeight("99%");
		studentTable.setFixedLayout(false);
		studentTable.setVflex(true);
		center.setStyle("border: none");


		South south = new South();
		south.setStyle("border: none");
		Panel southPanel = new Panel();
		
		south.appendChild(southPanel);
		
		pnlBtnRight = new Panel();
		pnlBtnRight.setAlign("right");
		pnlBtnRight.appendChild(bRefresh);
		pnlBtnRight.appendChild(bSendAssistance);

		hboxBtnRight = new Hbox();
		hboxBtnRight.appendChild(pnlBtnRight);
		hboxBtnRight.setWidth("100%");
		hboxBtnRight.setStyle("text-align:right");
		
		southPanel.appendChild(hboxBtnRight);
		
		mainLayout.appendChild(south);
	}

	// Init Search Editors
	private void dynInit() {

		fPeriod = new WTableDirEditor("CA_PeriodClass_ID", true, false, true, getPeriodClass(form.getWindowNo()));
		fPeriod.addValueChangeListener(this);
		if (getPeriodClass() != null)
			fPeriod.setValue(getPeriodClass().getCA_PeriodClass_ID());
		
		fCourse = new WTableDirEditor("CA_CourseDef_ID", true, false, true, getCourseInPeriod(form.getWindowNo()));
		fCourse.addValueChangeListener(this);
		if (getCurrentCourse(true) != null) {
			if (isDuplicate()) 
				fCourse.setValue(getCurrentMatterAssignment().get(0).getCA_GroupAssignment().getCA_CourseDef_ID());
			else
				fCourse.setValue(getCurrentCourse(false).getCA_CourseDef_ID());
		}

		fSubject = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, getScheduleMatterAssignmentLookup(form.getWindowNo()));
		//fSubject.setReadWrite(false);
		fSubject.addValueChangeListener(this);
		if (getCurrentMatterAssignment().size()>0)
			fSubject.setValue(getCurrentMatterAssignment().get(0).get_ID());
		
		bSendAssistance.addActionListener(this);
		
		bRefresh.addActionListener(this);
	}

	public void ShowSecondaryFields() {
	
		parameterLayout.removeChild(parameterLayout.getRows());
		
		Rows rows = null;
		Row row = null;
		rows = parameterLayout.newRows();
		
		row = rows.newRow();
		row.appendChild(lDay);
		row.appendChild(lDayNo);
		
		row = rows.newRow();
		row.appendChild(lDate);
		row.appendChild(fDate.getComponent());
		row.appendChild(lPeriod);
		
		if (isByPeriod() && isSustitution())
			fPeriod.setReadWrite(false);
		else
			fPeriod.setReadWrite(true);
		
		fPeriod.getComponent().setWidth("90%");
		row.appendChild(fPeriod.getComponent());

		row = rows.newRow();
		row.appendChild(lCourse);
		fCourse.getComponent().setWidth("90%");
		row.appendChild(fCourse.getComponent());
		row.appendChild(lSubject);
		fSubject.getComponent().setWidth("90%");
		row.appendChild(fSubject.getComponent());
	}
	
	public void hideSecondaryFields() {
		
		parameterLayout.removeChild(parameterLayout.getRows());
		
		Rows rows = null;
		Row row = null;
		rows = parameterLayout.newRows();
		
		row = rows.newRow();
		row.appendChild(lDate);
		row.appendChild(fDate.getComponent());

		row = rows.newRow();
		row.appendChild(lCourse);
		fCourse.getComponent().setWidth("90%");
		row.appendChild(fCourse.getComponent());
	}
	
	@Override
	public void valueChange(ValueChangeEvent evt) {
		String name = evt.getPropertyName();
		Object value = evt.getNewValue();

		clean();

		if (value == null)
			return;

		if ("CA_PeriodClass_ID".equals(name)) {
			
			fPeriod.setValue(value);

			int CA_PeriodClass_ID = (Integer) value;

			periodClass = new MCAPeriodClass(m_ctx, CA_PeriodClass_ID, null);
			//currentCourse = currentCourse();
			//currentMatterAssignment = currentMatterAssignment();
			
			if (getCurrentCourse(true) != null) {
				
				fCourse = new WTableDirEditor("CA_CourseDef_ID", true, false, true, getCourseInPeriod(form.getWindowNo()));
				fCourse.addValueChangeListener(this);
				
				if (getCurrentMatterAssignment().size()>0) {
					
					fSubject = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, getScheduleMatterAssignmentLookup(form.getWindowNo()));
					fSubject.addValueChangeListener(this);
				
					fSubject.setValue(getCurrentMatterAssignment().get(0).get_ID());
					
					if (isDuplicate()) 
						fCourse.setValue(getCurrentMatterAssignment().get(0).getCA_GroupAssignment().getCA_CourseDef_ID());
					else
						fCourse.setValue(getCurrentCourse(false).get_ID());
				}
			}
			else {
				
				fCourse = new WTableDirEditor("CA_CourseDef_ID", true, false, true, getCourseInPeriod(form.getWindowNo()));
				fCourse.addValueChangeListener(this);
				
				fSubject.setValue(null);
				fCourse.setValue(null);
			}
			
		} else if ("CA_CourseDef_ID".equals(name)) {
			
			fCourse.setValue(value);
			
			int CA_CourseDef_ID = (Integer) value;
			
			//currentCourse = new X_CA_CourseDef(m_ctx, CA_CourseDef_ID, null);
			
			setCurrentCourse(m_ctx, CA_CourseDef_ID, null);
			//currentMatterAssignment = currentMatterAssignment();
			
			fSubject = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, getScheduleMatterAssignmentLookup(form.getWindowNo()));
			//fSubject.setReadWrite(false);
			fSubject.addValueChangeListener(this);
			
			if (getCurrentMatterAssignment().size()>0 && getCurrentCourse(false) != null) {
				
				fSubject.setValue(getCurrentMatterAssignment().get(0).get_ID());
				fCourse.setValue(getCurrentCourse(false).get_ID());
			}
			else {
				
				fSubject.setValue(null);
				fCourse.setValue(null);
			}
		} else if ("CA_MatterAssignment_ID".equals(name))
		{
			fSubject.setValue(value);
			setCurrentMatterAssignment(m_ctx, (Integer)value, null);				
		}
		
		

		refreshHeader();
	}

	@Override
	public void tableChanged(WTableModelEvent event) {
		//Checkbox & Observations
		if (!setting && event.getLastRow() >= 0 && event.getColumn() >= 0)
			setAssistenceRow(event.getLastRow(), event.getColumn());
	}

	@Override
	public void onEvent(Event event) throws Exception {
		//Combobox
		if(event.getTarget() instanceof Combobox)
		{
			int rowNo = -1;
			int colNo = -1;
			Combobox currentCombo = (Combobox)event.getTarget();

			// find current row
			for(int x=0; x <= studentTable.getRowCount()-1;x++)
			{
				Combobox motiveCombo = (Combobox)studentTable.getValueAt(x,6);
				Combobox commentCombo = (Combobox)studentTable.getValueAt(x,7);

				if(motiveCombo == currentCombo) {
					rowNo = x;
					colNo = 6;
				}
				if(commentCombo == currentCombo) {
					rowNo = x;
					colNo = 7;
				}
			}
			if (!setting)
				setAssistenceRow(rowNo, colNo);
		}
		
		else if(event.getTarget().equals(bSendAssistance))
		{
			boolean sendAssistance = FDialog.ask(form.getWindowNo(), null, Msg.getMsg(Env.getCtx(), "SureSendAssistance"));

			if(sendAssistance)
			{
				if (sendAssistance())
					refreshHeader();
			}
		}
		
		else if(event.getTarget().equals(bRefresh)) {
			
			refreshHeader();
		}
	}

	@Override
	public Object getComments() {
		
		Combobox commentBox = new Combobox();
		commentBox.setStyle("ipadcombobox");

		String name = null;
		for (MRefList comment : getListComments()) {
			name = DB.getSQLValueString(
					null,
					"SELECT NAME FROM AD_Ref_List_Trl WHERE AD_Ref_List_ID = "
							+ comment.get_ID() + " AND AD_Language = '"
							+ Env.getContext(m_ctx, "#AD_Language")
							+ "'");

			if (name == "" || name == null)
				commentBox.appendItem(comment.getName(), comment.getValue());
			else
				commentBox.appendItem(name, comment.getValue());
		}
		commentBox.addEventListener("onChange", this);
		return commentBox;
	}

	@Override
	public Object getMotive() {
		
		Combobox motiveBox = new Combobox();
		motiveBox.setStyle("ipadcombobox");
		motiveBox.appendItem(Msg.translate(m_ctx, "Justified"), "J");
		motiveBox.appendItem(Msg.translate(m_ctx, "Unjustified"), "U");
		motiveBox.addEventListener("onChange", this);
		return motiveBox;
	}


	public void refreshHeader(){

		if (isByPeriod()) {
			
			ShowSecondaryFields();
			
			if(fCourse.getValue() == null || fSubject.getValue() == null) {
				studentTable.clear();
				showErrorMessage("NoOpenPeriod");
				return;
			}
		} else { 
			if(fCourse.getValue() == null) {
				ShowSecondaryFields();
				studentTable.clear();
				showErrorMessage("NoCourse");
				return;
			}
			else {
				if (validateDayWeek(getCurrentCourse(false)))
					hideSecondaryFields();
				else {
					showErrorMessage("Holiday");
					return;
				}
			}
		}
		
		Vector<Vector<Object>> data = getAssistanceData();

		studentTable.clear();
		studentTable.getModel().removeTableModelListener(this);

		ListModelTable modelP = new ListModelTable(data);
		modelP.addTableModelListener(this);
		studentTable.setData(modelP, getColumnNames());

		studentTable.setColumnClass(0, IDColumn.class, true);		//  0-ID
		studentTable.setColumnClass(1, String.class, true);			//  1-Name
		studentTable.setColumnClass(2, String.class, true);			//  2-Course
		studentTable.setColumnClass(3, Checkbox.class, false);		//  3-Assistance
		studentTable.setColumnClass(4, Checkbox.class, false);		//  4-Absence
		studentTable.setColumnClass(5, Checkbox.class, false); 		//  5-Delay
		studentTable.setColumnClass(6, Combobox.class, false);		//  6-Motive
		studentTable.setColumnClass(7, Combobox.class, false);		//  7-Comment
		studentTable.setColumnClass(8, String.class, false);		//  8-Observations
		
		studentTable.addActionListener(this);
		
		bSendAssistance.setDisabled(false);
		
		if (refreshAssistance()) {
			studentTable.setColumnClass(0, IDColumn.class, true);		//  0-ID
			studentTable.setColumnClass(1, String.class, true);			//  1-Name
			studentTable.setColumnClass(2, String.class, true);			//  2-Course
			studentTable.setColumnClass(3, Checkbox.class, true);		//  3-Assistance
			studentTable.setColumnClass(4, Checkbox.class, true);		//  4-Absence
			studentTable.setColumnClass(5, Checkbox.class, true); 		//  5-Delay
			studentTable.setColumnClass(6, Combobox.class, true);		//  6-Motive
			studentTable.setColumnClass(7, Combobox.class, true);		//  7-Comment
			studentTable.setColumnClass(8, String.class, true);			//  8-Observations
			
			bSendAssistance.setDisabled(true);
		}
	}


	public void setAssistanceData(int bPartner_ID, boolean assistance, boolean absence, 
			boolean delay, String motive, String comment, String observation) {
		
		setting = true;
		for (int i=0; i <= studentTable.getRowCount()-1; i++){
			
			int studentTable_ID = ((IDColumn) studentTable.getValueAt(i, 0)).getRecord_ID();
			
			if (studentTable_ID > 0 && studentTable_ID == bPartner_ID){
				
				studentTable.setValueAt(assistance, i, 3);
				studentTable.setValueAt(absence, i, 4);
				studentTable.setValueAt(delay, i, 5);
				studentTable.setValueAt(selectItemMotive(motive), i, 6);
				studentTable.setValueAt(selectItemComment(comment), i, 7);
				
				if (observation == null)
					studentTable.setValueAt("", i, 8);
				else
					studentTable.setValueAt(observation, i, 8);
				
				break;
			}
		}
		setting = false;
	}

	public Combobox selectItemComment(String value){

		Combobox commentBox = (Combobox)getComments();

		for (int index = 0; index <= commentBox.getItemCount()-1; index++) {
			if (value != null && value.equals((String)commentBox.getItemAtIndex(index).getValue())) {
				commentBox.setSelectedItem(commentBox.getItemAtIndex(index));
				break;
			}
		}
		return commentBox;
	}

	public Combobox selectItemMotive(String value) {

		Combobox motiveBox = (Combobox)getMotive();

		for (int index = 0; index <= motiveBox.getItemCount()-1; index++) {
			if (value != null && value.equals((String)motiveBox.getItemAtIndex(index).getValue())) {
				motiveBox.setSelectedItem(motiveBox.getItemAtIndex(index));
				break;
			}
		}
		return motiveBox;
	}

	private void clean()
	{

	}

	public void setAssistenceRow(int row, int column){
		
		setting = true;

		int studentTable_ID = ((IDColumn) studentTable.getValueAt(row, 0)).getRecord_ID();
		boolean assistance = (Boolean) studentTable.getValueAt(row, 3);
		boolean absence = (Boolean) studentTable.getValueAt(row, 4);
		boolean delay = (Boolean) studentTable.getValueAt(row, 5);
		Combobox motiveBox = (Combobox)studentTable.getValueAt(row, 6);
		Combobox commentBox = (Combobox)studentTable.getValueAt(row, 7);
		String observation = (String)studentTable.getValueAt(row, 8);

		if (column == 3 && !assistance) {
			studentTable.setValueAt(true, row, 3);
			setting = false;
			return ;
		} else if (column == 4 && !absence) {
			studentTable.setValueAt(true, row, 4);
			setting = false;
			return ;
		} else if (column == 5 && !delay) {
			studentTable.setValueAt(true, row, 5);
			setting = false;
			return ;
		} else if (column == 3 && assistance) {
			studentTable.setValueAt(false, row, 4);
			studentTable.setValueAt(false, row, 5);
			studentTable.setValueAt((Combobox)getMotive(), row, 6);
			studentTable.setValueAt((Combobox)getComments(), row, 7);
			studentTable.setValueAt("", row, 8);
			absence = false;
			delay = false;
		} else if (column == 4 && absence) {
			studentTable.setValueAt(false, row, 3);
			studentTable.setValueAt(false, row, 5);
			assistance = false;
			delay = false;

		} else if (column == 5 && delay) {
			studentTable.setValueAt(false, row, 3);
			studentTable.setValueAt(false, row, 4);
			assistance = false;
			absence = false;
		} else if (column == 6 && (!absence && !delay)) {
			studentTable.setValueAt(false, row, 3);
			studentTable.setValueAt(false, row, 4);
			studentTable.setValueAt(true, row, 5);
			assistance = false;
			absence = false;
			delay = true;
		} else if (column == 7 && (!absence && !delay)) {
			studentTable.setValueAt(false, row, 3);
			studentTable.setValueAt(false, row, 4);
			studentTable.setValueAt(true, row, 5);
			assistance = false;
			absence = false;
			delay = true;
		} else if (column == 8 && (!absence && !delay)) {
			studentTable.setValueAt(false, row, 3);
			studentTable.setValueAt(false, row, 4);
			studentTable.setValueAt(true, row, 5);
			assistance = false;
			absence = false;
			delay = true;
		} else 
			;

		int indexMotive = motiveBox.getSelectedIndex();
		String motive = "J";
		if (indexMotive >= 0) {
			motive = (String)motiveBox.getSelectedItem().getValue();
		} else {
			if (getCurrentCourse(false) != null)
				if (Integer.parseInt(getCurrentCourse(false).getSection()) < 5)
					motiveBox.setSelectedIndex(0);
				else {
					motiveBox.setSelectedIndex(1);
					motive = "U";
				}
			else
				motiveBox.setSelectedIndex(0);
			
			studentTable.setValueAt(motiveBox, row, 6);
		}
		
		int indexComment = commentBox.getSelectedIndex();
		String comment = null;
		if (indexComment >= 0) {
			comment = (String)commentBox.getSelectedItem().getValue();
		}

		setAssistanceLine(studentTable_ID, assistance, absence, delay, motive, comment, observation);

		setting = false;
	}
	
	@Override
	public ADForm getForm() {
		return form;
	}

	@Override
	public void showErrorMessage(String message){
		FDialog.error(form.getWindowNo(), Msg.translate(m_ctx, message));
	}

	@Override
	public void dispose(){
		form.dispose();
		SessionManager.getAppDesktop().closeActiveWindow();
	}

}
