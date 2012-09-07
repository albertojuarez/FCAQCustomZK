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
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zhtml.Span;
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
	
	private Button bSave = new Button(Msg.getMsg(Env.getCtx(),"Save"));
	
	private Borderlayout scheduleLayout = new Borderlayout();
	
	public WSchedule()
	{
		try
		{
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
		
		AD_Column_ID = 1000734;        //  CA_CourseDef.CA_CourseDef_ID
		MLookup group = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
		fGroup= new WTableDirEditor("CA_CourseDef_ID", true, false, true, group);
		fGroup.addValueChangeListener(this);
		
		AD_Column_ID = 1000934;        //  CA_SubjectMatter.CA_SubjectMatter_ID
		MLookup subject = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
		fSubject= new WTableDirEditor("CA_SubjectMatter_ID", true, false, true, subject);
		fSubject.addValueChangeListener(this);
	}
	
	
	private void zkInit()
	{
		form.appendChild(mainLayout);
		mainLayout.setWidth("99%");
		mainLayout.setHeight("100%");
		
		parameterPanel.appendChild(parameterLayout);
		
		lGroup = new Label(Msg.getMsg(Env.getCtx(), "Group"));
		lBPartner = new Label(Msg.getMsg(Env.getCtx(), "BPartner"));
		lSubject = new Label(Msg.getMsg(Env.getCtx(), "Subject"));
		
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
		
		row.appendChild(lGroup);
		row.appendChild(fGroup.getComponent());
		
		row.appendChild(lSubject);
		row.appendChild(fSubject.getComponent());
		
		row = rows.newRow();
		row.appendChild(new Space());
		row.appendChild(bSave);
		
		
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
		Label header = new Label(Msg.getMsg(Env.getCtx(),"Period"));
		header.setStyle("font-size:20px;");
		span.appendChild(header);
		
		span = new Span();
		span.setParent(schedulePanelNorth);
		span.setStyle("height: 99%; display: inline-block; width: 14%;");
		header = new Label(Msg.getMsg(Env.getCtx(),"Day") + " 1");
		header.setStyle("font-size:20px;");
		span.appendChild(header);
		span = new Span();
		span.setParent(schedulePanelNorth);
		span.setStyle("height: 99%; display: inline-block; width: 14%;");
		header = new Label(Msg.getMsg(Env.getCtx(),"Day") + " 2");
		header.setStyle("font-size:20px;");
		span.appendChild(header);
		span = new Span();
		span.setParent(schedulePanelNorth);
		span.setStyle("height: 99%; display: inline-block; width: 14%;");
		header = new Label(Msg.getMsg(Env.getCtx(),"Day") + " 3");
		header.setStyle("font-size:20px;");
		span.appendChild(header);
		span = new Span();
		span.setParent(schedulePanelNorth);
		span.setStyle("height: 99%; display: inline-block; width: 14%;");
		header = new Label(Msg.getMsg(Env.getCtx(),"Day") + " 4");
		header.setStyle("font-size:20px;");
		span.appendChild(header);
		span = new Span();
		span.setParent(schedulePanelNorth);
		span.setStyle("height: 99%; display: inline-block; width: 14%;");
		header = new Label(Msg.getMsg(Env.getCtx(),"Day") + " 5");
		header.setStyle("font-size:20px;");
		span.appendChild(header);
		span = new Span();
		span.setParent(schedulePanelNorth);
		span.setStyle("height: 99%; display: inline-block; width: 14%;");
		header = new Label(Msg.getMsg(Env.getCtx(),"Day") + " 6");
		header.setStyle("font-size:20px;");
		span.appendChild(header);
		
		
		Vbox periodLayout = new Vbox();
		
		Center scheduleCenter = new Center();
		//scheduleCenter.setFlex(true);
		
		
		periodLayout.setHeight("400px");
		periodLayout.setWidth("99%");

		scheduleCenter.appendChild(periodLayout);
		scheduleLayout.appendChild(scheduleCenter);
		
		for(int x=1; x<=8; x++)
		{
			Period period = new Period(x);
			periodLayout.appendChild(period);
		}
	}
	
	
	
	@Override
	public void valueChange(ValueChangeEvent evt) {

	}

	@Override
	public void onEvent(Event event) throws Exception {

	}

	@Override
	public ADForm getForm() {
		return form;
	}
	
	
	
	
	class Period extends Panel
	{
		
		List<ClassRoom> classRooms = new ArrayList<ClassRoom>();
		Label lPeriod = new Label();
		
		
		public Period(int periodNo){
			super();
			
			lPeriod.setText(String.valueOf(periodNo));
			lPeriod.setStyle("font-size:20px;");
			
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
			
			for(int x=0; x<=5; x++)
			{
				ClassRoom classRoom = new ClassRoom(style);
				
				span = new Span();
				span.setParent(this);
				span.setStyle("height: 99%; display: inline-block; width: 14%;");
				span.appendChild(classRoom);
				
			}
		}
	}
	
	class ClassRoom extends Panel
	{
		private Grid fieldGrid = GridFactory.newGridLayout();
		
		Combobox fSubject = new Combobox(); // Materias
		Combobox fTeacher = new Combobox(); // Profesores
		Combobox fGroup   = new Combobox(); // Grupos
		
		Label lSubject = new Label("Materia 1");
		Label lTeacher = new Label("Profesor 1");
		Label lGroup = new Label("Grupo 1");
		
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
			//row.appendChild(fSubject);
			row.appendChild(lSubject);
			
			row = rows.newRow();
			row.setStyle(style + " border:none; text-align: center;");
			//row.appendChild(fTeacher);
			row.appendChild(lTeacher);
			
			
			//row = rows.newRow();
			//row.appendChild(fGroup);
			
		}
		
		
	}

}
