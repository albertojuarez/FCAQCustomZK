package org.eevolution.form;

import java.util.ArrayList;
import java.util.List;

import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Panel;
import org.fcaq.model.X_CA_CourseDef;
import org.fcaq.model.X_CA_ScheduleDay;
import org.fcaq.model.X_CA_SchedulePeriod;
import org.zkoss.zhtml.Span;

public class Period extends Panel{


	List<ClassRoom> classRooms = new ArrayList<ClassRoom>();
	Label lPeriod = new Label();
	public boolean iseditablemode = false;
	String realPeriodNo = "";


	public Period(int periodNo, List<X_CA_ScheduleDay> days, List<X_CA_SchedulePeriod> periods, boolean iseditablemode){

		super();
		
		this.iseditablemode = iseditablemode;

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
		span.setStyle("height: 99%; display: inline-block; width: 5%; margin-left:30px; margin-right:-30px; margin-top:30px; margin-bottom:-30px;");
		span.appendChild(lPeriod);

		List<ClassRoom> classRooms = new ArrayList<ClassRoom>();

		for(int x=0; x<=5; x++)
		{

			ClassRoom classRoom = new ClassRoom(style,iseditablemode);		
			classRoom.setDayno(x+1);
			classRoom.setPeriodno(realPeriodNo);



			span = new Span();
			span.setParent(this);
			span.setStyle("height: 99%; display: inline-block; width: 14%;");
			span.appendChild(classRoom);

			classRooms.add(classRoom);


		}

		for(X_CA_SchedulePeriod period : periods)
		{
			if(realPeriodNo.equals(period.getCA_PeriodClass().getName())){
				for(ClassRoom classroom : classRooms)
				{
					for(int day=1; day<=6; day++) // X_CA_ScheduleDay day : days)
					{
						if(classroom.getPeriodno().equals(realPeriodNo) && classroom.getDayno()==day && period.getCA_ScheduleDay().getDayNo()==day)
						{
							classroom.setPeriod(period);
						}
					}
				}
			}
		}



	}

}
