package org.eevolution.form;

import java.sql.Timestamp;
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
import org.fcaq.util.DateUtils;
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

	
	North scheduleNorth = new North();

	

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
		int AD_Column_ID = 2893;  
		MLookup teacher = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, AD_Column_ID, DisplayType.Search);
		fBPartner = new WSearchEditor("C_BPartner_ID", true, false, true, teacher);
		fBPartner.addValueChangeListener(this);
	}


	private void zkInit()
	{
		form.appendChild(mainLayout);
		mainLayout.setWidth("99%");
		mainLayout.setHeight("100%");

		North north = new North();
		north.setStyle("border: none");
		mainLayout.appendChild(north);
		north.appendChild(parameterPanel);
		Rows rows = null;
		Row row = null;
		parameterLayout.setWidth("800px");
		rows = parameterLayout.newRows();

		parameterPanel.appendChild(parameterLayout);

		if(iseditablemode)
		{

			lBPartner = new Label(Msg.getMsg(Env.getCtx(), "Find"));
			row = rows.newRow();
			row.appendChild(lBPartner);
			row.appendChild(fBPartner.getComponent());

		}
		row = rows.newRow();
		row.appendChild(new Space());
		row.appendChild(new Label( new Timestamp(System.currentTimeMillis()).toLocaleString() +  "   D\u00EDa actual " + DateUtils.getDateNo()));


		scheduleLayout.setWidth("99%");
		scheduleLayout.setHeight("100%");

		Center mainCenter = new Center();

		mainCenter.setFlex(true);
		mainCenter.appendChild(scheduleLayout);
		mainLayout.appendChild(mainCenter);
		
		renderNorth();

		




		//scheduleCenter.setFlex(true);


		periodLayout.setHeight("400px");
		periodLayout.setWidth("99%");

		scheduleCenter.appendChild(periodLayout);
		scheduleLayout.appendChild(scheduleCenter);

		if(days!=null && periods!=null)
		{
			for(int x=1; x<=9; x++)
			{
				Period period = new Period(x, days, periods, iseditablemode, (MBPartner) periods.get(0).getC_BPartner());
				period.iseditablemode=iseditablemode;
				periodLayout.appendChild(period);
			}
		}
	}



	private void renderNorth() {
		
		scheduleLayout.removeChild(scheduleNorth);
		
		scheduleNorth = new North();
		
		Panel schedulePanelNorth = new Panel();

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
		header = new Label(drawmode==0?"Day 1":"Monday");
		header.setStyle("font-size:20px;");
		span.appendChild(header);
		span = new Span();
		span.setParent(schedulePanelNorth);
		span.setStyle("height: 99%; display: inline-block; width: 14%;");
		header = new Label(drawmode==0?"Day 2":"Tuesday");
		header.setStyle("font-size:20px;");
		span.appendChild(header);
		span = new Span();
		span.setParent(schedulePanelNorth);
		span.setStyle("height: 99%; display: inline-block; width: 14%;");
		header = new Label(drawmode==0?"Day 3":"Wednesday");
		header.setStyle("font-size:20px;");
		span.appendChild(header);
		span = new Span();
		span.setParent(schedulePanelNorth);
		span.setStyle("height: 99%; display: inline-block; width: 14%;");
		header = new Label(drawmode==0?"Day 4":"Thursday");
		header.setStyle("font-size:20px;");
		span.appendChild(header);
		span = new Span();
		span.setParent(schedulePanelNorth);
		span.setStyle("height: 99%; display: inline-block; width: 14%;");
		header = new Label(drawmode==0?"Day 5":"Friday");
		header.setStyle("font-size:20px;");
		span.appendChild(header);
		span = new Span();
		span.setParent(schedulePanelNorth);
		span.setStyle("height: 99%; display: inline-block; width: 14%;");
		header = new Label(drawmode==0?"Day 6":"");
		header.setStyle("font-size:20px;");
		span.appendChild(header);
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

		renderNorth();
		
		scheduleLayout.removeChild(scheduleCenter);

		scheduleCenter = new Center();
		periodLayout=new Vbox();

		periodLayout.setHeight("400px");
		periodLayout.setWidth("99%");

		scheduleCenter.appendChild(periodLayout);
		scheduleLayout.appendChild(scheduleCenter);

		for(int x=1; x<=9; x++)
		{
			Period period = null;
			if(!currentBPartner.get_ValueAsBoolean("IsStudent"))
			{
				period = new Period(x, days, periods, iseditablemode,currentBPartner);
				period.iseditablemode=iseditablemode;
			}
			else
			{
				period = new Period(x, days, periods, false,currentBPartner);
				period.iseditablemode=false;
			}
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
