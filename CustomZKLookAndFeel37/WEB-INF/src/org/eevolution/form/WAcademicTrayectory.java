package org.eevolution.form;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
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
import org.compiere.model.MBPartner;
import org.compiere.model.MColumn;
import org.compiere.model.Query;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.fcaq.model.X_CA_CourseDef;
import org.fcaq.util.AcademicUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Hbox;

public class WAcademicTrayectory extends AcademicTrayectory implements IFormController, EventListener, WTableModelListener, ValueChangeListener{

	
	private CustomForm form = new CustomForm();
	private Borderlayout mainLayout = new Borderlayout();
	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();
	private Hbox hboxBtnRight;
	private Panel pnlBtnRight;
	private Button bShowComments = new Button();
	
	private WTableDirEditor fCourseDef = null;
	private Label lCourseDef = new Label();
	
	public static CLogger log = CLogger.getCLogger(AcademicTrayectory.class);

	public WAcademicTrayectory()
	{
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

	
	private void zkInit() {
		form.appendChild(mainLayout);
		mainLayout.setWidth("99%");
		mainLayout.setHeight("100%");

		parameterPanel.appendChild(parameterLayout);


		North north = new North();
		north.setStyle("border: none");
		mainLayout.appendChild(north);
		north.appendChild(parameterPanel);


		parameterLayout.setWidth("800px");
		parameterLayout.setHeight("50px");

		repaintParameterPanel();

		Center center = new Center();
		center.setFlex(true);
		mainLayout.appendChild(center);

		center.appendChild(((WListbox)examTable));
		((WListbox)examTable).setWidth("99%");
		((WListbox)examTable).setHeight("99%");
		center.setStyle("border: none");

		South south = new South();
		south.setStyle("border: none");
		mainLayout.appendChild(south);
		Panel southPanel = new Panel();
		south.appendChild(southPanel);

		pnlBtnRight = new Panel();
		pnlBtnRight.setAlign("right");
		pnlBtnRight.appendChild(bShowComments);

		hboxBtnRight = new Hbox();
		hboxBtnRight.appendChild(pnlBtnRight);
		hboxBtnRight.setWidth("100%");
		hboxBtnRight.setStyle("text-align:right");

		southPanel.appendChild(hboxBtnRight);
		southPanel.setWidth("100%");
	}
	
	


	private void repaintParameterPanel() {
		Rows rows = null;
		Row row = null;

		try{
			parameterLayout.removeChild(parameterLayout.getRows());
		}catch(Exception e)
		{
			// Nothing to do, just ignore
		}

		rows = parameterLayout.newRows();

		row = rows.newRow();
		row.appendChild(lCourseDef);
		row.appendChild(fCourseDef.getComponent());
		
	}


	private void dynInit() {
		bShowComments.setLabel(Msg.getMsg(Env.getCtx(), "Print"));
		bShowComments.addActionListener(this);
		
		examTable = new WListbox();
		((WListbox)examTable).setWidth("100%");
		((WListbox)examTable).setHeight("100%");
		((WListbox)examTable).setFixedLayout(false);
		((WListbox)examTable).setVflex(true);
		((WListbox)examTable).setStyle("overflow:auto;");
		
		lCourseDef.setText(Msg.getMsg(Env.getCtx(), "Group"));
		
		String whereClause = 
				" AND CA_CourseDef.CA_CourseDef_ID IN (SELECT c.CA_CourseDef_ID FROM CA_CourseDef c " + 
				"INNER JOIN CA_Role_OthersAccess ra ON (c.Modality = ra.Modality AND c.Section = ra.Section AND ra.IsActive = 'Y') " + 
				"INNER JOIN AD_Role r ON (ra.AD_Role_ID = r.AD_Role_ID AND r.AD_Role_ID = " + Env.getContextAsInt(Env.getCtx(), "#AD_Role_ID") + ") " +
				"WHERE c.IsActive = 'Y' AND c.IsElective='N') and CA_CourseDef.IsSport='N' AND CA_CourseDef.Grade='12' AND CA_CourseDef.CA_SchoolYear_ID=" + schoolYear.get_ID();

		int coursecolumn_id = MColumn.getColumn_ID("CA_CourseDef", "CA_CourseDef_ID");
		fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.buildLookup(coursecolumn_id, whereClause, form.getWindowNo()));
		fCourseDef.addValueChangeListener(this);

	}


	@Override
	public void valueChange(ValueChangeEvent evt) {
		String name = evt.getPropertyName();
		Object value = evt.getNewValue();

		if("CA_CourseDef_ID".equals(name))
		{
			if(value==null)
			{
				currentCourse = null;
			}
			else
			{
				fCourseDef.setValue(value);
				currentCourse = new X_CA_CourseDef(m_ctx, (Integer)value, null);
			}
		}
		
		refreshExamTable();
	}

	private void refreshExamTable() {

		examTable.setRowCount(0);

		if(currentCourse==null)
			return;
		
		Vector<String> columns = getColumns();

		Vector<Vector<Object>> data = getStudentData();

		ListModelTable modelP = new ListModelTable(data);

		modelP.addTableModelListener(this);
		((WListbox)examTable).setData(modelP, columns);

		((WListbox)examTable).setStyle("sizedByContent=true");

		examTable.setColumnClass(0, String.class, true); //key
		examTable.setColumnClass(1, String.class, true); //student
		examTable.setColumnClass(2, BigDecimal.class, true); // avegrade
		examTable.setColumnClass(3, BigDecimal.class, isReadOnly());//Monografy
		examTable.setColumnClass(4, BigDecimal.class, true); // ave1
		examTable.setColumnClass(5, BigDecimal.class, true); // ave2
		examTable.setColumnClass(6, BigDecimal.class, true); // avefinal
		
		examTable.autoSize();
		((WListbox)examTable).setWidth("100%");
		((WListbox)examTable).setHeight("100%");
		
		if(LOAD_FROM_CAS)
			refreshAverages();
	}


	private boolean isReadOnly() {
		return false;
	}


	@Override
	public void tableChanged(WTableModelEvent event) {
		if(event.getIndex0()>=0)
		{
			int column = event.getColumn();
			String studentvalue = (String) examTable.getValueAt(event.getIndex0(), 0);
			MBPartner student = new Query(Env.getCtx(), MBPartner.Table_Name, MBPartner.COLUMNNAME_Value+"=?", null)
			.setOnlyActiveRecords(true)
			.setParameters(studentvalue)
			.first();
			
			if(column==(3)) //Monografy
			{
				BigDecimal newValue = (BigDecimal)examTable.getValueAt(event.getIndex0(), 3);
				if(student!=null)
				{
					if(newValue.compareTo(new BigDecimal(0))<0 || newValue.compareTo(new BigDecimal(100))>0)
					{
						newValue = BigDecimal.ZERO;
						examTable.setValueAt(newValue, event.getIndex0(), 3);

					}
					else
					{
						saveExam(student, event.getIndex0(), newValue);
					}
				}
			}
		}
	}




	@Override
	public void onEvent(Event event) throws Exception {

		if(event.getTarget().equals(bShowComments))
		{
			
				List<String> description = new ArrayList<String>();
				

				if(currentCourse!=null)
					description.add(fCourseDef.getDisplay());

				WGradeViewer  gradeViewer = new WGradeViewer(description, examTable);
				
				gradeViewer.setSizable(true);
				gradeViewer.setWidth("700px");
				gradeViewer.setHeight("600px");
				gradeViewer.setShadow(true);
				gradeViewer.setBorder("normal");
				gradeViewer.setClosable(true);
				gradeViewer.setTitle(Msg.translate(Env.getCtx(),"Grades"));
				gradeViewer.setContentStyle("overflow: auto");
	
				AEnv.showCenterScreen(gradeViewer);

		}
	}

	@Override
	public ADForm getForm() {
		return form;
	}

}
