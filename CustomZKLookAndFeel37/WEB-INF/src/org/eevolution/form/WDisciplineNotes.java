package org.eevolution.form;

import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.panel.StatusBarPanel;
import org.adempiere.webui.window.FDialog;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.fcaq.components.INoteEditor;
import org.fcaq.components.WNoteEditor;
import org.fcaq.util.AcademicUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zul.Space;

public class WDisciplineNotes extends DisciplineNotes implements IFormController, EventListener, WTableModelListener, ValueChangeListener{

	private CustomForm form = new CustomForm();
	private Borderlayout mainLayout = new Borderlayout();
	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();


	private Panel southPanel = new Panel();
	private StatusBarPanel statusBar = new StatusBarPanel();
	private WListbox noteTable = null; // ListboxFactory.newDataTable();

	// Form Components

	private Checkbox isElective = new Checkbox();

	private Checkbox isOverFour = new Checkbox();
	private Label lCourseDef = null;
	private Label lParcial = null;
	private Label lSubjectMatter = null;

	private WTableDirEditor fCourseDef = null;
	private WTableDirEditor fParcial = null;
	private WTableDirEditor fMatterAssignment = null;


	public WDisciplineNotes()
	{
		try{
			loadStartData();
			dynInit();
			zkInit();
		}
		catch(Exception e)
		{

		}

	}


	private void dynInit()
	{
		isElective.setSelected(false);
		isOverFour.setSelected(true);
		
		noteTable = new WListbox();
		noteTable.setWidth("100%");
		noteTable.setHeight("100%");
		noteTable.setFixedLayout(false);
		noteTable.setVflex(true);
		
		isElective.setLabel(Msg.getMsg(Env.getCtx(), "Is Elective"));
		isOverFour.setLabel(Msg.getMsg(Env.getCtx(), "Scale 100"));

		fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),currentBPartner.get_ID(), isElective.isSelected()));
		fCourseDef.addValueChangeListener(this);

		fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, AcademicUtil.getMatterAssignmentLookup(form.getWindowNo(),currentBPartner.get_ID()));
		fMatterAssignment.addValueChangeListener(this);

		fParcial = new WTableDirEditor("CA_Parcial_ID", true, false, true, AcademicUtil.getParcialLookup(form.getWindowNo(),currentSchoolYear.get_ID()));
		fParcial.addValueChangeListener(this);
		fParcial.setValue(AcademicUtil.getCurrentParcial(m_ctx).get_ID());
		
		isElective.addActionListener(this);
		
	}


	private void zkInit()
	{
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
		row.appendChild(new Space());
		row.appendChild(isOverFour);
		row = rows.newRow();
		row.appendChild(lCourseDef);
		row.appendChild(fCourseDef.getComponent());
		fCourseDef.getComponent().setWidth("60%");
		row.appendChild(lSubjectMatter);
		row.appendChild(fMatterAssignment.getComponent());
		row = rows.newRow();
		row.appendChild(lParcial);
		row.appendChild(fParcial.getComponent());

		Center center = new Center();
		center.setFlex(true);
		mainLayout.appendChild(center);

		center.appendChild(noteTable);
		noteTable.setWidth("99%");
		noteTable.setHeight("99%");
		center.setStyle("border: none");

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
		form.dispose();
	}


	@Override
	public void valueChange(ValueChangeEvent evt) {

	}


	@Override
	public void tableChanged(WTableModelEvent event) {

	}


	@Override
	public void onEvent(Event event) throws Exception {

		if (event.getTarget().equals(isElective))
		{
			fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),currentBPartner.get_ID(), isElective.isSelected()));
			fCourseDef.addValueChangeListener(this);

			repaintParameterPanel();
			refreshHeader();
		}
	}

	private void refreshHeader() {

	}


	private void repaintParameterPanel() {

		parameterLayout.removeChild(parameterLayout.getRows());
		Rows rows = null;
		Row row = null;
		parameterLayout.setWidth("800px");
		rows = parameterLayout.newRows();
		
		row = rows.newRow();
		row.appendChild(new Space());
		row.appendChild(isElective);
		row.appendChild(new Space());
		row.appendChild(isOverFour);
		row = rows.newRow();
		row.appendChild(lCourseDef);
		row.appendChild(fCourseDef.getComponent());
		fCourseDef.getComponent().setWidth("60%");
		row.appendChild(lSubjectMatter);
		row.appendChild(fMatterAssignment.getComponent());
		row = rows.newRow();
		row.appendChild(lParcial);
		row.appendChild(fParcial.getComponent());
	}


	@Override
	public INoteEditor getNoteComponent() {
		return new WNoteEditor();
	}
}
