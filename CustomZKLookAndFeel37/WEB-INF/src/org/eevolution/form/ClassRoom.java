package org.eevolution.form;

import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.fcaq.model.X_CA_PeriodClass;
import org.fcaq.model.X_CA_SchedulePeriod;

public class ClassRoom extends Panel{

	
	private Grid fieldGrid = GridFactory.newGridLayout();
	
	Combobox fSubject = new Combobox(); // Materias
	Combobox fTeacher = new Combobox(); // Profesores
	Combobox fGroup   = new Combobox(); // Grupos
	
	Label lSubject = new Label("");
	Label lTeacher = new Label("");
	Label lGroup = new Label("");
	
	private int dayno = 0;
	private int periodno = 0;
	private X_CA_SchedulePeriod period=null;
	
	public ClassRoom(String style)
	{
		super();
		
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
		
	}
	
	public void setDayno(int dayno)
	{
		this.dayno = dayno;
	}
	
	public void setPeriodno(int periodno)
	{
		this.periodno = periodno;
	}
	
	public int getDayno()
	{
		return dayno;
	}
	
	public int getPeriodno()
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
}
