package org.eevolution.form;

import java.util.List;

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
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.MBPartner;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.fcaq.model.X_CA_CourseDef;
import org.fcaq.model.X_CA_ElectivePeriod;
import org.fcaq.model.X_CA_GroupAssignment;
import org.fcaq.model.X_CA_MatterAssignment;
import org.fcaq.model.X_CA_TeacherAssignment;
import org.fcaq.model.X_CA_PeriodClass;
import org.fcaq.model.X_CA_Schedule;
import org.fcaq.model.X_CA_ScheduleDay;
import org.fcaq.model.X_CA_SchedulePeriod;
import org.fcaq.model.X_CA_SubjectMatter;
import org.fcaq.model.X_I_CA_Schedule;
import org.fcaq.util.AcademicUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.North;

public class ClassRoom extends Panel implements EventListener, ValueChangeListener{


	private Grid fieldGrid = GridFactory.newGridLayout();



	Label lSubject = new Label("");
	Label lTeacher = new Label("");
	Label lGroup = new Label("");

	private Button edit = new Button("Edit");
	Checkbox isElective = new Checkbox();


	private int dayno = 0;
	private String periodno = "";
	private X_CA_SchedulePeriod period=null;

	public boolean iseditablemode = false;

	MBPartner currentBPartner = null;

	Button bsave = new Button("Save");
	Button bdelete = new Button("Delete");


	public void setTeacher(MBPartner teacher)
	{
		this.currentBPartner = teacher;
		fGroup = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(0,currentBPartner.get_ID(), isElective.isSelected()));
		fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, AcademicUtil.getMatterAssignmentLookup(0,currentBPartner.get_ID(), 0));
	}
	public MBPartner getTeacher()
	{
		return currentBPartner;
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

	public void setDayno(int dayno)
	{
		this.dayno = dayno;
	}

	public void setPeriodno(String periodno)
	{
		this.periodno = periodno;
	}

	public int getDayno()
	{
		return dayno;
	}

	public String getPeriodno()
	{
		return periodno;
	}

	public X_CA_SchedulePeriod getPeriod()
	{
		return period;
	}

	public void setPeriod(X_CA_SchedulePeriod period)
	{
		this.period = period;
		repaintSchedulePeriod();
	}



	private void repaintSchedulePeriod()
	{
		lSubject.setText(period.getCA_SubjectMatter()!=null?period.getCA_SubjectMatter().getName():"");
		lTeacher.setText(period.getC_BPartner()!=null?period.getC_BPartner().getName():"");

		if(period.getCA_CourseDef_ID()>0)
		{
			lGroup.setText( period.getCA_CourseDef().getGrade() + " - " + period.getCA_CourseDef().getParallel());
		}
		else
		{
			lGroup.setText("");
		}

	}




	Window editWindow = new Window();

	WTableDirEditor fGroup = null;
	WTableDirEditor fMatterAssignment = null; 
	North northC = new North();
	Borderlayout mainCLayout = new Borderlayout();


	@Override
	public void onEvent(Event event) throws Exception {
		if(event.getTarget().equals(edit))
		{
			editWindow = new Window();
			showEditWindow();
			AEnv.showCenterScreen(editWindow);
		}
		else if(event.getTarget().equals(isElective))
		{

			fGroup = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(0,currentBPartner.get_ID(), isElective.isSelected()));
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

	private void deleteCurrentPeriod() {
		if(period!=null)
		{

			try{


				X_CA_Schedule schedule = new Query(Env.getCtx(), X_CA_Schedule.Table_Name, X_CA_Schedule.COLUMNNAME_CA_CourseDef_ID+ "=?", null)
				.setOnlyActiveRecords(true).setParameters(period.getCA_CourseDef_ID()).first();

				X_CA_ScheduleDay day = new Query(Env.getCtx(), X_CA_ScheduleDay.Table_Name, X_CA_ScheduleDay.COLUMNNAME_DayNo + "=? AND " + X_CA_ScheduleDay.COLUMNNAME_CA_Schedule_ID + "=?", null)
				.setOnlyActiveRecords(true).setParameters(dayno, schedule.getCA_Schedule_ID()).first();

				List<X_CA_SchedulePeriod> tmpperiods = new Query(Env.getCtx(), X_CA_SchedulePeriod.Table_Name, X_CA_SchedulePeriod.COLUMNNAME_CA_ScheduleDay_ID+"=? AND " +
						X_CA_SchedulePeriod.COLUMNNAME_C_BPartner_ID+"=? AND " + 
						X_CA_SchedulePeriod.COLUMNNAME_CA_SubjectMatter_ID + "=? AND "+
						X_CA_SchedulePeriod.COLUMNNAME_ElectiveSubject_ID + "=? AND " + 
						X_CA_SchedulePeriod.COLUMNNAME_CA_PeriodClass_ID + "=?", null)
				.setOnlyActiveRecords(true)
				.setParameters(day.get_ID(), currentBPartner.get_ID(), period.getCA_SubjectMatter_ID(), period.getElectiveSubject_ID(), period.getCA_PeriodClass_ID())
				.list();

				for(X_CA_SchedulePeriod tmpperiod : tmpperiods )
				{
					tmpperiod.deleteEx(true);
				}


				period.deleteEx(true);
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
	public void valueChange(ValueChangeEvent evt) {
		String name = evt.getPropertyName();
		Object value = evt.getNewValue();


		if (value == null)
			return;

		if ("CA_CourseDef_ID".equals(name))
		{
			fGroup.setValue(value);
			fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, AcademicUtil.getMatterAssignmentLookup(0,currentBPartner.get_ID(), (Integer)fGroup.getValue()));
			fMatterAssignment.addValueChangeListener(this);

			showEditWindow();
		}
	}



	private void createSchedule() {
		
		deleteCurrentPeriod();

		boolean success = createStandardSchedule();

		if(!success)
		{
			FDialog.error(0, "Can't create standar Schedule");
			return;
		}

		success = createTeacherSchedule();
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

		String whereClause = X_CA_Schedule.COLUMNNAME_C_BPartner_ID + "=? ";
		X_CA_Schedule schedule = new Query(Env.getCtx(), X_CA_Schedule.Table_Name, whereClause, null)
		.setOnlyActiveRecords(true)
		.setParameters(currentBPartner.get_ID())
		.firstOnly();

		if(schedule==null)
		{
			schedule = new X_CA_Schedule(Env.getCtx(), 0, null);
		}

		schedule.setC_BPartner_ID(currentBPartner.get_ID());
		schedule.save();

		// Find or create Day

		whereClause = X_CA_ScheduleDay.COLUMNNAME_CA_Schedule_ID + "=? AND " + X_CA_ScheduleDay.COLUMNNAME_DayNo + "=?";
		X_CA_ScheduleDay day = new Query(Env.getCtx(), X_CA_ScheduleDay.Table_Name, whereClause, null)
		.setOnlyActiveRecords(true)
		.setParameters(schedule.get_ID(),dayno)
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

		if(group.getSection().equals("05")) // Secundaria
		{
			whereClause = X_CA_PeriodClass.COLUMNNAME_Name + "=? AND " + X_CA_PeriodClass.COLUMNNAME_CA_ScheduleClass_ID + "=?"  ;

			X_CA_PeriodClass period = new Query(Env.getCtx(), X_CA_PeriodClass.Table_Name, whereClause, null)
			.setParameters(periodno, 1000003)
			.setOnlyActiveRecords(true)
			.first();

			whereClause = X_CA_SchedulePeriod.COLUMNNAME_CA_ScheduleDay_ID + "=? AND " + X_CA_SchedulePeriod.COLUMNNAME_CA_PeriodClass_ID + "=?";

			X_CA_SchedulePeriod schPeriod = new Query(Env.getCtx(), X_CA_SchedulePeriod.Table_Name, whereClause, null)
			.setParameters(day.get_ID(), period.get_ID())
			.setOnlyActiveRecords(true)
			.firstOnly();

			X_CA_MatterAssignment assignment = new X_CA_MatterAssignment(Env.getCtx(), (Integer)fMatterAssignment.getValue(), null);

			if(schPeriod==null)
			{
				schPeriod = new X_CA_SchedulePeriod(Env.getCtx(), 0,null);
			}

			schPeriod.setCA_ScheduleDay_ID(day.get_ID()); // dia
			schPeriod.setCA_CourseDef_ID(group.getCA_CourseDef_ID()); // curso


			schPeriod.setCA_PeriodClass_ID(period.get_ID());
			//schPeriod.setScheduleType(ischedule.get_ValueAsString("ScheduleType"));

			boolean iselective = assignment.getCA_SubjectMatter().isElective();

			if(iselective)
			{
				schPeriod.setCA_SubjectMatter_ID(assignment.getElectiveSubject_ID());
				schPeriod.set_ValueOfColumn("ElectiveSubject_ID", assignment.getCA_SubjectMatter_ID());
			}
			else
				schPeriod.setCA_SubjectMatter_ID(assignment.getCA_SubjectMatter_ID());

			schPeriod.saveEx();

			this.setPeriod(schPeriod);

			return true;

		}
		else // primaria && <
		{

		}


		return false;

	}



	private boolean createStandardSchedule() {


		X_CA_CourseDef course = new X_CA_CourseDef(Env.getCtx(), (Integer) fGroup.getValue(), null);

		if(course==null)
			return true;

		// Find or create schedule

		String whereClause = X_CA_Schedule.COLUMNNAME_CA_CourseDef_ID + "=? ";
		X_CA_Schedule schedule = new Query(Env.getCtx(), X_CA_Schedule.Table_Name, whereClause, null)
		.setOnlyActiveRecords(true)
		.setParameters(course.get_ID())
		.firstOnly();
		if(schedule==null)
		{
			schedule = new X_CA_Schedule(Env.getCtx(), 0, null);
		}
		schedule.setCA_CourseDef_ID(course.getCA_CourseDef_ID());
		schedule.save();

		// Find or create Day

		whereClause = X_CA_ScheduleDay.COLUMNNAME_CA_Schedule_ID + "=? AND " + X_CA_ScheduleDay.COLUMNNAME_DayNo + "=?";
		X_CA_ScheduleDay day = new Query(Env.getCtx(), X_CA_ScheduleDay.Table_Name, whereClause, null)
		.setOnlyActiveRecords(true)
		.setParameters(schedule.get_ID(),dayno)
		.firstOnly();
		if(day==null)
		{
			day = new X_CA_ScheduleDay(Env.getCtx(), 0, null);
		}

		day.setCA_Schedule_ID(schedule.get_ID());
		day.setDayNo(dayno);
		day.saveEx();

		//Find or create period

		whereClause = X_CA_PeriodClass.COLUMNNAME_Name + "=? AND " + X_CA_PeriodClass.COLUMNNAME_CA_ScheduleClass_ID + "=?"  ;

		X_CA_PeriodClass period = new Query(Env.getCtx(), X_CA_PeriodClass.Table_Name, whereClause, null)
		.setParameters(periodno, 1000003)
		.setOnlyActiveRecords(true)
		.first();

		if(period!=null)
		{
			whereClause = X_CA_SchedulePeriod.COLUMNNAME_CA_ScheduleDay_ID + "=? AND ( " + X_CA_SchedulePeriod.COLUMNNAME_CA_PeriodClass_ID + "=? )";


			boolean iselective = isElective.isSelected();

			if(iselective)
				whereClause += " AND " + X_CA_SchedulePeriod.COLUMNNAME_C_BPartner_ID + "=? ";


			Query query = new Query(Env.getCtx(), X_CA_SchedulePeriod.Table_Name, whereClause, null)
			.setOnlyActiveRecords(true);

			if(iselective)
				query.setParameters(day.get_ID(), 0, currentBPartner.get_ID());
			else
				query.setParameters(day.get_ID(), 0);

			X_CA_SchedulePeriod schPeriod = query.first();		

			if(schPeriod==null)
			{
				schPeriod = new X_CA_SchedulePeriod(Env.getCtx(), 0, null);
			}


			X_CA_MatterAssignment assignment = new X_CA_MatterAssignment(Env.getCtx(), (Integer)fMatterAssignment.getValue(), null);

			schPeriod.setCA_ScheduleDay_ID(day.get_ID()); // dia
			schPeriod.setC_BPartner_ID(currentBPartner.getC_BPartner_ID()); // profesor


			if(iselective)
			{
				schPeriod.setCA_SubjectMatter_ID(assignment.getElectiveSubject_ID());
				schPeriod.set_ValueOfColumn("ElectiveSubject_ID", assignment.getCA_SubjectMatter_ID());
			}
			else
				schPeriod.setCA_SubjectMatter_ID(assignment.getCA_SubjectMatter_ID());

			schPeriod.setCA_PeriodClass_ID(period.get_ID());




			schPeriod.saveEx();


			if(iselective)
			{

				/*
				whereClause = X_CA_MatterAssignment.COLUMNNAME_CA_MatterAssignment_ID + 
						" IN ( Select " +  X_CA_TeacherAssignment.COLUMNNAME_CA_MatterAssignment_ID +
						" FROM " + X_CA_TeacherAssignment.Table_Name + 
						" WHERE " + X_CA_TeacherAssignment.COLUMNNAME_C_BPartner_ID + "=?)" +
						" AND " + X_CA_MatterAssignment.COLUMNNAME_ElectiveSubject_ID + "=?" + 
						" AND " + X_CA_MatterAssignment.COLUMNNAME_CA_GroupAssignment_ID + 
						" IN ( SELECT " + X_CA_GroupAssignment.COLUMNNAME_CA_GroupAssignment_ID + 
						" FROM " + X_CA_GroupAssignment.Table_Name +
						" WHERE " + X_CA_GroupAssignment.COLUMNNAME_CA_CourseDef_ID + "=?) " +
						" AND " + X_CA_MatterAssignment.COLUMNNAME_CA_SubjectMatter_ID + "=?"; 


				X_CA_MatterAssignment assignment2 = new Query(Env.getCtx(), X_CA_MatterAssignment.Table_Name, whereClause, null)
				.setOnlyActiveRecords(true)
				.setParameters(currentBPartner.get_ID(),assignment.getElectiveSubject_ID(), course.get_ID(), assignment.getCA_MatterAssignment_ID())
				.first();*/

				if(assignment !=null)
				{

					whereClause = X_CA_ElectivePeriod.COLUMNNAME_CA_MatterAssignment_ID + "=? AND " + 
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







				return true;



			}

		}
		return true;
	}
}