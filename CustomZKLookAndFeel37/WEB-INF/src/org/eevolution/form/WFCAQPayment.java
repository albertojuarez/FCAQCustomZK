package org.eevolution.form;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.apps.BusyDialog;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.DesktopTabpanel;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Tab;
import org.adempiere.webui.component.Tabbox;
import org.adempiere.webui.component.Tabpanels;
import org.adempiere.webui.component.Tabs;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WNumberEditor;
import org.adempiere.webui.editor.WStringEditor;
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
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_S_TimeExpenseLine;
import org.compiere.model.MBPartner;
import org.compiere.model.MBankAccount;
import org.compiere.model.MColumn;
import org.compiere.model.MDocType;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MQuery;
import org.compiere.model.MRefList;
import org.compiere.model.MSequence;
import org.compiere.model.MTimeExpenseLine;
import org.compiere.model.Query;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.ValueNamePair;
import org.fcaq.model.I_CA_Holder;
import org.fcaq.model.MCAHolder;
import org.fcaq.model.MCAPaymentType;
import org.fcaq.model.X_CA_SchoolYear;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Html;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vbox;

public class WFCAQPayment extends FCAQPayment implements IFormController, EventListener, WTableModelListener, ValueChangeListener{

	private Tabbox tabbedPane = new Tabbox();
	private Borderlayout mainPanel = new Borderlayout();
	private ConfirmPanel confirmPanelSel = new ConfirmPanel(true);
	private ConfirmPanel confirmPanelGen = new ConfirmPanel(false, true, false, false, false, false, false);
	private StatusBarPanel statusBar = new StatusBarPanel();
	private Borderlayout genPanel = new Borderlayout();
	private Borderlayout studentPanel = new Borderlayout();
	private Html info = new Html();
	private BusyDialog progressWindow;
	private String currentStudentValue = null;
	private Tab tabCreditInfo = null;

	
	private CustomForm form = new CustomForm();
	
	private Vbox paymentNorthVertialPanel = new Vbox();
	private Vbox paymentCenterVertialPanel = new Vbox();
	
	private Panel paymentTopPanel = new Panel();
	private Grid paymentTopLayout = GridFactory.newGridLayout();
	
	private Panel studentTablePanel = new Panel();
	private  Borderlayout studentTableLayout = new Borderlayout();
	
	private Panel paymentTablePanel = new Panel();
	private  Borderlayout paymentTableLayout = new Borderlayout();
	
	private Panel paymentButtonPanel = new Panel();
	private Grid paymentButtonLayout = GridFactory.newGridLayout();
	
	private Panel paymentSummaryTopPanel = new Panel();
	private Grid paymentSummaryTopLayout = GridFactory.newGridLayout();
	
	private Panel  paymentTotalPanel = new  Panel();
	private Grid paymentTotalLayout = GridFactory.newGridLayout();
	
	private Panel  paymentConfirmPanel = new  Panel();
	private Grid paymentConfirmLayout = GridFactory.newGridLayout();
	
	
	private Label lGeneralData = new Label();
	private Checkbox worksInCollege = new Checkbox();
	private Label lStudentData = new Label();
	
	private Label lSingleMonth = new Label();
	private WNumberEditor fSingleMonth = new WNumberEditor();
	private Label lSingleYear = new Label();
	private WNumberEditor fSingleYear = new WNumberEditor();;
	private Label lSingleTotal = new Label();
	private WNumberEditor fSingleTotal = new WNumberEditor();
	private Label lConsolidated = new Label();
	private Label lConMonth = new Label();
	private WNumberEditor fConMonth = new WNumberEditor();
	private Label lConYear = new Label();
	private WNumberEditor fConYear = new WNumberEditor();
	private Label lConTotal = new Label();
	private  WNumberEditor fConTotal = new WNumberEditor();
	
	private Label lPaymentReceipt = new Label();
	private Label lDocumentType = new Label();
	private Combobox fDocumentType = new Combobox();
	private Label lDocumentNo = new Label();
	private Textbox fDocumentNo = new Textbox();
	private Label lDate = new Label();
	private WDateEditor fDate = new WDateEditor();
	private Label lConcept = new Label();
	private Label lValue = new Label();
	private  WNumberEditor fValue = new  WNumberEditor();
	private Label lValueDiff = new Label();
	private  WNumberEditor fValueDiff = new  WNumberEditor();
	private  Label lTotal = new Label();
	private  WNumberEditor fTotal = new  WNumberEditor();
	
	private Button bPaymentPrint = new Button(Msg.translate(ctx, "Imprimir Comprobante"));
	private Button bSavePayment = new Button(Msg.translate(ctx, "CompletePayments"));

	private Label currentBox = new Label();
	
	protected Label holderLabel = new Label();
	//protected WStringEditor holderField = new WStringEditor();
	private Combobox holderField = new Combobox();
	
	protected Label lScheduleYear = new Label();
	protected WEditor fScheduleYear;
	
	protected Label ldiscoutType = new Label();
	private Listbox fdiscoutType = new Listbox();
	
	
	public WFCAQPayment()
	{
		try
		{
			
			loadStartData();
			dynInit();
			zkInit();
			
			Borderlayout contentPane = new Borderlayout();
			form.appendChild(contentPane);
			contentPane.setWidth("99%");
			contentPane.setHeight("100%");
			Center center = new Center();
			center.setStyle("border: none");
			contentPane.appendChild(center);
			center.appendChild(tabbedPane);
			center.setFlex(true);
			South south = new South();
			south.setStyle("border: none");
			contentPane.appendChild(south);
			south.appendChild(statusBar);
			LayoutUtils.addSclass("status-border", statusBar);
			south.setHeight("22px");			
			
			loadStudentTable();
			loadPaymentTable();
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "", e);
		}
	}

	
	


	private void zkInit() {
		
		
		mainPanel.setWidth("99%");
		mainPanel.setHeight("90%");
		mainPanel.setStyle("border: none; position: absolute");
		
		DesktopTabpanel tabpanel = new DesktopTabpanel();
		tabpanel.appendChild(mainPanel);
		Tabpanels tabPanels = new Tabpanels();
		tabPanels.appendChild(tabpanel);
		tabbedPane.appendChild(tabPanels);
		Tabs tabs = new Tabs();
		tabbedPane.appendChild(tabs);
		Tab tab = new Tab(Msg.getMsg(Env.getCtx(), "Payment"));
		tabs.appendChild(tab);
		
		paymentNorthVertialPanel.setHeight("250px");
		paymentNorthVertialPanel.setWidth("99%");
		
		//Define top panel
		paymentTopPanel.appendChild(paymentTopLayout);
		paymentTopPanel.setWidth("99%");
		//paymentTopPanel.setHeight("50px");
		Rows rows = null;
		Row row = null;
		rows = paymentTopLayout.newRows();
		row = rows.newRow();
		lGeneralData.setStyle("font-weight: bold");
		row.appendChild(lGeneralData);
		row = rows.newRow();

		row.appendChild(holderLabel.rightAlign());
		row.appendChild(holderField);
		row.appendChild(new Space());
		row.appendChild(lScheduleYear.rightAlign());
		row.appendChild(fScheduleYear.getComponent());
		row.appendChild(new Space());
		row.appendChild(worksInCollege);
		row.appendChild(new Space());
		row.appendChild(ldiscoutType.rightAlign());
		row.appendChild(fdiscoutType);	
		row.appendChild(new Space());
		row.appendChild(currentBox.rightAlign());
		row.appendChild(new Space());
		row.appendChild(bPaymentPrint);
		//row = rows.newRow();
		//row.appendChild(new Space());
		//row = rows.newRow();
		//row.appendChild(new Html("<hr>"));
		
		paymentNorthVertialPanel.appendChild(paymentTopPanel);
		
		
		//Define Table Students
		studentTablePanel.appendChild(studentTableLayout);
		studentTablePanel.appendChild((WListbox)studentTable);
		((WListbox)studentTable).setWidth("99%");
		((WListbox)studentTable).setHeight("100%");
		
		paymentNorthVertialPanel.appendChild(studentTablePanel);
		
		//South Panel
		paymentButtonPanel.appendChild(paymentButtonLayout);
		rows = paymentButtonLayout.newRows();
		row = rows.newRow();
		lStudentData.setStyle("font-weight: bold");
		row.appendChild(lStudentData);
		row = rows.newRow();
		row.appendChild(lSingleMonth.rightAlign());
		fSingleMonth.setReadWrite(false);
		row.appendChild(fSingleMonth.getComponent());
		row.appendChild(lSingleYear.rightAlign());
		fSingleYear.setReadWrite(false);
		row.appendChild(fSingleYear.getComponent());
		row.appendChild(lSingleTotal.rightAlign());
		fSingleTotal.setReadWrite(false);
		row.appendChild(fSingleTotal.getComponent());;

		row = rows.newRow();
		//row.appendChild(new Space());
		//row = rows.newRow();
		//row.appendChild(new Html("<hr>"));
		//row = rows.newRow();
		lConsolidated.setStyle("font-weight: bold");
		row.appendChild(lConsolidated);
		
		row = rows.newRow();
		row.appendChild(lConMonth.rightAlign());
		fConMonth.setReadWrite(true);
		row.appendChild(fConMonth.getComponent());
		row.appendChild(lConYear.rightAlign());
		fConYear.setReadWrite(true);
		row.appendChild(fConYear.getComponent());
		row.appendChild(lConTotal.rightAlign());
		fConTotal.setReadWrite(false);
		row.appendChild(fConTotal.getComponent());
		
		
		paymentNorthVertialPanel.appendChild(paymentButtonPanel);
		
		North north = new North();
		north.appendChild(paymentNorthVertialPanel);
		mainPanel.appendChild(north);	
		
		paymentCenterVertialPanel.setWidth("99%");
		paymentCenterVertialPanel.setHeight("99%");
		
		paymentSummaryTopPanel.appendChild(paymentSummaryTopLayout);
		paymentSummaryTopPanel.setWidth("800px");
		paymentSummaryTopPanel.setHeight("100%");
		
		
		paymentTotalPanel.appendChild(paymentTotalLayout);
		paymentTotalPanel.setWidth("99%");
		paymentTotalPanel.setHeight("100%");

		paymentConfirmPanel.appendChild(paymentConfirmLayout);
		paymentConfirmPanel.setWidth("99%");
		paymentConfirmPanel.setHeight("100%");

		
		rows = paymentSummaryTopLayout.newRows();
		row = rows.newRow();
		lPaymentReceipt.setStyle("font-weight: bold");
		
		row.appendChild(lPaymentReceipt);
		row = rows.newRow();
		row.appendChild(lDocumentType.rightAlign());
		row.appendChild(fDocumentType);
		row = rows.newRow();
		fDocumentNo.setReadonly(true);
		row.appendChild(lDocumentNo.rightAlign());
		row.appendChild(fDocumentNo);
		row.appendChild(lDate.rightAlign());
		row.appendChild(fDate.getComponent());
		
		row.appendChild(lValue.rightAlign());
		row.appendChild(fValue.getComponent());
		
		row = rows.newRow();
		
		paymentCenterVertialPanel.appendChild(paymentSummaryTopPanel);
		

		paymentTablePanel.appendChild(paymentTableLayout);
		paymentTablePanel.appendChild((WListbox)paymentTable);
		((WListbox)paymentTable).setWidth("99%");
		((WListbox)paymentTable).setHeight("100%");
		
		paymentCenterVertialPanel.appendChild(paymentTablePanel);
		
		rows = paymentTotalLayout.newRows();
		row = rows.newRow();
		row.setAlign("right");
		row.appendChild(lTotal.rightAlign());
		row.setAlign("right");
		row.appendChild(fTotal.getComponent());
		fTotal.setReadWrite(false);
		fTotal.getComponent().setStyle("font-size:16px;");
		lTotal.setStyle("font-size:16px;");
		row = rows.newRow();
		row.setAlign("right");
		fValueDiff.setReadWrite(false);
		fValueDiff.getComponent().setStyle("font-size:16px;");
		lValueDiff.setStyle("font-size:16px;");
		row.appendChild(lValueDiff.rightAlign());
		row.setAlign("right");
		row.appendChild(fValueDiff.getComponent());
		
		paymentCenterVertialPanel.appendChild(paymentTotalPanel);
		Center center = new Center();
		center.appendChild(paymentCenterVertialPanel);
		mainPanel.appendChild(center);
		
		
		South south = new South();
		south.appendChild( paymentConfirmPanel);
		confirmPanelGen.setStyle("margin-top:2px; margin-bottom:2px; margin-right:2px;margin-left:2px;");
		mainPanel.appendChild(south);
		
		rows =  paymentConfirmLayout.newRows();
		row = rows.newRow();
		row.setAlign("center");
		row.appendChild(bSavePayment);
		
		//
		tabpanel = new DesktopTabpanel();
		tabPanels.appendChild(tabpanel);
		tabpanel.appendChild(genPanel);
		tabCreditInfo = new Tab(Msg.getMsg(Env.getCtx(), "Credit Status"));
		tabCreditInfo.addEventListener(Events.ON_CLICK , this);
		tabs.appendChild(tabCreditInfo);
		
		tabpanel = new DesktopTabpanel();
		tabPanels.appendChild(tabpanel);
		tabpanel.appendChild(studentPanel);
		
	}

	

	private void dynInit() {		
		paymentTable = ListboxFactory.newDataTable();
		studentTable = ListboxFactory.newDataTable();

		MLookup lookup = MLookupFactory.get (ctx, form.getWindowNo(), 0, MColumn.getColumn_ID(X_CA_SchoolYear.Table_Name, X_CA_SchoolYear.COLUMNNAME_CA_SchoolYear_ID), DisplayType.TableDir);
		fScheduleYear = new WTableDirEditor (X_CA_SchoolYear.COLUMNNAME_CA_SchoolYear_ID, true, false, true, lookup);
		lScheduleYear.setText(Msg.translate(Env.getCtx(), X_CA_SchoolYear.COLUMNNAME_CA_SchoolYear_ID));
		//fScheduleYear.setValue(AcademicUtil.getCurrentSchoolYear());
		fScheduleYear.setValue(getCurrentSchoolYear(ctx));
		fScheduleYear.addValueChangeListener(this);
		//fScheduleYear.setValue(Env.getContextAsInt(Env.getCtx(), X_CA_SchoolYear.COLUMNNAME_CA_SchoolYear_ID));
		//("C_BPartner_ID", true, false, true, lookup);
		
		
		
		holderLabel.setText(Msg.getElement(Env.getCtx(), "Value", false));
    	holderLabel.setMandatory(true);
        //holderField = new WStringEditor ("CA_Holder_ID", false, false, true, 10, 30, null, null);	
		
		//lParent.setText(Msg.translate(ctx, "Parent"));		
		//int AD_Column_ID = 2893;        //  C_BPartner.C_BPartner_ID
		//MLookup lookupBP = MLookupFactory.get (ctx, form.getWindowNo(), 0, AD_Column_ID, DisplayType.Search);
		//fParent = new WSearchEditor("C_BPartner_ID", true, false, true, lookupBP);
		
		lGeneralData.setText(Msg.translate(ctx, "GeneralData"));
		
		worksInCollege.setLabel(Msg.translate(ctx, "WorksInCollege"));
		worksInCollege.setEnabled(false);
		
		ldiscoutType.setText(Msg.translate(Env.getCtx(), "StudentDiscountType"));
		fdiscoutType.setValue(Msg.translate(Env.getCtx(), "StudentDiscountType"));
		fdiscoutType.setMold("select");
		fdiscoutType.setRows(1);
		fdiscoutType.setDisabled(true);
		fdiscoutType.addEventListener(Events.ON_CLICK, this);
		fillStudentDiscountType(fdiscoutType);
		
		
		lStudentData.setText(Msg.translate(ctx, "StudentData"));
		/*lChildNo.setText(Msg.translate(ctx,"ChildNo"));
		lScholarship.setText(Msg.translate(ctx, "Scholarship"));*/
		lSingleMonth.setText(Msg.translate(ctx, "SingleMonth"));
		lSingleYear.setText(Msg.translate(ctx, "SingleYear"));
		lSingleTotal.setText(Msg.translate(ctx, "SingleTotal"));
				
		lConsolidated.setText(Msg.translate(ctx,"FamilyConsolidated"));
		lConMonth.setText(Msg.translate(ctx, "ConsolidatedMonth"));
		lConYear.setText(Msg.translate(ctx, "ConsolidatedYear"));
		lConTotal.setText(Msg.translate(ctx, "ConsolidatedTotal"));
		lValueDiff.setText(Msg.translate(ctx, "Difference"));
		lTotal.setText(Msg.getElement(ctx, "TotalLines"));
		//lPaymentModality.setText(Msg.translate(ctx, "PaymentModality"));
		
		lPaymentReceipt.setText(Msg.translate(ctx, "PaymentReceipt"));
		lDocumentType.setText(Msg.translate(ctx, "DocumentType"));
		lDocumentNo.setText(Msg.translate(ctx, "DocumentNo"));
		lDate.setText(Msg.translate(ctx, "Date"));
		fDate.setValue(new Timestamp(System.currentTimeMillis()));
		lConcept.setText(Msg.translate(ctx, "Concept"));
		lValue.setText(Msg.translate(ctx, "PaymentValue"));
		//lPaymentMode.setText(Msg.translate(ctx, "PaymentMode"));		
		//fPaymentMode.appendItem("Diners", "DI");
		//fPaymentMode.appendItem("Pago Anticipado", "AP");
		//fPaymentMode.appendItem("Rol de Pagos", "PR");
		//fPaymentMode.appendItem("Pago Mesual", "MP");
		//fPaymentMode.appendItem("Pago Web", "WP");
		
		currentBox.setText("Caja Actual: " + cashbox.getAccountNo());
		
		for(MDocType doctype : doctypes)
		{
			fDocumentType.appendItem(doctype.getName(), doctype.get_ID());
			fDocumentType.setSelectedIndex(0);
		}
		
		
		fDocumentNo.setText(MSequence.getDocumentNo(Env.getContextAsInt(ctx, "@#AD_Client_ID@"), "CA_Payment", null) );
		
		
		holderField.addEventListener(Events.ON_SELECT , this);
		holderField.addEventListener(Events.ON_CHANGING , this);
		
		((WListbox)studentTable).addActionListener(this);
		//bNewPayment.addActionListener(this);
		bSavePayment.addActionListener(this);
		
		//addPaymentRow();
		
	}
	
	/**
	 *  Fill Posting Type
	 *  @param cb Listox to be filled
	 */
	
	protected void fillStudentDiscountType (Listbox cb)
	{
		int AD_Reference_ID = 1000110;
		ValueNamePair[] pt = MRefList.getList(Env.getCtx(), AD_Reference_ID, true);
		
		for (int i = 0; i < pt.length; i++)
		{
			cb.appendItem(pt[i].getName(), pt[i]);
		}
	} // fillPostingType
	
	private void loadStudentTable() {
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>(); //new getCandidateData(fEvaluator.getValue(), fDate.getValue(), fGroup.getValue());
		Vector<String> columnNames = getStudentColumnNames();
		
		((WListbox)studentTable).clear();
		((WListbox)studentTable).getModel().removeTableModelListener(this);

		ListModelTable model = new ListModelTable(data);
		model.addTableModelListener(this);
		((WListbox)studentTable).addActionListener(this);
		((WListbox)studentTable).setData(model, columnNames);
		
		studentTable.setColumnClass(0, Boolean.class, true);        //  ID
		studentTable.setColumnClass(1, String.class, true);        //  1- Código
		studentTable.setColumnClass(2, String.class, true);          // 2- Nombre
		studentTable.setColumnClass(3, String.class, true);          // 3- Código seguro
		studentTable.setColumnClass(4, String.class, true);          // 4- Matricula
		studentTable.setColumnClass(5, String.class, true);          // 5- Codigo Bus
		studentTable.setColumnClass(6, Integer.class, true);
		studentTable.setColumnClass(7, Button.class, true); 	// Student Info
		studentTable.setColumnClass(8, Button.class, true); 	// Student Statement
	}
	
	private void loadPaymentTable() {
		
		Vector<Vector<Object>> rows = new Vector<Vector<Object>>();
		
		List<MCAPaymentType> paymentTypes = MCAPaymentType.getPaymentTypes(Env.getCtx(), null);
		
		for (MCAPaymentType paymentType :  paymentTypes)
		{	
			Vector<Object> data = new Vector<Object>();			
			Combobox  paymentTypeCombo = new Combobox();
			for(MCAPaymentType type :  paymentTypes)
				 paymentTypeCombo.appendItem(type.getName(), type.getValue());
			
			 paymentTypeCombo.addEventListener(Events.ON_CHANGE, this);
			 paymentTypeCombo.setSelectedIndex(0);
			 paymentTypeCombo.setValue(paymentType.getName());
			 
			data.add(paymentTypeCombo); // 0
			
			Combobox cardType = new Combobox();
			cardType.addEventListener(Events.ON_CHANGE, this);
			int index = 0;
			for(MRefList card : cardTypes)
			{
				
				 cardType.appendItem(card.getName(), card.getValue());
				 if (card.getValue().equals(paymentType.getCreditCardType()))
					 cardType.setSelectedIndex(index);
					 
			}
			
			data.add(cardType);  // 1
			
			if(paymentType.getNumberOfShares() > 0)
				data.add(paymentType.getNumberOfShares()); // 2
			else
				data.add(null); // 2

	
			data.add(paymentType.isHasInterests()); // 3
			
			Combobox bankAccount = new Combobox();
			data.add(bankAccount); // 4
			data.add(""); // 5
			data.add(new BigDecimal(0.0)); // 6
			rows.add(data);
		}
		
		((WListbox)paymentTable).clear();
		((WListbox)paymentTable).getModel().removeTableModelListener(this);
		
		ListModelTable model = new ListModelTable(rows);		
		model.addTableModelListener(this);
		((WListbox)studentTable).addActionListener(this);
		((WListbox)paymentTable).setData(model, getPaymentColumnNames());
		paymentTable.setColumnClass(0, Combobox.class, true);          
		paymentTable.setColumnClass(1, Combobox.class, true);       
		paymentTable.setColumnClass(2, BigDecimal.class, true);        
		paymentTable.setColumnClass(3, Boolean.class, true);       
		paymentTable.setColumnClass(4, String.class, true);          
		paymentTable.setColumnClass(5, String.class, true);         
		paymentTable.setColumnClass(6, BigDecimal.class, false);
	}


	@Override
	public void valueChange(ValueChangeEvent evt) {

		String name = evt.getPropertyName();
		Object value = evt.getNewValue();
		
		clean();
		
		if (value == null)
			return;
		
		if ("C_BPartner_ID".equals(name))
		{
			//fParent.setValue(value);
			holderField.setValue(value);
			//loadBPartner();
		}
		
	}


	@Override
	public void tableChanged(WTableModelEvent event) {
		if (event.getModel().equals(((WListbox)studentTable).getModel()))
		{	
			currentStudentValue = (String)studentTable.getValueAt(event.getIndex0(), INDEX_VALUE);
			Boolean isPayment = (Boolean) studentTable.getValueAt(event.getIndex0(), 0);
			calculatePayment();
		}
		if (event.getModel().equals(((WListbox)paymentTable).getModel()))
		{
			calculateTatalPayment();
		}
	}


	@Override
	public void onEvent(Event event) throws Exception {
		
		if (event.getTarget() instanceof Button)
		{
			Button button = (Button) event.getTarget();
			if (button.getLabel().equals(Msg.translate(Env.getCtx(), I_C_BPartner.COLUMNNAME_C_BPartner_ID)))
			{
				
				int AD_Window_ID = 1000061;
				MQuery query = new MQuery(I_C_BPartner.Table_ID);
				query.addRestriction(I_C_BPartner.COLUMNNAME_Value, MQuery.EQUAL,  getCurrentStudentValue());
				SessionManager.getAppDesktop().openWindow(AD_Window_ID, query);
			}
			if (button.getLabel().equals(Msg.translate(Env.getCtx(), I_S_TimeExpenseLine.COLUMNNAME_S_TimeExpenseLine_ID)))
			{
				Env.setContext(ctx, "#CA_Holder_ID",  holder.getCA_Holder_ID());
				Env.setContext(ctx, "#C_BPartner_ID", MBPartner.get(ctx, getCurrentStudentValue()).getC_BPartner_ID());
				SessionManager.getAppDesktop().openBrowse(1000002);
			}
			
		}
		if (event.getTarget().equals(holderField) && event.getName().equals(Events.ON_CHANGE))
        {	
			fillComboBoxHoder (holderField.getValue());
            return;
        }
		if (event.getTarget().equals(holderField) && event.getName().equals(Events.ON_SELECT))
        {	
			if (holderField.getSelectedItem().getValue() != null)
				findCustomer(holderField.getSelectedItem().getValue());
			
            return;
        }
		//else if(event.getTarget().equals(bNewPayment))
		//{
			//addPaymentRow();
		//}
		else if(event.getTarget().equals(bSavePayment))
		{
				if (validatePayment())
				{	
					
				
				boolean success = completePayments( 
						fDocumentNo.getText(),
						((Integer) fDocumentType.getSelectedItem().getValue()).intValue(),  
						(Timestamp)fDate.getValue(), 
						((Integer)fScheduleYear.getValue()).intValue(), getPaymenData() , getStudentData());
				
				if(!success)
					FDialog.error(0, message_error);
				else
				{
					FDialog.error(0, "Pagos generados");
					dinamycRefresh();
				}
			}
			
		}
		else if(event.getTarget() instanceof Combobox)
		{
			refreshSelectedRow((Combobox)event.getTarget());
		}
	}


	private void fillComboBoxHoder(String value) {
		StringBuilder whereClause = new StringBuilder();
		whereClause
		.append(I_CA_Holder.COLUMNNAME_Value).append(" LIKE ? OR ")
		.append(I_CA_Holder.COLUMNNAME_Name).append(" LIKE ?");

		String searchKey = null;
		if (value != null && value.length() > 6)
				searchKey = "%" + value.replace(" ", "%") + "%";
		else 
			return;
		
		List<MCAHolder> holders = new Query(Env.getCtx(), I_CA_Holder.Table_Name, whereClause.toString(), null)
		.setClient_ID()
				.setParameters(searchKey, searchKey)
				.list();
		
		holderField.removeAllItems();
		
		for (MCAHolder holder : holders)
			holderField.appendItem(holder.getValue() + " " + holder.getName() , holder.getValue());

	}


	private Vector<Vector<Object>> getPaymenData() {
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		ListModelTable model = ((WListbox)paymentTable).getModel();
		int rows = model.getNoRows();
		int topIndex = 1; 
		for (int row = 0; row <= rows - topIndex; row++)
		{	
			Vector<Object> line = new Vector<Object>();
			line.add(true);
			line.set(PAYMENT_TYPE, model.getValueAt(row, PAYMENT_TYPE));
			line.set(PAYMENT_CREDIT_CARD_TYPE,  model.getValueAt(row,PAYMENT_CREDIT_CARD_TYPE));
			line.set(PAYMENT_NUMBER_OF_SHARES, model.getValueAt(row,PAYMENT_NUMBER_OF_SHARES));
			line.set(PAYMENT_HAS_INTERESTS, model.getValueAt(row,PAYMENT_HAS_INTERESTS));
			line.add(PAYMENT_BANK_ACCOUNT_NO, model.getValueAt(row,PAYMENT_BANK_ACCOUNT_NO));
			line.add(PAYMENT_REFERENCE_NO, model.getValueAt(row,PAYMENT_REFERENCE_NO));
			line.add(PAYMENT_PAY_AMT, model.getValueAt(row,PAYMENT_PAY_AMT));			
			data.add(line);
		}	
		return data;
	}

	private Vector<Vector<Object>> getStudentData() {
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		ListModelTable model = ((WListbox) studentTable).getModel();
		int rows = model.getNoRows();
		int topIndex = 1;
		for (int row = 0; row <= rows - topIndex; row++) {
			Vector<Object> line = new Vector<Object>();
			line.add(true);
			line.set(STUDENT_IS_PAID, model.getValueAt(row, STUDENT_IS_PAID));
			line.set(STUDENT_VALUE, model.getValueAt(row, STUDENT_VALUE));
			line.set(STUDENT_NAME, model.getValueAt(row, STUDENT_NAME));
			line.set(STUDENT_GRADE, model.getValueAt(row, STUDENT_GRADE));
			line.add(STUDENT_MODALITY, model.getValueAt(row, STUDENT_MODALITY));
			line.add(STUDENT_TRANSPORT,model.getValueAt(row, STUDENT_TRANSPORT));
			line.add(STUDENT_CHILD_NO, model.getValueAt(row, STUDENT_CHILD_NO));
			line.add(STUDENT_INFO, model.getValueAt(row, STUDENT_INFO));
			line.add(STUDENT_STATEMENT, model.getValueAt(row, STUDENT_STATEMENT));
			data.add(line);
		}
		return data;
	}



	private boolean validatePayment() {
		BigDecimal payment = fValue.getValue() != null ? (BigDecimal) fValue.getValue() :  BigDecimal.ZERO;
		BigDecimal paymentTotal =  fTotal.getValue()  != null ?  (BigDecimal) fTotal.getValue() :  BigDecimal.ZERO;		
		
		if (payment.signum() == 0)
		{	
			FDialog.error(form.getWindowNo() , form , "No existe monto a pagar");
			return false;
		}	
		
		if (paymentTotal.signum() == 0)
		{	
			FDialog.error(form.getWindowNo() , form , "No se han ingresado pagos");
			return false;
		}	
		
		if (payment.compareTo(paymentTotal) != 0)
		{	
			FDialog.error(form.getWindowNo() , form , "No debe existir diferencia para procesar el pago");
		}	
		return true;
	}





	private void findCustomer(Object display) {
		//find if code is a holder
		holder = findHolder(display);
		fdiscoutType.setValue(MRefList.getListName(ctx, 1000110, holder.getStudentDiscountType()));
		if (holder != null)
		{
			worksInCollege.setChecked(holder.isEmployee());
			loadStuden(loadStudentData(holder));	
		}	
	}

	private MCAHolder findHolder(Object display) {
	
		StringBuilder whereClause = new 	StringBuilder();
		whereClause
		.append(I_CA_Holder.COLUMNNAME_Value).append("=? OR ")
		.append(I_CA_Holder.COLUMNNAME_Name).append(" LIKE '%").append(display).append("%' OR ")
		.append("EXISTS (SELECT 1 FROM C_BPartner p  WHERE p.C_BPartner_ID=CA_Holder.BPartner_Parent_ID AND p.Value=? OR p.TaxID=?)");
		return new Query(Env.getCtx() , I_CA_Holder.Table_Name , whereClause.toString(), null)
		.setClient_ID().setParameters(display.toString(), display.toString() , display.toString())
		.first();
	}

	@Override
	public ADForm getForm() {
		return form;
	}
	
	private void calculatePayment() {
		paymentValue = Env.ZERO;
		ListModelTable model = ((WListbox)studentTable).getModel();
		int topIndex = 1;
		int rows = studentTable.getRowCount();
		for (int row = 0; row <= rows - topIndex; row++) {
			boolean isApplyPayment  = (Boolean) model.getValueAt(row, 0);
			String partnerValue = (String) model.getValueAt(row, 1);
			if (isApplyPayment)
			{	
				for (MTimeExpenseLine line : paymentSchedules)
				{
					if (line.getC_BPartner().getValue().equals(partnerValue))
						paymentValue = paymentValue.add(line.getApprovalAmt());
					
				}
			}
		}
		fValue.setValue(paymentValue);
	}
	
	private void loadStuden(Vector<Vector<Object>> data)
	{
		Vector<String> columnNames = getStudentColumnNames();
		((WListbox)studentTable).clear();
		((WListbox)studentTable).getModel().removeTableModelListener(this);
		ListModelTable model = new ListModelTable(data);
		model.addTableModelListener(this);		
		((WListbox)studentTable).setData(model, columnNames);
		
		int row = 0;
		for (Vector<Object> cols : data)
		{
			Button bStudent = new Button(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
			bStudent.setWidth("90%");
			bStudent.setLabel(Msg.translate(Env.getCtx(), I_C_BPartner.COLUMNNAME_C_BPartner_ID));
			bStudent.addActionListener(this);		
			cols.set(7, bStudent);
			
			Button bStatement = new Button(Msg.translate(Env.getCtx() , I_S_TimeExpenseLine.COLUMNNAME_S_TimeExpenseLine_ID));
			bStatement.setWidth("90%");
			bStatement.setLabel(Msg.translate(Env.getCtx() , I_S_TimeExpenseLine.COLUMNNAME_S_TimeExpenseLine_ID));
			bStatement.addActionListener(this);
			cols.set(8, bStatement);
			//data.set(row, cols);
			
		}
		
		studentTable.setColumnClass(0, Boolean.class, false);       // 1- ID
		studentTable.setColumnClass(1, String.class, true);          // 2- Código
		studentTable.setColumnClass(2, String.class, true);          // 3- Nombre
		studentTable.setColumnClass(3, String.class, true);          // 4- Código seguro
		studentTable.setColumnClass(4, String.class, true);          // 5- Matricula
		studentTable.setColumnClass(5, String.class, true);          // 6- Codigo Bus
		studentTable.setColumnClass(6, Integer.class, true);
		studentTable.setColumnClass(7, Button.class, false); 	// Student Info
		studentTable.setColumnClass(8, Button.class, false); 	// Student Statement
		((WListbox)studentTable).setSelectedIndex(0);
		refresh();
	}
	
	
	private void refresh()
	{
		int row = studentTable.getSelectedRow();
		String value = (String)studentTable.getValueAt(row, INDEX_VALUE);
		Boolean isPayment = (Boolean) studentTable.getValueAt(row, 0);
		
		calculateItems(value);
		
		fSingleMonth.setValue(singleMonth);
		fSingleYear.setValue(singleYear);
		fSingleTotal.setValue(singleTotal);
		
		fConMonth.setValue(consolidatedMonth);
		fConMonth.setReadWrite(false);
		fConYear.setValue(consolidatedYear);
		fConYear.setReadWrite(false);
		fConTotal.setValue(consolidatedTotal);
		fConTotal.setReadWrite(false);
		fValue.setValue(consolidatedTotal);
	}

	private void addPaymentRow()
	{		
		Vector<Vector<Object>> rows = new Vector<Vector<Object>>();
		Combobox paymentType = new Combobox();
		for( MRefList payT :  paymentTypes)
			paymentType.appendItem(payT.getName(), payT.getValue());
		
		paymentType.addEventListener(Events.ON_CHANGE, this);
		paymentType.setSelectedIndex(0);
		
		for (MRefList payT :  paymentTypes)
		{	
			Vector<Object> data = new Vector<Object>();
			paymentType.setValue(payT.getName());

			
			data.add(paymentType);
			
			Combobox cardType = new Combobox();
			cardType.addEventListener(Events.ON_CHANGE, this);
			data.add(cardType);
			data.add(1);
	
			data.add(new Boolean(false));
			
			Combobox bankAccount = new Combobox();
	
			data.add(bankAccount);
			
			data.add("");
			
			data.add(0);
			rows.add(data);
		}
		
		((WListbox)paymentTable).clear();
		((WListbox)paymentTable).getModel().removeTableModelListener(this);
		
		ListModelTable model = new ListModelTable(rows);		
		model.addTableModelListener(this);
		((WListbox)paymentTable).setData(model, getPaymentColumnNames());
		paymentTable.setColumnClass(1, Combobox.class, false);          
		paymentTable.setColumnClass(2, Combobox.class, false);       
		paymentTable.setColumnClass(3, BigDecimal.class, false);        
		paymentTable.setColumnClass(4, Boolean.class, false);         
		paymentTable.setColumnClass(5, String.class, false);          
		paymentTable.setColumnClass(6, String.class, false);         
		paymentTable.setColumnClass(7, BigDecimal.class, false);
		//refreshSelectedRow(paymentType);
	}	
	
	
	private void refreshSelectedRow(Combobox current)
	{
		int rowNo = -1;
		boolean updatecardtype = true;
		
		// find current row
		for(int x=0; x<=paymentTable.getRowCount()-1;x++)
		{
			Combobox rowCombo = (Combobox)paymentTable.getValueAt(x,1);
			Combobox rowCombo2 = (Combobox)paymentTable.getValueAt(x,2);
			
			if(rowCombo.equals(current) || rowCombo2.equals(current))
			{
				updatecardtype = rowCombo2.equals(current) ? false : true;
				rowNo=x;
				current=rowCombo;
			}
		}
		
		// Update cardType
		Combobox cardCombo = (Combobox)paymentTable.getValueAt(rowNo,2);

		if(updatecardtype)
		{
			cardCombo.removeAllItems();
			
			if(!current.getSelectedItem().getValue().equals("C")) //Tarjeta de Crédito
			{
				cardCombo.appendItem("N/A", "NA");
			}
			else 
			{
				for(MRefList card : cardTypes)
				{
					cardCombo.appendItem(card.getName(), card.getValue());
				}
			}
			
			cardCombo.setSelectedIndex(0);
		}
		
		// Update bankAccount
		Combobox bankCombo = (Combobox)paymentTable.getValueAt(rowNo,5);
		bankCombo.removeAllItems();
		if(!current.getSelectedItem().getValue().equals("E") && !current.getSelectedItem().getValue().equals("C")) //Efectivo,  Tarjeta de Crédito
		{
			for(MBankAccount bankaccount : bankAccounts)
			{
				if(bankaccount.get_ID()!=cashbox.get_ID() && 
						bankaccount.getC_Bank_ID()!=bankbox.get_ID() &&
						bankaccount.getC_Bank_ID()!=bankdiners.get_ID()&&
						bankaccount.getC_Bank_ID()!=bankvisa.get_ID())
				{
					bankCombo.appendItem(bankaccount.getName(), bankaccount.get_ID());
				}	
			}
		}
		else if(current.getSelectedItem().getValue().equals("C")) //Tarjeta de Crédito
		{	
			String cardType = (String)cardCombo.getItemAtIndex(cardCombo.getSelectedIndex()).getValue();
			
			if("D".equals(cardType))
			{
				for(MBankAccount bankaccount : bankAccounts)
				{
					if(bankaccount.getC_Bank_ID()==bankdiners.get_ID())
					{
						bankCombo.appendItem(bankaccount.getName(), bankaccount.get_ID());
					}	
				}
			}
			else if("V".equals(cardType))
			{
				for(MBankAccount bankaccount : bankAccounts)
				{
					if(bankaccount.getC_Bank_ID()==bankvisa.get_ID())
					{
						bankCombo.appendItem(bankaccount.getName(), bankaccount.get_ID());
					}	
				}
			}
		}
		else
		{
			bankCombo.appendItem(cashbox.getName(), cashbox.get_ID());
		}
		bankCombo.setSelectedIndex(0);
	}

	@Override
	public void cleanComponents() {
		//fChildNo.setValue("");
		//fChildNo.setReadonly(true);
		
		fSingleMonth.setValue(Env.ZERO);
		fSingleYear.setValue(Env.ZERO);
		fSingleTotal.setValue(Env.ZERO);
		
		fConMonth.setValue(Env.ZERO);
		fConYear.setValue(Env.ZERO);
		fConTotal.setValue(Env.ZERO);
		fValue.setValue(Env.ZERO);
		loadStudentTable();
		loadPaymentTable();
	}





	@Override
	public void showErrorMessage(String message) {
		FDialog.error(0, message);
	}


	@Override
	public String getComboValue(int row, int column) {
		
		
		String retValue = "";
		
		Combobox combo = (Combobox) paymentTable.getValueAt(row, column);
		
		Object value = combo.getSelectedItem().getValue();
		if(value instanceof String)
			retValue = (String)value;
		else if(value instanceof java.lang.Integer)
			retValue = String.valueOf(value);
			
		
		return retValue;
	}





	@Override
	public void dinamycRefresh() {
		//loadPaymentTable();
	
		if(studentTable.getRowCount()>0)
		{
			((WListbox)studentTable).setSelectedIndex(0);
			//refresh();
		}
		
		fDocumentNo.setText(MSequence.getDocumentNo(Env.getContextAsInt(ctx, "@#AD_Client_ID@"), "CA_Payment", null) );
	}
	
	private String getCurrentStudentValue()
	{
		ListModelTable model = ((WListbox)studentTable).getModel();
		currentStudentValue = (String) studentTable.getValueAt(studentTable.getSelectedRow(), 1);
		return currentStudentValue;
	}
	
	
	private void calculateTatalPayment()
	{
		BigDecimal totalPayment = BigDecimal.ZERO;
		BigDecimal payment = fValue.getValue() != null ? (BigDecimal) fValue.getValue() : BigDecimal.ZERO;
		
		ListModelTable model = ((WListbox)paymentTable).getModel();
		int rows = model.getRowCount();
		int topIndex = 1;
		for (int row = 0; row <= rows - topIndex; row++)
		{
			BigDecimal paymentLine = (BigDecimal) paymentTable.getValueAt(row, 6);
			if (payment != null)
			{
				totalPayment = totalPayment.add(paymentLine);
			}
		}
		BigDecimal diffPayment = payment.subtract(totalPayment);
		fValueDiff.setValue(diffPayment);
		fTotal.setValue(totalPayment);
	}
	
}
