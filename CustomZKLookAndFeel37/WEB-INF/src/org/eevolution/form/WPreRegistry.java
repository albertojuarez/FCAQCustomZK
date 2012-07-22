package org.eevolution.form;

import java.util.logging.Level;

import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.apps.form.WMatch;
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
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.panel.StatusBarPanel;
import org.compiere.model.MMatchPO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
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
	private Label lFNameCandidate = new Label();
	private Textbox fFNameCandidate = new Textbox();
	private Label lSNameCandidate = new Label();
	private Textbox fSNameCandidate = new Textbox();
	private Label lFSurnameCandidate = new Label();
	private Textbox fFSurnameCandidate = new Textbox();
	private Label lSSurnameCandidate = new Label();
	private Textbox fSSurnameCandidate = new Textbox();
	private Label lIdTypeCandidate = new Label();
	private Combobox fIdTypeCandidate = new Combobox();
	private Label lIdNumberCandidate = new Label();
	private Textbox fIdNumberCandidate = new Textbox();	
	private Label lBirthday = new Label();
	private WDateEditor fBirthday = new WDateEditor("Birthday","Birthday",false,false,true);
	private Label lGender = new Label();
	private Combobox fGender = new Combobox();
	private Label lCountryBirth = new Label();
	private Combobox fCountryBirth = new Combobox();
	private Label lPhoneCandidate = new Label();
	private Textbox fPhoneCandidate = new Textbox();
	private Label lEmailCandidate = new Label();
	private Textbox fEmailCandidate = new Textbox();
	private Label lNationality2 = new Label();
	private Combobox fNationality2 = new Combobox();
	private Label lNationality3 = new Label();
	private Combobox fNationality3 = new Combobox();
	
	//AddressCandidate
	private Label lAddressCandidate = new Label();
	private Label lMainStreet = new Label();
	private Textbox fMainStreet = new Textbox();
	private Label lSideStreet = new Label();
	private Textbox fSideStreet = new Textbox();
	private Label lSector = new Label();
	private Combobox fSector = new Combobox();
	private Label lParish = new Label();
	private Combobox fParish = new Combobox();
	
	//infoFather
	private Label lInfoFather = new Label();
	private Label lFNameFather = new Label();
	private Textbox fFNameFather = new Textbox();
	private Label lSNameFather = new Label();
	private Textbox fSNameFather = new Textbox();
	private Label lFSurnameFather = new Label();
	private Textbox fFSurnameFather = new Textbox();
	private Label lSSurnameFather = new Label();
	private Textbox fSSurnameFather = new Textbox();
	private Label lIdTypeFather = new Label();
	private Combobox fIdTypeFather = new Combobox();
	private Label lIdNumberFather = new Label();
	private Textbox fIdNumberFather = new Textbox();	
	private Label lPhoneFather = new Label();
	private Textbox fPhoneFather = new Textbox();
	private Label lMPhoneFather = new Label();
	private Textbox fMPhoneFather = new Textbox();
	private Label lHPhoneFather = new Label();
	private Textbox fHPhoneFather = new Textbox();
	private Label lOPhoneFather = new Label();
	private Textbox fOPhoneFather = new Textbox();
	private Label lEPhoneFather = new Label();
	private Textbox fEPhoneFather = new Textbox();
	private Label lEmailFather = new Label();
	private Textbox fEmailFather = new Textbox();
	private Label lEmail2Father = new Label();
	private Textbox fEmail2Father = new Textbox();
	private Label lWCollegeFather = new Label();
	private Checkbox fWCollegeFather = new Checkbox();
	private Label lSCollegeFather = new Label();
	private Checkbox fSCollegeFather = new Checkbox();
	private Label lSYearsFather = new Label();
	private Textbox fSYearsFather = new Textbox();
	private Label lGraduateFather = new Label();
	private Checkbox fGraduateFather = new Checkbox();
	private Label lGYearFather = new Label();
	private Textbox fGYearFather = new Textbox();
	
	//infoMother
	private Label lInfoMother = new Label();
	private Label lFNameMother = new Label();
	private Textbox fFNameMother = new Textbox();
	private Label lSNameMother = new Label();
	private Textbox fSNameMother = new Textbox();
	private Label lFSurnameMother = new Label();
	private Textbox fFSurnameMother = new Textbox();
	private Label lSSurnameMother = new Label();
	private Textbox fSSurnameMother = new Textbox();
	private Label lIdTypeMother = new Label();
	private Combobox fIdTypeMother = new Combobox();
	private Label lIdNumberMother = new Label();
	private Textbox fIdNumberMother = new Textbox();	
	private Label lPhoneMother = new Label();
	private Textbox fPhoneMother = new Textbox();
	private Label lMPhoneMother = new Label();
	private Textbox fMPhoneMother = new Textbox();
	private Label lHPhoneMother = new Label();
	private Textbox fHPhoneMother = new Textbox();
	private Label lOPhoneMother = new Label();
	private Textbox fOPhoneMother = new Textbox();
	private Label lEPhoneMother = new Label();
	private Textbox fEPhoneMother = new Textbox();
	private Label lEmailMother = new Label();
	private Textbox fEmailMother = new Textbox();
	private Label lEmail2Mother = new Label();
	private Textbox fEmail2Mother = new Textbox();
	private Label lWCollegeMother = new Label();
	private Checkbox fWCollegeMother = new Checkbox();
	private Label lSCollegeMother = new Label();
	private Checkbox fSCollegeMother = new Checkbox();
	private Label lSYearsMother = new Label();
	private Textbox fSYearsMother = new Textbox();
	private Label lGraduateMother = new Label();
	private Checkbox fGraduateMother = new Checkbox();
	private Label lGYearMother = new Label();
	private Textbox fGYearMother = new Textbox();

	//infoEntry
	private Label lInfoEntry = new Label();
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
		lFNameCandidate.setText(Msg.translate(Env.getCtx(), "FirstName"));
		lSNameCandidate.setText(Msg.translate(Env.getCtx(), "SecondName"));
		lFSurnameCandidate.setText(Msg.translate(Env.getCtx(), "FirstSurname"));
		lSSurnameCandidate.setText(Msg.translate(Env.getCtx(), "SecondSurname"));
		lIdTypeCandidate.setText(Msg.translate(Env.getCtx(), "IdType"));
		lIdNumberCandidate.setText(Msg.translate(Env.getCtx(), "IdNumber"));
		lBirthday.setText(Msg.translate(Env.getCtx(), "Birthday"));
		lGender.setText(Msg.translate(Env.getCtx(), "Gender"));
		lCountryBirth.setText(Msg.translate(Env.getCtx(), "CountryBirth"));
		lPhoneCandidate.setText(Msg.translate(Env.getCtx(), "Phone"));
		lEmailCandidate.setText(Msg.translate(Env.getCtx(), "EMail"));
		lNationality2.setText(Msg.translate(Env.getCtx(), "Nationality2"));
		lNationality3.setText(Msg.translate(Env.getCtx(), "Nationality3"));
		lAddressCandidate.setText(Msg.translate(Env.getCtx(), "Address"));
		lMainStreet.setText(Msg.translate(Env.getCtx(), "MainStreet"));
		lSideStreet.setText(Msg.translate(Env.getCtx(), "SideStreet"));
		lSector.setText(Msg.translate(Env.getCtx(), "Sector"));
		lParish.setText(Msg.translate(Env.getCtx(), "Parish"));
		lInfoFather.setText(Msg.translate(Env.getCtx(), "InfoFather"));
		lFNameFather.setText(Msg.translate(Env.getCtx(), "FirstName"));
		lSNameFather.setText(Msg.translate(Env.getCtx(), "SecondName"));
		lFSurnameFather.setText(Msg.translate(Env.getCtx(), "FirstSurname"));
		lSSurnameFather.setText(Msg.translate(Env.getCtx(), "SecondSurname"));
		lIdTypeFather.setText(Msg.translate(Env.getCtx(), "IdType"));
		lIdNumberFather.setText(Msg.translate(Env.getCtx(), "IdNumber"));
		lPhoneFather.setText(Msg.translate(Env.getCtx(), "Phone"));
		lMPhoneFather.setText(Msg.translate(Env.getCtx(), "Phone2"));
		lHPhoneFather.setText(Msg.translate(Env.getCtx(), "Phone3"));
		lOPhoneFather.setText(Msg.translate(Env.getCtx(), "Phone4"));;		
		lEPhoneFather.setText(Msg.translate(Env.getCtx(), "Ext"));
		lEmailFather.setText(Msg.translate(Env.getCtx(), "EMail"));
		lEmail2Father.setText(Msg.translate(Env.getCtx(), "EMail2"));
		lWCollegeFather.setText(Msg.translate(Env.getCtx(), "CollegeEmployee"));
		lSCollegeFather.setText(Msg.translate(Env.getCtx(), "StudiedCollege"));
		lSYearsFather.setText(Msg.translate(Env.getCtx(), "Years"));
		lGraduateFather.setText(Msg.translate(Env.getCtx(), "Graduate"));
		lGYearFather.setText(Msg.translate(Env.getCtx(), "Year"));
		lInfoMother.setText(Msg.translate(Env.getCtx(), "InfoMother"));
		lFNameMother.setText(Msg.translate(Env.getCtx(), "FirstName"));
		lSNameMother.setText(Msg.translate(Env.getCtx(), "SecondName"));
		lFSurnameMother.setText(Msg.translate(Env.getCtx(), "FirstSurname"));
		lSSurnameMother.setText(Msg.translate(Env.getCtx(), "SecondSurname"));
		lIdTypeMother.setText(Msg.translate(Env.getCtx(), "IdType"));
		lIdNumberMother.setText(Msg.translate(Env.getCtx(), "IdNumber"));
		lPhoneMother.setText(Msg.translate(Env.getCtx(), "Phone"));
		lMPhoneMother.setText(Msg.translate(Env.getCtx(), "Phone2"));
		lHPhoneMother.setText(Msg.translate(Env.getCtx(), "Phone3"));
		lOPhoneMother.setText(Msg.translate(Env.getCtx(), "Phone4"));
		lEPhoneMother.setText(Msg.translate(Env.getCtx(), "Ext"));
		lEmailMother.setText(Msg.translate(Env.getCtx(), "EMail"));
		lEmail2Mother.setText(Msg.translate(Env.getCtx(), "EMail2"));	
		lWCollegeMother.setText(Msg.translate(Env.getCtx(), "CollegeEmployee"));
		lSCollegeMother.setText(Msg.translate(Env.getCtx(), "StudiedCollege"));
		lSYearsMother.setText(Msg.translate(Env.getCtx(), "Years"));
		lGraduateMother.setText(Msg.translate(Env.getCtx(), "Graduate"));
		lGYearMother.setText(Msg.translate(Env.getCtx(), "Year"));
		lInfoEntry.setText(Msg.translate(Env.getCtx(), "InfoEntry"));
		lSection.setText(Msg.translate(Env.getCtx(), "Section"));
		lModality.setText(Msg.translate(Env.getCtx(), "Modality"));
		lGrade.setText(Msg.translate(Env.getCtx(), "Grade"));
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
		fFNameCandidate.setWidth("100%");
		fSNameCandidate.setWidth("100%");
		row.appendChild(lFNameCandidate.rightAlign());
		row.appendChild(fFNameCandidate);
		row.appendChild(lSNameCandidate.rightAlign());
		row.appendChild(fSNameCandidate);
		
		row = rows.newRow();
		fFSurnameCandidate.setWidth("100%");
		fSSurnameCandidate.setWidth("100%");
		row.appendChild(lFSurnameCandidate.rightAlign());
		row.appendChild(fFSurnameCandidate);
		row.appendChild(lSSurnameCandidate.rightAlign());
		row.appendChild(fSSurnameCandidate);
		
		row = rows.newRow();
		fIdTypeCandidate.setWidth("100%");
		fIdNumberCandidate.setWidth("100%");
		row.appendChild(lIdTypeCandidate.rightAlign());
		row.appendChild(fIdTypeCandidate);
		row.appendChild(lIdNumberCandidate.rightAlign());
		row.appendChild(fIdNumberCandidate);
		
		row = rows.newRow();
		fGender.setWidth("100%");
		row.appendChild(lBirthday.rightAlign());
		row.appendChild(fBirthday.getComponent());
		row.appendChild(lGender.rightAlign());
		row.appendChild(fGender);
		
		row = rows.newRow();
		fCountryBirth.setWidth("100%");
		fPhoneCandidate.setWidth("100%");
		row.appendChild(lCountryBirth.rightAlign());
		row.appendChild(fCountryBirth);
		row.appendChild(lPhoneCandidate.rightAlign());
		row.appendChild(fPhoneCandidate);
		
		row = rows.newRow();
		fEmailCandidate.setWidth("100%");
		row.appendChild(lEmailCandidate.rightAlign());
		row.appendChild(fEmailCandidate);
		
		row = rows.newRow();
		fNationality2.setWidth("100%");
		row.appendChild(lNationality2.rightAlign());
		row.appendChild(fNationality2);
		
		row = rows.newRow();
		fNationality3.setWidth("100%");
		row.appendChild(lNationality3.rightAlign());
		row.appendChild(fNationality3);

	    row = rows.newRow();
		row.appendChild(new Space());
		
		row = rows.newRow();
		lAddressCandidate.setStyle("font-weight:bold");
		row.appendChild(lAddressCandidate.rightAlign());
		
		row = rows.newRow();
		fMainStreet.setWidth("100%");
		fSideStreet.setWidth("100%");
		row.appendChild(lMainStreet.rightAlign());
		row.appendChild(fMainStreet);
		row.appendChild(lSideStreet.rightAlign());
		row.appendChild(fSideStreet);
		
		row = rows.newRow();
		fSector.setWidth("100%");
		fParish.setWidth("100%");
		row.appendChild(lSector.rightAlign());
		row.appendChild(fSector);
		row.appendChild(lParish.rightAlign());
		row.appendChild(fParish);
		
	    row = rows.newRow();
		row.appendChild(new Space());
		
		row = rows.newRow();
		lInfoFather.setStyle("font-weight:bold");
		row.appendChild(lInfoFather.rightAlign());
		
		row = rows.newRow();
		fFNameFather.setWidth("100%");
		fSNameFather.setWidth("100%");
		row.appendChild(lFNameFather.rightAlign());
		row.appendChild(fFNameFather);
		row.appendChild(lSNameFather.rightAlign());
		row.appendChild(fSNameFather);
		
		row = rows.newRow();
		fFSurnameFather.setWidth("100%");
		fSSurnameFather.setWidth("100%");
		row.appendChild(lFSurnameFather.rightAlign());
		row.appendChild(fFSurnameFather);
		row.appendChild(lSSurnameFather.rightAlign());
		row.appendChild(fSSurnameFather);
		
		row = rows.newRow();
		fIdTypeFather.setWidth("100%");
		fIdNumberFather.setWidth("100%");
		row.appendChild(lIdTypeFather.rightAlign());
		row.appendChild(fIdTypeFather);
		row.appendChild(lIdNumberFather.rightAlign());
		row.appendChild(fIdNumberFather);
		
		row = rows.newRow();
		fPhoneFather.setWidth("100%");
		row.appendChild(lPhoneFather.rightAlign());
		row.appendChild(fPhoneFather);
		
		row = rows.newRow();
		fMPhoneFather.setWidth("100%");
		fHPhoneFather.setWidth("100%");
		row.appendChild(lMPhoneFather.rightAlign());
		row.appendChild(fMPhoneFather);
		row.appendChild(lHPhoneFather.rightAlign());
		row.appendChild(fHPhoneFather);
		
		row = rows.newRow();
		fOPhoneFather.setWidth("100%");
		fEPhoneFather.setWidth("100%");
		row.appendChild(lOPhoneFather.rightAlign());
		row.appendChild(fOPhoneFather);
		row.appendChild(lEPhoneFather.rightAlign());
		row.appendChild(fEPhoneFather);
		
		row = rows.newRow();
		fEmailFather.setWidth("100%");
		fEmail2Father.setWidth("100%");
		row.appendChild(lEmailFather.rightAlign());
		row.appendChild(fEmailFather);
		row.appendChild(lEmail2Father.rightAlign());
		row.appendChild(fEmail2Father);
		
		row = rows.newRow();
		fWCollegeFather.setWidth("100%");
		row.appendChild(lWCollegeFather.rightAlign());
		row.appendChild(fWCollegeFather);

		row = rows.newRow();
		fSCollegeFather.setWidth("100%");
		fSYearsFather.setWidth("100%");
		row.appendChild(lSCollegeFather.rightAlign());
		row.appendChild(fSCollegeFather);
		row.appendChild(lSYearsFather.rightAlign());
		row.appendChild(fSYearsFather);
		
		row = rows.newRow();
		fGraduateFather.setWidth("100%");
		fGYearFather.setWidth("100%");
		row.appendChild(lGraduateFather.rightAlign());
		row.appendChild(fGraduateFather);
		row.appendChild(lGYearFather.rightAlign());
		row.appendChild(fGYearFather);
		
	    row = rows.newRow();
		row.appendChild(new Space());
		
		row = rows.newRow();
		lInfoMother.setStyle("font-weight:bold");
		row.appendChild(lInfoMother.rightAlign());
		
		row = rows.newRow();
		fFNameMother.setWidth("100%");
		fSNameMother.setWidth("100%");
		row.appendChild(lFNameMother.rightAlign());
		row.appendChild(fFNameMother);
		row.appendChild(lSNameMother.rightAlign());
		row.appendChild(fSNameMother);
		
		row = rows.newRow();
		fFSurnameMother.setWidth("100%");
		fSSurnameMother.setWidth("100%");
		row.appendChild(lFSurnameMother.rightAlign());
		row.appendChild(fFSurnameMother);
		row.appendChild(lSSurnameMother.rightAlign());
		row.appendChild(fSSurnameMother);
		
		row = rows.newRow();
		fIdTypeMother.setWidth("100%");
		fIdNumberMother.setWidth("100%");
		row.appendChild(lIdTypeMother.rightAlign());
		row.appendChild(fIdTypeMother);
		row.appendChild(lIdNumberMother.rightAlign());
		row.appendChild(fIdNumberMother);
		
		row = rows.newRow();
		fPhoneMother.setWidth("100%");
		row.appendChild(lPhoneMother.rightAlign());
		row.appendChild(fPhoneMother);
		
		row = rows.newRow();
		fMPhoneMother.setWidth("100%");
		fHPhoneMother.setWidth("100%");
		row.appendChild(lMPhoneMother.rightAlign());
		row.appendChild(fMPhoneMother);
		row.appendChild(lHPhoneMother.rightAlign());
		row.appendChild(fHPhoneMother);
		
		row = rows.newRow();
		fOPhoneMother.setWidth("100%");
		fEPhoneMother.setWidth("100%");
		row.appendChild(lOPhoneMother.rightAlign());
		row.appendChild(fOPhoneMother);
		row.appendChild(lEPhoneMother.rightAlign());
		row.appendChild(fEPhoneMother);

		row = rows.newRow();
		fEmailMother.setWidth("100%");
		fEmail2Mother.setWidth("100%");
		row.appendChild(lEmailMother.rightAlign());
		row.appendChild(fEmailMother);
		row.appendChild(lEmail2Mother.rightAlign());
		row.appendChild(fEmail2Mother);
		
		row = rows.newRow();
		fWCollegeMother.setWidth("100%");
		row.appendChild(lWCollegeMother.rightAlign());
		row.appendChild(fWCollegeMother);

		row = rows.newRow();
		fSCollegeMother.setWidth("100%");
		fSYearsMother.setWidth("100%");
		row.appendChild(lSCollegeMother.rightAlign());
		row.appendChild(fSCollegeMother);
		row.appendChild(lSYearsMother.rightAlign());
		row.appendChild(fSYearsMother);
		
		row = rows.newRow();
		fGraduateMother.setWidth("100%");
		fGYearMother.setWidth("100%");
		row.appendChild(lGraduateMother.rightAlign());
		row.appendChild(fGraduateMother);
		row.appendChild(lGYearMother.rightAlign());
		row.appendChild(fGYearMother);
		
	    row = rows.newRow();
		row.appendChild(new Space());
		
		row = rows.newRow();
		lInfoEntry.setStyle("font-weight:bold");
		row.appendChild(lInfoEntry.rightAlign());
		
		row = rows.newRow();
		fSection.setWidth("100%");
		row.appendChild(lSection.rightAlign());
		row.appendChild(fSection);
		
		row = rows.newRow();
		fModality.setWidth("100%");
		row.appendChild(lModality.rightAlign());
		row.appendChild(fModality);
		
		row = rows.newRow();
		fGrade.setWidth("100%");
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
