package org.eevolution.form;

import java.util.ArrayList;
import java.util.List;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.MBPartner;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.fcaq.model.X_CA_Schedule;
import org.fcaq.model.X_CA_ScheduleDay;
import org.fcaq.model.X_CA_SchedulePeriod;
import org.zkoss.zhtml.Span;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vbox;

public class WSchedule extends Schedule implements IFormController, EventListener, ValueChangeListener{

	private CustomForm form = new CustomForm();
	private Borderlayout mainLayout = new Borderlayout();
	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();
	private Panel mainPanel = new Panel();

	private Label lBPartner = null;
	private WSearchEditor fBPartner = null; // Profesor, Alumno
	private Label lGroup = null;
	private WTableDirEditor fGroup = null; // Curso (Grado, paralelo)
	private Label lSubject = null;
	private WTableDirEditor fSubject = null; // Materia

	private Button bSave = new Button("Save");

	private Borderlayout scheduleLayout = new Borderlayout();
	Vbox periodLayout = new Vbox();
	
	Center scheduleCenter = new Center();


	public WSchedule()
	{
		try
		{
			loadStardData();

			if(iseditablemode)
				dynInit();

			zkInit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void dynInit()
	{
		int AD_Column_ID = 2893;        //  C_BPartner.C_BPartner_ID
		MLookup teacher = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, AD_Column_ID, DisplayType.Search);
		fBPartner = new WSearchEditor("C_BPartner_ID", true, false, true, teacher);
		fBPartner.addValueChangeListener(this);

		/*
		AD_Column_ID = 1000734;        //  CA_CourseDef.CA_CourseDef_ID
		MLookup group = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
		fGroup= new WTableDirEditor("CA_CourseDef_ID", true, false, true, group);
		fGroup.addValueChangeListener(this);

		AD_Column_ID = 1000934;        //  CA_SubjectMatter.CA_SubjectMatter_ID
		MLookup subject = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
		fSubject= new WTableDirEditor("CA_SubjectMatter_ID", true, false, true, subject);
		fSubject.addValueChangeListener(this);*/
	}


	private void zkInit()
	{
		form.appendChild(mainLayout);
		mainLayout.setWidth("99%");
		mainLayout.setHeight("100%");

		if(iseditablemode)
		{
			parameterPanel.appendChild(parameterLayout);

			//lGroup = new Label(Msg.getMsg(Env.getCtx(), "Group"));
			lBPartner = new Label(Msg.getMsg(Env.getCtx(), "Find"));
			//lSubject = new Label(Msg.getMsg(Env.getCtx(), "Subject"));

			North north = new North();
			north.setStyle("border: none");
			mainLayout.appendChild(north);
			north.appendChild(parameterPanel);
			Rows rows = null;
			Row row = null;
			parameterLayout.setWidth("800px");
			rows = parameterLayout.newRows();
			row = rows.newRow();

			row.appendChild(lBPartner);
			row.appendChild(fBPartner.getComponent());

			/*
			row.appendChild(lGroup);
			row.appendChild(fGroup.getComponent());

			row.appendChild(lSubject);
			row.appendChild(fSubject.getComponent());*/

			row = rows.newRow();
			row.appendChild(new Space());
			row.appendChild(bSave);

		}

		scheduleLayout.setWidth("99%");
		scheduleLayout.setHeight("100%");

		Center mainCenter = new Center();

		mainCenter.setFlex(true);
		mainCenter.appendChild(scheduleLayout);
		mainLayout.appendChild(mainCenter);

		Panel schedulePanelNorth = new Panel();

		North scheduleNorth = new North();
		scheduleNorth.appendChild(schedulePanelNorth);
		scheduleLayout.appendChild(scheduleNorth);
		scheduleNorth.setStyle("border: none");

		Span span = new Span();
		span.setParent(schedulePanelNorth);
		span.setStyle("height: 99%; display: inline-block; width:10%;");
		Label header = new Label("Period");
		header.setStyle("font-size:20px;");
		span.appendChild(header);

		span = new Span();
		span.setParent(schedulePanelNorth);
		span.setStyle("height: 99%; display: inline-block; width: 14%;");
		header = new Label("Day 1");
		header.setStyle("font-size:20px;");
		span.appendChild(header);
		span = new Span();
		span.setParent(schedulePanelNorth);
		span.setStyle("height: 99%; display: inline-block; width: 14%;");
		header = new Label("Day 2");
		header.setStyle("font-size:20px;");
		span.appendChild(header);
		span = new Span();
		span.setParent(schedulePanelNorth);
		span.setStyle("height: 99%; display: inline-block; width: 14%;");
		header = new Label("Day 3");
		header.setStyle("font-size:20px;");
		span.appendChild(header);
		span = new Span();
		span.setParent(schedulePanelNorth);
		span.setStyle("height: 99%; display: inline-block; width: 14%;");
		header = new Label("Day 4");
		header.setStyle("font-size:20px;");
		span.appendChild(header);
		span = new Span();
		span.setParent(schedulePanelNorth);
		span.setStyle("height: 99%; display: inline-block; width: 14%;");
		header = new Label("Day 5");
		header.setStyle("font-size:20px;");
		span.appendChild(header);
		span = new Span();
		span.setParent(schedulePanelNorth);
		span.setStyle("height: 99%; display: inline-block; width: 14%;");
		header = new Label("Day 6");
		header.setStyle("font-size:20px;");
		span.appendChild(header);


		

		//scheduleCenter.setFlex(true);


		periodLayout.setHeight("400px");
		periodLayout.setWidth("99%");

		scheduleCenter.appendChild(periodLayout);
		scheduleLayout.appendChild(scheduleCenter);

		for(int x=1; x<=9; x++)
		{
			Period period = new Period(x, days, periods, iseditablemode);
			period.iseditablemode=iseditablemode;
			periodLayout.appendChild(period);
		}
	}



	@Override
	public void valueChange(ValueChangeEvent evt) {
		String name = evt.getPropertyName();
		Object value = evt.getNewValue();

		clean();
	
		if ("C_BPartner_ID".equals(name))
		{
			fBPartner.setValue(value);
			
			if(value==null)
				return;
			currentBPartner = new MBPartner(ctx, (Integer)fBPartner.getValue(), null);
			
			if(currentBPartner.get_ValueAsBoolean("IsStudent"))
			{
				schedule = loadStudentSchedule();
			}
			else{
				schedule = loadTeacherSchedule();
			}
		}
		
		if(schedule!=null)
		{
			loadSchedule(schedule);
		}
		
		scheduleLayout.removeChild(scheduleCenter);
		
		scheduleCenter = new Center();
		periodLayout=new Vbox();
		
		periodLayout.setHeight("400px");
		periodLayout.setWidth("99%");

		scheduleCenter.appendChild(periodLayout);
		scheduleLayout.appendChild(scheduleCenter);
		
		for(int x=1; x<=9; x++)
		{
			Period period = new Period(x, days, periods, iseditablemode);
			period.iseditablemode=iseditablemode;
			periodLayout.appendChild(period);
		}
	}

	@Override
	public void onEvent(Event event) throws Exception {

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

	}
	
	public void clean()
	{
		schedule = null;
		days = null;
		periods = null;
	}
}
