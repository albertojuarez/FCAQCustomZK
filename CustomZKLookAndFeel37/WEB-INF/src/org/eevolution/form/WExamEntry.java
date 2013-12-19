package org.eevolution.form;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.component.Button;
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
import org.compiere.model.MBPartner;
import org.compiere.model.MColumn;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MLookupInfo;
import org.compiere.model.Query;
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.fcaq.model.X_CA_CourseDef;
import org.fcaq.model.X_CA_ExamType;
import org.fcaq.model.X_CA_ExamTypeDate;
import org.fcaq.model.X_CA_MatterAssignment;
import org.fcaq.model.X_CA_SubjectMatter;
import org.fcaq.util.AcademicUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Space;

public class WExamEntry extends ExamEntry implements IFormController, EventListener, WTableModelListener, ValueChangeListener {


	private CustomForm form = new CustomForm();
	private Borderlayout mainLayout = new Borderlayout();
	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();
	private Panel southPanel = new Panel();
	private Checkbox isElective = new Checkbox();
	private Hbox hboxBtnRight;
	private Panel pnlBtnRight;
	private Button bChekNotes = new Button();
	private Button bShowComments = new Button();

	private WTableDirEditor fExamType = null;
	private WTableDirEditor fTeacher = null;
	private WTableDirEditor fCourseDef = null;
	private WTableDirEditor fModality = null;
	private WTableDirEditor fGrade = null;
	private WTableDirEditor fSubjectMatter=null;
	private WTableDirEditor fMatterAssignment=null;


	private Label lExamType = new Label();
	private Label lTeacher = new Label();
	private Label lCourseDef = new Label();
	private Label lModality = new Label();
	private Label lGrade = new Label();
	private Label lSubject = new Label();


	public static CLogger log = CLogger.getCLogger(ExamEntry.class);



	public WExamEntry()
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
		pnlBtnRight.appendChild(bChekNotes);

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
		row.appendChild(lExamType);
		row.appendChild(fExamType.getComponent());

		if("RE".equals(currentExamType.getValue()))
		{
			row = rows.newRow();
			row.appendChild(new Space());
			row.appendChild(isElective);
			row.appendChild(lCourseDef);
			row.appendChild(fCourseDef.getComponent());

			row = rows.newRow();
			row.appendChild(lSubject); // Etiqueta de materia
			row.appendChild(fMatterAssignment.getComponent());
		}
		else
		{
			row = rows.newRow();
			row.appendChild(lModality);
			row.appendChild(fModality.getComponent());
			row.appendChild(lGrade);
			row.appendChild(fGrade.getComponent());
			row = rows.newRow();
			row.appendChild(lSubject);
			row.appendChild(fSubjectMatter.getComponent());
		}
	}

	// Init Search Editor
	private void dynInit() {

		bShowComments.setLabel(Msg.getMsg(Env.getCtx(), "ShowComments"));
		bChekNotes.setLabel(Msg.getMsg(Env.getCtx(), "Chek Grades"));

		examTable = new WListbox();
		((WListbox)examTable).setWidth("100%");
		((WListbox)examTable).setHeight("100%");
		((WListbox)examTable).setFixedLayout(false);
		((WListbox)examTable).setVflex(true);
		((WListbox)examTable).setStyle("overflow:auto;");

		isElective.setSelected(false);
		isElective.setLabel(Msg.getMsg(Env.getCtx(), "Is Elective"));

		lCourseDef.setText(Msg.getMsg(Env.getCtx(), "Group"));
		lExamType.setText(Msg.getMsg(Env.getCtx(), "ExamType"));
		lTeacher.setText(Msg.getMsg(Env.getCtx(), "Teacher"));
		lModality.setText(Msg.getMsg(Env.getCtx(), "Modality"));
		lGrade.setText(Msg.getMsg(Env.getCtx(), "Grade"));
		lSubject.setText(Msg.getMsg(Env.getCtx(), "Subject"));

		String whereClause = "";
		//Teacher

		if(currentTeacher!=null)
			whereClause = "AND C_BPartner_ID = " + currentTeacher.get_ID();
		else
			whereClause = " AND C_BPartner_ID in ( " +
					" SELECT ta.C_BPartner_ID FROM CA_Role_OthersAccess ra " +
					" Inner Join Ca_Teacherassignment Ta On Ta.Isactive='Y' " +
					" Inner Join Ca_Matterassignment Ma On Ma.Ca_Matterassignment_Id = Ta.Ca_Matterassignment_Id And Ma.Isactive='Y' " +
					" Inner Join Ca_Groupassignment Ga On Ga.Ca_Groupassignment_Id = Ma.Ca_Groupassignment_Id " +
					" inner join CA_CourseDef cd on cd.ca_coursedef_id = ga.ca_coursedef_id and cd.section = ra.section and cd.modality = ra.modality " +
					" WHERE ra.AD_Role_ID =  " + Env.getContextAsInt(m_ctx, 0,"#AD_Role_ID") + " ) " ;

		int teacherColumn = MColumn.getColumn_ID("CA_TeacherAssignment", "Course_BPartner_ID");

		MLookupInfo info = MLookupFactory.getLookupInfo (Env.getCtx(), form.getWindowNo(), teacherColumn, DisplayType.Table);
		MLookup lookup = new MLookup(info,0);
		String sql = info.Query.substring(0, info.Query.indexOf(" ORDER BY"));
		sql = sql + whereClause;
		info.Query = sql;


		fTeacher = new WTableDirEditor("C_BPartner_ID", true, false, true, lookup);
		fTeacher.addValueChangeListener(this);
		if(currentTeacher!=null)
			fTeacher.setValue(currentTeacher.get_ID());

		//Tipo de Examen


		whereClause = "AND CA_SchoolYear_ID =" + schoolYear.get_ID() +
				(currentTeacher!=null? " AND (CA_ExamType_ID IN (SELECT CA_ExamType_ID FROM CA_ExamAssignment WHERE C_BPartner_ID=" + currentTeacher.get_ID() + " AND " +
						" CA_SchoolYear_ID=" + schoolYear.get_ID() + " ) OR Value='RE')" : "" );

		int examColumn = MColumn.getColumn_ID("CA_ExamType", "CA_ExamType_ID");
		fExamType = new WTableDirEditor("CA_ExamType_ID", true, false, true, AcademicUtil.buildLookup(examColumn, whereClause, form.getWindowNo()));
		fExamType.addValueChangeListener(this);
		if(currentExamType!=null)
			fExamType.setValue(currentExamType.get_ID());

		//Group
		if(currentTeacher!=null)
			fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),currentTeacher.get_ID(), isElective.isSelected()));
		else
			fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),0, isElective.isSelected()));
		fCourseDef.addValueChangeListener(this);

		//Subject when recovery
		fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, AcademicUtil.getMatterAssignmentLookup(form.getWindowNo(),0));
		fMatterAssignment.addValueChangeListener(this);

		whereClause = " AND AD_Ref_List.IsActive='Y'";
		lookup = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, MColumn.getColumn_ID("CA_CourseDef", "Modality"), DisplayType.List);
		fModality = new WTableDirEditor("Modality", true, false, true, lookup);
		fModality.addValueChangeListener(this);

		whereClause = "AND AD_Ref_List.IsActive='Y'";
		lookup = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), 0, MColumn.getColumn_ID("CA_CourseDef", "Grade"), DisplayType.List);
		fGrade = new WTableDirEditor("Grade", true,false,true,lookup);
		fGrade.addValueChangeListener(this);

		whereClause = " AND 1=0";
		int subjectColumn = MColumn.getColumn_ID("CA_SubjectMatter", "CA_SubjectMatter_ID");
		fSubjectMatter = new WTableDirEditor("CA_SubjectMatter_ID", true, false, true, AcademicUtil.buildLookup(subjectColumn, whereClause, form.getWindowNo()));
		fSubjectMatter.addValueChangeListener(this);

		isElective.addActionListener(this);

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
			}
		}
		else if("CA_ExamType_ID".equals(name))
		{
			if(value==null)
			{
				currentExamType = null;
			}
			else
			{
				fExamType.setValue(value);
				currentExamType = new X_CA_ExamType(m_ctx, (Integer)value, null);
				if(currentTeacher!=null)
				{
					fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),currentTeacher.get_ID(), isElective.isSelected()));
					fCourseDef.addValueChangeListener(this);
					fCourseDef.setValue(null);
					currentCourse=null;


					String whereClause = " AND AD_Ref_List.IsActive='Y' " +
							(currentTeacher!=null? " AND (AD_Ref_List.Value IN (SELECT ea.Modality FROM CA_ExamAssignment ea WHERE ea.C_BPartner_ID=" + currentTeacher.get_ID() + " AND " +
									" ea.CA_SchoolYear_ID=" + schoolYear.get_ID() + " AND ea.IsActive='Y' ))" : "" );

					MLookupInfo info = MLookupFactory.getLookupInfo (Env.getCtx(), form.getWindowNo(), MColumn.getColumn_ID("CA_CourseDef", "Modality"), DisplayType.List);
					MLookup lookup = new MLookup(info,0);
					String sql = info.Query.substring(0, info.Query.indexOf(" ORDER BY"));
					sql = sql + whereClause;
					info.Query = sql;
					fModality = new WTableDirEditor("Modality", true, false, true, lookup);
					fModality.addValueChangeListener(this);

					whereClause = " AND AD_Ref_List.IsActive='Y' " +
							(currentTeacher!=null? " AND (AD_Ref_List.Value IN (SELECT ea.Grade FROM CA_ExamAssignment ea WHERE ea.C_BPartner_ID=" + currentTeacher.get_ID() + " AND " +
									" ea.CA_SchoolYear_ID=" + schoolYear.get_ID() + " AND ea.IsActive='Y'))" : "" );


					info = MLookupFactory.getLookupInfo (Env.getCtx(), form.getWindowNo(), MColumn.getColumn_ID("CA_CourseDef", "Grade"), DisplayType.List);
					lookup = new MLookup(info,0);
					sql = info.Query.substring(0, info.Query.indexOf(" ORDER BY"));
					sql = sql + whereClause;
					info.Query = sql;
					fGrade = new WTableDirEditor("Grade", true,false,true,lookup);
					fGrade.addValueChangeListener(this);
					currentModality=null;
					currentGrade=null;


				}
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
		else if("Modality".equals(name))
		{
			if(value==null)
			{
				currentModality=null;
			}
			else
			{
				fModality.setValue(value);
				currentModality = (String)value;
				currentGrade=null;
				fGrade.setValue(null);
			}
		}
		else if("Grade".equals(name))
		{
			if(value==null)
			{
				currentGrade=null;
			}
			else
			{
				fGrade.setValue(value);
				currentGrade=(String)value;


				String whereClause = " AND (CA_SubjectMatter_ID IN (SELECT CA_SubjectMatter_ID FROM CA_ExamAssignment WHERE C_BPartner_ID=" + currentTeacher.get_ID() + " AND " +
						" CA_SchoolYear_ID=" + schoolYear.get_ID() + " AND Modality='"+ currentModality+"' AND Grade='"+currentGrade+"'))";

				int subjectColumn = MColumn.getColumn_ID("CA_SubjectMatter", "CA_SubjectMatter_ID");
				fSubjectMatter = new WTableDirEditor("CA_SubjectMatter_ID", true, false, true, AcademicUtil.buildLookup(subjectColumn, whereClause, form.getWindowNo()));
				fSubjectMatter.addValueChangeListener(this);
				currentSubject=null;
			}

		}
		else if("CA_SubjectMatter_ID".equals(name))
		{
			if(value==null)
			{
				currentSubject=null;
			}
			else
			{
				fSubjectMatter.setValue(value);
				currentSubject = new X_CA_SubjectMatter(m_ctx, (Integer)value, null);
			}
		}


		repaintParameterPanel();
		refreshExamTable();
	}

	private void refreshExamTable() {

		examTable.setRowCount(0);

		if(currentExamType==null)
			return;

		if("RE".equals(currentExamType.getValue()))
		{
			if(currentTeacher==null || currentCourse==null || currentAssignment==null)
				return;
		}
		else
		{
			if(currentTeacher==null || currentModality==null || currentGrade==null || currentSubject==null)
				return;
		}

		Vector<String> columns = getColumns();

		Vector<Vector<Object>> data = getStudentData();

		ListModelTable modelP = new ListModelTable(data);

		modelP.addTableModelListener(this);
		((WListbox)examTable).setData(modelP, columns);

		((WListbox)examTable).setStyle("sizedByContent=true");

		if("RE".equals(currentExamType.getValue()))
			examTable.setColumnClass(0, Boolean.class, false);
		else
			examTable.setColumnClass(0, String.class, true);

		examTable.setColumnClass(1, String.class, true);
		examTable.setColumnClass(2, String.class, true);
		examTable.setColumnClass(3, BigDecimal.class, true);
		examTable.setColumnClass(4, BigDecimal.class, true);
		examTable.setColumnClass(5, BigDecimal.class, true);
		examTable.setColumnClass(6, BigDecimal.class, isReadOnly());
		examTable.setColumnClass(7, BigDecimal.class, true);






		examTable.autoSize();
		((WListbox)examTable).setWidth("100%");
		((WListbox)examTable).setHeight("100%");

	}

	private boolean isReadOnly() {

		String whereClause = X_CA_ExamType.COLUMNNAME_Value + "=? AND " + 
				X_CA_ExamType.COLUMNNAME_CA_SchoolYear_ID + "=?";

		X_CA_ExamType examType = new Query(m_ctx, X_CA_ExamType.Table_Name, whereClause, null)
		.setOnlyActiveRecords(true)
		.setParameters(currentExamType.getValue(), schoolYear.get_ID())
		.first();

		whereClause = X_CA_ExamTypeDate.COLUMNNAME_CA_ExamType_ID + "=? AND " +
				X_CA_ExamTypeDate.COLUMNNAME_Modality + "=? AND " +
				X_CA_ExamTypeDate.COLUMNNAME_Grade + "=? ";

		List<Object> parameters = new ArrayList<Object>();

		if("RE".equals(currentExamType.getValue()))
		{
			parameters.add(examType.get_ID());
			parameters.add(currentCourse.getModality());
			parameters.add(currentCourse.getGrade());
		}
		else
		{
			parameters.add(examType.get_ID());
			parameters.add(currentModality);
			parameters.add(currentGrade);
		}

		X_CA_ExamTypeDate examDate = new Query(m_ctx, X_CA_ExamTypeDate.Table_Name, whereClause, null)
		.setOnlyActiveRecords(true)
		.setParameters(parameters)
		.first();

		if(examDate==null)
			return true;



		if(examDate.getDateFrom().getTime()< System.currentTimeMillis() && 
				System.currentTimeMillis()< examDate.getDateTo().getTime() )
			return false;
		else
			return true;
	}

	@Override
	public void tableChanged(WTableModelEvent event) {
		if(event.getIndex0()>=0)
		{
			int column = event.getColumn();
			String studentvalue = (String) examTable.getValueAt(event.getIndex0(), (!"RE".equals(currentExamType.getValue())) ? 0:1);
			MBPartner student = new Query(Env.getCtx(), MBPartner.Table_Name, MBPartner.COLUMNNAME_Value+"=?", null)
			.setOnlyActiveRecords(true)
			.setParameters(studentvalue)
			.first();

			if(!"RE".equals(currentExamType.getValue()))
			{
				overrideMatterAssignment(student);
			}

			if(column==0) //enable/disable exam
			{
				boolean isenabled = (Boolean)examTable.getValueAt(event.getIndex0(), 0);
				if(!isenabled)
				{
					tryDeleteExam(student);
					examTable.setValueAt(BigDecimal.ZERO, event.getIndex0(), 6+inccolumn);
					calculateAnuals(student, event.getIndex0());
				}
			}
			else if(column==(6+inccolumn)) //Exam Grade
			{
				BigDecimal newValue = (BigDecimal)examTable.getValueAt(event.getIndex0(), 6+inccolumn);
				if(student!=null)
				{
					if(newValue.compareTo(new BigDecimal(0))<0 || newValue.compareTo(new BigDecimal(AcademicUtil.getCurrentYearConfig(m_ctx).getNoteScale()))>0)
					{
						newValue = BigDecimal.ZERO;
						examTable.setValueAt(newValue, event.getIndex0(), 6+inccolumn);

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

		if (event.getTarget().equals(isElective))
		{
			if(currentTeacher!=null)
			{
				fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),currentTeacher.get_ID(), isElective.isSelected()));
				fCourseDef.addValueChangeListener(this);
				repaintParameterPanel();
			}
		}

	}

	@Override
	public ADForm getForm() {
		return form;
	}


}
