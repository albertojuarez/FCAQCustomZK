package org.eevolution.form;

import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.panel.IFormLauncher;
import org.adempiere.webui.panel.StatusBarPanel;
import org.adempiere.webui.window.FDialog;
import org.compiere.minigrid.IDColumn;
import org.compiere.model.MRefList;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.fcaq.model.MCAPeriodClass;
import org.fcaq.model.X_CA_CourseDef;
import org.fcaq.model.X_CA_MatterAssignment;
import org.fcaq.model.X_CA_PeriodClass;
import org.fcaq.util.AcademicUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;

public class WSubstitution extends Substitution 
implements IFormController, IFormLauncher, EventListener, WTableModelListener, ValueChangeListener {

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
	private Window detail = null;
	private WListbox detailTable = ListboxFactory.newDataTable();

	private Label lDay = new Label();
	private Label lDayNo = new Label();
	private Label lSubject = new Label();
	private Label lDate = new Label();
	private Label lCourse = new Label();
	private Label lPeriod = new Label();
	private Label lAliasSubject = new Label();

	private WDateEditor fDate = new WDateEditor();
	private WTableDirEditor fSubject = null;
	private WTableDirEditor fCourse = null;
	private WTableDirEditor fPeriod = null;
	
	private boolean setting = false;

	public WSubstitution() {
		
		try {	
			
			southPanel.appendChild(new Separator());	
			southPanel.appendChild(statusBar);
		}
		catch(Exception e) {
			
			log.log(Level.SEVERE, "", e);
		}
	}

	@Override
	public void init() {
		
		loadStartData(form.getProcessInfo().getAD_PInstance_ID());
		dynInit();
		zkInit();
		refreshHeader();
	}

	private void dynInit() {
		
		lDayNo.setText("" + getDayNo());
		
		fDate.setValue(getCurrentDate());
		fDate.setReadWrite(false);
		
		fCourse = new WTableDirEditor(X_CA_CourseDef.COLUMNNAME_CA_CourseDef_ID, 
				true, false, true, getCourseLookup(form.getWindowNo()));
		fCourse.setValue(getCurrentCourse_ID());
		fCourse.setReadWrite(false);
		
		fSubject = new WTableDirEditor(X_CA_MatterAssignment.COLUMNNAME_CA_MatterAssignment_ID, 
				true, false, true, getMatterAssignmentLookup(form.getWindowNo()));
		fSubject.setReadWrite(false);
		
		if (getCurrentMatterAssignment_ID() > 0)
			fSubject.setValue(getCurrentMatterAssignment_ID());
		
		fPeriod = new WTableDirEditor(MCAPeriodClass.COLUMNNAME_CA_PeriodClass_ID, 
				true, false, true, getPeriodClassLookup(form.getWindowNo()));
		fPeriod.addValueChangeListener(this);
		
		if (getCurrentPeriodClass_ID() > 0)
			fPeriod.setValue(getCurrentPeriodClass_ID());
		
		bRefresh.addActionListener(this);
		bSendAssistance.addActionListener(this);
	}

	private void zkInit() {
		
		form.appendChild(mainLayout);
		mainLayout.setWidth("99%");
		mainLayout.setHeight("100%");
		
		parameterPanel.appendChild(parameterLayout);
		
		lDay.setText(Msg.getMsg(m_ctx, "Day"));
		lSubject.setText(Msg.getMsg(m_ctx, "SubjectMatter"));
		lDate.setText(Msg.getMsg(m_ctx, "Date"));
		lCourse.setText(Msg.getMsg(m_ctx, "Group"));
		lPeriod.setText(Msg.getMsg(m_ctx, "PeriodClass"));
		
		bSendAssistance.setLabel(Msg.getMsg(m_ctx, "SendAssistance"));
		bRefresh.setLabel(Msg.getMsg(m_ctx, "Refresh"));
		
		North north = new North();
		north.setStyle("border: none");
		north.setHeight("120px");
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

	private void refreshHeader() {
		
		if(fCourse.getValue() == null) {
			
			studentTable.clear();
			return;
		}
		
		if (isByPeriod()) {
			
			if(fSubject.getValue() == null || fPeriod.getValue() == null) {
				
				studentTable.clear();
				return;
			}
			ShowSecondaryFields();
		} 
		else { 
			
			if (validateDayWeek())
				hideSecondaryFields();
			else
				return;
		}
		
		Vector<Vector<Object>> data = getAssistanceData();
		ListModelTable modelP = new ListModelTable(data);
		modelP.addTableModelListener(this);
		studentTable.clear();
		studentTable.getModel().removeTableModelListener(this);
		studentTable.setData(modelP, getColumnNames());
		studentTable.addActionListener(this);
		
		setColumnClasses(false);
		bSendAssistance.setDisabled(false);
		
		if (refreshAssistance()) {
			
			setColumnClasses(true);
			bSendAssistance.setDisabled(true);
		}
	}

	private void setColumnClasses(boolean readOnly) {
		
		studentTable.setColumnClass(ID, IDColumn.class, true);
		
		if (isByPeriod() && !readOnly)
			studentTable.setColumnClass(DETAIL, Button.class, false);
		else
			studentTable.setColumnClass(DETAIL, Button.class, true);
		
		studentTable.setColumnClass(STUDENT_NAME, String.class, true);
		studentTable.setColumnClass(COURSE_NAME, String.class, true);
		studentTable.setColumnClass(ASSISTANCE, Checkbox.class, readOnly);
		studentTable.setColumnClass(ABSENCE, Checkbox.class, readOnly);
		studentTable.setColumnClass(DELAY, Checkbox.class, readOnly);
		studentTable.setColumnClass(MOTIVE, Combobox.class, readOnly);
		studentTable.setColumnClass(COMMENT, Combobox.class, readOnly);
		studentTable.setColumnClass(OBSERVATION, String.class, readOnly);
	}

	protected void setAssistanceData(int C_BPartner_ID, boolean assistance, boolean absence, 
			boolean delay, String motive, String comment, String observation) {
		
		setting = true;
		
		for (int i=0; i <= studentTable.getRowCount()-1; i++){
			
			int studentTable_ID = ((IDColumn) studentTable.getValueAt(i, ID)).getRecord_ID();
			
			if (studentTable_ID > 0 && studentTable_ID == C_BPartner_ID){
				
				studentTable.setValueAt(assistance, i, ASSISTANCE);
				studentTable.setValueAt(absence, i, ABSENCE);
				studentTable.setValueAt(delay, i, DELAY);
				studentTable.setValueAt(selectItemMotive(motive), i, MOTIVE);
				studentTable.setValueAt(selectItemComment(comment), i, COMMENT);
				
				if (observation == null)
					studentTable.setValueAt("", i, OBSERVATION);
				else
					studentTable.setValueAt(observation, i, OBSERVATION);
				
				break;
			}
		}
		
		setting = false;
	}

	@Override
	protected void clearStudentTable() {
		
		studentTable.clear();
	}

	private void ShowSecondaryFields() {
		
		parameterLayout.removeChild(parameterLayout.getRows());
		Rows rows = parameterLayout.newRows();
		Row row = null;
		
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
		
		if (isAliasSubject()) {
			
			lAliasSubject = new Label(getAliasSubject());
			
			row = rows.newRow();
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(lAliasSubject);
		}
	}

	private void hideSecondaryFields() {
		
		parameterLayout.removeChild(parameterLayout.getRows());
		Rows rows = parameterLayout.newRows();
		Row row = null;
		
		row = rows.newRow();
		row.appendChild(lDate);
		row.appendChild(fDate.getComponent());

		row = rows.newRow();
		row.appendChild(lCourse);
		fCourse.getComponent().setWidth("90%");
		row.appendChild(fCourse.getComponent());
	}

	@Override
	protected Object getButtonDetail() {
		
		Button bDetail = new Button();
		bDetail.setWidth("90%");
		bDetail.setLabel(Msg.translate(m_ctx, "Detail"));
		bDetail.addActionListener(this);
		return bDetail;
	}

	@Override
	protected Object getMotive() {
		
		Combobox motiveBox = new Combobox();
		motiveBox.setStyle("ipadcombobox");
		motiveBox.appendItem(Msg.translate(m_ctx, "Justified"), "J");
		motiveBox.appendItem(Msg.translate(m_ctx, "Unjustified"), "U");
		motiveBox.addEventListener(Events.ON_CHANGE, this);
		return motiveBox;
	}

	@Override
	protected Object getComments() {
		
		Combobox commentBox = new Combobox();
		commentBox.setStyle("ipadcombobox");
		commentBox.addEventListener(Events.ON_CHANGE, this);
		
		for (MRefList comment : getListComments()) {
			
			commentBox.appendItem(getRefListTrlName(comment), comment.getValue());
		}
		
		return commentBox;
	}

	private Combobox selectItemMotive(String value) {
		
		Combobox motiveBox = (Combobox)getMotive();
		
		for (int index = 0; index <= motiveBox.getItemCount()-1; index++) {
			
			if (value != null && value.equals((String)motiveBox.getItemAtIndex(index).getValue())) {
				
				motiveBox.setSelectedItem(motiveBox.getItemAtIndex(index));
				break;
			}
		}
		return motiveBox;
	}

	private Combobox selectItemComment(String value){
		
		Combobox commentBox = (Combobox)getComments();
		
		for (int index = 0; index <= commentBox.getItemCount()-1; index++) {
			
			if (value != null && value.equals((String)commentBox.getItemAtIndex(index).getValue())) {
				
				commentBox.setSelectedItem(commentBox.getItemAtIndex(index));
				break;
			}
		}
		return commentBox;
	}

	@Override
	protected void showErrorMessage(String message) {
		
		FDialog.error(form.getWindowNo(), Msg.translate(m_ctx, message));
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		
		String name = evt.getPropertyName();
		Object value = evt.getNewValue();
		Object oldValue = evt.getOldValue();
		
		if (value == null) {
			
			fPeriod.setValue(oldValue);
			return;
		}
		
		if (MCAPeriodClass.COLUMNNAME_CA_PeriodClass_ID.equals(name)) {
			
			fPeriod.setValue(value);
			setCurrentPeriodClass_ID((Integer) value);
		}
		
		refreshHeader();
	}

	@Override
	public void tableChanged(WTableModelEvent event) {
		
		if (!setting && event.getLastRow() >= 0 && event.getColumn() > ID)
			setAssistenceRow(event.getLastRow(), event.getColumn());
	}

	@Override
	public void onEvent(Event event) throws Exception {
		
		if (event.getTarget() instanceof Combobox) {
			
			int rowNo = -1;
			int colNo = -1;
			Combobox currentCombo = (Combobox)event.getTarget();
			
			for(int x=0; x <= studentTable.getRowCount()-1;x++) {
				
				Combobox motiveCombo = (Combobox)studentTable.getValueAt(x, MOTIVE);
				Combobox commentCombo = (Combobox)studentTable.getValueAt(x, COMMENT);
				
				if(motiveCombo == currentCombo) {
					rowNo = x;
					colNo = MOTIVE;
					break;
				}
				if(commentCombo == currentCombo) {
					rowNo = x;
					colNo = COMMENT;
					break;
				}
			}
			
			if (!setting)
				setAssistenceRow(rowNo, colNo);
		}
		else if (event.getTarget().equals(bSendAssistance)) {
			
			boolean sendAssistance = FDialog.ask(form.getWindowNo(), null, 
					Msg.getMsg(Env.getCtx(), "SureSendAssistance"));
			
			if(sendAssistance) {
				
				if (sendAssistance())
					refreshHeader();
			}
		}
		else if (event.getTarget().equals(bRefresh)) {
			
			refreshHeader();
		}
		else if (event.getTarget().getId().equals("Ok") 
				|| event.getTarget().getId().equals("Cancel")) {
			
			detail.dispose();
			refreshHeader();
		}
		else if (event.getTarget() instanceof Button) {
			
			int rowNo = -1;
			Button currentButton = (Button)event.getTarget();
			
			for(int x=0; x <= studentTable.getRowCount()-1;x++) {
				
				Button detailButton = (Button)studentTable.getValueAt(x, DETAIL);
				
				if(detailButton == currentButton) {
					rowNo = x;
					break;
				}
			}
			
			if(rowNo >= 0)
				showDetail(((IDColumn)studentTable.getValueAt(rowNo, ID)).getRecord_ID(), 
						(String)studentTable.getValueAt(rowNo, STUDENT_NAME));
		}
	}

	@Override
	public ADForm getForm() {
		
		return form;
	}

	public void setAssistenceRow(int row, int column) {
		
		setting = true;
		
		int studentTable_ID = ((IDColumn) studentTable.getValueAt(row, ID)).getRecord_ID();
		boolean assistance = (Boolean) studentTable.getValueAt(row, ASSISTANCE);
		boolean absence = (Boolean) studentTable.getValueAt(row, ABSENCE);
		boolean delay = (Boolean) studentTable.getValueAt(row, DELAY);
		Combobox motiveBox = (Combobox)studentTable.getValueAt(row, MOTIVE);
		Combobox commentBox = (Combobox)studentTable.getValueAt(row, COMMENT);
		String observation = (String)studentTable.getValueAt(row, OBSERVATION);
		
		if (column == ASSISTANCE && !assistance) {
			studentTable.setValueAt(true, row, ASSISTANCE);
			setting = false;
			return ;
		} else if (column == ABSENCE && !absence) {
			studentTable.setValueAt(true, row, ABSENCE);
			setting = false;
			return ;
		} else if (column == DELAY && !delay) {
			studentTable.setValueAt(true, row, DELAY);
			setting = false;
			return ;
		} else if (column == ASSISTANCE && assistance) {
			studentTable.setValueAt(false, row, ABSENCE);
			studentTable.setValueAt(false, row, DELAY);
			studentTable.setValueAt((Combobox)getMotive(), row, MOTIVE);
			studentTable.setValueAt((Combobox)getComments(), row, COMMENT);
			studentTable.setValueAt("", row, OBSERVATION);
			absence = false;
			delay = false;
		} else if (column == ABSENCE && absence) {
			studentTable.setValueAt(false, row, ASSISTANCE);
			studentTable.setValueAt(false, row, DELAY);
			assistance = false;
			delay = false;
		} else if (column == DELAY && delay) {
			studentTable.setValueAt(false, row, ASSISTANCE);
			studentTable.setValueAt(false, row, ABSENCE);
			assistance = false;
			absence = false;
		} else if (column == MOTIVE) {
			
			if (!absence && !delay) {
				studentTable.setValueAt(false, row, ASSISTANCE);
				studentTable.setValueAt(false, row, ABSENCE);
				studentTable.setValueAt(true, row, DELAY);
				assistance = false;
				absence = false;
				delay = true;
			}
			if ("U".equals(motiveBox.getItemAtIndex(motiveBox.getSelectedIndex()).getValue())) {
				studentTable.setValueAt((Combobox)getComments(), row, COMMENT);
				studentTable.setValueAt("", row, OBSERVATION);
			}
		} else if (column == COMMENT) {
			
			if (!absence && !delay) {
				studentTable.setValueAt(false, row, ASSISTANCE);
				studentTable.setValueAt(false, row, ABSENCE);
				studentTable.setValueAt(true, row, DELAY);
				assistance = false;
				absence = false;
				delay = true;
			}
			if (motiveBox.getSelectedIndex() >= 0 
					&& "U".equals(motiveBox.getItemAtIndex(motiveBox.getSelectedIndex()).getValue()))
				studentTable.setValueAt((Combobox)getComments(), row, COMMENT);
			
		} else if (column == OBSERVATION) {
			
			if (!absence && !delay) {
				studentTable.setValueAt(false, row, ASSISTANCE);
				studentTable.setValueAt(false, row, ABSENCE);
				studentTable.setValueAt(true, row, DELAY);
				assistance = false;
				absence = false;
				delay = true;
			}
			if (motiveBox.getSelectedIndex() >= 0 
					&& "U".equals(motiveBox.getItemAtIndex(motiveBox.getSelectedIndex()).getValue()))
				studentTable.setValueAt("", row, OBSERVATION);
		} else 
			;
		
		int indexMotive = motiveBox.getSelectedIndex();
		String motive = "J";
		if (indexMotive >= 0)
			motive = (String)motiveBox.getSelectedItem().getValue();
		else {
			
			if (Integer.parseInt(getSection()) < Integer.parseInt(X_CA_CourseDef.SECTION_Secundaria))
				motiveBox.setSelectedIndex(0);
			else {
				
				if (commentBox.getSelectedIndex() >= 0 || !observation.isEmpty()) {
					motiveBox.setSelectedIndex(0);
					motive = "J";
				}
				else {
					motiveBox.setSelectedIndex(1);
					motive = "U";
				}
			}
			
			studentTable.setValueAt(motiveBox, row, MOTIVE);
		}
		
		int indexComment = commentBox.getSelectedIndex();
		String comment = null;
		if (indexComment >= 0) {
			comment = (String)commentBox.getSelectedItem().getValue();
		}
		
		setAssistanceLine(studentTable_ID, assistance, absence, delay, motive, comment, observation);
		
		setting = false;
	}

	private void showDetail(int studentTable_ID, String studentName) {
		
		detail = new Window();
		Borderlayout mainDLayout = new Borderlayout();
		mainDLayout.setWidth("99%");
		mainDLayout.setHeight("99%");
		Grid parameterDLayout = GridFactory.newGridLayout();
		parameterDLayout.setWidth("99%");
		Panel parameterDPanel = new Panel();
		parameterDPanel.appendChild(parameterDLayout);
		
		Label lDDate = new Label();
		Label lDDay = new Label();
		Label lDDayNo = new Label();
		Label lDStudent = new Label();
		Label fDStudent = new Label();
		
		WDateEditor fDDate = new WDateEditor();
		
		detailTable.clear();
		detailTable.getModel().removeTableModelListener(this);
		
		Vector<Vector<String>> data = getAttendanceOfDay(studentTable_ID);
		Vector<String> columnNames = getColumnNamesDetail();
		ListModelTable modelP = new ListModelTable(data);
		modelP.addTableModelListener(this);
		
		detailTable.setData(modelP, columnNames);
		
		if (isByPeriod()) {
			detailTable.setColumnClass(0, String.class, true);
			detailTable.setColumnClass(1, String.class, true);
			detailTable.setColumnClass(2, String.class, true);
		}
		else {
			detailTable.setColumnClass(0, String.class, true);
			detailTable.setColumnClass(1, String.class, true);
		}
		
		lDDate.setText(Msg.translate(m_ctx, "Date"));
		lDDay.setText(Msg.translate(m_ctx, "Day"));
		lDDayNo.setText("" + getDayNo());
		lDStudent.setText(Msg.getMsg(Env.getCtx(), "Student"));
		
		fDDate.setValue(getCurrentDate());
		fDDate.setReadWrite(false);
		fDStudent.setText(studentName);
		
		Row row = null;
		
		North northD = new North();
		northD.setStyle("border: none");
		mainDLayout.appendChild(northD);
		northD.appendChild(parameterDPanel);
		
		Rows rows = parameterDLayout.newRows();
		
		row = rows.newRow();
		row.appendChild(lDDate);
		row.appendChild(fDDate.getComponent());
		
		if (isByPeriod()) {
			
			row.appendChild(lDDay);
			row.appendChild(lDDayNo);
		}
		
		row = rows.newRow();
		row.appendChild(lDStudent);
		row.appendChild(fDStudent);
		
		Center centerD = new Center();
		mainDLayout.appendChild(centerD);
		centerD.appendChild(detailTable);
		detailTable.setWidth("99%");
		detailTable.setHeight("99%");
		centerD.setStyle("border: none");
		
		South southD = new South();
		mainDLayout.appendChild(southD);
		
		ConfirmPanel confirmPanel = new ConfirmPanel(true);
		southD.appendChild(confirmPanel);
		
		confirmPanel.addActionListener(Events.ON_CLICK, this);
		confirmPanel.addActionListener(Events.ON_CANCEL, this);
		
		detail.setSizable(true);
		detail.setWidth("900px");
		detail.setHeight("450px");
		detail.setShadow(true);
		detail.setBorder("normal");
		detail.setClosable(true);
		detail.setTitle(Msg.translate(Env.getCtx(),"Comments"));
		detail.setContentStyle("overflow: auto");
		detail.appendChild(mainDLayout);
		
		AEnv.showCenterScreen(detail);
	}

	@Override
	public Object getTableDirDisplay(String columnName, int recordID) {
		
		StringBuilder whereClause = new StringBuilder();
		
		whereClause.append(" AND ").append(columnName)
			.append("=").append(recordID);
		
		WTableDirEditor fDCourse = null;
		
		if (X_CA_CourseDef.COLUMNNAME_CA_CourseDef_ID.equals(columnName)) {
			
			fDCourse = new WTableDirEditor(columnName, true, false, true, 
				AcademicUtil.buildLookup(AcademicUtil.COLUMN_CourseDef_ID, 
						whereClause.toString(), form.getWindowNo()));
		}
		else if (X_CA_MatterAssignment.COLUMNNAME_CA_MatterAssignment_ID.equals(columnName)) {
			
			fDCourse = new WTableDirEditor(columnName, true, false, true, 
					AcademicUtil.buildLookup(AcademicUtil.COLUMN_MatterAssignment_ID, 
							whereClause.toString(), form.getWindowNo()));
		}
		else if (X_CA_PeriodClass.COLUMNNAME_CA_PeriodClass_ID.equals(columnName)) {
			
			fDCourse = new WTableDirEditor(columnName, true, false, true, 
					AcademicUtil.buildLookup(getCOLUMN_PeriodClass_ID(), 
							whereClause.toString(), form.getWindowNo()));
		}
		
		if (fDCourse == null)
			return "";
		else
			fDCourse.setValue(recordID);
		
		return fDCourse.getDisplay();
	}

}
