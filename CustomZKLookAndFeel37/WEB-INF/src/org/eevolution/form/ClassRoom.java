package org.eevolution.form;

import java.util.List;
import java.util.Vector;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.Lookup;
import org.compiere.model.MBPartner;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.fcaq.components.IClassRoom;
import org.fcaq.model.X_CA_CourseDef;
import org.fcaq.model.X_CA_ElectivePeriod;
import org.fcaq.model.X_CA_GroupAssignment;
import org.fcaq.model.X_CA_MatterAssignment;
import org.fcaq.model.X_CA_PeriodClass;
import org.fcaq.model.X_CA_Schedule;
import org.fcaq.model.X_CA_ScheduleClass;
import org.fcaq.model.X_CA_ScheduleDay;
import org.fcaq.model.X_CA_SchedulePeriod;
import org.fcaq.model.X_CA_SchoolYear;
import org.fcaq.model.X_CA_SubjectMatter;
import org.fcaq.model.X_CA_TeacherAssignment;
import org.fcaq.process.ProcessJustification;
import org.fcaq.util.AcademicUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.North;

public class ClassRoom extends Panel implements IClassRoom, EventListener, ValueChangeListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1802967505996912308L;

	private Grid fieldGrid = GridFactory.newGridLayout();
	
	Label lSubject = new Label("");
	Label lTeacher = new Label("");
	Label lGroup = new Label("");
	
	private Button edit = new Button("Edit");
	Checkbox isElective = new Checkbox();
	
	private int dayno = 0;
	private String periodno = "";
	private X_CA_SchedulePeriod period=null;
	private X_CA_MatterAssignment matterAssignment = null;
	
	public boolean iseditablemode = false;
	
	MBPartner currentBPartner = null;
	X_CA_SchoolYear schoolYear = null;
	
	Button bsave = new Button("Save");
	Button bdelete = new Button("Delete");
	
	@Override
	public void setTeacher(MBPartner teacher)
	{
		this.currentBPartner = teacher;
		fGroup = new WTableDirEditor("CA_CourseDef_ID", true, false, true, getCourseLookup(0, currentBPartner, isElective.isSelected(), schoolYear)); 
		fGroup.addValueChangeListener(this);
		fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, getMatterAssignmentLookup(0, currentBPartner, 0));
	}

	private Lookup getMatterAssignmentLookup(int windowNo, MBPartner teacher, int CA_CourseDef_ID) 
	{
		int COLUMN_MatterAssignment_ID = 1000984;
		
		StringBuilder whereClause = new StringBuilder();
		
		whereClause.append(" AND EXISTS (SELECT 1 FROM ").append(X_CA_GroupAssignment.Table_Name)
			.append(" ga WHERE ga.").append(X_CA_GroupAssignment.COLUMNNAME_IsActive)
			.append("='Y' AND ga.").append(X_CA_GroupAssignment.COLUMNNAME_CA_CourseDef_ID)
			.append("=").append(CA_CourseDef_ID)
			.append(" AND ga.").append(X_CA_GroupAssignment.COLUMNNAME_CA_GroupAssignment_ID)
			.append("=").append(X_CA_MatterAssignment.Table_Name)
			.append(".").append(X_CA_MatterAssignment.COLUMNNAME_CA_GroupAssignment_ID)
			.append(") AND EXISTS (SELECT 1 FROM ").append(X_CA_TeacherAssignment.Table_Name)
			.append(" ta WHERE ta.").append(X_CA_TeacherAssignment.COLUMNNAME_IsActive)
			.append("='Y' AND ta.").append(X_CA_TeacherAssignment.COLUMNNAME_C_BPartner_ID)
			.append("=").append(teacher != null ? teacher.get_ID() : 0)
			.append(" AND ta.").append(X_CA_TeacherAssignment.COLUMNNAME_CA_MatterAssignment_ID)
			.append("=").append(X_CA_MatterAssignment.Table_Name)
			.append(".").append(X_CA_MatterAssignment.COLUMNNAME_CA_MatterAssignment_ID)
			.append(")");

		return AcademicUtil.buildLookup(COLUMN_MatterAssignment_ID, whereClause.toString(), windowNo);
	}

	private Lookup getCourseLookup(int windowNo, MBPartner teacher, boolean isElective, X_CA_SchoolYear schoolYear) 
	{
		int COLUMN_CourseDef_ID = 1000734;
		
		StringBuilder whereClause = new StringBuilder();
		
		whereClause.append(" AND EXISTS (SELECT 1 FROM ").append(X_CA_TeacherAssignment.Table_Name)
			.append(" ta INNER JOIN ").append(X_CA_MatterAssignment.Table_Name)
			.append(" ma ON ma.").append(X_CA_MatterAssignment.COLUMNNAME_CA_MatterAssignment_ID)
			.append("=ta.").append(X_CA_TeacherAssignment.COLUMNNAME_CA_MatterAssignment_ID)
			.append(" INNER JOIN ").append(X_CA_GroupAssignment.Table_Name)
			.append(" ga ON ga.").append(X_CA_GroupAssignment.COLUMNNAME_CA_GroupAssignment_ID)
			.append("=ma.").append(X_CA_MatterAssignment.COLUMNNAME_CA_GroupAssignment_ID)
			.append(" WHERE ta.").append(X_CA_TeacherAssignment.COLUMNNAME_IsActive)
			.append("='Y' AND ma.").append(X_CA_MatterAssignment.COLUMNNAME_IsActive)
			.append("='Y' AND ga.").append(X_CA_GroupAssignment.COLUMNNAME_IsActive)
			.append("='Y' AND ta.").append(X_CA_TeacherAssignment.COLUMNNAME_C_BPartner_ID)
			.append("=").append(teacher != null ? teacher.get_ID() : 0)
			.append(" AND ga.").append(X_CA_GroupAssignment.COLUMNNAME_CA_CourseDef_ID)
			.append("=").append(X_CA_CourseDef.Table_Name)
			.append(".").append(X_CA_CourseDef.COLUMNNAME_CA_CourseDef_ID)
			.append(") AND ").append(X_CA_CourseDef.COLUMNNAME_IsElective)
			.append("=").append(isElective ? "'Y'" : "'N'")
			.append(" AND ").append(X_CA_CourseDef.COLUMNNAME_CA_SchoolYear_ID)
			.append("=").append(schoolYear != null ? schoolYear.get_ID() : 0);

		return AcademicUtil.buildLookup(COLUMN_CourseDef_ID, whereClause.toString(), windowNo);
	}

	public MBPartner getTeacher()
	{
		return currentBPartner;
	}

	@Override
	public void setSchoolYear(X_CA_SchoolYear schoolYear2)
	{
		this.schoolYear = schoolYear2;
	}

	public ClassRoom(String style, boolean editablemode)
	{
		super();

		isElective.setLabel("Es electivo");
		isElective.addActionListener(this);
		bsave.addActionListener(this);
		bdelete.addActionListener(this);

		this.iseditablemode = editablemode;
		this.setStyle(style);

		this.appendChild(fieldGrid);
		fieldGrid.setStyle("border:none;");

		lSubject.setStyle("text-align: center;");
		lTeacher.setStyle("text-align: center;");
		lGroup.setStyle("text-align: center;");

		Rows rows = fieldGrid.newRows();

		Row row = rows.newRow();
		row.setStyle(style + " border:none; text-align: center;");
		row.appendChild(lSubject);

		row = rows.newRow();
		row.setStyle(style + " border:none; text-align: center;");
		row.appendChild(lTeacher);

		row = rows.newRow();
		row.setStyle(style + " border:none; text-align: center;");
		row.appendChild(lGroup);

		if(iseditablemode)
		{
			row = rows.newRow();
			row.setStyle(style + " border:none; text-align: center;");
			row.appendChild(edit);
		}

		edit.addActionListener(this);

	}

	@Override
	public void setDayno(int dayno)
	{
		this.dayno = dayno;
	}

	@Override
	public void setPeriodno(String periodno)
	{
		this.periodno = periodno;
	}

	@Override
	public int getDayno()
	{
		return dayno;
	}

	@Override
	public String getPeriodno()
	{
		return periodno;
	}

	public X_CA_SchedulePeriod getPeriod()
	{
		return period;
	}

	@Override
	public void setPeriod(Vector<Object> periodLine)
	{
		this.period = (X_CA_SchedulePeriod) periodLine.get(0);
		this.matterAssignment = (X_CA_MatterAssignment) periodLine.get(1);
		repaintSchedulePeriod();
	}

	private void repaintSchedulePeriod()
	{
		if (matterAssignment != null)
			if (matterAssignment.getElectiveSubject_ID() > 0)
				lSubject.setText(matterAssignment.getCA_SubjectMatter().getName() +
						" " + matterAssignment.getElectiveSubject().getName() + 
						(matterAssignment.isAttendance() ? "" : "*"));
			else if (matterAssignment.getCA_SubjectMatter_ID() > 0) {
				
				if (period.getCA_SubjectMatter_ID() > 0) {
					
					X_CA_SubjectMatter subjectMatter = new X_CA_SubjectMatter(Env.getCtx(), period.getCA_SubjectMatter_ID(), null);
					
					lSubject.setText(subjectMatter.getName() 
							+ (matterAssignment.isAttendance() ? "" : "*"));
				}
				else {
					lSubject.setText(matterAssignment.getCA_SubjectMatter().getName() 
							+ (matterAssignment.isAttendance() ? "" : "*"));
				}
			}
			else
				lSubject.setText("");
		else
			lSubject.setText("");
		
		if (this.getTeacher().isStudent()) {
			
			int C_BPartner_ID = ProcessJustification.getBPartnerTeacher(Env.getCtx(), matterAssignment, null);
			
			if (C_BPartner_ID > 0) {
				MBPartner teacher = new MBPartner(Env.getCtx(), C_BPartner_ID, null);
				lTeacher.setText(teacher.getName());
			}
			else
				lTeacher.setText("");
			
			lGroup.setText("");
		}
		else {
			
			lTeacher.setText("");
			lGroup.setText((X_CA_CourseDef)matterAssignment.getCA_GroupAssignment().getCA_CourseDef()!=null 
					? matterAssignment.getCA_GroupAssignment().getCA_CourseDef().getGrade() + " - " 
					+ (matterAssignment.getCA_GroupAssignment().getCA_CourseDef().getParallel()!=null
						? matterAssignment.getCA_GroupAssignment().getCA_CourseDef().getParallel() 
						: (matterAssignment.getCA_GroupAssignment().getCA_CourseDef().getName()!=null
						? matterAssignment.getCA_GroupAssignment().getCA_CourseDef().getName() : "")) : "");
		}
	}


	Window editWindow = new Window();

	WTableDirEditor fGroup = null;
	WTableDirEditor fMatterAssignment = null; 
	North northC = new North();
	Borderlayout mainCLayout = new Borderlayout();

	@Override
	public void onEvent(Event event) throws Exception 
	{
		if(event.getTarget().equals(edit))
		{
			editWindow = new Window();
			showEditWindow();
			AEnv.showCenterScreen(editWindow);
		}
		else if(event.getTarget().equals(isElective))
		{
			fGroup = new WTableDirEditor("CA_CourseDef_ID", true, false, true, getCourseLookup(0, currentBPartner, isElective.isSelected(), schoolYear));
			fGroup.addValueChangeListener(this);

			showEditWindow();
		}
		else if(event.getTarget().equals(bsave))
		{
			createSchedule();
			editWindow.dispose();
		}
		else if(event.getTarget().equals(bdelete))
		{
			deleteCurrentPeriod();
			lSubject.setText("");
			lGroup.setText("");
			lTeacher.setText("");
			editWindow.dispose();
		}
	}

	private void deleteCurrentPeriod() 
	{
		if(period!=null)
		{
			try {
				
				X_CA_Schedule schedule = new Query(Env.getCtx(), X_CA_Schedule.Table_Name, X_CA_Schedule.COLUMNNAME_C_BPartner_ID + "=? AND " + X_CA_Schedule.COLUMNNAME_CA_SchoolYear_ID + "=?", null)
				.setOnlyActiveRecords(true).setParameters(currentBPartner.get_ID(), schoolYear != null ? schoolYear.get_ID() : 0).first();
				
				X_CA_ScheduleDay day = new Query(Env.getCtx(), X_CA_ScheduleDay.Table_Name, X_CA_ScheduleDay.COLUMNNAME_DayNo + "=? AND " + X_CA_ScheduleDay.COLUMNNAME_CA_Schedule_ID + "=?", null)
				.setOnlyActiveRecords(true).setParameters(dayno, schedule.getCA_Schedule_ID()).first();
				
				List<X_CA_SchedulePeriod> tmpperiods = new Query(Env.getCtx(), X_CA_SchedulePeriod.Table_Name, X_CA_SchedulePeriod.COLUMNNAME_CA_ScheduleDay_ID+"=? AND " +
						X_CA_SchedulePeriod.COLUMNNAME_CA_CourseDef_ID+"=? AND " + 
						X_CA_SchedulePeriod.COLUMNNAME_CA_MatterAssignment_ID + "=? AND " + 
						X_CA_SchedulePeriod.COLUMNNAME_CA_PeriodClass_ID + "=?", null)
				.setOnlyActiveRecords(true)
				.setParameters(day.get_ID(), period.getCA_CourseDef_ID(), period.getCA_MatterAssignment_ID(), period.getCA_PeriodClass_ID())
				.list();
				
				for(X_CA_SchedulePeriod tmpperiod : tmpperiods)
				{
					tmpperiod.deleteEx(true);
				}
				
				//period.deleteEx(true);
				period = null;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void showEditWindow()
	{
		Panel parameterCPanel = new Panel();
		Grid parameterCLayout = GridFactory.newGridLayout();

		parameterCLayout = buildParameterLayout();
		parameterCPanel.appendChild(parameterCLayout);

		mainCLayout.removeChild(northC);

		northC = new North();
		northC.setStyle("border: none");
		mainCLayout.appendChild(northC);
		northC.appendChild(parameterCPanel);

		editWindow.setSizable(false);
		editWindow.setWidth("250px");
		editWindow.setHeight("170px");
		editWindow.setShadow(true);
		editWindow.setBorder("normal");
		editWindow.setClosable(true);
		editWindow.setTitle(Msg.translate(Env.getCtx(),"Comments"));
		editWindow.setContentStyle("overflow: auto");
		editWindow.appendChild(mainCLayout);
	}

	private Grid buildParameterLayout()
	{
		Grid parameter = GridFactory.newGridLayout();

		Rows rows = null;
		Row row = null;
		parameter.setWidth("99%");
		rows = parameter.newRows();

		row = rows.newRow();
		row.appendChild(isElective);
		row = rows.newRow();
		row.appendChild(fGroup.getComponent());
		row = rows.newRow();
		row.appendChild(fMatterAssignment.getComponent());
		fMatterAssignment.actionRefresh();

		row = rows.newRow();
		row.appendChild(bsave);
		row = rows.newRow();
		row.appendChild(bdelete);

		return parameter;
	}

	@Override
	public void valueChange(ValueChangeEvent evt) 
	{
		String name = evt.getPropertyName();
		Object value = evt.getNewValue();

		if (value == null)
			return;

		if ("CA_CourseDef_ID".equals(name))
		{
			fGroup.setValue(value);
			fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, getMatterAssignmentLookup(0,currentBPartner, (Integer)fGroup.getValue()));
			fMatterAssignment.addValueChangeListener(this);

			showEditWindow();
		}
	}



	private void createSchedule() {
		
		deleteCurrentPeriod();
		
		boolean success = createTeacherSchedule();
		if(!success)
		{
			FDialog.error(0, "Can't create standar Schedule");
			return;
		}
	}
	
	private boolean createTeacherSchedule() {
		
		if(currentBPartner==null)
			return false;
		
		// Find or create schedule
		if (schoolYear == null)
			return false;
		
		String whereClause = X_CA_Schedule.COLUMNNAME_C_BPartner_ID + "=? AND " +
				X_CA_Schedule.COLUMNNAME_CA_SchoolYear_ID + "=?";
		
		X_CA_Schedule schedule = new Query(Env.getCtx(), X_CA_Schedule.Table_Name, whereClause, null)
			.setOnlyActiveRecords(true)
			.setParameters(currentBPartner.get_ID(), schoolYear.get_ID())
			.firstOnly();
		
		if(schedule==null)
		{
			schedule = new X_CA_Schedule(Env.getCtx(), 0, null);
		}
		
		schedule.setC_BPartner_ID(currentBPartner.get_ID());
		schedule.setCA_SchoolYear_ID(schoolYear.get_ID());
		schedule.save();
		
		// Find or create Day
		whereClause = X_CA_ScheduleDay.COLUMNNAME_CA_Schedule_ID + "=? AND " + X_CA_ScheduleDay.COLUMNNAME_DayNo + "=?";
		X_CA_ScheduleDay day = new Query(Env.getCtx(), X_CA_ScheduleDay.Table_Name, whereClause, null)
			.setOnlyActiveRecords(true)
			.setParameters(schedule.get_ID(), dayno)
			.firstOnly();
		
		if(day==null)
		{
			day = new X_CA_ScheduleDay(Env.getCtx(), 0, null);
		}
		
		day.setCA_Schedule_ID(schedule.get_ID());
		day.setDayNo(dayno);
		day.saveEx();
		
		//Find or create period
		X_CA_CourseDef group = new X_CA_CourseDef(Env.getCtx(),(Integer) fGroup.getValue(), null);
		
		if(group.getSection().equals(X_CA_CourseDef.SECTION_Secundaria)) // Secundaria
		{
			whereClause = "EXISTS (SELECT 1 FROM " + X_CA_ScheduleClass.Table_Name + 
					" WHERE " + X_CA_ScheduleClass.COLUMNNAME_IsActive + 
					"=? AND " + X_CA_ScheduleClass.COLUMNNAME_ActivityType + 
					" IS NULL AND " + X_CA_ScheduleClass.COLUMNNAME_Section +
					" =? AND " + X_CA_ScheduleClass.COLUMNNAME_CA_ScheduleClass_ID +
					"=" + X_CA_PeriodClass.Table_Name + "." + X_CA_PeriodClass.COLUMNNAME_CA_ScheduleClass_ID + 
					") AND " + X_CA_PeriodClass.COLUMNNAME_Name + "=?";
			
			X_CA_PeriodClass period = new Query(Env.getCtx(), X_CA_PeriodClass.Table_Name, whereClause, null)
			.setParameters(true, X_CA_CourseDef.SECTION_Secundaria, periodno)
			.setOnlyActiveRecords(true)
			.first();
			
			X_CA_MatterAssignment assignment = new X_CA_MatterAssignment(Env.getCtx(), (Integer)fMatterAssignment.getValue(), null);
			
			whereClause = X_CA_SchedulePeriod.COLUMNNAME_CA_ScheduleDay_ID + 
					"=? AND " + X_CA_SchedulePeriod.COLUMNNAME_CA_PeriodClass_ID + 
					"=? AND " + X_CA_SchedulePeriod.COLUMNNAME_CA_CourseDef_ID + 
					"=? AND " + X_CA_SchedulePeriod.COLUMNNAME_CA_MatterAssignment_ID + 
					"=?";
			
			X_CA_SchedulePeriod schPeriod = new Query(Env.getCtx(), X_CA_SchedulePeriod.Table_Name, whereClause, null)
				.setParameters(day.get_ID(), period.get_ID(), group.get_ID(), assignment.get_ID())
				.setOnlyActiveRecords(true)
				.firstOnly();
			
			if(schPeriod==null)
			{
				schPeriod = new X_CA_SchedulePeriod(Env.getCtx(), 0,null);
			}
			
			schPeriod.setCA_ScheduleDay_ID(day.get_ID()); // dia
			schPeriod.setCA_CourseDef_ID(group.getCA_CourseDef_ID()); // curso
			schPeriod.setCA_PeriodClass_ID(period.get_ID());
			schPeriod.setCA_MatterAssignment_ID(assignment.get_ID());
			
			if(assignment.getCA_GroupAssignment().getCA_CourseDef().isElective())
			{
				schPeriod.setCA_SubjectMatter_ID(assignment.getElectiveSubject_ID());
				schPeriod.setElectiveSubject_ID(assignment.getCA_SubjectMatter_ID());
			}
			else
				schPeriod.setCA_SubjectMatter_ID(assignment.getCA_SubjectMatter_ID());
			
			schPeriod.saveEx();
			
			if (assignment.getCA_GroupAssignment().getCA_CourseDef().isElective())
				setElectivePeriod(assignment, day, period);
			
			Vector<Object> periodLine = new Vector<Object>();
			
			periodLine.add(schPeriod);
			periodLine.add(assignment);
			
			this.setPeriod(periodLine);
			
			return true;
		}
		else // primaria && <
		{
			
		}
		
		return false;
	}

	private void setElectivePeriod(X_CA_MatterAssignment assignment, X_CA_ScheduleDay day, X_CA_PeriodClass period) {
		
		if(assignment !=null)
		{
			String whereClause = X_CA_ElectivePeriod.COLUMNNAME_CA_MatterAssignment_ID + "=? AND " + 
					X_CA_ElectivePeriod.COLUMNNAME_CA_PeriodClass_ID + "=? AND " + X_CA_ElectivePeriod.COLUMNNAME_DayNo +"=?" ;

			X_CA_ElectivePeriod electivePeriod = new Query(Env.getCtx(), X_CA_ElectivePeriod.Table_Name, whereClause, null)
			.setOnlyActiveRecords(true)
			.setParameters(assignment.get_ID(), period.getCA_PeriodClass_ID(), day.getDayNo())
			.first();

			if(electivePeriod==null)
			{
				electivePeriod = new X_CA_ElectivePeriod(Env.getCtx(), 0, null);
			}

			electivePeriod.setDayNo(day.getDayNo());
			electivePeriod.setCA_PeriodClass_ID(period.get_ID());
			electivePeriod.setCA_MatterAssignment_ID(assignment.get_ID());

			electivePeriod.saveEx();
		}
	}

	@Override
	public void setWindowNo(int windowdNo) {
		// TODO Auto-generated method stub
		
	}
}