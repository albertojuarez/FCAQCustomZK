package org.eevolution.form;

import java.util.logging.Level;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.fcaq.model.X_CA_CourseDef;
import org.fcaq.model.X_CA_EvaluationPeriod;
import org.fcaq.model.X_CA_GroupAssignment;
import org.fcaq.model.X_CA_MatterAssignment;
import org.fcaq.model.X_CA_Parcial;
import org.fcaq.model.X_CA_SubjectMatter;
import org.fcaq.util.AcademicUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.North;
import org.zkoss.zul.Space;

public class WSendNotes extends SendNotes
implements IFormController, EventListener, ValueChangeListener
{

	// Layout components
	private CustomForm form = new CustomForm();
	private Borderlayout mainLayout = new Borderlayout();
	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();
	private Button sendButton = new Button("Send");
	private Button cancelButton = new Button("Cancel");


	private Label lCourseDef = null;
	private Label lParcial = null;
	private Label lSubjectMatter = null;

	private WTableDirEditor fCourseDef = null;
	private WTableDirEditor fParcial = null;
	private WTableDirEditor fMatterAssignment = null;

	private Checkbox isElective = new Checkbox();
	private Checkbox isDiscipline = new Checkbox();


	public WSendNotes()
	{
		super();
		try
		{
			loadStartData();
			dynInit();
			zkInit();
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "", e);
		}

	}

	// Init Search Editor
	private void dynInit() {


		isElective.setSelected(false);
		isElective.setLabel(Msg.getMsg(Env.getCtx(), "Is Elective"));
		
		isDiscipline.setSelected(false);
		isDiscipline.setLabel(Msg.getMsg(Env.getCtx(), "Is Discipline"));


		fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),currentBPartner.get_ID(), isElective.isSelected()));
		fCourseDef.addValueChangeListener(this);

		fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, AcademicUtil.getMatterAssignmentLookup(form.getWindowNo(),currentBPartner.get_ID()));
		fMatterAssignment.addValueChangeListener(this);


		fParcial = new WTableDirEditor("CA_Parcial_ID", true, false, true, AcademicUtil.getParcialLookup(form.getWindowNo(),currentSchoolYear.get_ID()));
		fParcial.addValueChangeListener(this);
		fParcial.setValue(AcademicUtil.getCurrentParcial(m_ctx).get_ID());
		currentParcial = new X_CA_Parcial(m_ctx, (Integer)fParcial.getValue(), null);

		isElective.addActionListener(this);
		
		sendButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
	}


	// Init Components
	private void zkInit() {

		form.appendChild(mainLayout);
		mainLayout.setWidth("99%");
		mainLayout.setHeight("100%");

		parameterPanel.appendChild(parameterLayout);

		lCourseDef = new Label();
		lCourseDef.setText(Msg.getMsg(Env.getCtx(), "Group"));

		lParcial = new Label();
		lParcial.setText(Msg.getMsg(Env.getCtx(), "Parcial"));

		lSubjectMatter = new Label();
		lSubjectMatter.setText(Msg.getMsg(Env.getCtx(), "SubjectMatter"));

		North north = new North();
		north.setStyle("border: none");
		mainLayout.appendChild(north);
		north.appendChild(parameterPanel);
		Rows rows = null;
		Row row = null;
		parameterLayout.setWidth("800px");
		rows = parameterLayout.newRows();

		row = rows.newRow();

		row.appendChild(new Space());
		row.appendChild(isElective);
		isElective.setWidth("39%");
		row.appendChild(new Space());
		row.appendChild(isDiscipline);
		isDiscipline.setWidth("39%");

		row.appendChild(lCourseDef);
		row.appendChild(fCourseDef.getComponent());
		fCourseDef.getComponent().setWidth("60%");
		row = rows.newRow();
		row.appendChild(lSubjectMatter);
		row.appendChild(fMatterAssignment.getComponent());
		row.appendChild(lParcial);
		row.appendChild(fParcial.getComponent());
		row = rows.newRow();
		row.appendChild(sendButton);
		row.appendChild(cancelButton);

	}


	@Override
	public void showErrorMessage(String message) {
		FDialog.error(form.getWindowNo(), message);
	}
	
	@Override
	public void showInfoMessage(String message) {
		FDialog.info(form.getWindowNo(), null, message);
	}
	
	
	@Override
	public void dispose() {

	}

	@Override
	public void valueChange(ValueChangeEvent evt) {

		String name = evt.getPropertyName();
		Object value = evt.getNewValue();


		if (value == null)
			return;

		if ("CA_CourseDef_ID".equals(name))
		{


			fCourseDef.setValue(value);

			fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, AcademicUtil.getMatterAssignmentLookup(form.getWindowNo(),currentBPartner.get_ID(), (Integer)fCourseDef.getValue()));
			fMatterAssignment.addValueChangeListener(this);

			fMatterAssignment.actionRefresh();

			repaintParameterPanel();

		}

		if ("CA_MatterAssignment_ID".equals(name))
		{


			fMatterAssignment.setValue(value);

			X_CA_MatterAssignment assignment = new X_CA_MatterAssignment(m_ctx, (Integer) fMatterAssignment.getValue(), null);

			X_CA_SubjectMatter tmpSubject = (X_CA_SubjectMatter) assignment.getCA_SubjectMatter();
			X_CA_CourseDef tmpCourse = new X_CA_CourseDef(m_ctx,(Integer) fCourseDef.getValue(), null);

		}
		if ("CA_Parcial_ID".equals(name))
		{
			fParcial.setValue(value);

		}
	}


	public void repaintParameterPanel()
	{
		parameterLayout.removeChild(parameterLayout.getRows());

		Rows rows = null;
		Row row = null;
		parameterLayout.setWidth("800px");
		rows = parameterLayout.newRows();

		row = rows.newRow();		
		row.appendChild(new Space());
		row.appendChild(isElective);
		isElective.setWidth("39%");
		row.appendChild(new Space());
		row.appendChild(isDiscipline);
		isDiscipline.setWidth("39%");

		row.appendChild(lCourseDef);
		row.appendChild(fCourseDef.getComponent());
		fCourseDef.getComponent().setWidth("60%");
		row = rows.newRow();

		row.appendChild(lSubjectMatter);
		row.appendChild(fMatterAssignment.getComponent());

		row.appendChild(lParcial);
		row.appendChild(fParcial.getComponent());
		
		row = rows.newRow();
		row.appendChild(sendButton);
		row.appendChild(cancelButton);
	}


	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getTarget()==sendButton)
		{
			
			
			int GradeWindowNo = Env.getContextAsInt(Env.getCtx(), "GradeWindowNo");
			int DisciplineWindowNo =  Env.getContextAsInt(Env.getCtx(), "DisciplineWindowNo");
			
			

			if(GradeWindowNo >0)
				SessionManager.getAppDesktop().closeWindow(GradeWindowNo);
			if(DisciplineWindowNo >0)
				SessionManager.getAppDesktop().closeWindow(DisciplineWindowNo);
			
			prepareSendNotes();
			loadStudentData();
			sendNotes();

		}
		else if (event.getTarget()==cancelButton)
		{
			dispose();	
		}
		else if (event.getTarget().equals(isElective))
		{

			fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),currentBPartner.get_ID(), isElective.isSelected()));
			fCourseDef.addValueChangeListener(this);

			if(isElective.isSelected())
			{
				fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, AcademicUtil.getMatterAssignmentLookup(form.getWindowNo(),currentBPartner.get_ID(), 0));
				fMatterAssignment.addValueChangeListener(this);
			}


			repaintParameterPanel();
		}
	}

	private void prepareSendNotes() {

		if(fCourseDef.getValue()==null || fParcial.getValue()==null)
			return;

		currentCourse  = new X_CA_CourseDef(m_ctx, (Integer)fCourseDef.getValue(), null);

		currentParcial = new X_CA_Parcial(m_ctx, (Integer)fParcial.getValue(), null);

		if(fMatterAssignment.getValue()==null)
			return;
		currentMatterAssignment = new X_CA_MatterAssignment(m_ctx, (Integer)fMatterAssignment.getValue(), null);
		
		isdiscipline = isDiscipline.isSelected();
	}

	@Override
	public ADForm getForm() {

		return form;
	}

}
