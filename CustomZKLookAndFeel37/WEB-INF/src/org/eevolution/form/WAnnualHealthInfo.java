package org.eevolution.form;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.Datebox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.NumberBox;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.WAppsAction;
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
import org.adempiere.webui.window.FDialog;
import org.compiere.minigrid.IDColumn;
import org.compiere.util.Msg;
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

public class WAnnualHealthInfo extends AnnualHealthInfo 
implements IFormController, EventListener, WTableModelListener, ValueChangeListener{

	private CustomForm form = new CustomForm();
	private Borderlayout mainLayout = new Borderlayout();
	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();
	private WListbox studentTable = ListboxFactory.newDataTable();
	private Hbox hboxBtnRight;
	private Panel pnlBtnRight;
	private Panel southPanel = new Panel();
	private StatusBarPanel statusBar = new StatusBarPanel();
	
	private Label lCourse = null;
	private Label lSeqNo = null;
	private Label lSchoolYear = null;
	private WTableDirEditor fCourse = null;
	private Combobox fSeqNo = null;
	
	private Button bRefresh;
	private Button bOk;
	
	private boolean setting = false;
	
	public WAnnualHealthInfo() {
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

	private void zkInit() throws IOException {
		
		form.appendChild(mainLayout);
		mainLayout.setWidth("99%");
		mainLayout.setHeight("100%");
		
		parameterPanel.appendChild(parameterLayout);
		
		lSchoolYear = new Label();
		lSchoolYear.setText(Msg.translate(m_ctx, "SchoolYear") +
				" " + getCurrentSchoolYear().getName());
		
		lCourse = new Label();
		lCourse.setText(Msg.getMsg(m_ctx, "Group"));
		
		lSeqNo = new Label();
		lSeqNo.setText(Msg.getMsg(m_ctx, "SeqNo"));
		
		WAppsAction ok = new WAppsAction (ConfirmPanel.A_OK, null, ConfirmPanel.A_OK);
		bOk = ok.getButton();
		bOk.setLabel(Msg.getMsg(m_ctx,"Ok").replaceAll("[&]",""));
		bOk.addActionListener(this);
		
		WAppsAction refresh = new WAppsAction (ConfirmPanel.A_REFRESH, null, ConfirmPanel.A_REFRESH);
		bRefresh = refresh.getButton();
		bRefresh.setLabel(Msg.getMsg(m_ctx, "Refresh"));
		bRefresh.addActionListener(this);
		
		North north = new North();
		north.setStyle("border: none");
		mainLayout.appendChild(north);
		north.appendChild(parameterPanel);
		Rows rows = null;
		Row row = null;
		parameterLayout.setWidth("99%");
		rows = parameterLayout.newRows();
		
		row = rows.newRow();
		row.appendChild(new Space());
		row.appendChild(lSchoolYear);
		
		row = rows.newRow();
		row.appendChild(lCourse);
		fCourse.getComponent().setWidth("90%");
		row.appendChild(fCourse.getComponent());
		row.appendChild(lSeqNo);
		fSeqNo.setWidth("90%");
		row.appendChild(fSeqNo);
		
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
		//Panel southPanel = new Panel();
		
		south.appendChild(southPanel);
		
		pnlBtnRight = new Panel();
		pnlBtnRight.setAlign("center");
		pnlBtnRight.appendChild(bRefresh);
		pnlBtnRight.appendChild(bOk);

		hboxBtnRight = new Hbox();
		hboxBtnRight.appendChild(pnlBtnRight);
		hboxBtnRight.setWidth("100%");
		hboxBtnRight.setStyle("text-align:right");
		
		southPanel.appendChild(hboxBtnRight);
		
		mainLayout.appendChild(south);
	}

	private void dynInit() {
				
		fCourse = new WTableDirEditor("CA_CourseDef_ID", true, false, true, 
				getCourseLookup(getForm().getWindowNo(), getCurrentTeacher().get_ID()));
		fCourse.addValueChangeListener(this);
		
		fSeqNo = new Combobox();
		fSeqNo.setStyle("ipadcombobox");
		fSeqNo.appendItem("1", "1");
		fSeqNo.appendItem("2", "2");
		fSeqNo.addEventListener(Events.ON_CHANGE, this);
	}

	public void refreshHeader() {
		
		if(fCourse.getValue() == null 
				|| (fSeqNo.getValue() == null || fSeqNo.getValue().length() == 0)) {
			
			studentTable.clear();
			return;
		}
		
		Vector<Vector<Object>> data = getAnnualHealthData();
		
		studentTable.clear();
		studentTable.getModel().removeTableModelListener(this);
		
		ListModelTable modelP = new ListModelTable(data);
		modelP.addTableModelListener(this);
		studentTable.setData(modelP, getColumnNames());
		
		studentTable.setColumnClass(STUDENT_NAME, String.class, true);			//  0-Name
		studentTable.setColumnClass(ID, IDColumn.class, true);					//  1-ID
		studentTable.setColumnClass(COURSE_NAME, String.class, true);			//  2-Course
		studentTable.setColumnClass(DATE, Datebox.class, true);					// 	3-Date
		studentTable.setColumnClass(WEIGHT, NumberBox.class, false);			//  4-Weight
		studentTable.setColumnClass(HEIGHT, NumberBox.class, false);			//  5-Height
		studentTable.setColumnClass(BMI, NumberBox.class, false); 				//  6-BMI
		studentTable.setColumnClass(CONDITION, WTableDirEditor.class, true);	//  7-Condition
		
		studentTable.addActionListener(this);
		
		refreshAnnualHealthInfo();
	}

	protected void initNumberComponent() {
		
		setting = true;
		
		for (int i=0; i <= studentTable.getRowCount()-1; i++) {
			
			studentTable.setValueAt(BigDecimal.ZERO, i, WEIGHT);
			studentTable.setValueAt(BigDecimal.ZERO, i, HEIGHT);
			studentTable.setValueAt(BigDecimal.ZERO, i, BMI);
		}
		
		setting = false;
	}
	protected void setAnnualHealthData(int C_BPartner_ID, String schoolYearName, Timestamp date, 
			BigDecimal weight, BigDecimal height, BigDecimal bmi, String condition) {
		
		setting = true;
		
		for (int i=0; i <= studentTable.getRowCount()-1; i++) {
			
			int studentTable_ID = ((IDColumn) studentTable.getValueAt(i, ID)).getRecord_ID();
			
			if (studentTable_ID > 0 && studentTable_ID == C_BPartner_ID){
				
				studentTable.setValueAt(date, i, DATE);
				studentTable.setValueAt(weight, i, WEIGHT);
				studentTable.setValueAt(height, i, HEIGHT);
				studentTable.setValueAt(bmi, i, BMI);
				studentTable.setValueAt(condition, i, CONDITION);
				
				break;
			}
		}
		
		setting = false;
	}

	public void setAnnualHealthInfoRow(int row, int column){
		
		setting = true;
		
		int C_BPartner_ID = ((IDColumn) studentTable.getValueAt(row, ID)).getRecord_ID();
		BigDecimal weight = (BigDecimal) studentTable.getValueAt(row, WEIGHT);
		BigDecimal height = (BigDecimal) studentTable.getValueAt(row, HEIGHT);
		
		if (BigDecimal.ZERO.equals(weight) || BigDecimal.ZERO.equals(height)) {
			
			setting = false;
			return;
		}
		
		setAnnualHealthInfo(C_BPartner_ID, weight, height);
		
		setting = false;
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		
		String name = evt.getPropertyName();
		Object value = evt.getNewValue();
		
		if (value == null)
			return;
		
		if ("CA_CourseDef_ID".equals(name)) {
			
			fCourse.setValue(value);
			
			int CA_CourseDef_ID = (Integer) value;
			
			setCurrentCourse(m_ctx, CA_CourseDef_ID, null);
		}
		
		refreshHeader();
	}

	@Override
	public void tableChanged(WTableModelEvent event) {
		
		//Numberbox
		if (!setting && event.getLastRow() >= 0 
				&& event.getColumn() > ID && event.getColumn() != BMI)
			
			setAnnualHealthInfoRow(event.getLastRow(), event.getColumn());
	}

	@Override
	public void onEvent(Event event) throws Exception {
		
		if (event.getTarget() instanceof Combobox) {
			
			refreshHeader();
		}
		else if (event.getTarget().equals(bOk)) {
			
			sendAnnualHealthInfo();
			
			refreshHeader();
		}
		else if (event.getTarget().equals(bRefresh)) {
			
			refreshHeader();
		}
	}

	@Override
	protected void setStatusBarMessage(String msg, boolean error) {
		
		statusBar.setStatusLine(msg, error);
	}

	@Override
	public ADForm getForm() {
		return form;
	}

	@Override
	protected int getCurrentSeqNo() {
		
		return Integer.parseInt(fSeqNo.getValue());
	}

	@Override
	protected Object getNumberComponent(boolean listener) {
		
		NumberBox numberBox = new NumberBox(false);
		
		if (listener)
			numberBox.addEventListener("onChange", this);
		
		return numberBox;
	}

	@Override
	protected void showErrorMessage(String message){
		FDialog.error(form.getWindowNo(), Msg.translate(m_ctx, message));
	}
}
