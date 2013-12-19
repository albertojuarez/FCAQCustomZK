package org.eevolution.form;

import java.util.List;
import java.util.Vector;

import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.MBPartner;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.fcaq.components.IClassRoom;
import org.fcaq.model.X_CA_CourseDef;
import org.fcaq.model.X_CA_MatterAssignment;
import org.fcaq.model.X_CA_PeriodClass;
import org.fcaq.model.X_CA_Schedule;
import org.fcaq.model.X_CA_ScheduleClass;
import org.fcaq.model.X_CA_ScheduleDay;
import org.fcaq.model.X_CA_SchedulePeriod;
import org.fcaq.model.X_CA_SchoolYear;
import org.fcaq.model.X_CA_SubjectMatter;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

public class ClassSubstitution extends Panel implements IClassRoom, EventListener, ValueChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8740364033727959674L;

	private Grid fieldGrid = GridFactory.newGridLayout();

	Label lSubject = new Label("");
	Label lGroup = new Label("");
	
	private Checkbox isSubstitution = new Checkbox();

	private int dayNo = 0;
	private String periodNo = "";
	private X_CA_SchedulePeriod period=null;
	private X_CA_MatterAssignment matterAssignment = null;
	
	private int windowNo = 0; 

	MBPartner currentTeacher = null;
	X_CA_SchoolYear schoolYear = null;

	public ClassSubstitution(String style, boolean editablemode) {
		
		super();
		
		this.setStyle(style);
		
		this.appendChild(fieldGrid);
		fieldGrid.setStyle("border:none;");
		
		lSubject.setStyle("text-align: center;");
		lGroup.setStyle("text-align: center;");
		
		Rows rows = fieldGrid.newRows();
		
		Row row = rows.newRow();
		row.setStyle(style + " border:none; text-align: center;");
		row.appendChild(lSubject);
		
		row = rows.newRow();
		row.setStyle(style + " border:none; text-align: center;");
		row.appendChild(lGroup);
		
		isSubstitution.setText(Msg.getElement(Env.getCtx(), "IsSubstitution"));
		
		row = rows.newRow();
		row.setStyle(style + " border:none; text-align: center;");
		row.appendChild(isSubstitution);
		
		isSubstitution.addActionListener(this);
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEvent(Event event) throws Exception {
		
		if(event.getTarget().equals(isSubstitution)) {
			
			if (isSubstitution.isSelected())
				createSchedule();
			else {
				
				deleteCurrentPeriod(FDialog.ask(windowNo, null, 
						Msg.translate(Env.getAD_Language(Env.getCtx()), "DeleteRecord?")));
			}
		}
	}

	public void setWindowNo(int windowNo) {
		
		this.windowNo = windowNo;
	}

	@Override
	public void setDayno(int dayNo) {
		
		this.dayNo = dayNo;
	}

	@Override
	public void setPeriodno(String periodNo) {
		
		this.periodNo = periodNo;
	}

	@Override
	public void setSchoolYear(X_CA_SchoolYear schoolYear) {
		
		this.schoolYear = schoolYear;
	}

	@Override
	public void setTeacher(MBPartner teacher) {
		
		this.currentTeacher = teacher;
	}

	@Override
	public void setPeriod(Vector<Object> periodLine) {
		
		this.period = (X_CA_SchedulePeriod) periodLine.get(0);
		this.matterAssignment = (X_CA_MatterAssignment) periodLine.get(1);
		repaintSchedulePeriod();
	}

	@Override
	public String getPeriodno() {
		
		return periodNo;
	}

	@Override
	public int getDayno() {
		
		return dayNo;
	}

	public MBPartner getTeacher()
	{
		return currentTeacher;
	}

	private void repaintSchedulePeriod()
	{
		if (period != null) {
			
			if (!period.isSubstitution())
				
				isSubstitution.setVisible(false);
			else
				
				isSubstitution.setSelected(true);
		}
		
		if (matterAssignment != null) {
			
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
			
			lGroup.setText((X_CA_CourseDef)matterAssignment.getCA_GroupAssignment().getCA_CourseDef()!=null 
					? matterAssignment.getCA_GroupAssignment().getCA_CourseDef().getGrade() + " - " 
					+ (matterAssignment.getCA_GroupAssignment().getCA_CourseDef().getParallel()!=null
						? matterAssignment.getCA_GroupAssignment().getCA_CourseDef().getParallel() 
						: (matterAssignment.getCA_GroupAssignment().getCA_CourseDef().getName()!=null
						? matterAssignment.getCA_GroupAssignment().getCA_CourseDef().getName() : "")) : "");
		}
		else
			lSubject.setText("");
			lGroup.setText("");
	}

	private void createSchedule() {
		
		boolean success = createTeacherSchedule();
		if(!success)
		{
			FDialog.error(0, "Can't create standar Schedule");
			return;
		}
	}
	
	private boolean createTeacherSchedule() {
		
		if(currentTeacher==null)
			return false;
		
		if (schoolYear == null)
			return false;
		
		String whereClause = X_CA_Schedule.COLUMNNAME_C_BPartner_ID + "=? AND " +
				X_CA_Schedule.COLUMNNAME_CA_SchoolYear_ID + "=?";
		
		X_CA_Schedule schedule = new Query(Env.getCtx(), X_CA_Schedule.Table_Name, whereClause, null)
			.setOnlyActiveRecords(true)
			.setParameters(currentTeacher.get_ID(), schoolYear.get_ID())
			.firstOnly();
		
		if(schedule==null) {
			
			schedule = new X_CA_Schedule(Env.getCtx(), 0, null);
			
			schedule.setC_BPartner_ID(currentTeacher.get_ID());
			schedule.setCA_SchoolYear_ID(schoolYear.get_ID());
			schedule.save();
		}
		
		whereClause = X_CA_ScheduleDay.COLUMNNAME_CA_Schedule_ID + "=? AND " + 
				X_CA_ScheduleDay.COLUMNNAME_DayNo + "=?";
		
		X_CA_ScheduleDay day = new Query(Env.getCtx(), X_CA_ScheduleDay.Table_Name, whereClause, null)
			.setOnlyActiveRecords(true)
			.setParameters(schedule.get_ID(), dayNo)
			.firstOnly();
		
		if(day==null) {
			
			day = new X_CA_ScheduleDay(Env.getCtx(), 0, null);
			
			day.setCA_Schedule_ID(schedule.get_ID());
			day.setDayNo(dayNo);
			day.saveEx();
		}
		
		whereClause = "EXISTS (SELECT 1 FROM " + X_CA_ScheduleClass.Table_Name + 
				" WHERE " + X_CA_ScheduleClass.COLUMNNAME_IsActive + 
				"=? AND " + X_CA_ScheduleClass.COLUMNNAME_ActivityType + 
				" IS NULL AND " + X_CA_ScheduleClass.COLUMNNAME_Section +
				" =? AND " + X_CA_ScheduleClass.COLUMNNAME_CA_ScheduleClass_ID +
				"=" + X_CA_PeriodClass.Table_Name + "." + X_CA_PeriodClass.COLUMNNAME_CA_ScheduleClass_ID + 
				") AND " + X_CA_PeriodClass.COLUMNNAME_Name + "=?";
		
		X_CA_PeriodClass period = new Query(Env.getCtx(), X_CA_PeriodClass.Table_Name, whereClause, null)
			.setParameters(true, X_CA_CourseDef.SECTION_Secundaria, periodNo)
			.setOnlyActiveRecords(true)
			.first();
		
		whereClause = X_CA_SchedulePeriod.COLUMNNAME_CA_ScheduleDay_ID + 
				"=? AND " + X_CA_SchedulePeriod.COLUMNNAME_CA_PeriodClass_ID + 
				"=? AND " + X_CA_SchedulePeriod.COLUMNNAME_IsSubstitution + 
				"=?";
		
		X_CA_SchedulePeriod schPeriod = new Query(Env.getCtx(), X_CA_SchedulePeriod.Table_Name, whereClause, null)
			.setParameters(day.get_ID(), period.get_ID(), true)
			.setOnlyActiveRecords(true)
			.firstOnly();
		
		if(schPeriod==null) {
			
			schPeriod = new X_CA_SchedulePeriod(Env.getCtx(), 0,null);
			
			schPeriod.setCA_ScheduleDay_ID(day.get_ID());
			schPeriod.setCA_PeriodClass_ID(period.get_ID());
			schPeriod.setIsSubstitution(true);
			schPeriod.saveEx();
		}
		
		Vector<Object> periodLine = new Vector<Object>();
		
		periodLine.add(schPeriod);
		periodLine.add(null);
		
		this.setPeriod(periodLine);
		
		return true;
	}

	private void deleteCurrentPeriod(boolean delete) {
		
		if (!delete) {
			
			isSubstitution.setSelected(true);
			return;
		}
		
		if(period!=null)
		{
			try {
				
				X_CA_Schedule schedule = new Query(Env.getCtx(), X_CA_Schedule.Table_Name, 
						X_CA_Schedule.COLUMNNAME_C_BPartner_ID + "=? AND " + X_CA_Schedule.COLUMNNAME_CA_SchoolYear_ID + "=?", null)
					.setOnlyActiveRecords(true)
					.setParameters(currentTeacher.get_ID(), schoolYear != null ? schoolYear.get_ID() : 0)
					.first();
				
				X_CA_ScheduleDay day = new Query(Env.getCtx(), X_CA_ScheduleDay.Table_Name, 
						X_CA_ScheduleDay.COLUMNNAME_DayNo + "=? AND " + X_CA_ScheduleDay.COLUMNNAME_CA_Schedule_ID + "=?", null)
					.setOnlyActiveRecords(true)
					.setParameters(dayNo, schedule.getCA_Schedule_ID())
					.first();
				
				List<X_CA_SchedulePeriod> tmpperiods = new Query(Env.getCtx(), X_CA_SchedulePeriod.Table_Name, 
						X_CA_SchedulePeriod.COLUMNNAME_CA_ScheduleDay_ID + "=? AND " +
						X_CA_SchedulePeriod.COLUMNNAME_CA_PeriodClass_ID + "=? AND " +
						X_CA_SchedulePeriod.COLUMNNAME_IsSubstitution + "=?", null)
					.setOnlyActiveRecords(true)
					.setParameters(day.get_ID(), period.getCA_PeriodClass_ID(), true)
					.list();
				
				for(X_CA_SchedulePeriod tmpperiod : tmpperiods)
				{
					tmpperiod.deleteEx(true);
				}
				
				period = null;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
