package org.eevolution.form;

import java.math.BigDecimal;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.panel.StatusBarPanel;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Separator;

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
	private WListbox noteTable = ListboxFactory.newDataTable();
	private Button bSendNotes = new Button();
	private Button bShowComments = new Button();
    private Hbox hboxBtnRight;
    private Panel pnlBtnRight;
	
	// Form Components
	private Label lCourseDef = null;
	private Label lParcial = null;
	private Label lSubjectMatter = null;
	
	private WSearchEditor fCourseDef = null;
	private Combobox fParcial = null;
	private Combobox fSubjectMatter = null;
	
	
	public WAcademicNote()
	{
		try
		{
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
		Vector<String> columnNames = getColumnNamesNote();
		
		noteTable.clear();
		noteTable.getModel().removeTableModelListener(this);
		
		ListModelTable modelP = new ListModelTable(data);
		modelP.addTableModelListener(this);
		noteTable.setData(modelP, columnNames);
		
		noteTable.setColumnClass(0, String.class, true);			//  0-Student
		noteTable.setColumnClass(1, BigDecimal.class, false);		//  1-NoteHeading
		noteTable.setColumnClass(2, BigDecimal.class, false);		//  2-NoteHeading2
		noteTable.setColumnClass(3, BigDecimal.class, true);		//  3-Final
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
		fParcial = new Combobox();
		
		lSubjectMatter = new Label();
		lSubjectMatter.setText(Msg.getMsg(Env.getCtx(), "SubjectMatter"));
		fSubjectMatter = new Combobox();
		
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
		row.appendChild(lCourseDef);
		row.appendChild(fCourseDef.getComponent());
		row = rows.newRow();
		row.appendChild(lSubjectMatter);
		row.appendChild(fSubjectMatter);
		row.appendChild(lParcial);
		row.appendChild(fParcial);
		
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
		int AD_Column_ID = 2893;        //  CA_CourseDef.CA_CourseDef_ID
		MLookup lookupBP = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, AD_Column_ID, DisplayType.Search);
		fCourseDef = new WSearchEditor("CA_CourseDef_ID", true, false, true, lookupBP);
		fCourseDef.addValueChangeListener(this);
		bShowComments.addActionListener(this);
		bSendNotes.addActionListener(this);
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tableChanged(WTableModelEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		if (event.getTarget().equals(bShowComments)) 
		{
			showComments();
		}
	}

	private void showComments() {
		// TODO Auto-generated method stub
		Window comments = new Window();
		
		// Layout components
		Borderlayout mainCLayout = new Borderlayout();
		Panel parameterCPanel = new Panel();
		//parameterCPanel.setWidth("400px");
		Grid parameterCLayout = GridFactory.newGridLayout();
		WListbox noteHeadingTable = ListboxFactory.newDataTable();
		
		//Form components
		Label lStudent = new Label();
		Textbox fStudent = new Textbox();
		
		Vector<Vector<Object>> data = getNoteHeadingData();
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

	@Override
	public ADForm getForm() {
		// TODO Auto-generated method stub
		return form;
	}

}
