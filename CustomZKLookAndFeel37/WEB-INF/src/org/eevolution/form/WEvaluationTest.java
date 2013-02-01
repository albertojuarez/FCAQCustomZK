
	package org.eevolution.form;

	import java.math.BigDecimal;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
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
import org.adempiere.webui.window.FDialog;
import org.compiere.model.MBPartner;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.fcaq.model.X_CA_CourseDef;
import org.fcaq.model.X_CA_EvaluationPeriod;
import org.fcaq.model.X_CA_MatterAssignment;
import org.fcaq.util.AcademicUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zul.Space;

	public class WEvaluationTest extends EvaluationTest
	implements IFormController, EventListener, ValueChangeListener, WTableModelListener
	{

		// Layout components
		private CustomForm form = new CustomForm();
		private Borderlayout mainLayout = new Borderlayout();
		private Panel parameterPanel = new Panel();
		private Grid parameterLayout = GridFactory.newGridLayout();

		private Label lCourseDef = null;
		private Label lEvaluation = null;
		private Label lSubjectMatter = null;

		private WTableDirEditor fCourseDef = null;
		private WTableDirEditor fEvaluation = null;
		private WTableDirEditor fMatterAssignment = null;

		private Checkbox isElective = new Checkbox();


		public WEvaluationTest()
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

			studentTable = new WListbox();
			((WListbox)studentTable).setWidth("100%");
			((WListbox)studentTable).setHeight("100%");
			((WListbox)studentTable).setFixedLayout(false);
			((WListbox)studentTable).setVflex(true);
			((WListbox)studentTable).setStyle("overflow:auto;");
			
			isElective.setSelected(false);
			isElective.setLabel(Msg.getMsg(Env.getCtx(), "Is Elective"));


			fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),currentBPartner.get_ID(), isElective.isSelected()));
			fCourseDef.addValueChangeListener(this);

			fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, AcademicUtil.getMatterAssignmentLookup(form.getWindowNo(),currentBPartner.get_ID(), 0));
			fMatterAssignment.addValueChangeListener(this);


			fEvaluation = new WTableDirEditor("CA_EvaluationPeriod_ID", true, false, true, AcademicUtil.getEvaluationPeriodLookup(form.getWindowNo(),currentSchoolYear.get_ID(),0));
			fEvaluation.addValueChangeListener(this);


			isElective.addActionListener(this);
			

			
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

			row.appendChild(lCourseDef);
			row.appendChild(fCourseDef.getComponent());
			fCourseDef.getComponent().setWidth("60%");
			row = rows.newRow();
			row.appendChild(lSubjectMatter);
			row.appendChild(fMatterAssignment.getComponent());
			row.appendChild(lEvaluation);
			row.appendChild(fEvaluation.getComponent());
			row = rows.newRow();
			
			Center center = new Center();
			center.setFlex(true);
			mainLayout.appendChild(center);

			center.appendChild(((WListbox)studentTable));
			((WListbox)studentTable).setWidth("99%");
			((WListbox)studentTable).setHeight("99%");
			center.setStyle("border: none");

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

		@Override
		public void valueChange(ValueChangeEvent evt) {

			String name = evt.getPropertyName();
			Object value = evt.getNewValue();


			if (value == null)
				return;

			if ("CA_CourseDef_ID".equals(name))
			{


				fCourseDef.setValue(value);
				
				currentCourse = new X_CA_CourseDef(m_ctx, (Integer)fCourseDef.getValue(), null);
				
				isSport = currentCourse.isSport();
				
				fEvaluation = new WTableDirEditor("CA_EvaluationPeriod_ID", true, false, true, AcademicUtil.getEvaluationPeriodLookup(form.getWindowNo(),currentSchoolYear.get_ID(),(Integer)fCourseDef.getValue()));
				fEvaluation.addValueChangeListener(this);
				if(AcademicUtil.getCurrentEvaluationPeriod(m_ctx, (Integer)fCourseDef.getValue())!=null)
				{
					fEvaluation.setValue(AcademicUtil.getCurrentEvaluationPeriod(m_ctx, (Integer)fCourseDef.getValue()).get_ID());
					currentEvaluation = new X_CA_EvaluationPeriod(m_ctx, (Integer)fEvaluation.getValue(), null);
				}
				
				fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, AcademicUtil.getMatterAssignmentLookup(form.getWindowNo(),currentBPartner.get_ID(), (Integer)fCourseDef.getValue()));
				fMatterAssignment.addValueChangeListener(this);
				currentMatterAssignment=null;

				fMatterAssignment.actionRefresh();
				
				

				repaintParameterPanel();
				refreshStudentTable();

			}

			if ("CA_MatterAssignment_ID".equals(name))
			{


				fMatterAssignment.setValue(value);

				refreshStudentTable();

			}
			if ("CA_EvaluationPeriod_ID".equals(name))
			{
				fEvaluation.setValue(value);
				refreshStudentTable();

			}
		}


		private void refreshStudentTable() {
			completed=false;
			prepareLoadTable();
			
			if(currentCourse!=null &&   currentEvaluation !=null && currentMatterAssignment!=null)
			{
				Vector<String> columns = buildNoteHeading();

				Vector<Vector<Object>> data = getStudentData();

				ListModelTable modelP = new ListModelTable(data);

				
				((WListbox)studentTable).setData(modelP, columns);

				((WListbox)studentTable).setStyle("sizedByContent=true");
				
				modelP.addTableModelListener(this);


				studentTable.setColumnClass(0, String.class, true);
				studentTable.setColumnClass(1, Object.class, true);
				studentTable.setColumnClass(2, BigDecimal.class, true);
				studentTable.setColumnClass(3, BigDecimal.class, completed);
				studentTable.setColumnClass(4, BigDecimal.class, true);
				studentTable.setColumnClass(5, BigDecimal.class, true);
				
				studentTable.autoSize();
				((WListbox)studentTable).setWidth("100%");
				((WListbox)studentTable).setHeight("100%");
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

			row.appendChild(lCourseDef);
			row.appendChild(fCourseDef.getComponent());
			fCourseDef.getComponent().setWidth("60%");
			row = rows.newRow();

			row.appendChild(lSubjectMatter);
			row.appendChild(fMatterAssignment.getComponent());

			row.appendChild(lEvaluation);
			row.appendChild(fEvaluation.getComponent());
			
			
		}


		@Override
		public void onEvent(Event event) throws Exception {
			if (event.getTarget().equals(isElective))
			{

				fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true,AcademicUtil.getCourseLookup(form.getWindowNo(),currentBPartner.get_ID(), isElective.isSelected()));
				fCourseDef.addValueChangeListener(this);


				repaintParameterPanel();
			}
		}

		private void prepareLoadTable() {

			studentTable.setRowCount(0);
			
			if(fCourseDef.getValue()==null || fEvaluation.getValue()==null)
				return;

			currentCourse  = new X_CA_CourseDef(m_ctx, (Integer)fCourseDef.getValue(), null);

			currentEvaluation = new X_CA_EvaluationPeriod(m_ctx, (Integer)fEvaluation.getValue(), null);

			if(fMatterAssignment.getValue()==null)
				return;
			currentMatterAssignment = new X_CA_MatterAssignment(m_ctx, (Integer)fMatterAssignment.getValue(), null);
		}

		@Override
		public ADForm getForm() {

			return form;
		}

		@Override
		public void tableChanged(WTableModelEvent event) {
			if(event.getIndex0()>=0)
			{
				String studentvalue = (String) studentTable.getValueAt(event.getIndex0(),0);
				BigDecimal newValue = (BigDecimal)studentTable.getValueAt(event.getIndex0(), 3);

				MBPartner student = new Query(Env.getCtx(), MBPartner.Table_Name, MBPartner.COLUMNNAME_Value+"=?", null)
				.setOnlyActiveRecords(true)
				.setParameters(studentvalue)
				.first();
				
				if(student!=null && loopblock==false)
				{
					loopblock = true;
					
					if(newValue.compareTo(new BigDecimal(0))<0 || newValue.compareTo(new BigDecimal(100))>0)
					{
						newValue = BigDecimal.ZERO;
						studentTable.setValueAt(newValue, event.getIndex0(), 3);

					}
					else
					{
						refreshTestEvaluationPeriod( student, newValue, event.getIndex0());
					}
				}
				else
				{
					loopblock=false;
				}
			}
		}

	}


