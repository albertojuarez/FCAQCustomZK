package org.eevolution.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Panel;
import org.compiere.model.MBPartner;
import org.fcaq.components.IClassRoom;
import org.fcaq.model.X_CA_ScheduleDay;
import org.fcaq.model.X_CA_SchedulePeriod;
import org.fcaq.model.X_CA_SchoolYear;
import org.zkoss.zhtml.Span;
import org.zkoss.zk.ui.Component;

public class Period extends Panel{


	List<ClassRoom> classRooms = new ArrayList<ClassRoom>();
	Label lPeriod = new Label();
	public boolean iseditablemode = false;
	String realPeriodNo = "";

	MBPartner currentBPartner = null;
	X_CA_SchoolYear currentSchoolYear = null;


	public Period(int periodNo, List<X_CA_ScheduleDay> days, Vector<Vector<Object>> periods, 
			boolean iseditablemode, MBPartner teacher, X_CA_SchoolYear schoolYear, 
			boolean isSubstitution, int windowNo){

		super();
		
		this.iseditablemode = iseditablemode;
		this.currentBPartner = teacher;
		this.currentSchoolYear = schoolYear;

		lPeriod.setText(String.valueOf(periodNo));
		lPeriod.setStyle("font-size:20px;");
		
		realPeriodNo = "C" + periodNo;

		String style = "";

		if(periodNo%2>0)
		{
			style="background-color:#EFEFEF;";
		}
		else
		{
			style="background-color:#FCFCFC;";
		}

		this.setStyle(style);
		
		Span span = new Span();
		span.setParent(this);
		span.setStyle("height: 99%; display: inline-block; width: 5%; margin-left:30px; " +
				"margin-right:-30px; margin-top:30px; margin-bottom:-30px;");
		span.appendChild(lPeriod);

		List<IClassRoom> classRooms = new ArrayList<IClassRoom>();

		for(int x=0; x<=5; x++) {
			
			IClassRoom classRoom = null;
			
			if (isSubstitution) {
				
				classRoom = new ClassSubstitution(style, iseditablemode);
				classRoom.setWindowNo(windowNo);
			}
			else
				classRoom = new ClassRoom(style,iseditablemode);
			
			classRoom.setDayno(x+1);
			classRoom.setPeriodno(realPeriodNo);
			classRoom.setSchoolYear(currentSchoolYear);
			classRoom.setTeacher(currentBPartner);
			span = new Span();
			span.setParent(this);
			span.setStyle("height: 99%; display: inline-block; width: 14%;");
			span.appendChild((Component)classRoom);

			classRooms.add(classRoom);


		}

		if(periods!=null)
		{
			for(Vector<Object> periodLine : periods)
			{
				X_CA_SchedulePeriod period = (X_CA_SchedulePeriod)periodLine.get(0);
				
				if (realPeriodNo.equals(period.getCA_PeriodClass().getName())) {
					for(IClassRoom classroom : classRooms)
					{
						for(int day=1; day<=6; day++) // X_CA_ScheduleDay day : days)
						{
							if(classroom.getPeriodno().equals(realPeriodNo) && classroom.getDayno()==day && period.getCA_ScheduleDay().getDayNo()==day)
							{
								classroom.setPeriod(periodLine);
							}
						}
					}
				}
			}
		}
	}
}
