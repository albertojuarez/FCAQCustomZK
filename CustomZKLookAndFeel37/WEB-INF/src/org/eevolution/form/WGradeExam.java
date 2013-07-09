package org.eevolution.form;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Combobox;
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
import org.compiere.model.MBPartner;
import org.compiere.model.MColumn;
import org.compiere.model.Query;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.fcaq.model.X_CA_CourseDef;
import org.fcaq.model.X_CA_ExamType;
import org.fcaq.model.X_CA_MatterAssignment;
import org.fcaq.util.AcademicUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Space;

public class WGradeExam extends GradeExam implements IFormController, EventListener, WTableModelListener, ValueChangeListener {

	private CustomForm form = new CustomForm();
	private Borderlayout mainLayout = new Borderlayout();
	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();
	private Panel southPanel = new Panel();
	private Checkbox isElective = new Checkbox();
	private Hbox hboxBtnRight;
	private Panel pnlBtnRight;
	private Button bShowComments = new Button();

	private WTableDirEditor fTeacher = null;
	private WTableDirEditor fCourseDef = null;
	private WTableDirEditor fMatterAssignment=null;
	private Combobox         fTry = null;


	private Label lTeacher = new Label();
	private Label lCourseDef = new Label();
	private Label lSubject = new Label();
	private Label lTry  = new Label();


	public static CLogger log = CLogger.getCLogger(ExamEntry.class);



	public WGradeExam()
	{
		try
		{
			loadStartData();
			dynInit();
			zkInit();
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "", e);
		}
	}

	private void zkInit() {
		form.appendChild(mainLayout);
		mainLayout.setWidth("99%");
		mainLayout.setHeight("100%");

		parameterPanel.appendChild(parameterLayout);


		North north = new North();
		north.setStyle("border: none");
		mainLayout.appendChild(north);
		north.appendChild(parameterPanel);


		parameterLayout.setWidth("800px");
		parameterLayout.setHeight("110px");

		repaintParameterPanel();

		Center center = new Center();
		center.setFlex(true);
		mainLayout.appendChild(center);

		center.appendChild(((WListbox)examTable));
		((WListbox)examTable).setWidth("99%");
		((WListbox)examTable).setHeight("99%");
		center.setStyle("border: none");

		South south = new South();
		south.setStyle("border: none");
		mainLayout.appendChild(south);
		Panel southPanel = new Panel();
		south.appendChild(southPanel);

		pnlBtnRight = new Panel();
		pnlBtnRight.setAlign("right");
		pnlBtnRight.appendChild(bShowComments);

		hboxBtnRight = new Hbox();
		hboxBtnRight.appendChild(pnlBtnRight);
		hboxBtnRight.setWidth("100%");
		hboxBtnRight.setStyle("text-align:right");

		southPanel.appendChild(hboxBtnRight);
		southPanel.setWidth("100%");

	}

	private void repaintParameterPanel() {
		Rows rows = null;
		Row row = null;

		try{
			parameterLayout.removeChild(parameterLayout.getRows());
		}catch(Exception e)
		{
			// Nothing to do, just ignore
		}

		rows = parameterLayout.newRows();

		row = rows.newRow();
		row.appendChild(lTeacher);
		row.appendChild(fTeacher.getComponent());

		row = rows.newRow();
		row.appendChild(new Space());
		row.appendChild(isElective);
		row.appendChild(lCourseDef);
		row.appendChild(fCourseDef.getComponent());

		row = rows.newRow();
		row.appendChild(lSubject); // Etiqueta de materia
		row.appendChild(fMatterAssignment.getComponent());

		row.appendChild(lTry);
		row.appendChild(fTry);
	}

	// Init Search Editor
	private void dynInit() {

		bShowComments.setLabel(Msg.getMsg(Env.getCtx(), "Print"));
		bShowComments.addActionListener(this);

		examTable = new WListbox();
		((WListbox)examTable).setWidth("100%");
		((WListbox)examTable).setHeight("100%");
		((WListbox)examTable).setFixedLayout(false);
		((WListbox)examTable).setVflex(true);
		((WListbox)examTable).setStyle("overflow:auto;");

		isElective.setSelected(false);
		isElective.setLabel(Msg.getMsg(Env.getCtx(), "Is Elective"));

		lCourseDef.setText(Msg.getMsg(Env.getCtx(), "Group"));
		lTeacher.setText(Msg.getMsg(Env.getCtx(), "Teacher"));
		lSubject.setText(Msg.getMsg(Env.getCtx(), "Subject"));
		lTry.setText(Msg.getMsg(m_ctx, "ExamGradeTry"));


		String whereClause = "";
		//Teacher

		if(currentTeacher!=null)
			whereClause = "AND C_BPartner_ID = " + currentTeacher.get_ID();
		else
			whereClause = "AND C_BPartner_ID in (SELECT C_BPartner_ID FROM CA_TeacherAssignment WHERE IsActive='Y')";

		int teacherColumn = MColumn.getColumn_ID("C_BPartner", "C_BPartner_ID");
		fTeacher = new WTableDirEditor("C_BPartner_ID", true, false, true, AcademicUtil.buildLookup(teacherColumn, whereClause, form.getWindowNo()));
		fTeacher.addValueChangeListener(this);
		if(currentTeacher!=null)
			fTeacher.setValue(currentTeacher.get_ID());

		//Group
		if(currentTeacher!=null)
			fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),currentTeacher.get_ID(), isElective.isSelected()));
		else
			fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),0, isElective.isSelected()));
		fCourseDef.addValueChangeListener(this);

		//Subject when recovery
		fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, AcademicUtil.getMatterAssignmentLookup(form.getWindowNo(),0));
		fMatterAssignment.addValueChangeListener(this);

		isElective.addActionListener(this);

		fTry = new Combobox();
		fTry.appendItem("1", "1");
		fTry.appendItem("2", "2");
		fTry.appendItem("3", "3");
		fTry.appendItem("4", "4");
		fTry.addEventListener(Events.ON_CHANGE, this);



	}



	@Override
	public void valueChange(ValueChangeEvent evt) {
		String name = evt.getPropertyName();
		Object value = evt.getNewValue();

		if ("C_BPartner_ID".equals(name))
		{
			if(value==null)
			{
				currentTeacher=null;
			}
			else
			{
				fTeacher.setValue(value);
				currentTeacher= new MBPartner(m_ctx, (Integer)value, null);
				fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),currentTeacher.get_ID(), isElective.isSelected()));
				fCourseDef.addValueChangeListener(this);
				fCourseDef.setValue(null);
				currentCourse=null;
			}
		}
		else if("CA_CourseDef_ID".equals(name))
		{
			if(value==null)
			{
				currentCourse = null;
			}
			else
			{
				fCourseDef.setValue(value);
				currentCourse = new X_CA_CourseDef(m_ctx, (Integer)value, null);
				fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, 
						AcademicUtil.getMatterAssignmentLookup(form.getWindowNo(),currentTeacher.get_ID(), (Integer)fCourseDef.getValue()));
				fMatterAssignment.addValueChangeListener(this);
				fMatterAssignment.setValue(null);
				currentAssignment=null;

			}
		}
		else if("CA_MatterAssignment_ID".equals(name))
		{
			if(value==null)
			{
				currentAssignment=null;
			}
			else
			{
				fMatterAssignment.setValue(value);
				currentAssignment = new X_CA_MatterAssignment(m_ctx, (Integer)value, null);
			}
		}


		repaintParameterPanel();
		refreshExamTable();
	}

	private void refreshExamTable() {

		examTable.setRowCount(0);

		if(currentTeacher==null || currentCourse==null || currentAssignment==null || currentTry==null)
			return;

		Vector<String> columns = getColumns();


		Vector<Vector<Object>> data = getStudentData();

		ListModelTable modelP = new ListModelTable(data);

		modelP.addTableModelListener(this);
		((WListbox)examTable).setData(modelP, columns);

		((WListbox)examTable).setStyle("sizedByContent=true");

		examTable.setColumnClass(0, String.class, true);
		examTable.setColumnClass(1, String.class, true);
		examTable.setColumnClass(2, String.class, true);
		examTable.setColumnClass(3, BigDecimal.class, isReadOnly());

		examTable.autoSize();
		((WListbox)examTable).setWidth("100%");
		((WListbox)examTable).setHeight("100%");

	}


	private boolean isReadOnly() {

		String whereClause = X_CA_ExamType.COLUMNNAME_Value + "=? AND " + 
				X_CA_ExamType.COLUMNNAME_CA_SchoolYear_ID + "=?";

		X_CA_ExamType examType = new Query(m_ctx, X_CA_ExamType.Table_Name, whereClause, null)
		.setOnlyActiveRecords(true)
		.setParameters("GE", schoolYear.get_ID())
		.first();

		if(examType.getDateFrom().getTime()< System.currentTimeMillis() && 
				System.currentTimeMillis()< examType.getDateTo().getTime() )
			return false;
		else
			return true;
	}

	@Override
	public void tableChanged(WTableModelEvent event) {
		if(event.getIndex0()>=0)
		{
			int column = event.getColumn();
			String studentvalue = (String) examTable.getValueAt(event.getIndex0(), 0);
			MBPartner student = new Query(Env.getCtx(), MBPartner.Table_Name, MBPartner.COLUMNNAME_Value+"=?", null)
			.setOnlyActiveRecords(true)
			.setParameters(studentvalue)
			.first();

			if(column==(3)) //Exam Grade
			{
				
				BigDecimal newValue = (BigDecimal)examTable.getValueAt(event.getIndex0(), 3);
				
				if(newValue==null)
				{
					newValue = BigDecimal.ZERO;
				}
				
				if(student!=null)
				{
					if(newValue.compareTo(new BigDecimal(0))<0 || newValue.compareTo(new BigDecimal(100))>0)
					{
						newValue = BigDecimal.ZERO;
						examTable.setValueAt(newValue, event.getIndex0(), 3);

					}
					else
					{
						saveExam(student, event.getIndex0(), newValue);
					}
				}
			}
		}
	}


	@Override
	public void onEvent(Event event) throws Exception {
		if(event.getTarget().equals(fTry))
		{
			currentTry = fTry.getValue();
			refreshExamTable();
		}

		else if (event.getTarget().equals(isElective))
		{

			fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),currentTeacher.get_ID(), isElective.isSelected()));
			fCourseDef.addValueChangeListener(this);
			repaintParameterPanel();
		}
		else if(event.getTarget().equals(bShowComments))
		{

			List<String> description = new ArrayList<String>();

			if(currentTeacher!=null)
				description.add(currentTeacher.getName());
			if(currentCourse!=null)
				description.add(fCourseDef.getDisplay());
			if(currentAssignment!=null)
				description.add(fMatterAssignment.getDisplay());

			description.add("Intento: " + currentTry);

			WGradeViewer  gradeViewer = new WGradeViewer(description, examTable);

			gradeViewer.setSizable(true);
			gradeViewer.setWidth("700px");
			gradeViewer.setHeight("600px");
			gradeViewer.setShadow(true);
			gradeViewer.setBorder("normal");
			gradeViewer.setClosable(true);
			gradeViewer.setTitle(Msg.translate(Env.getCtx(),"Grades"));
			gradeViewer.setContentStyle("overflow: auto");

			AEnv.showCenterScreen(gradeViewer);

		}
	}



	@Override
	public ADForm getForm() {
		return form;
	}

}
