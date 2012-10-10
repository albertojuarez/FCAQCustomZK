package org.eevolution.form;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.fcaq.model.X_CA_CourseDef;
import org.fcaq.model.X_CA_EvaluationPeriod;
import org.fcaq.model.X_CA_PeriodClass;
import org.fcaq.model.X_CA_SchedulePeriod;
import org.fcaq.model.X_CA_SubjectMatter;
import org.fcaq.util.AcademicUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.North;

public class ClassRoom extends Panel implements EventListener{


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


	public ClassRoom(String style, boolean editablemode)
	{
		super();

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

	@Override
	public void onEvent(Event event) throws Exception {
		showEditWindow();
	}


	Window editWindow = null;

	WTableDirEditor fGroup = null;
	WSearchEditor fSubject = null; 
	WSearchEditor fElectiveSubject = null; 



	private void showEditWindow()
	{

		editWindow = new Window();

		Borderlayout mainCLayout = new Borderlayout();
		Panel parameterCPanel = new Panel();
		Grid parameterCLayout = GridFactory.newGridLayout();
		parameterCPanel.appendChild(parameterCLayout);


		buildParameterLayout(parameterCLayout);

		North northC = new North();
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

		AEnv.showCenterScreen(editWindow);
	}

	private void buildParameterLayout(Grid parameter)
	{

		fGroup = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.buildLookup(AcademicUtil.COLUMN_CourseDef_ID, " AND " + X_CA_CourseDef.COLUMNNAME_IsElective+"='" +(isElective.isSelected()?"Y'":"N'") , 0));
		fSubject = new WSearchEditor("CA_SubjectMatter_ID", true, false, true, AcademicUtil.buildLookup(AcademicUtil.COLUMN_SubjectMatter_ID, " AND " + X_CA_SubjectMatter.COLUMNNAME_IsElectiveSelection+"='N'", 0));
		fElectiveSubject = new WSearchEditor("CA_SubjectMatter_ID", true, false, true, AcademicUtil.buildLookup(AcademicUtil.COLUMN_SubjectMatter_ID, " AND " + X_CA_SubjectMatter.COLUMNNAME_IsElectiveSelection+"='Y'", 0));



		Rows rows = null;
		Row row = null;
		parameter.setWidth("99%");
		rows = parameter.newRows();

		row = rows.newRow();
		row.appendChild(isElective);
		row = rows.newRow();
		row.appendChild(fGroup.getComponent());
		row = rows.newRow();
		row.appendChild(fSubject.getComponent());
		row = rows.newRow();
		row.appendChild(fElectiveSubject.getComponent());

	}
}
