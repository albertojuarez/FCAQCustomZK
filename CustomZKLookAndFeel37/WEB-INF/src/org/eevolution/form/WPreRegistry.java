package org.eevolution.form;

import java.util.logging.Level;

import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.panel.StatusBarPanel;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Div;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;

public class WPreRegistry 	
implements IFormController, EventListener, WTableModelListener, ValueChangeListener{

	private CustomForm form = new CustomForm();
	
	public WPreRegistry() {
		log.info("WinNo=" + m_WindowNo
				+ " - AD_Client_ID=" + m_AD_Client_ID + ", AD_Org_ID=" + m_AD_Org_ID + ", By=" + m_by);
		try
		{
			//	UI
			zkInit();
			//
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "", e);
		}
	}

	/**	Window No			*/
	private int         	m_WindowNo = 0;
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(WPreRegistry.class);
	
	private int     m_AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());
	private int     m_AD_Org_ID = Env.getAD_Org_ID(Env.getCtx());
	private int     m_by = Env.getAD_User_ID(Env.getCtx());
	
	private Panel mainPanel = new Panel();
	private StatusBarPanel statusBar = new StatusBarPanel();
	private Borderlayout mainLayout = new Borderlayout();
	private Panel centerPanel = new Panel();
	private Panel southPanel = new Panel();
	private Grid centerLayout = GridFactory.newGridLayout();
	private Grid southLayout = GridFactory.newGridLayout();
	
	//infoCandidate
	private Label lInfoCandidate = new Label();
	private Label lIdNumberCandidate = new Label();
	private Textbox fIdNumberCandidate = new Textbox();	
	private Label lFNameCandidate = new Label();
	private Textbox fFNameCandidate = new Textbox();
	private Label lSNameCandidate = new Label();
	private Textbox fSNameCandidate = new Textbox();
	private Label lFSurnameCandidate = new Label();
	private Textbox fFSurnameCandidate = new Textbox();
	private Label lSSurnameCandidate = new Label();
	private Textbox fSSurnameCandidate = new Textbox();
	private Label lBirthday = new Label();
	private WDateEditor fBirthday = new WDateEditor("Birthday","Birthday",false,false,true);
	private Label lGender = new Label();
	private Combobox fGender = new Combobox();
	private Label lRegistryType = new Label();
	private Combobox fRegistryType = new Combobox();
	private Label lProvenance = new Label();
	private Textbox fProvenance = new Textbox();
	
	//infoFather
	private Label lInfoFather = new Label();
	private Label lIdNumberFather = new Label();
	private Textbox fIdNumberFather = new Textbox();
	private Label lFNameFather = new Label();
	private Textbox fFNameFather = new Textbox();
	private Label lSNameFather = new Label();
	private Textbox fSNameFather = new Textbox();
	private Label lFSurnameFather = new Label();
	private Textbox fFSurnameFather = new Textbox();
	private Label lSSurnameFather = new Label();
	private Textbox fSSurnameFather = new Textbox();
	private Checkbox fAlumnusFather = new Checkbox();
	private Label lSYearsFather = new Label();
	private Textbox fSYearsFather = new Textbox();
	private Checkbox fGraduateFather = new Checkbox();
	private Checkbox fEmployeeFather = new Checkbox();

	//infoMother
	private Label lInfoMother = new Label();
	private Label lIdNumberMother = new Label();
	private Textbox fIdNumberMother = new Textbox();	
	private Label lFNameMother = new Label();
	private Textbox fFNameMother = new Textbox();
	private Label lSNameMother = new Label();
	private Textbox fSNameMother = new Textbox();
	private Label lFSurnameMother = new Label();
	private Textbox fFSurnameMother = new Textbox();
	private Label lSSurnameMother = new Label();
	private Textbox fSSurnameMother = new Textbox();
	private Checkbox fAlumnusMother = new Checkbox();
	private Label lSYearsMother = new Label();
	private Textbox fSYearsMother = new Textbox();
	private Checkbox fGraduateMother = new Checkbox();
	private Checkbox fEmployeeMother = new Checkbox();

	//infoEntry
	private Label lAddInfo = new Label();
	private Label lFoundOutForm = new Label();
	private WStringEditor fFoundOutForm = new WStringEditor();
	private Label lSection = new Label();
	private Combobox fSection = new Combobox();
	private Label lModality = new Label();
	private Combobox fModality = new Combobox();
	private Label lGrade = new Label();
	private Combobox fGrade = new Combobox();
	
	//buttons
	private Button bSend = new Button();
	private Button bCancel = new Button();
	
	
	private void zkInit() throws Exception {
		form.appendChild(mainPanel);
		mainPanel.setStyle("width: 99%; height: 100%; padding: 0; margin: 0");
		mainPanel.appendChild(mainLayout);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		centerPanel.appendChild(centerLayout);
		centerLayout.setVflex(true);
		southPanel.appendChild(southLayout);
		lInfoCandidate.setText(Msg.translate(Env.getCtx(), "InfoCandidate"));
		lIdNumberCandidate.setText(Msg.translate(Env.getCtx(), "IdNumber"));
		lFNameCandidate.setText(Msg.translate(Env.getCtx(), "FirstName"));
		lSNameCandidate.setText(Msg.translate(Env.getCtx(), "SecondName"));
		lFSurnameCandidate.setText(Msg.translate(Env.getCtx(), "FirstSurname"));
		lSSurnameCandidate.setText(Msg.translate(Env.getCtx(), "SecondSurname"));
		lBirthday.setText(Msg.translate(Env.getCtx(), "Birthday"));
		lGender.setText(Msg.translate(Env.getCtx(), "Gender"));
		fGender.appendItem("M", "1");
		fGender.appendItem("F", "2");
		lRegistryType.setText(Msg.translate(Env.getCtx(), "RegistryType"));
		fRegistryType.appendItem(Msg.translate(Env.getCtx(), "IsStudent"), "1");
		fRegistryType.appendItem(Msg.translate(Env.getCtx(), "Exchange"), "2");		
		lProvenance.setText(Msg.translate(Env.getCtx(), "Provenance"));
		lInfoFather.setText(Msg.translate(Env.getCtx(), "InfoFather"));
		lIdNumberFather.setText(Msg.translate(Env.getCtx(), "IdNumber"));
		lFNameFather.setText(Msg.translate(Env.getCtx(), "FirstName"));
		lSNameFather.setText(Msg.translate(Env.getCtx(), "SecondName"));
		lFSurnameFather.setText(Msg.translate(Env.getCtx(), "FirstSurname"));
		lSSurnameFather.setText(Msg.translate(Env.getCtx(), "SecondSurname"));
		fAlumnusFather.setText(Msg.translate(Env.getCtx(), "Alumnus"));
		lSYearsFather.setText(Msg.translate(Env.getCtx(), "Years"));
		fGraduateFather.setText(Msg.translate(Env.getCtx(), "Graduate"));
		fEmployeeFather.setText(Msg.translate(Env.getCtx(), "IsEmployee"));
		lInfoMother.setText(Msg.translate(Env.getCtx(), "InfoMother"));
		lIdNumberMother.setText(Msg.translate(Env.getCtx(), "IdNumber"));
		lFNameMother.setText(Msg.translate(Env.getCtx(), "FirstName"));
		lSNameMother.setText(Msg.translate(Env.getCtx(), "SecondName"));
		lFSurnameMother.setText(Msg.translate(Env.getCtx(), "FirstSurname"));
		lSSurnameMother.setText(Msg.translate(Env.getCtx(), "SecondSurname"));
		fAlumnusMother.setText(Msg.translate(Env.getCtx(), "Alumnus"));
		lSYearsMother.setText(Msg.translate(Env.getCtx(), "Years"));
		fGraduateMother.setText(Msg.translate(Env.getCtx(), "Graduate"));
		fEmployeeMother.setText(Msg.translate(Env.getCtx(), "IsEmployee"));
		lAddInfo.setText(Msg.translate(Env.getCtx(), "AdditionalInfo"));
		lFoundOutForm.setText(Msg.translate(Env.getCtx(), "FoundOutForm"));
		lSection.setText(Msg.translate(Env.getCtx(), "Section"));
		fSection.appendItem(Msg.translate(Env.getCtx(), "Nursery"), "1");
		fSection.appendItem(Msg.translate(Env.getCtx(), "Primary"), "2");
		fSection.appendItem(Msg.translate(Env.getCtx(), "Secondary"), "3");
		lModality.setText(Msg.translate(Env.getCtx(), "Modality"));
		fModality.appendItem(Msg.translate(Env.getCtx(), "National"), "1");
		fModality.appendItem(Msg.translate(Env.getCtx(), "international"), "2");
		lGrade.setText(Msg.translate(Env.getCtx(), "Grade"));
		fGrade.appendItem(Msg.translate(Env.getCtx(), "1"), "1");
		fGrade.appendItem(Msg.translate(Env.getCtx(), "2"), "2");
		fGrade.appendItem(Msg.translate(Env.getCtx(), "3"), "3");
		fGrade.appendItem(Msg.translate(Env.getCtx(), "4"), "4");
		fGrade.appendItem(Msg.translate(Env.getCtx(), "5"), "5");
		fGrade.appendItem(Msg.translate(Env.getCtx(), "6"), "6");
		bSend.setLabel(Msg.translate(Env.getCtx(), "Send"));
		bCancel.setLabel(Msg.translate(Env.getCtx(), "CancelRegistry"));
		
		//Center
		Center center = new Center();
		center.setFlex(true);
		mainLayout.appendChild(center);
		center.appendChild(centerPanel);
		
		Rows rows = centerLayout.newRows();
		
		Row row = rows.newRow();
		lInfoCandidate.setStyle("font-weight:bold");
		row.appendChild(lInfoCandidate.rightAlign());

		row = rows.newRow();
		row.appendChild(lIdNumberCandidate.rightAlign());
		row.appendChild(fIdNumberCandidate);
		
		row = rows.newRow();
		row.appendChild(lFNameCandidate.rightAlign());
		row.appendChild(fFNameCandidate);
		row.appendChild(lSNameCandidate.rightAlign());
		row.appendChild(fSNameCandidate);
		row.appendChild(lFSurnameCandidate.rightAlign());
		row.appendChild(fFSurnameCandidate);
		row.appendChild(lSSurnameCandidate.rightAlign());
		row.appendChild(fSSurnameCandidate);
		
		row = rows.newRow();
		row.appendChild(lBirthday.rightAlign());
		row.appendChild(fBirthday.getComponent());
		row.appendChild(lGender.rightAlign());
		row.appendChild(fGender);
		row.appendChild(lRegistryType.rightAlign());
		row.appendChild(fRegistryType);
		row.appendChild(lProvenance.rightAlign());
		row.appendChild(fProvenance);

	    row = rows.newRow();
		row.appendChild(new Space());
		
		row = rows.newRow();
		lInfoFather.setStyle("font-weight:bold");
		row.appendChild(lInfoFather.rightAlign());
		
		row = rows.newRow();
		row.appendChild(lIdNumberFather.rightAlign());
		row.appendChild(fIdNumberFather);
		
		row = rows.newRow();
		row.appendChild(lFNameFather.rightAlign());
		row.appendChild(fFNameFather);
		row.appendChild(lSNameFather.rightAlign());
		row.appendChild(fSNameFather);
		row.appendChild(lFSurnameFather.rightAlign());
		row.appendChild(fFSurnameFather);
		row.appendChild(lSSurnameFather.rightAlign());
		row.appendChild(fSSurnameFather);
		
		row = rows.newRow();
		row.appendChild(new Space());
		row.appendChild(fAlumnusFather);
		row.appendChild(lSYearsFather.rightAlign());
		row.appendChild(fSYearsFather);
		row.appendChild(new Space());
		row.appendChild(fGraduateFather);
		row.appendChild(new Space());
		row.appendChild(fEmployeeFather);
		
	    row = rows.newRow();
		row.appendChild(new Space());
		
		row = rows.newRow();
		lInfoMother.setStyle("font-weight:bold");
		row.appendChild(lInfoMother.rightAlign());
		
		row = rows.newRow();
		row.appendChild(lIdNumberMother.rightAlign());
		row.appendChild(fIdNumberMother);
		
		row = rows.newRow();
		row.appendChild(lFNameMother.rightAlign());
		row.appendChild(fFNameMother);
		row.appendChild(lSNameMother.rightAlign());
		row.appendChild(fSNameMother);
		row.appendChild(lFSurnameMother.rightAlign());
		row.appendChild(fFSurnameMother);
		row.appendChild(lSSurnameMother.rightAlign());
		row.appendChild(fSSurnameMother);

		row = rows.newRow();
		row.appendChild(new Space());
		row.appendChild(fAlumnusMother);
		row.appendChild(lSYearsMother.rightAlign());
		row.appendChild(fSYearsMother);
		row.appendChild(new Space());
		row.appendChild(fGraduateMother);
		row.appendChild(new Space());
		row.appendChild(fEmployeeMother);
		
	    row = rows.newRow();
		row.appendChild(new Space());
		
		row = rows.newRow();
		lAddInfo.setStyle("font-weight:bold");
		row.appendChild(lAddInfo.rightAlign());
		
		row = rows.newRow();
		row.appendChild(lFoundOutForm);
        row.appendChild(fFoundOutForm.getComponent());
        
		row = rows.newRow();
		row.appendChild(lSection.rightAlign());
		row.appendChild(fSection);
		row.appendChild(lModality.rightAlign());
		row.appendChild(fModality);
		row.appendChild(lGrade.rightAlign());
		row.appendChild(fGrade);
		
		//South
		South south = new South();
		mainLayout.appendChild(south);
		south.appendChild(southPanel);
		
		Rows southRows = southLayout.newRows();
		
		Row southRow = southRows.newRow();
		southRow.appendChild(new Separator());
		
		Div div = new Div();
		div.appendChild(new Space());
		div.appendChild(bSend);
		div.appendChild(new Space());
		div.appendChild(bCancel);
		
		southRow = southRows.newRow();
		southRow.appendChild(div);
		
		southRow = southRows.newRow();
		LayoutUtils.addSclass("status-border", statusBar);
		statusBar.setStatusLine("");
		southRow.appendChild(statusBar);
		
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
		bSend.addActionListener(this);
		bCancel.addActionListener(this);
	}

	@Override
	public ADForm getForm() {
		// TODO Auto-generated method stub
		return form;
	}

}
