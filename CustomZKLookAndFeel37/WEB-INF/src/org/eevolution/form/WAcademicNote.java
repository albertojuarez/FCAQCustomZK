package org.eevolution.form;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MLookupInfo;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.TrxRunnable;
import org.fcaq.components.INoteEditor;
import org.fcaq.components.WNoteEditor;
import org.fcaq.model.X_CA_CourseDef;
import org.fcaq.model.X_CA_MatterAssignment;
import org.fcaq.model.X_CA_NoteLine;
import org.fcaq.model.X_CA_Parcial;
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
	private WListbox noteTable = null; // ListboxFactory.newDataTable();
	private WListbox noteHeadingTable = ListboxFactory.newDataTable();
	private Button bSendNotes = new Button();
	private Button bShowComments = new Button();
	private Hbox hboxBtnRight;
	private Panel pnlBtnRight;
	

	// Form Components
	private Label lCourseDef = null;
	private Label lParcial = null;
	private Label lSubjectMatter = null;

	private WTableDirEditor fCourseDef = null;
	private WTableDirEditor fParcial = null;
	private WTableDirEditor fMatterAssignment = null;

	private Checkbox isElective = new Checkbox();


	public WAcademicNote()
	{
		try
		{

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

		noteTable.clear();
		noteTable.getModel().removeTableModelListener(this);

		ListModelTable modelP = new ListModelTable(data);
		modelP.addTableModelListener(this);
		noteTable.setData(modelP, columnNames);

		noteTable.setColumnClass(0, String.class, true);			//  0-Student
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
		row.appendChild(lParcial);
		row.appendChild(fParcial.getComponent());

		Center center = new Center();
		center.setFlex(true);
		mainLayout.appendChild(center);

		center.appendChild(noteTable);
		noteTable.setWidth("99%");
		noteTable.setHeight("99%");
		center.setStyle("border: none");

		South south = new South();
		south.setStyle("border: none");
		mainLayout.appendChild(south);
		Panel southPanel = new Panel();
		south.appendChild(southPanel);

		pnlBtnRight = new Panel();
		pnlBtnRight.setAlign("right");
		pnlBtnRight.appendChild(bShowComments);
		pnlBtnRight.appendChild(bSendNotes);

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
		noteTable.setWidth("100%");
		noteTable.setHeight("100%");
		noteTable.setFixedLayout(false);
		noteTable.setVflex(true);
		noteTable.setStyle("overflow:auto;");
		
		

		isElective.setSelected(false);
		isElective.setLabel(Msg.getMsg(Env.getCtx(), "Is Elective"));


		fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),currentBPartner.get_ID(), isElective.isSelected()));
		fCourseDef.addValueChangeListener(this);

		fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, AcademicUtil.getMatterAssignmentLookup(form.getWindowNo(),currentBPartner.get_ID()));
		fMatterAssignment.addValueChangeListener(this);

		fParcial = new WTableDirEditor("CA_Parcial_ID", true, false, true, AcademicUtil.getParcialLookup(form.getWindowNo(),currentSchoolYear.get_ID()));
		fParcial.addValueChangeListener(this);
		fParcial.setValue(AcademicUtil.getCurrentParcial(m_ctx).get_ID());

		bShowComments.addActionListener(this);
		bSendNotes.addActionListener(this);
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
			fCourseDef.setValue(value);

			fMatterAssignment = new WTableDirEditor("CA_MatterAssignment_ID", true, false, true, AcademicUtil.getMatterAssignmentLookup(form.getWindowNo(),currentBPartner.get_ID(), (Integer)fCourseDef.getValue()));
			fMatterAssignment.addValueChangeListener(this);

			repaintParameterPanel();
			refreshHeader();

		}
		if ("CA_MatterAssignment_ID".equals(name))
		{
			fMatterAssignment.setValue(value);
			refreshHeader();
		}
		if ("CA_Parcial_ID".equals(name))
		{
			fParcial.setValue(value);
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
			if(isElective.isSelected())
				inccol = 1;
			else
				inccol = 0;
			
			fCourseDef = new WTableDirEditor("CA_CourseDef_ID", true, false, true, AcademicUtil.getCourseLookup(form.getWindowNo(),currentBPartner.get_ID(), isElective.isSelected()));
			fCourseDef.addValueChangeListener(this);

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
		row.appendChild(lSubjectMatter);
		row.appendChild(fMatterAssignment.getComponent());
		row.appendChild(lParcial);
		row.appendChild(fParcial.getComponent());
	}

	@Override
	public void refreshHeader(){

		noteTable.clear();
		noteTable.getModel().removeTableModelListener(this); 

		if(fCourseDef.getValue()==null || fMatterAssignment.getValue()==null || fParcial.getValue()==null)
			return;

		currentCourse  = new X_CA_CourseDef(m_ctx, (Integer)fCourseDef.getValue(), null);
		currentMatterAssignment = new X_CA_MatterAssignment(m_ctx, (Integer)fMatterAssignment.getValue(), null);

		if(currentMatterAssignment.getElectiveSubject_ID()>0)
			currentSubject = new X_CA_SubjectMatter(m_ctx, currentMatterAssignment.getElectiveSubject_ID(), null);
		else
			currentSubject = new X_CA_SubjectMatter(m_ctx, currentMatterAssignment.getCA_SubjectMatter_ID(), null);

		currentParcial = new X_CA_Parcial(m_ctx, (Integer)fParcial.getValue(), null);

		Vector<String> columns = buildNoteHeading();

		if(headingLines==null)
			return;


		Vector<Vector<Object>> data = getStudentData();

		ListModelTable modelP = new ListModelTable(data);

		modelP.addTableModelListener(this);
		noteTable.setData(modelP, columns);

		refreshNotes();

		noteTable.setStyle("sizedByContent=true");
		
		noteTable.setColumnClass(0, String.class, true);
		
		if(inccol==1)
		{
			noteTable.setColumnClass(1, String.class, true);
		}
		
		int index = 1;
		for(index = 1+inccol; index<= headingLines.size() + inccol; index++)
		{
			noteTable.setColumnClass(index, org.fcaq.components.WNoteEditor.class, note!=null?note.isSent():false);
		}
		noteTable.setColumnClass(noteTable.getColumnCount()-1, org.fcaq.components.WNoteEditor.class, false);

		noteTable.autoSize();
		noteTable.setWidth("100%");
		noteTable.setHeight("100%");

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
	
	private void showComments(List<INoteEditor> noteEditors) {

		comments = new Window();

		// Layout components
		Borderlayout mainCLayout = new Borderlayout();
		Panel parameterCPanel = new Panel();

		Grid parameterCLayout = GridFactory.newGridLayout();


		//Form components
		Label lStudent = new Label();
		Textbox fStudent = new Textbox();

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();

		for(INoteEditor editor : noteEditors)
		{
			Vector<Object> row = new Vector<Object>();

			row.add(editor.getNoteHeading()!=null? editor.getNoteHeading().getName():"Final");
			row.add(editor);
			row.add(editor.getNoteLine()!=null?(editor.getNoteLine().getComments()!=null?editor.getNoteLine().getComments():""):"");

			data.add(row);
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
		row.appendChild(lStudent);
		row.appendChild(fStudent);

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
		//Falta poner todo en el FDialog o el que corresponda y mostrar la forma
	}


	public void saveComments()
	{
		for(int row =0;row<=noteHeadingTable.getRowCount()-1; row++)
		{
			INoteEditor editor = (INoteEditor)noteHeadingTable.getValueAt(row, 1);
			String comment = (String)noteHeadingTable.getValueAt(row, 2);

			if(editor.getNoteLine_ID()>0)
			{
				X_CA_NoteLine noteLine = new X_CA_NoteLine(m_ctx, editor.getNoteLine_ID(), null);


				if(noteLine!=null)
				{
					noteLine.setComments(comment!=null?comment:"");
					noteLine.saveEx();
				}	
			}
		}
	}
}
