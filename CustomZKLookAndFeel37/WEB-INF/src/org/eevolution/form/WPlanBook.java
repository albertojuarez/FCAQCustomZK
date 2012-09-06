package org.eevolution.form;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.editor.WDateEditor;
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


public class WPlanBook extends PlanBook implements IFormController, EventListener, ValueChangeListener{

	private CustomForm form = new CustomForm();
	private Borderlayout mainLayout = new Borderlayout();
	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();
	private Panel mainPanel = new Panel();
	
	private Label lGroup = new Label();
	private Label lBPartner = new Label();
	private Label lDateFrm = new Label();
	private Label lSubject = new Label();

	private WTableDirEditor fGroup = null;
	private WSearchEditor fBPartner = null;	
	private WDateEditor fDateFrom = null;
	private WTableDirEditor fSubject = null;

	private Grid labelGrid = GridFactory.newGridLayout();
	
	private Label lDay = new Label(Msg.getMsg(Env.getCtx(), "Day"));
	private Label lUnit = new Label(Msg.getMsg(Env.getCtx(), "Unit"));
	private Label lLesson = new Label(Msg.getMsg(Env.getCtx(), "Lesson"));
	private Label lStage = new Label(Msg.getMsg(Env.getCtx(), "Stage"));
	private Label lActivity = new Label(Msg.getMsg(Env.getCtx(), "Activity"));
	private Label lHomework = new Label(Msg.getMsg(Env.getCtx(), "Homework"));
	private Label lValues = new Label(Msg.getMsg(Env.getCtx(), "Values"));	
	private Label lComments = new Label(Msg.getMsg(Env.getCtx(), "Comments"));
	private Label lLink = new Label(Msg.getMsg(Env.getCtx(), "Link"));
	private Label lImage = new Label(Msg.getMsg(Env.getCtx(), "Image"));

	List<PlanBookDay> planDays = new ArrayList<PlanBookDay>();

	private Button bBack = new Button("<<");
	private Button bNext = new Button(">>");
	private Label lPeriod = new Label("1");
	private Panel periodControl = new Panel(); 
	
	private Button bSave = new Button("Save");
	
	
	public WPlanBook()
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
		 fDateFrom = new WDateEditor();
		
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
		
		for(int x=0; x<=4; x++)
		{
			PlanBookDay planDay = new PlanBookDay();
			//Add missing parameters
			planDays.add(planDay);
		}
	}
	
	private void zkInit()
	{
		form.appendChild(mainLayout);
		mainLayout.setWidth("99%");
		mainLayout.setHeight("100%");
		
		parameterPanel.appendChild(parameterLayout);
		
		lGroup = new Label(Msg.getMsg(Env.getCtx(), "Group"));
		lBPartner = new Label(Msg.getMsg(Env.getCtx(), "Teacher"));
		lDateFrm = new Label(Msg.getMsg(Env.getCtx(), "Date"));
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

		row.appendChild(lGroup);
		row.appendChild(fGroup.getComponent());
		row.appendChild(lBPartner);
		row.appendChild(fBPartner.getComponent());
		row = rows.newRow();
		row.appendChild(lDateFrm);
		row.appendChild(fDateFrom.getComponent());
		row.appendChild(lSubject);
		row.appendChild(fSubject.getComponent());
		
		row = rows.newRow();
		row.appendChild(new Space());
		
		row = rows.newRow();
		row.appendChild(new Space());
		row.appendChild(periodControl);
		
		Span span = new Span();
		span.setParent(periodControl);
		span.setStyle("height: 99%; display: inline-block; width: 20%;");
		span.appendChild(bBack);
		
		span = new Span();
		span.setParent(periodControl);
		span.setStyle("height: 99%; display: inline-block; width: 10%;");
		span.appendChild(lPeriod);
		
		span = new Span();
		span.setParent(periodControl);
		span.setStyle("height: 99%; display: inline-block; width: 20%;");
		span.appendChild(bNext);
		
		row.appendChild(new Space());
		row.appendChild(bSave);
		
		Center center = new Center();
		center.setFlex(true);
		center.appendChild(mainPanel);
		
		span = new Span();
		span.setParent(mainPanel);
		span.setStyle("height: 99%; display: inline-block; width: 10%;");
		span.appendChild(labelGrid);
		
		labelGrid.setWidth("90%");
		rows = labelGrid.newRows();
		
		row = rows.newRow();
		row.appendChild(new Space());
		row = rows.newRow();
		row.appendChild(new Space());
		row = rows.newRow();
		row.appendChild(lDay.rightAlign());
		row = rows.newRow();
		row.appendChild(new Space());
		row = rows.newRow();
		row.setHeight("35px");
		row.appendChild(lUnit.rightAlign());
		row = rows.newRow();
		row.setHeight("35px");
		row.appendChild(lLesson.rightAlign());
		row = rows.newRow();
		row.setHeight("35px");
		row.appendChild(lStage.rightAlign());
		row = rows.newRow();
		row.setHeight("35px");
		row.appendChild(lActivity.rightAlign());
		row = rows.newRow();
		row.setHeight("35px");
		row.appendChild(lHomework.rightAlign());
		row = rows.newRow();
		row.setHeight("35px");
		row.appendChild(lValues.rightAlign());
		row = rows.newRow();
		row.setHeight("35px");
		row.appendChild(lComments.rightAlign());
		row = rows.newRow();
		row.setHeight("35px");
		row.appendChild(lLink.rightAlign());
		row = rows.newRow();
		row.setHeight("35px");
		row.appendChild(lImage.rightAlign());
		
		for (PlanBookDay planDay : planDays)
		{
			span = new Span();
			span.setParent(mainPanel);
			span.setStyle("height: 99%; display: inline-block; width: 16%;");
			span.appendChild(planDay);
		}
	
		mainLayout.appendChild(center);
	}
	
	


	@Override
	public void onEvent(Event event) throws Exception {

	}

	@Override
	public ADForm getForm() {
		return form;
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}
	
	class PlanBookDay extends Panel
	{
		private Grid fieldGrid = GridFactory.newGridLayout();
		Label lDate = new Label(new Timestamp(System.currentTimeMillis()).toLocaleString());
		Label lDayName = new Label("Monday");
		Label lDayNo = new Label("1");
		
		Textbox fUnit = new Textbox();
		Textbox fLesson = new Textbox();
		Textbox fStage = new Textbox();
		Textbox fActivity = new Textbox();
		Textbox fHomework = new Textbox();
		Textbox fValues = new Textbox();
		Textbox fComments = new Textbox();
		Textbox fLink = new Textbox();
		Textbox fImage = new Textbox();

		
		public PlanBookDay()
		{
			this.appendChild(fieldGrid);
			
			fieldGrid.setWidth("100%");
			Rows rows = fieldGrid.newRows();
			
			Row row = rows.newRow();
			row.appendChild(lDate);
			row = rows.newRow();
			row.appendChild(lDayName);
			row = rows.newRow();
			row.appendChild(lDayNo);
			row = rows.newRow();
			row.appendChild(new Space());
			row = rows.newRow();
			row.setHeight("35px");
			row.appendChild(fUnit);
			row = rows.newRow();
			row.setHeight("35px");
			row.appendChild(fLesson);
			row = rows.newRow();
			row.setHeight("35px");
			row.appendChild(fStage);
			row = rows.newRow();
			row.setHeight("35px");
			row.appendChild(fActivity);
			row = rows.newRow();
			row.setHeight("35px");
			row.appendChild(fHomework);
			row = rows.newRow();
			row.setHeight("35px");
			row.appendChild(fValues);
			row = rows.newRow();
			row.setHeight("35px");
			row.appendChild(fComments);
			row = rows.newRow();
			row.setHeight("35px");
			row.appendChild(fLink);
			row = rows.newRow();
			row.setHeight("35px");
			row.appendChild(fImage);
			
			

		}
		
	}


}
