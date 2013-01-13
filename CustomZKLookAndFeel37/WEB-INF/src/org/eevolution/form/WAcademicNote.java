package org.eevolution.form;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListHead;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.WListItemRenderer;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.component.WTableColumn;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.panel.StatusBarPanel;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.MBPartner;
import org.compiere.model.MColumn;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MLookupInfo;
import org.compiere.model.Query;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.TrxRunnable;
import org.fcaq.components.INoteEditor;
import org.fcaq.components.WNoteEditor;
import org.fcaq.model.X_CA_ConcatenatedSubject;
import org.fcaq.model.X_CA_CourseDef;
import org.fcaq.model.X_CA_DisciplineConfig;
import org.fcaq.model.X_CA_EvaluationPeriod;
import org.fcaq.model.X_CA_GroupAssignment;
import org.fcaq.model.X_CA_MatterAssignment;
import org.fcaq.model.X_CA_NoteCategory;
import org.fcaq.model.X_CA_NoteHeadingLine;
import org.fcaq.model.X_CA_NoteLine;
import org.fcaq.model.X_CA_NoteType;
import org.fcaq.model.X_CA_Parcial;
import org.fcaq.model.X_CA_SubjectConfig;
import org.fcaq.model.X_CA_SubjectMatter;
import org.fcaq.util.AcademicUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;

public class WAcademicNote extends AcademicNote
implements IFormController, EventListener, WTableModelListener, ValueChangeListener
{

	// Layout components
	private CustomForm form = new CustomForm();
	private Borderlayout mainLayout = new Borderlayout();
	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();
	private Panel southPanel = new Panel();
	private StatusBarPanel statusBar = new StatusBarPanel();
	private WListbox noteHeadingTable = ListboxFactory.newDataTable();
	private Button bSendNotes = new Button();
	private Button bShowComments = new Button();
	private Hbox hboxBtnRight;
	private Panel pnlBtnRight;


	// Form Components
	private Label lCourseDef = null;
	private Label lParcial = null;
	private Label lSubjectMatter = null;
	private Label lConcat = new Label("");

	private WTableDirEditor fCourseDef = null;
	private WTableDirEditor fParcial = null;
	private WTableDirEditor fMatterAssignment = null;
	private WTableDirEditor fSubject = null;

	private Checkbox isElective = new Checkbox();

	private WTableDirEditor noteCategory  = null;
	private WTableDirEditor noteType = null;

	private WDateEditor fDate = new WDateEditor();




	public WAcademicNote()
	{
		try
		{
			Env.setContext(Env.getCtx(), "GradeWindowNo", form.getWindowNo());
			loadStartData();

			dynInit();
			zkInit();
			loadNoteTable();
			southPanel.appendChild(new Separator());
			southPanel.appendChild(statusBar);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "", e);
		}
	}


	private void loadNoteTable() {

		Vector<Vector<Object>> data = getNoteData();
		Vector<String> columnNames = getEmptyColumn();

		((WListbox)noteTable).clear();
		((WListbox)noteTable).getModel().removeTableModelListener(this);

		ListModelTable modelP = new ListModelTable(data);
		modelP.addTableModelListener(this);
		((WListbox)noteTable).setData(modelP, columnNames);

		((WListbox)noteTable).setColumnClass(0, String.class, true);			//  0-Student
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

		bShowComments.setLabel(Msg.getMsg(Env.getCtx(), "ShowComments"));
		bSendNotes.setLabel(Msg.getMsg(Env.getCtx(), "SendNotes"));
		
		fDate.setValue(new Date());

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
		row.appendChild(fSubject.getComponent());
		row.appendChild(lParcial);
		row.appendChild(fParcial.getComponent());

		row = rows.newRow();
		row.appendChild(new Space());
		row.appendChild(lConcat);


		Center center = new Center();
		center.setFlex(true);
		mainLayout.appendChild(center);

		center.appendChild(((WListbox)noteTable));
		((WListbox)noteTable).setWidth("99%");
		((WListbox)noteTable).setHeight("99%");
		center.setStyle("border: none");

		South south = new South();
		south.setStyle("border: none");
		mainLayout.appendChild(south);
		Panel southPanel = new Panel();
		south.appendChild(southPanel);

		pnlBtnRight = new Panel();
		pnlBtnRight.setAlign("right");
		pnlBtnRight.appendChild(bShowComments);
		//pnlBtnRight.appendChild(bSendNotes);

		hboxBtnRight = new Hbox();
		hboxBtnRight.appendChild(pnlBtnRight);
		hboxBtnRight.setWidth("100%");
		hboxBtnRight.setStyle("text-align:right");

		southPanel.appendChild(hboxBtnRight);
		southPanel.setWidth("100%");

	}

	// Init Search Editor
	private void dynInit() {

		noteTable = new WListbox();
		((WListbox)noteTable).setWidth("100%");
		((WListbox)noteTable).setHeight("100%");
		((WListbox)noteTable).setFixedLayout(false);
		((WListbox)noteTable).setVflex(true);
		((WListbox)noteTable).setStyle("overflow:auto;");

		fDate.setValue(new Timestamp(date.getTime()));
		fDate.addValueChangeListener(this);

		isElective.setSelected(false);
		isElective.setLabel(Msg.getMsg(Env.getCtx(), "Is Elective"));


		fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),currentBPartner.get_ID(), isElective.isSelected()));
		fCourseDef.addValueChangeListener(this);

		fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, AcademicUtil.getMatterAssignmentLookup(form.getWindowNo(),currentBPartner.get_ID()));
		fMatterAssignment.addValueChangeListener(this);

		fSubject = new WTableDirEditor("CA_SubjectMatter_ID", true, false, true, AcademicUtil.getSubjectLookup(form.getWindowNo(),currentBPartner.get_ID(),0,0));
		fSubject.addValueChangeListener(this);


		fParcial = new WTableDirEditor("CA_Parcial_ID", true, false, true, AcademicUtil.getParcialLookup(form.getWindowNo(),currentSchoolYear.get_ID(), 0));
		fParcial.addValueChangeListener(this);
		//fParcial.setValue(AcademicUtil.getCurrentParcial(m_ctx,0)!=null?AcademicUtil.getCurrentParcial(m_ctx,0).get_ID():null);
		//currentParcial = new X_CA_Parcial(m_ctx, (Integer)fParcial.getValue(), null);

		int noteCategoryColumn_ID = MColumn.getColumn_ID(X_CA_NoteHeadingLine.Table_Name, X_CA_NoteHeadingLine.COLUMNNAME_CA_NoteCategory_ID);
		int noteTypeColumn_ID = MColumn.getColumn_ID(X_CA_NoteHeadingLine.Table_Name, X_CA_NoteHeadingLine.COLUMNNAME_CA_NoteType_ID);



		String whereClause =  " AND User1_ID=" + currentUser.get_ID() + " AND " + X_CA_NoteCategory.COLUMNNAME_CA_NoteCategory_ID + 
				" IN ( SELECT " + X_CA_NoteHeadingLine.COLUMNNAME_CA_NoteCategory_ID + 
				" FROM " + X_CA_NoteHeadingLine.Table_Name + " WHERE IsActive='Y') ORDER BY " + X_CA_NoteCategory.COLUMNNAME_SeqNo;



		MLookupInfo info = MLookupFactory.getLookupInfo (Env.getCtx(), form.getWindowNo(), noteCategoryColumn_ID, DisplayType.TableDir);
		MLookup lookup = new MLookup(info,0);
		String sql = info.Query.substring(0, info.Query.indexOf(" ORDER BY"));
		sql = sql + whereClause;
		info.Query = sql;

		noteCategory  = new WTableDirEditor("CA_NoteCategory_ID", true, false, true, lookup);
		noteType = new WTableDirEditor("CA_NoteType_ID", true, false, true, AcademicUtil.buildLookup(noteTypeColumn_ID, " AND User1_ID=" + currentUser.get_ID(), form.getWindowNo()));

		noteCategory.addValueChangeListener(this);
		noteType.addValueChangeListener(this);

		bShowComments.addActionListener(this);
		isElective.addActionListener(this);
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {

		String name = evt.getPropertyName();
		Object value = evt.getNewValue();

		clean();

		if (value == null)
			return;

		if ("CA_CourseDef_ID".equals(name))
		{
			isfilterenabled = false;

			fCourseDef.setValue(value);
			
			fParcial = new WTableDirEditor("CA_Parcial_ID", true, false, true, AcademicUtil.getParcialLookup(form.getWindowNo(),currentSchoolYear.get_ID(), (Integer)fCourseDef.getValue()));
			fParcial.addValueChangeListener(this);
			
			if(AcademicUtil.getCurrentParcial(m_ctx, (Integer)fCourseDef.getValue())!=null)
			{
				fParcial.setValue(AcademicUtil.getCurrentParcial(m_ctx, (Integer)fCourseDef.getValue()).get_ID());
				currentParcial = new X_CA_Parcial(m_ctx, (Integer)fParcial.getValue(), null);
			}
			

			fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, AcademicUtil.getMatterAssignmentLookup(form.getWindowNo(),currentBPartner.get_ID(), (Integer)fCourseDef.getValue()));
			fMatterAssignment.addValueChangeListener(this);

			fSubject = new WTableDirEditor("CA_SubjectMatter_ID", true, false, true, AcademicUtil.getSubjectLookup(form.getWindowNo(),currentBPartner.get_ID(),
					(Integer)fCourseDef.getValue(), 
					currentParcial!=null?
							((X_CA_EvaluationPeriod)currentParcial.getCA_EvaluationPeriod()).get_ValueAsInt("SeqNo")
							:0));
			
			fSubject.addValueChangeListener(this);

			fSubject.actionRefresh();
			fMatterAssignment.actionRefresh();

			X_CA_CourseDef tmpCourse = new X_CA_CourseDef(m_ctx,(Integer) fCourseDef.getValue(), null);

			if(tmpCourse!=null)
			{
				String section = tmpCourse.getSection();
				if(Integer.parseInt(section)<=3)
				{
					isfilterenabled = true;
					filtertype=2;
				}
			}
			
			

			repaintParameterPanel();
			refreshHeader();

		}
		if ("CA_SubjectMatter_ID".equals(name))
		{
			isfilterenabled = false;

			fSubject.setValue(value);

			X_CA_SubjectMatter tmpSubject = new X_CA_SubjectMatter(m_ctx,(Integer) fSubject.getValue(), null);



			X_CA_CourseDef tmpCourse = new X_CA_CourseDef(m_ctx,(Integer) fCourseDef.getValue(), null);

			X_CA_DisciplineConfig generalconfig = AcademicUtil.getParticularConfigByGroup(tmpCourse);

			if(generalconfig!=null) // if null asuming default config
			{
				if(generalconfig.getFilterType().equals(X_CA_DisciplineConfig.FILTERTYPE_Category))
				{
					isfilterenabled = true;
					filtertype=1;
				}
				else if(generalconfig.getFilterType().equals(X_CA_DisciplineConfig.FILTERTYPE_Subcategory))
				{
					isfilterenabled = true;
					filtertype=2;
				}
			}

			X_CA_SubjectConfig subjectconfig = AcademicUtil.getParticularConfigByGroupAndSubject(tmpCourse, tmpSubject);

			if(subjectconfig!=null) // if null asuming last config
			{
				if(subjectconfig.getFilterType().equals(X_CA_SubjectConfig.FILTERTYPE_Category))
				{
					isfilterenabled = true;
					filtertype=1;
				}
				else if(subjectconfig.getFilterType().equals(X_CA_SubjectConfig.FILTERTYPE_Subcategory))
				{
					isfilterenabled = true;
					filtertype=2;
				}
			}

			repaintParameterPanel();
			refreshHeader();
		}
		if ("CA_MatterAssignment_ID".equals(name))
		{
			isfilterenabled = false;

			fMatterAssignment.setValue(value);

			X_CA_MatterAssignment assignment = new X_CA_MatterAssignment(m_ctx, (Integer) fMatterAssignment.getValue(), null);

			X_CA_SubjectMatter tmpSubject = (X_CA_SubjectMatter) assignment.getCA_SubjectMatter();
			X_CA_CourseDef tmpCourse = new X_CA_CourseDef(m_ctx,(Integer) fCourseDef.getValue(), null);

			X_CA_DisciplineConfig generalconfig = AcademicUtil.getParticularConfigByGroup(tmpCourse);


			if(generalconfig!=null) // if null asuming default config
			{
				if(generalconfig.getFilterType().equals(X_CA_DisciplineConfig.FILTERTYPE_Category))
				{
					isfilterenabled = true;
					filtertype=1;
				}
				else if(generalconfig.getFilterType().equals(X_CA_DisciplineConfig.FILTERTYPE_Subcategory))
				{
					isfilterenabled = true;
					filtertype=2;
				}
			}

			X_CA_SubjectConfig subjectconfig = AcademicUtil.getParticularConfigByGroupAndSubject(tmpCourse, tmpSubject);

			if(subjectconfig!=null) // if null asuming last config
			{
				if(subjectconfig.getFilterType().equals(X_CA_SubjectConfig.FILTERTYPE_Category))
				{
					isfilterenabled = true;
					filtertype=1;
				}
				else if(subjectconfig.getFilterType().equals(X_CA_SubjectConfig.FILTERTYPE_Subcategory))
				{
					isfilterenabled = true;
					filtertype=2;
				}
			}


			repaintParameterPanel();
			refreshHeader();
		}
		if ("CA_Parcial_ID".equals(name))
		{
			fParcial.setValue(value);
			repaintParameterPanel();
			refreshHeader();
		}

		if("CA_NoteCategory_ID".equals(name))
		{

			noteCategory.setValue(value);

			int noteTypeColumn_ID = MColumn.getColumn_ID(X_CA_NoteHeadingLine.Table_Name, X_CA_NoteHeadingLine.COLUMNNAME_CA_NoteType_ID);

			String whereClause = " AND " + X_CA_NoteType.COLUMNNAME_CA_NoteType_ID + " IN (SELECT " + X_CA_NoteHeadingLine.COLUMNNAME_CA_NoteType_ID + 
					" FROM " + X_CA_NoteHeadingLine.Table_Name + " WHERE " + X_CA_NoteHeadingLine.COLUMNNAME_CA_NoteCategory_ID + "= " 
					+ (Integer)noteCategory.getValue() + ") ORDER BY " + X_CA_NoteType.COLUMNNAME_SeqNo;

			noteType = new WTableDirEditor("CA_NoteType_ID", true, false, true, AcademicUtil.buildLookup(noteTypeColumn_ID, " AND User1_ID=" + currentUser.get_ID() + whereClause, form.getWindowNo()));
			noteType.addValueChangeListener(this);
			repaintParameterPanel();
			refreshHeader();

		}
		if("CA_NoteType_ID".equals(name))
		{
			noteType.setValue(value);
			refreshHeader();
		}
		if("Date".equals(name))
		{
			date = (Timestamp) fDate.getValue();
			refreshHeader();

		}



	}

	@Override
	public void tableChanged(WTableModelEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEvent(Event event) throws Exception {

		if (event.getTarget().getId().equals("Ok"))
		{
			saveComments();
			comments.dispose();

		}
		else if (event.getTarget().getId().equals("Cancel"))
		{
			comments.dispose();	
		}
		else if (event.getTarget().equals(isElective))
		{

			fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),currentBPartner.get_ID(), isElective.isSelected()));
			fCourseDef.addValueChangeListener(this);

			if(isElective.isSelected())
			{
				inccol = 1;
				fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, AcademicUtil.getMatterAssignmentLookup(form.getWindowNo(),currentBPartner.get_ID(), 0));
				fMatterAssignment.addValueChangeListener(this);
			}
			else
			{
				fSubject = new WTableDirEditor("CA_SubjectMatter_ID", true, false, true, AcademicUtil.getSubjectLookup(form.getWindowNo(),currentBPartner.get_ID(),
						0,
						currentParcial!=null?
						((X_CA_EvaluationPeriod)currentParcial.getCA_EvaluationPeriod()).get_ValueAsInt("SeqNo")
						:0));
				
				fSubject.addValueChangeListener(this);
				inccol = 0;
			}

			repaintParameterPanel();
			refreshHeader();
		}
		else if (event.getTarget().equals(bShowComments)) 
		{
			int row = noteTable.getSelectedRow();

			if(row>=0)
			{

				rowNoteEditors = new ArrayList<INoteEditor>();

				for(int column=0 ; column<= noteTable.getColumnCount()-1; column++)
				{
					Object object = noteTable.getValueAt(row, column);

					if(object instanceof INoteEditor)
					{
						rowNoteEditors.add((INoteEditor) object);
					}
				}

				showComments(rowNoteEditors);
			}

		}
		else if(event.getTarget().equals(bSendNotes))
		{
			boolean sendnotes = FDialog.ask(form.getWindowNo(), null, Msg.getMsg(Env.getCtx(), "SureSendNotes"));

			if(sendnotes)
			{
				sendNotes();
			}
		}
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
		Env.setContext(Env.getCtx(), "IsGradeEnabled", 0);

		form.dispose();
		SessionManager.getAppDesktop().closeWindow(form.getWindowNo());

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

		if(isElective.isSelected())
		{
			row.appendChild(lSubjectMatter);
			row.appendChild(fMatterAssignment.getComponent());
		}
		else
		{
			row.appendChild(lSubjectMatter);
			row.appendChild(fSubject.getComponent());
		}

		row.appendChild(lParcial);
		row.appendChild(fParcial.getComponent());


		if(isfilterenabled)
		{
			Label lf1 = new Label(Msg.translate(m_ctx, "notecategory"));
			Label lf2 = new Label(Msg.translate(m_ctx, "notetype"));

			row = rows.newRow();
			row.appendChild(lf1);
			row.appendChild(noteCategory.getComponent());

			if(filtertype>1)
			{
				row.appendChild(lf2);
				row.appendChild(noteType.getComponent());
			}
		}
		
		if(fCourseDef.getValue()==null)
			return;
		
		currentCourse  = new X_CA_CourseDef(m_ctx, (Integer)fCourseDef.getValue(), null);
		
		if(currentCourse.isSport())
		{
			row.appendChild(fDate.getComponent());
		}
	}

	@Override
	public void refreshHeader(){



		System.out.println("Refresh Header Start At " + new Timestamp(System.currentTimeMillis()));
		((WListbox)noteTable).clear();
		((WListbox)noteTable).getModel().removeTableModelListener(this); 


		if(isfilterenabled && filtertype==2 && (noteType.getValue()==null || noteCategory.getValue()==null))
			return;

		if(isfilterenabled && filtertype==1 && (noteCategory.getValue()==null))
			return;

		if(isfilterenabled)
		{

			currentNoteCategory = new X_CA_NoteCategory(m_ctx,(Integer) noteCategory.getValue(), null);
			if(filtertype==2)
				currentNoteType = new X_CA_NoteType(m_ctx, (Integer)noteType.getValue(),null);
		}

		if(fCourseDef.getValue()==null || fParcial.getValue()==null)
			return;

		currentCourse  = new X_CA_CourseDef(m_ctx, (Integer)fCourseDef.getValue(), null);



		currentParcial = new X_CA_Parcial(m_ctx, (Integer)fParcial.getValue(), null);
		if(!isElective.isSelected())
		{
			if( fSubject.getValue()==null)
				return;

			currentSubject = new X_CA_SubjectMatter(m_ctx, (Integer)fSubject.getValue(), null);
		}
		else
		{

			if(fMatterAssignment.getValue()==null)
				return;
			currentMatterAssignment = new X_CA_MatterAssignment(m_ctx, (Integer)fMatterAssignment.getValue(), null);
			if(currentMatterAssignment.getElectiveSubject_ID()>0)
				currentSubject = new X_CA_SubjectMatter(m_ctx, currentMatterAssignment.getElectiveSubject_ID(), null);
		}

		if( currentSubject==null)
			return;

		if(!isElective.isSelected())
		{
			String where = X_CA_MatterAssignment.COLUMNNAME_CA_SubjectMatter_ID + "=? AND " + X_CA_MatterAssignment.COLUMNNAME_CA_GroupAssignment_ID + " IN (" +
					" SELECT " + X_CA_GroupAssignment.COLUMNNAME_CA_GroupAssignment_ID + " FROM " + X_CA_GroupAssignment.Table_Name + " WHERE " + 
					X_CA_GroupAssignment.COLUMNNAME_CA_CourseDef_ID + "=?)"; 


			currentMatterAssignment = new Query(m_ctx, X_CA_MatterAssignment.Table_Name, where, null )
			.setOnlyActiveRecords(true)
			.setParameters(currentSubject.get_ID(),currentCourse.get_ID())
			.first();
		}

		try
		{
			if( (currentParcial.getParcialAction().equals(X_CA_Parcial.PARCIALACTION_OpenParcial)) || 
					((Timestamp)currentParcial.get_Value("DateStart")).getTime()>System.currentTimeMillis() &&  
					((Timestamp)currentParcial.get_Value("DateEnd")).getTime()< System.currentTimeMillis() )
			{
				FDialog.warn(form.getWindowNo(), "El periodo de ingreso de notas esta cerrado");
			}
		}
		catch(Exception e)
		{
			//nothing to do
		}

		if(headingLines!=null)
			headingLines.clear();

		headingLines = null;

		if(currentCourse.isSport())
		{
			loadAsSport();
			return;
		}


		Vector<String> columns = buildNoteHeading();

		if(headingLines==null)
			return;


		Vector<Vector<Object>> data = getStudentData();

		ListModelTable modelP = new ListModelTable(data);

		modelP.addTableModelListener(this);
		((WListbox)noteTable).setData(modelP, columns);

		refreshNotes();

		((WListbox)noteTable).setStyle("sizedByContent=true");

		noteTable.setColumnClass(0, String.class, true);

		if(inccol==1)
		{
			noteTable.setColumnClass(1, String.class, true);
		}

		int index = 1;
		for(index = 1+inccol; index<= headingLines.size() + inccol; index++)
		{
			noteTable.setColumnClass(index, org.fcaq.components.WNoteEditor.class, false);
		}
		noteTable.setColumnClass(noteTable.getColumnCount()-1, org.fcaq.components.WNoteEditor.class, false);

		noteTable.autoSize();
		((WListbox)noteTable).setWidth("100%");
		((WListbox)noteTable).setHeight("100%");

		System.out.println("Refresh Header End At " + new Timestamp(System.currentTimeMillis()));


	}



	private void loadAsSport() {

		date = (Timestamp)fDate.getValue();
		Vector<String> columns = buildSportNoteHeading();


		Vector<Vector<Object>> data = getStudentData();

		ListModelTable modelP = new ListModelTable(data);

		modelP.addTableModelListener(this);
		((WListbox)noteTable).setData(modelP, columns);

		refreshNotes();

		((WListbox)noteTable).setStyle("sizedByContent=true");

		noteTable.setColumnClass(0, String.class, true);

		noteTable.setColumnClass(1, org.fcaq.components.WNoteEditor.class, note!=null?note.isSent():false);
		noteTable.setColumnClass(2, org.fcaq.components.WNoteEditor.class, note!=null?note.isSent():false);
		noteTable.setColumnClass(3, org.fcaq.components.WNoteEditor.class, note!=null?note.isSent():false);
		noteTable.setColumnClass(4, org.fcaq.components.WNoteEditor.class, note!=null?note.isSent():false);

		noteTable.setColumnClass(noteTable.getColumnCount()-1, org.fcaq.components.WNoteEditor.class, false);

		noteTable.setColumnClass(noteTable.getColumnCount()-1, org.fcaq.components.WNoteEditor.class, false);

		noteTable.autoSize();
		((WListbox)noteTable).setWidth("100%");
		((WListbox)noteTable).setHeight("100%");

		System.out.println("Refresh Header End At " + new Timestamp(System.currentTimeMillis()));

	}


	@Override
	public INoteEditor getNoteComponent() {
		return new WNoteEditor();
	}


	@Override
	public void repaintFinal(MBPartner studen, String value) {

		try{
			for(int x=0;x<=noteTable.getRowCount()-1; x++)
			{
				INoteEditor editor = (INoteEditor)noteTable.getValueAt(x, noteTable.getColumnCount()-2);
				if(editor.getStudent().get_ID() == studen.get_ID())
				{
					editor = (INoteEditor)noteTable.getValueAt(x, noteTable.getColumnCount()-1);
					editor.setFValue(new BigDecimal(value));
				}
			}
		}catch(Exception e)
		{
			//Nothing to do, just ignore
		}
	}

	private void clean()
	{

	}




	/// comments in notes

	Window comments = null;
	Checkbox isWaiting = new Checkbox();

	private void showComments(List<INoteEditor> noteEditors) {

		comments = new Window();

		// Layout components
		Borderlayout mainCLayout = new Borderlayout();
		Panel parameterCPanel = new Panel();

		Grid parameterCLayout = GridFactory.newGridLayout();


		//Form components
		Label lStudent = new Label();
		Textbox fStudent = new Textbox();
		isWaiting.setSelected(false);
		isWaiting.setLabel("Pendiente de Envio");

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();

		for(INoteEditor editor : noteEditors)
		{
			if(!editor.isFinal())
			{
				Vector<Object> row = new Vector<Object>();

				row.add(editor.getNoteHeading()!=null? editor.getNoteHeading().getName():"Final");
				row.add(editor);
				row.add(editor.getNoteLine()!=null?(editor.getNoteLine().getComments()!=null?editor.getNoteLine().getComments():""):"");
				if(editor.getNoteLine()!=null)
				{
					isWaiting.setSelected(editor.getNoteLine().isWaiting());
				}

				data.add(row);
			}
		}



		Vector<String> columnNames = getColumnNamesHeading();

		noteHeadingTable.clear();
		noteHeadingTable.getModel().removeTableModelListener(this);

		ListModelTable modelP = new ListModelTable(data);
		modelP.addTableModelListener(this);
		noteHeadingTable.setData(modelP, columnNames);

		noteHeadingTable.setColumnClass(0, String.class, true);			//  0-NoteHeading
		noteHeadingTable.setColumnClass(1, BigDecimal.class, true);		//  1-Note
		noteHeadingTable.setColumnClass(2, String.class, false);		//  2-Comment

		mainCLayout.setWidth("99%");
		mainCLayout.setHeight("99%");

		parameterCPanel.appendChild(parameterCLayout);

		lStudent.setText(Msg.getMsg(Env.getCtx(), "Student"));

		North northC = new North();
		northC.setStyle("border: none");
		mainCLayout.appendChild(northC);
		northC.appendChild(parameterCPanel);
		Rows rows = null;
		Row row = null;
		parameterCLayout.setWidth("99%");
		rows = parameterCLayout.newRows();

		row = rows.newRow();
		row.appendChild(isWaiting);

		Center centerC = new Center();
		mainCLayout.appendChild(centerC);

		centerC.appendChild(noteHeadingTable);
		noteHeadingTable.setWidth("99%");
		noteHeadingTable.setHeight("99%");
		centerC.setStyle("border: none");

		South southC = new South();
		mainCLayout.appendChild(southC);

		ConfirmPanel confirmPanel = new ConfirmPanel(true);
		southC.appendChild(confirmPanel);

		confirmPanel.addActionListener(Events.ON_CLICK, this);	
		confirmPanel.addActionListener(Events.ON_CANCEL, this);	

		comments.setSizable(true);
		comments.setWidth("700px");
		comments.setHeight("400px");
		comments.setShadow(true);
		comments.setBorder("normal");
		comments.setClosable(true);
		comments.setTitle(Msg.translate(Env.getCtx(),"Comments"));
		comments.setContentStyle("overflow: auto");
		comments.appendChild(mainCLayout);

		AEnv.showCenterScreen(comments);
	}


	public void saveComments()
	{

		MBPartner student = null;
		boolean hascomments=false;

		for(int row =0;row<=noteHeadingTable.getRowCount()-1; row++)
		{
			INoteEditor editor = (INoteEditor)noteHeadingTable.getValueAt(row, 1);
			String comment = (String)noteHeadingTable.getValueAt(row, 2);

			student = editor.getStudent();

			if(editor.getNoteLine_ID()>0)
			{
				X_CA_NoteLine noteLine = new X_CA_NoteLine(m_ctx, editor.getNoteLine_ID(), null);


				if(noteLine!=null)
				{
					noteLine.setIsWaiting(isWaiting.isSelected());
					noteLine.setComments(comment!=null?comment:"");
					noteLine.saveEx();



					if(noteLine.getComments().length()>0)
					{
						hascomments=true;
					}

				}	
			}
		}

		if(hascomments || isWaiting.isSelected())
		{
			repaintStudentName(student, "*");
		}
		else
		{
			repaintStudentName(student, "");
		}

	}
}
