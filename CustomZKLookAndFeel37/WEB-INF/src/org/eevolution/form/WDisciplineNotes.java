package org.eevolution.form;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Vector;

import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
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
import org.adempiere.webui.window.FDialog;
import org.compiere.model.MBPartner;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.fcaq.components.INoteEditor;
import org.fcaq.components.WNoteEditor;
import org.fcaq.model.X_CA_CourseDef;
import org.fcaq.model.X_CA_MatterAssignment;
import org.fcaq.model.X_CA_Parcial;
import org.fcaq.model.X_CA_SubjectMatter;
import org.fcaq.util.AcademicUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zul.Space;

public class WDisciplineNotes extends DisciplineNotes implements IFormController, EventListener, WTableModelListener, ValueChangeListener{

	private CustomForm form = new CustomForm();
	private Borderlayout mainLayout = new Borderlayout();
	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();


	private Panel southPanel = new Panel();
	private StatusBarPanel statusBar = new StatusBarPanel();
	// ListboxFactory.newDataTable();

	// Form Components

	private Checkbox isElective = new Checkbox();

	private Label lCourseDef = null;
	private Label lParcial = null;
	private Label lSubjectMatter = null;

	private WTableDirEditor fCourseDef = null;
	private WTableDirEditor fParcial = null;
	private WTableDirEditor fMatterAssignment = null;

	private Label absence = new Label("");


	public WDisciplineNotes()
	{
		try{
			loadStartData();
			dynInit();
			zkInit();
		}
		catch(Exception e)
		{

		}

	}


	private void dynInit()
	{
		isElective.setSelected(false);

		noteTable = new WListbox();
		((WListbox)noteTable).setWidth("100%");
		((WListbox)noteTable).setHeight("100%");
		((WListbox)noteTable).setFixedLayout(false);
		((WListbox)noteTable).setVflex(true);

		isElective.setLabel(Msg.getMsg(Env.getCtx(), "Is Elective"));

		fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),currentBPartner.get_ID(), isElective.isSelected()));
		fCourseDef.addValueChangeListener(this);

		fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, AcademicUtil.getMatterAssignmentLookup(form.getWindowNo(),currentBPartner.get_ID()));
		fMatterAssignment.addValueChangeListener(this);

		fParcial = new WTableDirEditor("CA_Parcial_ID", true, false, true, AcademicUtil.getParcialLookup(form.getWindowNo(),currentSchoolYear.get_ID()));
		fParcial.addValueChangeListener(this);
		fParcial.setValue(AcademicUtil.getCurrentParcial(m_ctx).get_ID());

		isElective.addActionListener(this);
		((WListbox)noteTable).addActionListener(this);

	}


	private void zkInit()
	{
		form.appendChild(mainLayout);
		mainLayout.setWidth("99%");
		mainLayout.setHeight("100%");

		parameterPanel.appendChild(parameterLayout);

		lCourseDef = new Label();
		lCourseDef.setText(Msg.getMsg(Env.getCtx(), "Group"));

		lParcial = new Label();
		lParcial.setText(Msg.getMsg(Env.getCtx(), "Parcial"));

		lSubjectMatter = new Label();
		lSubjectMatter.setText(Msg.getMsg(Env.getCtx(), "SubjectMatter"));


		North north = new North();
		north.setStyle("border: none");
		mainLayout.appendChild(north);
		north.appendChild(parameterPanel);
		Rows rows = null;
		Row row = null;
		parameterLayout.setWidth("1000px");
		rows = parameterLayout.newRows();

		row = rows.newRow();
		row.appendChild(new Space());
		row.appendChild(isElective);
		row.appendChild(new Space());
		row = rows.newRow();
		row.appendChild(lCourseDef);
		row.appendChild(fCourseDef.getComponent());
		fCourseDef.getComponent().setWidth("60%");
		row.appendChild(lSubjectMatter);
		row.appendChild(fMatterAssignment.getComponent());


		row = rows.newRow();
		row.appendChild(lParcial);
		row.appendChild(fParcial.getComponent());
		row.appendChild(new Space());
		row.appendChild(absence);


		Center center = new Center();
		center.setFlex(true);
		mainLayout.appendChild(center);

		center.appendChild(((WListbox)noteTable));
		((WListbox)noteTable).setWidth("99%");
		((WListbox)noteTable).setHeight("99%");
		center.setStyle("border: none");

	}

	@Override
	public ADForm getForm() {
		return form;
	}


	@Override
	public void showErrorMessage(String message) {
		FDialog.error(0, message);
	}


	@Override
	public void dispose() {
		form.dispose();
	}


	@Override
	public void valueChange(ValueChangeEvent evt) {

		String name = evt.getPropertyName();
		Object value = evt.getNewValue();

		if (value == null)
			return;

		if ("CA_CourseDef_ID".equals(name))
		{
			fCourseDef.setValue(value);

			fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, AcademicUtil.getMatterAssignmentLookup(form.getWindowNo(),currentBPartner.get_ID(), (Integer)fCourseDef.getValue()));
			fMatterAssignment.addValueChangeListener(this);


			repaintParameterPanel();
			refreshHeader();

		}
		if ("CA_MatterAssignment_ID".equals(name))
		{
			fMatterAssignment.setValue(value);
			refreshHeader();
		}
		if ("CA_Parcial_ID".equals(name))
		{
			fParcial.setValue(value);
		}

	}


	@Override
	public void tableChanged(WTableModelEvent event) {
	}


	@Override
	public void onEvent(Event event) throws Exception {

		if (event.getTarget().equals(isElective))
		{
			fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),currentBPartner.get_ID(), isElective.isSelected()));
			fCourseDef.addValueChangeListener(this);

			repaintParameterPanel();
			refreshHeader();
		}
		else if(event.getTarget().equals(noteTable))
		{
			int row = noteTable.getSelectedRow();
			MBPartner selectedstudent = ((INoteEditor)noteTable.getValueAt(row, 1)).getStudent();
			refreshDelayInfo(selectedstudent);
			displayAbsenceInfo(String.valueOf(assisNo), String.valueOf(delayNo));
			double discount = ( yearConfig.getRoundLimit().doubleValue()) * (assisNo + delayNo);
			setDiscontInfo(selectedstudent, discount);
		}
	}

	private void refreshHeader() {

		System.out.println("Refresh header start At " + new Timestamp(System.currentTimeMillis()));

		((WListbox)noteTable).clear();
		((WListbox)noteTable).getModel().removeTableModelListener(this); 

		if(fCourseDef.getValue()==null || fMatterAssignment.getValue()==null || fParcial.getValue()==null)
			return;

		currentCourse  = new X_CA_CourseDef(m_ctx, (Integer)fCourseDef.getValue(), null);
		currentMatterAssignment = new X_CA_MatterAssignment(m_ctx, (Integer)fMatterAssignment.getValue(), null);

		if(currentMatterAssignment.getElectiveSubject_ID()>0)
			currentSubject = new X_CA_SubjectMatter(m_ctx, currentMatterAssignment.getElectiveSubject_ID(), null);
		else
			currentSubject = new X_CA_SubjectMatter(m_ctx, currentMatterAssignment.getCA_SubjectMatter_ID(), null);

		currentParcial = new X_CA_Parcial(m_ctx, (Integer)fParcial.getValue(), null);

		System.out.println("Load discipline config At " + new Timestamp(System.currentTimeMillis()));

		loadDisciplineConfig();
		
		System.out.println("End At " + new Timestamp(System.currentTimeMillis()));

		Vector<String> columns = buildNoteHeading();

		System.out.println("Student Data Start At " + new Timestamp(System.currentTimeMillis()));
		Vector<Vector<Object>> data = getStudentData();
		System.out.println("End At " + new Timestamp(System.currentTimeMillis()));

		ListModelTable modelP = new ListModelTable(data);

		modelP.addTableModelListener(this);
		((WListbox)noteTable).setData(modelP, columns);



		System.out.println("Refresh Notes Start At " + new Timestamp(System.currentTimeMillis()));
		refreshNotes();
		System.out.println("End At " + new Timestamp(System.currentTimeMillis()));


		noteTable.setColumnClass(0, String.class, true);
		noteTable.setColumnClass(1, org.fcaq.components.WNoteEditor.class, note!=null?note.isSent():false);
		noteTable.setColumnClass(2, org.fcaq.components.WNoteEditor.class, note!=null?note.isSent():false);
		noteTable.setColumnClass(3, org.fcaq.components.WNoteEditor.class, note!=null?note.isSent():false);
		noteTable.setColumnClass(4, org.fcaq.components.WNoteEditor.class, note!=null?note.isSent():false);
		noteTable.setColumnClass(5, String.class, true);
		noteTable.setColumnClass(6, org.fcaq.components.WNoteEditor.class, note!=null?note.isSent():false);
		noteTable.setColumnClass(7, String.class, true);

	}


	@Override
	public void repaintFinal(MBPartner studen, String value) {

		try{
			for(int x=0;x<=noteTable.getRowCount()-1; x++)
			{
				INoteEditor editor = (INoteEditor)noteTable.getValueAt(x, 2);
				if(editor.getStudent().get_ID() == studen.get_ID())
				{
					editor = (INoteEditor)noteTable.getValueAt(x, 6);
					editor.setFValue(new BigDecimal(value));
				}
			}
		}catch(Exception e)
		{
			//Nothing to do, just ignore
		}
	}



	private void repaintParameterPanel() {

		parameterLayout.removeChild(parameterLayout.getRows());
		Rows rows = null;
		Row row = null;
		parameterLayout.setWidth("1000px");
		rows = parameterLayout.newRows();

		row = rows.newRow();
		row.appendChild(new Space());
		row.appendChild(isElective);
		row.appendChild(new Space());
		row = rows.newRow();
		row.appendChild(lCourseDef);
		row.appendChild(fCourseDef.getComponent());
		fCourseDef.getComponent().setWidth("60%");
		row.appendChild(lSubjectMatter);
		row.appendChild(fMatterAssignment.getComponent());


		row = rows.newRow();
		row.appendChild(lParcial);
		row.appendChild(fParcial.getComponent());
		row.appendChild(new Space());
		row.appendChild(absence);
	}


	@Override
	public INoteEditor getNoteComponent() {
		return new WNoteEditor();
	}


	@Override
	public void displayAbsenceInfo(String absence, String delay) {
		this.absence.setText("Faltas: " + absence + "   Atrasos: " + delay);
	}


	@Override
	public void setDiscontInfo(MBPartner student, double discount) {
		try
		{
			for(int row = 0; row<=noteTable.getRowCount(); row++)
			{
				INoteEditor editor = (INoteEditor)noteTable.getValueAt(row, 2);
				if(editor.getStudent().get_ID() == student.get_ID())
				{
					noteTable.setValueAt(String.valueOf(discount), row, 5);
				}
			}
		}catch(Exception e)
		{
			
		}
	}
}
