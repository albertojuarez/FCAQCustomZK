package org.eevolution.form;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Textbox;
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
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.fcaq.components.INoteEditor;
import org.fcaq.components.WNoteEditor;
import org.fcaq.model.X_CA_ConcatenatedSubject;
import org.fcaq.model.X_CA_CourseDef;
import org.fcaq.model.X_CA_EvaluationPeriod;
import org.fcaq.model.X_CA_GroupAssignment;
import org.fcaq.model.X_CA_MatterAssignment;
import org.fcaq.model.X_CA_Parcial;
import org.fcaq.model.X_CA_SubjectMatter;
import org.fcaq.model.X_CA_TeacherAssignment;
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
			Env.setContext(Env.getCtx(), "DisciplineWindowNo", form.getWindowNo());

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

		fParcial = new WTableDirEditor("CA_Parcial_ID", true, false, true, AcademicUtil.getParcialLookup(form.getWindowNo(),currentSchoolYear.get_ID(),0));
		fParcial.addValueChangeListener(this);
		//fParcial.setValue(AcademicUtil.getCurrentParcial(m_ctx,0).get_ID());

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
		Env.setContext(Env.getCtx(), "IsGradeEnabled", 0);
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
			
			fParcial = new WTableDirEditor("CA_Parcial_ID", true, false, true, AcademicUtil.getParcialLookup(form.getWindowNo(),currentSchoolYear.get_ID(),(Integer)fCourseDef.getValue()));
			fParcial.addValueChangeListener(this);
			
			if(AcademicUtil.getCurrentParcial(m_ctx, (Integer)fCourseDef.getValue())!=null)
				fParcial.setValue(AcademicUtil.getCurrentParcial(m_ctx, (Integer)fCourseDef.getValue()).get_ID());

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
			refreshHeader();
		}

	}


	@Override
	public void tableChanged(WTableModelEvent event) {
	}


	@Override
	public void onEvent(Event event) throws Exception {

		if (event.getTarget().equals(isElective))
		{
			if(isElective.isSelected())
			{
				inccol=1;
			}
			else
			{
				inccol=0;
			}
			
			fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),currentBPartner.get_ID(), isElective.isSelected()));
			fCourseDef.addValueChangeListener(this);

			repaintParameterPanel();
			refreshHeader();
		}
		else if(event.getTarget().equals(noteTable))
		{
			int row = noteTable.getSelectedRow();
			MBPartner selectedstudent = ((INoteEditor)noteTable.getValueAt(row, 1+inccol)).getStudent();
			refreshDelayInfo(selectedstudent,currentMatterAssignment, currentParcial);
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
		
		String whereClause =  X_CA_ConcatenatedSubject.COLUMNNAME_CA_MatterAssignment_ID + "=? AND " + X_CA_ConcatenatedSubject.COLUMNNAME_IsConcatenated + "=?";

		X_CA_ConcatenatedSubject csubject = new Query(m_ctx, X_CA_ConcatenatedSubject.Table_Name, whereClause, null)
		.setOnlyActiveRecords(true).setParameters(currentMatterAssignment.get_ID(), "N").first();


		if(csubject!=null)
		{
			X_CA_EvaluationPeriod evaperiod = (X_CA_EvaluationPeriod) csubject.getCA_EvaluationPeriod();
			X_CA_EvaluationPeriod p_evaperiod = (X_CA_EvaluationPeriod) currentParcial.getCA_EvaluationPeriod();

			if(evaperiod.getSeqNo().intValue() == p_evaperiod.getSeqNo().intValue())
				return;
		}


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
		noteTable.setColumnClass(1+inccol, org.fcaq.components.WNoteEditor.class, false);
		noteTable.setColumnClass(2+inccol, org.fcaq.components.WNoteEditor.class, false);
		noteTable.setColumnClass(3+inccol, org.fcaq.components.WNoteEditor.class, false);
		noteTable.setColumnClass(4+inccol, org.fcaq.components.WNoteEditor.class, false);
		noteTable.setColumnClass(5+inccol, String.class, true);
		noteTable.setColumnClass(6+inccol, org.fcaq.components.WNoteEditor.class, false);
		noteTable.setColumnClass(7+inccol, String.class, true);

	}


	@Override
	public void repaintFinal(MBPartner studen, String value) {

		try{
			for(int x=0;x<=noteTable.getRowCount()-1; x++)
			{
				INoteEditor editor = (INoteEditor)noteTable.getValueAt(x, 2+inccol);
				if(editor.getStudent().get_ID() == studen.get_ID())
				{
					editor = (INoteEditor)noteTable.getValueAt(x, 6+inccol);
					editor.setFValue(new BigDecimal(value));
				}
			}
		}catch(Exception e)
		{
			//Nothing to do, just ignore
		}
	}



	private void repaintParameterPanel() {

		Textbox lclassroom = new Textbox("");
		lclassroom.setReadonly(true);
		
		if(fCourseDef.getValue()!=null)
		{
			X_CA_CourseDef course = new X_CA_CourseDef(m_ctx, (Integer)fCourseDef.getValue(), null);
			
			if(course.getSection().equals("04")) // Solo en primaria
			{
				
				String whereClause = X_CA_MatterAssignment.COLUMNNAME_CA_MatterAssignment_ID + 
						" IN (SELECT " + X_CA_MatterAssignment.COLUMNNAME_CA_MatterAssignment_ID + " FROM " + X_CA_MatterAssignment.Table_Name +
						" WHERE " + X_CA_MatterAssignment.COLUMNNAME_CA_GroupAssignment_ID + 
						" IN (SELECT " + X_CA_GroupAssignment.COLUMNNAME_CA_GroupAssignment_ID + " FROM " + X_CA_GroupAssignment.Table_Name + 
						" WHERE " + X_CA_GroupAssignment.COLUMNNAME_CA_CourseDef_ID + "=? ) AND " +
						X_CA_MatterAssignment.COLUMNNAME_CA_MatterAssignment_ID + 
						" IN (SELECT " + X_CA_TeacherAssignment.COLUMNNAME_CA_MatterAssignment_ID + " FROM " + X_CA_TeacherAssignment.Table_Name +
						" WHERE " + X_CA_TeacherAssignment.COLUMNNAME_C_BPartner_ID + "=?))";
				
				List<X_CA_MatterAssignment> assignments = new Query(m_ctx, X_CA_MatterAssignment.Table_Name, whereClause, null)
						.setOnlyActiveRecords(true).setParameters(course.get_ID(), currentBPartner.get_ID()).setOrderBy(X_CA_MatterAssignment.COLUMNNAME_Created)
						.list();
				
				if(assignments.size()>0)
				{
					fMatterAssignment.setValue(assignments.get(0).get_ID());
					if("ES".equals(assignments.get(0).getAD_Language()))
					{
						lclassroom.setText("Espa\u00F1ol");
						useautocopy = true;
					}
					else if("EN".equals(assignments.get(0).getAD_Language()))
					{
						lclassroom.setText("English");
						useautocopy = true;
					}	
					else
					{
						useautocopy=false;
					}
						
					//lclassroom.setText("ES".equals(assignments.get(0).getAD_Language())?"Espa\u00F1ol":"English");
				}
			}
			else
			{
				lclassroom.setText("");
			}
		}
		
		
		
		
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
		fCourseDef.getComponent().setWidth("50%");
		
		if(lclassroom.getValue().length()>0)
		{
			row.appendChild(new Label("Aula"));
			row.appendChild(lclassroom);
		}
		else
		{
			row.appendChild(lSubjectMatter);
			row.appendChild(fMatterAssignment.getComponent());
		}
		
		


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
				INoteEditor editor = (INoteEditor)noteTable.getValueAt(row, 2+inccol);
				if(editor.getStudent().get_ID() == student.get_ID())
				{
					noteTable.setValueAt(String.valueOf(discount), row, 5+inccol);
				}
			}
		}catch(Exception e)
		{
			
		}
	}
}
