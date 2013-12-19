package org.eevolution.form;

import java.sql.Timestamp;

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
import org.fcaq.model.X_CA_SchoolYear;
import org.fcaq.util.AcademicUtil;
import org.fcaq.util.DateUtils;
import org.zkoss.zhtml.Span;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vbox;

public class WSubstitutionSchedule extends SubstitutionSchedule implements IFormController, EventListener, ValueChangeListener {
	
	private CustomForm form = new CustomForm();
	private Borderlayout mainLayout = new Borderlayout();
	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();
	
	private Label lBPartner = null;
	private WSearchEditor fBPartner = null; // Profesor, Alumno
	private Label lSchoolYear = null;
	private WTableDirEditor fSchoolYear = null;
	
	private Borderlayout scheduleLayout = new Borderlayout();
	Vbox periodLayout = new Vbox();
	
	Center scheduleCenter = new Center();
	North scheduleNorth = new North();

	public WSubstitutionSchedule() {
		
		try {
			
			loadStardData();
			
			dynInit();
			
			zkInit();
		}
		catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	private void dynInit() {
		
		MLookup teacher = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 
				COLUMN_BPartner_Teacher_ID, DisplayType.Search);
		fBPartner = new WSearchEditor(COLUMNNAME_BPartner_Teacher, true, false, true, teacher);
		fBPartner.addValueChangeListener(this);
		
		fSchoolYear = new WTableDirEditor(X_CA_SchoolYear.COLUMNNAME_CA_SchoolYear_ID, true, false, true, 
				AcademicUtil.buildLookup(COLUMN_SchoolYear_ID, "", form.getWindowNo()));
		fSchoolYear.setValue(getSchoolYear().get_ID());
		fSchoolYear.addValueChangeListener(this);
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
		parameterLayout.setWidth("99%");
		rows = parameterLayout.newRows();

		parameterPanel.appendChild(parameterLayout);
		
		lBPartner = new Label(Msg.getMsg(Env.getCtx(), "Teacher"));
		lSchoolYear = new Label(Msg.getMsg(ctx, "SchoolYear"));
		row = rows.newRow();
		row.appendChild(lSchoolYear);
		row.appendChild(fSchoolYear.getComponent());
		row.appendChild(lBPartner);
		row.appendChild(fBPartner.getComponent());
		
		row = rows.newRow();
		row.appendChild(new Space());
		row.appendChild(new Label( new Timestamp(System.currentTimeMillis()).toString() +  "   D\u00EDa actual " + DateUtils.getDayNo()));
		
		scheduleLayout.setWidth("99%");
		scheduleLayout.setHeight("100%");
		
		Center mainCenter = new Center();
		
		mainCenter.setFlex(true);
		mainCenter.appendChild(scheduleLayout);
		mainLayout.appendChild(mainCenter);
		
		renderNorth();
		
		//scheduleCenter.setFlex(true);
		
		periodLayout.setHeight("600px");
		periodLayout.setWidth("99%");
		
		scheduleCenter.appendChild(periodLayout);
		scheduleLayout.appendChild(scheduleCenter);
		
		if(days!=null && periods!=null)
		{
			for(int x=1; x<=8; x++)
			{
				Period period = new Period(x, days, periods, 
						true, currentBPartner, getSchoolYear(), 
						true, form.getWindowNo());
				
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
	}
	
	@Override
	public void valueChange(ValueChangeEvent evt) {
		String name = evt.getPropertyName();
		Object value = evt.getNewValue();

		clean();
		
		if (COLUMNNAME_BPartner_Teacher.equals(name))
		{
			fBPartner.setValue(value);

			if(value==null)
				return;
			currentBPartner = new MBPartner(ctx, (Integer)fBPartner.getValue(), null);
			
			loadTeacherSchedule();
		}
		
		if (X_CA_SchoolYear.COLUMNNAME_CA_SchoolYear_ID.equals(name)) {
			
			fSchoolYear.setValue(value);
			
			if (value==null)
				return;
			
			X_CA_SchoolYear schoolYear = new X_CA_SchoolYear(ctx, (Integer)fSchoolYear.getValue(), null);
			
			setSchoolYear(schoolYear);
			
			if (currentBPartner == null)
				return;
			else
				loadTeacherSchedule();
		}
		
		renderNorth();
		
		scheduleLayout.removeChild(scheduleCenter);

		scheduleCenter = new Center();
		periodLayout=new Vbox();

		periodLayout.setHeight("400px");
		periodLayout.setWidth("99%");

		scheduleCenter.appendChild(periodLayout);
		scheduleLayout.appendChild(scheduleCenter);
		
		for(int x=1; x<=8; x++)
		{
			Period period = null;
				
			period = new Period(x, days, periods, 
					true, currentBPartner, getSchoolYear(), 
					true, form.getWindowNo());
			
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
