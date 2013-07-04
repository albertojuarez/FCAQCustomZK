package org.eevolution.form;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.editor.WDateEditor;
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
import org.compiere.model.MBPartner;
import org.compiere.model.MRole;
import org.compiere.model.MUser;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.fcaq.model.MCATestEvaluationPeriod;
import org.fcaq.model.X_CA_CourseDef;
import org.fcaq.model.X_CA_EvaluationPeriod;
import org.fcaq.model.X_CA_MatterAssignmentSpecial;
import org.fcaq.model.X_CA_MatterSpecialAnualAvg;
import org.fcaq.model.X_CA_MatterSpecialNote;
import org.fcaq.model.X_CA_Parcial;
import org.fcaq.model.X_CA_SchoolYear;
import org.fcaq.model.X_CA_SchoolYearConfig;
import org.fcaq.util.AcademicUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;

public class WAcademicNoteSpecialMatter extends AcademicNoteSpecialMatter
		implements IFormController, EventListener, ValueChangeListener,
		WTableModelListener {
	// Layout components
	private CustomForm form = new CustomForm();
	private Borderlayout mainLayout = new Borderlayout();
	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();

	private Label lCourseDef = null;
	private Label lEvaluation = null;
	private Label lSubjectMatterSpecial = null;

	private WTableDirEditor fCourseDef = null;
	private WTableDirEditor fEvaluation = null;
	private WTableDirEditor fMatterAssignmentSpecial = null;

	public WAcademicNoteSpecialMatter() {
		// super();
		try {
			loadStartData();
			dynInit();
			zkInit();
		} catch (Exception e) {
			log.log(Level.SEVERE, "", e);
		}

	}

	public void loadStartData() {
		System.out.println("Load Start Data Start At "
				+ new Timestamp(System.currentTimeMillis()));
		currentSchoolYear = AcademicUtil.getCurrentSchoolYear(m_ctx);
		yearConfig = AcademicUtil.getCurrentYearConfig(m_ctx);

		int AD_User_ID = Env.getContextAsInt(Env.getCtx(), "#AD_User_ID");
		currentUser = new MUser(m_ctx, AD_User_ID, null);

		int AD_Role_ID = Env.getContextAsInt(Env.getCtx(), "#AD_Role_ID");
		currentRole = new MRole(m_ctx, AD_Role_ID, null);

		String whereClause = "C_BPartner_ID IN (Select C_BPartner_ID from AD_User where AD_User_ID = ?)";
		currentBPartner = new Query(m_ctx, MBPartner.Table_Name, whereClause,
				null).setParameters(AD_User_ID).setOnlyActiveRecords(true)
				.first();

		if (currentSchoolYear == null || currentUser == null
				|| currentRole == null || currentBPartner == null) {
			showErrorMessage("Cant load start data, User, Role or BPartner is missing... may be you are not permission to open");
			dispose();
		}

		System.out.println("Load Start Data End At "
				+ new Timestamp(System.currentTimeMillis()));
	}

	// Init Search Editor
	private void dynInit() {

		studentTable = new WListbox();
		((WListbox) studentTable).setWidth("100%");
		((WListbox) studentTable).setHeight("100%");
		((WListbox) studentTable).setFixedLayout(false);
		((WListbox) studentTable).setVflex(true);
		((WListbox) studentTable).setStyle("overflow:auto;");

		fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true,
				AcademicUtil.getCourseSpecialMatterLookup(form.getWindowNo(),
						currentBPartner.get_ID()));
		fCourseDef.addValueChangeListener(this);

		fMatterAssignmentSpecial = new WTableDirEditor(
				"CA_MatterAssignmentSpecial_ID", true, false, true,
				AcademicUtil.getMatterAssignmenSpecialtLookup(
						form.getWindowNo(), currentBPartner.get_ID(), 0, 0));
		fMatterAssignmentSpecial.addValueChangeListener(this);

		fEvaluation = new WTableDirEditor("CA_EvaluationPeriod_ID", true,
				false, true, AcademicUtil.getEvaluationPeriodLookup(
						form.getWindowNo(), currentSchoolYear.get_ID(), 0));
		fEvaluation.addValueChangeListener(this);

	}

	// Init Components
	private void zkInit() {

		form.appendChild(mainLayout);
		mainLayout.setWidth("99%");
		mainLayout.setHeight("100%");

		parameterPanel.appendChild(parameterLayout);

		lCourseDef = new Label();
		lCourseDef.setText(Msg.getMsg(Env.getCtx(), "Group"));

		lEvaluation = new Label();
		lEvaluation.setText(Msg.getMsg(Env.getCtx(), "Quimestre"));

		lSubjectMatterSpecial = new Label();
		lSubjectMatterSpecial
				.setText(Msg.getMsg(Env.getCtx(), "SubjectMatter"));

		North north = new North();
		north.setStyle("border: none");
		mainLayout.appendChild(north);
		north.appendChild(parameterPanel);
		Rows rows = null;
		Row row = null;
		parameterLayout.setWidth("800px");
		rows = parameterLayout.newRows();

		row = rows.newRow();

		row.appendChild(lCourseDef);
		row.appendChild(fCourseDef.getComponent());
		fCourseDef.getComponent().setWidth("60%");
		row = rows.newRow();
		row.appendChild(lSubjectMatterSpecial);
		row.appendChild(fMatterAssignmentSpecial.getComponent());
		row.appendChild(lEvaluation);
		row.appendChild(fEvaluation.getComponent());
		row = rows.newRow();

		Center center = new Center();
		center.setFlex(true);
		mainLayout.appendChild(center);

		center.appendChild(((WListbox) studentTable));
		((WListbox) studentTable).setWidth("99%");
		((WListbox) studentTable).setHeight("99%");
		center.setStyle("border: none");

	}

	@Override
	public void tableChanged(WTableModelEvent event) {

		if (event.getIndex0() >= 0) {
			int column = event.getColumn();
			
			String studentvalue = (String) studentTable.getValueAt(
					event.getIndex0(), 0);
			BigDecimal newValue = (BigDecimal) studentTable.getValueAt(
					event.getIndex0(), 2);
			


			MBPartner student = new Query(Env.getCtx(), MBPartner.Table_Name,
					MBPartner.COLUMNNAME_Value + "=?", null)
					.setOnlyActiveRecords(true).setParameters(studentvalue)
					.first();
			if (student != null && loopblock == false) {
				
				loopblock = true;
				
				if(newValue==null)
				{
					trySaveNull(student,
							event.getIndex0());
					
				}
				else
				{
					if(column==2)
					{
						studentTable.setValueAt(AcademicUtil.applyRound(newValue, newValue, "O"), event.getIndex0(), column);
					}
					
					if (newValue.compareTo(new BigDecimal(0)) < 0
							|| newValue.compareTo(currentMatterAssignmentSpecial
									.getMaxEntry()) > 0) {
						newValue = BigDecimal.ZERO;
						studentTable.setValueAt(newValue, event.getIndex0(), 2);
					} else {
						refreshMatterAssignmentSpecial(student, newValue,
								event.getIndex0());
						
						X_CA_MatterSpecialAnualAvg mtterSpecialAnualAvg = AcademicUtil
								.getMatterSpecialNoteAvgAnual(student,
										currentMatterAssignmentSpecial,
										currentCourse.getCA_CourseDef_ID(),
										currentSchoolYear.getCA_SchoolYear_ID());
						studentTable.setValueAt(mtterSpecialAnualAvg.getAmount(),
								event.getIndex0(), 3);
					}
				}
			} else {
				loopblock = false;
			}
		}
	}



	private void prepareLoadTable() {

		studentTable.setRowCount(0);

		if (fCourseDef.getValue() == null || fEvaluation.getValue() == null)
			return;

		currentCourse = new X_CA_CourseDef(m_ctx,
				(Integer) fCourseDef.getValue(), null);

		currentEvaluation = new X_CA_EvaluationPeriod(m_ctx,
				(Integer) fEvaluation.getValue(), null);

		if (fMatterAssignmentSpecial.getValue() == null)
			return;
		currentMatterAssignmentSpecial = new X_CA_MatterAssignmentSpecial(
				m_ctx, (Integer) fMatterAssignmentSpecial.getValue(), null);
	}



	private void refreshStudentTable() {
		completed = false;
		prepareLoadTable();

		if (currentCourse != null && currentEvaluation != null
				&& currentMatterAssignmentSpecial != null) {

			if (canEdit(currentMatterAssignmentSpecial)){
				Vector<String> columns = buildNoteHeading();
	
				Vector<Vector<Object>> data = getStudentData();
	
				ListModelTable modelP = new ListModelTable(data);
	
				((WListbox) studentTable).setData(modelP, columns);
	
				((WListbox) studentTable).setStyle("sizedByContent=true");
	
				modelP.addTableModelListener(this);
	
				studentTable.setColumnClass(0, String.class, true);
	
				studentTable.setColumnClass(1, Object.class, true);
				studentTable.setColumnClass(2, BigDecimal.class, completed);
				studentTable.setColumnClass(3, BigDecimal.class, true);
	
				// /studentTable.autoSize();
				((WListbox) studentTable).setWidth("100%");
				((WListbox) studentTable).setHeight("100%");
				
			}else {
				showInfoMessage(Msg.translate(Env.getCtx(), "Grade_not_entry_date"));
			}
		}
	}

	public void repaintParameterPanel() {
		parameterLayout.removeChild(parameterLayout.getRows());
		Rows rows = null;
		Row row = null;
		parameterLayout.setWidth("800px");
		rows = parameterLayout.newRows();
		row = rows.newRow();
		row.appendChild(lCourseDef);
		row.appendChild(fCourseDef.getComponent());
		fCourseDef.getComponent().setWidth("60%");
		row = rows.newRow();

		row.appendChild(lSubjectMatterSpecial);
		row.appendChild(fMatterAssignmentSpecial.getComponent());

		row.appendChild(lEvaluation);
		row.appendChild(fEvaluation.getComponent());

	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		String name = evt.getPropertyName();
		Object value = evt.getNewValue();
		if (value == null)
			return;
		if ("CA_CourseDef_ID".equals(name)) {
			fCourseDef.setValue(value);
			currentCourse = new X_CA_CourseDef(m_ctx,
					(Integer) fCourseDef.getValue(), null);
			fEvaluation = new WTableDirEditor("CA_EvaluationPeriod_ID", true,
					false, true, AcademicUtil.getEvaluationPeriodLookup(
							form.getWindowNo(), currentSchoolYear.get_ID(),
							(Integer) fCourseDef.getValue()));
			fEvaluation.addValueChangeListener(this);
			if (AcademicUtil.getCurrentEvaluationPeriod(m_ctx,
					(Integer) fCourseDef.getValue()) != null) {
				fEvaluation.setValue(AcademicUtil.getCurrentEvaluationPeriod(
						m_ctx, (Integer) fCourseDef.getValue()).get_ID());
				currentEvaluation = new X_CA_EvaluationPeriod(m_ctx,
						(Integer) fEvaluation.getValue(), null);
			}

			fMatterAssignmentSpecial = new WTableDirEditor(
					"CA_MatterAssignmentSpecial_ID", true, false, true,
					AcademicUtil.getMatterAssignmenSpecialtLookup(
							form.getWindowNo(), currentBPartner.get_ID(),
							(Integer) fCourseDef.getValue(),
							currentEvaluation.getCA_EvaluationPeriod_ID()));
			fMatterAssignmentSpecial.addValueChangeListener(this);
			currentMatterAssignmentSpecial = null;
			fMatterAssignmentSpecial.actionRefresh();
			repaintParameterPanel();
			refreshStudentTable();
		}

		if ("CA_MatterAssignmentSpecial_ID".equals(name)) {
			fMatterAssignmentSpecial.setValue(value);
			refreshStudentTable();
		}
		if ("CA_EvaluationPeriod_ID".equals(name)) {
			fEvaluation.setValue(value);
			currentEvaluation = new X_CA_EvaluationPeriod(m_ctx,
					(Integer) fEvaluation.getValue(), null);
			fMatterAssignmentSpecial = new WTableDirEditor(
					"CA_MatterAssignmentSpecial_ID", true, false, true,
					AcademicUtil.getMatterAssignmenSpecialtLookup(
							form.getWindowNo(), currentBPartner.get_ID(),
							(Integer) fCourseDef.getValue(),
							currentEvaluation.getCA_EvaluationPeriod_ID()));
			fMatterAssignmentSpecial.addValueChangeListener(this);
			currentMatterAssignmentSpecial = null;
			fMatterAssignmentSpecial.actionRefresh();
			repaintParameterPanel();
			refreshStudentTable();
		}
	}

	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public ADForm getForm() {
		return form;
	}

	@Override
	public void showErrorMessage(String message) {
	}

	@Override
	public void showInfoMessage(String message) {
		FDialog.info(form.getWindowNo(), null, message);
	}

	@Override
	public void dispose() {
	}
	
}
