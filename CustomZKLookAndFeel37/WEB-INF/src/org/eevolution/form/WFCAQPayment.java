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
import org.adempiere.webui.editor.WNumberEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.panel.StatusBarPanel;
import org.adempiere.webui.window.FDialog;
import org.compiere.minigrid.IDColumn;
import org.compiere.model.I_C_Payment;
import org.compiere.model.MBPartner;
import org.compiere.model.MBankAccount;
import org.compiere.model.MDocType;
import org.compiere.model.MRefList;
import org.compiere.model.MSequence;
import org.compiere.model.MTimeExpenseLine;
import org.compiere.model.Query;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.fcaq.model.I_CA_Holder;
import org.fcaq.model.MCAHolder;
import org.fcaq.model.MCAPaymentType;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vbox;

public class WFCAQPayment extends FCAQPayment implements IFormController, EventListener, WTableModelListener, ValueChangeListener{

	private MCAHolder holder = null;
	private Tabbox tabbedPane = new Tabbox();
	private Borderlayout mainPanel = new Borderlayout();
	//private Grid selNorthPanel = GridFactory.newGridLayout();
	private ConfirmPanel confirmPanelSel = new ConfirmPanel(true);
	private ConfirmPanel confirmPanelGen = new ConfirmPanel(false, true, false, false, false, false, false);
	private StatusBarPanel statusBar = new StatusBarPanel();
	private Borderlayout genPanel = new Borderlayout();
	private Borderlayout studentPanel = new Borderlayout();
	private Html info = new Html();
	//private Borderlayout mainLayout = new Borderlayout();
	private BusyDialog progressWindow;

	
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
	
	private Button bRefresh = new Button();
	//private Panel southPanel = new Panel();
	
	private Label lGeneralData = new Label();
	//private Label lParent = new Label();
	//private WSearchEditor fParent = null;
	private Checkbox worksInCollege = new Checkbox();
	private Label lStudentData = new Label();
	
	//Payment fields
	//private Label lChildNo = new Label();
	//private Textbox fChildNo = new Textbox();
	//private Label lScholarship = new Label();
	//private Textbox fScholarship = new Textbox();
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
	private Combobox fConcept = new Combobox();
	private Label lValue = new Label();
	private  WNumberEditor fValue = new  WNumberEditor();
	private  WNumberEditor fValueDiff = new  WNumberEditor();
	
	private Button bNewPayment = new Button(Msg.translate(ctx, "NewPayment"));
	private Button bSavePayment = new Button(Msg.translate(ctx, "CompletePayments"));

	private Label currentBox = new Label();
	
	protected Label holderLabel = new Label();
	protected WStringEditor holderField = new WStringEditor();
	
	
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
		paymentTopPanel.setWidth("800px");
		//paymentTopPanel.setHeight("50px");
		Rows rows = null;
		Row row = null;
		rows = paymentTopLayout.newRows();
		row = rows.newRow();
		lGeneralData.setStyle("font-weight: bold");
		row.appendChild(lGeneralData);
		row = rows.newRow();

		row.appendChild(holderLabel.rightAlign());
		row.appendChild(holderField.getComponent());
		row.appendChild(new Space());
		row.appendChild(worksInCollege);
		row.appendChild(new Space());
		row.appendChild(currentBox.rightAlign());
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
		
		/*row = rows.newRow();
		row.appendChild(lConcept.rightAlign());
		row.appendChild(fConcept);*/
		
		row.appendChild(lValue.rightAlign());
		row.appendChild(fValue.getComponent());
		fValueDiff.setReadWrite(false);
		row.appendChild(fValueDiff.getComponent());
		
		//row = rows.newRow();
		//row.appendChild(new Space());
		//row = rows.newRow();
		//row.appendChild(new Html("<hr>"));
		row = rows.newRow();
		row.appendChild(bNewPayment);
		row.appendChild(bSavePayment);
		
		paymentCenterVertialPanel.appendChild(paymentSummaryTopPanel);
		

		paymentTablePanel.appendChild(paymentTableLayout);
		paymentTablePanel.appendChild((WListbox)paymentTable);
		((WListbox)paymentTable).setWidth("99%");
		((WListbox)paymentTable).setHeight("100%");
		
		paymentCenterVertialPanel.appendChild(paymentTablePanel);

		Center center = new Center();
		center.appendChild(paymentCenterVertialPanel);
		mainPanel.appendChild(center);
		
		South south = new South();
		south.appendChild(confirmPanelGen);
		confirmPanelGen.setStyle("margin-top:2px; margin-bottom:2px; margin-right:2px;margin-left:2px;");
		
		confirmPanelGen.addActionListener(this);
		mainPanel.appendChild(south);

		//
		tabpanel = new DesktopTabpanel();
		tabPanels.appendChild(tabpanel);
		tabpanel.appendChild(genPanel);
		tab = new Tab(Msg.getMsg(Env.getCtx(), "Credit Status"));
		tabs.appendChild(tab);
		
		tabpanel = new DesktopTabpanel();
		tabPanels.appendChild(tabpanel);
		tabpanel.appendChild(studentPanel);
		tab = new Tab(Msg.getMsg(Env.getCtx(), "Data User"));
		tabs.appendChild(tab);
		
	}

	

	private void dynInit() {		
		paymentTable = ListboxFactory.newDataTable();
		studentTable = ListboxFactory.newDataTable();

		holderLabel.setText(Msg.getElement(Env.getCtx(), "Value", false));
    	holderLabel.setMandatory(true);
        holderField = new WStringEditor ("CA_Holder_ID", false, false, true, 10, 30, null, null);	
		
		//lParent.setText(Msg.translate(ctx, "Parent"));		
		//int AD_Column_ID = 2893;        //  C_BPartner.C_BPartner_ID
		//MLookup lookupBP = MLookupFactory.get (ctx, form.getWindowNo(), 0, AD_Column_ID, DisplayType.Search);
		//fParent = new WSearchEditor("C_BPartner_ID", true, false, true, lookupBP);
		
		lGeneralData.setText(Msg.translate(ctx, "GeneralData"));
		
		worksInCollege.setLabel(Msg.translate(ctx, "WorksInCollege"));
		worksInCollege.setEnabled(false);
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
		
		
		//fParent.addValueChangeListener(this);
		holderField.getComponent().addEventListener(Events.ON_CHANGE, this);
		
		((WListbox)studentTable).addActionListener(this);
		bNewPayment.addActionListener(this);
		bSavePayment.addActionListener(this);
		
		//addPaymentRow();
		
	}
	
	private void loadStudentTable() {
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>(); //new getCandidateData(fEvaluator.getValue(), fDate.getValue(), fGroup.getValue());
		Vector<String> columnNames = getStudentColumnNames();
		
		((WListbox)studentTable).clear();
		((WListbox)studentTable).getModel().removeTableModelListener(this);
		
		ListModelTable model = new ListModelTable(data);
		model.addTableModelListener(this);
		((WListbox)studentTable).setData(model, columnNames);
		
		studentTable.setColumnClass(0, Boolean.class, true);        //  ID
		studentTable.setColumnClass(1, String.class, false);        //  1- Código
		studentTable.setColumnClass(2, String.class, false);          // 2- Nombre
		studentTable.setColumnClass(3, String.class, false);          // 3- Código seguro
		studentTable.setColumnClass(4, String.class, false);          // 4- Matricula
		studentTable.setColumnClass(5, String.class, false);          // 5- Codigo Bus
		studentTable.setColumnClass(6, Integer.class, false);

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

			
			data.add(paymentTypeCombo);
			
			Combobox cardType = new Combobox();
			cardType.addEventListener(Events.ON_CHANGE, this);
			int index = 0;
			for(MRefList card : cardTypes)
			{
				
				 cardType.appendItem(card.getName(), card.getValue());
				 if (card.getValue().equals(paymentType.getCreditCardType()))
					 cardType.setSelectedIndex(index);
					 
			}
			
			data.add(cardType);
			if(paymentType.getNumberOfShares() > 0)
				data.add(paymentType.getNumberOfShares());
			else
				data.add(null);
	
			data.add(paymentType.isHasInterests());
			
			Combobox bankAccount = new Combobox();
			data.add(bankAccount);
			data.add("");
			data.add(Env.ZERO);
			rows.add(data);
		}
		
		((WListbox)paymentTable).clear();
		((WListbox)paymentTable).getModel().removeTableModelListener(this);
		
		ListModelTable model = new ListModelTable(rows);		
		model.addTableModelListener(this);
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
		String value = (String)studentTable.getValueAt(event.getIndex0(), INDEX_VALUE);
		Boolean isPayment = (Boolean) studentTable.getValueAt(event.getIndex0(), 0);
		calculatePayment();
	}


	@Override
	public void onEvent(Event event) throws Exception {

		if (event.getTarget().equals(holderField.getComponent()))
        {	
			findCustomer(holderField.getDisplay());
            return;
        }
		
		if(event.getTarget().equals(studentTable))
		{
			if(studentTable.getSelectedRow()>=0)
			{
				//refresh();
			}
		}
		else if(event.getTarget().equals(bNewPayment))
		{
			//addPaymentRow();
		}
		else if(event.getTarget().equals(bSavePayment))
		{
			boolean success = completePayments(((Integer) fDocumentType.getSelectedItem().getValue()).intValue() , fDocumentNo.getText(), (Timestamp)fDate.getValue());
			
			if(!success)
				FDialog.error(0, message_error);
			else
			{
				FDialog.error(0, "Pagos generados");
				dinamycRefresh();
			}
			
		}
		else if(event.getTarget() instanceof Combobox)
		{
			refreshSelectedRow((Combobox)event.getTarget());
		}
	}


	private void findCustomer(String display) {
		//find if code is a holder
		MCAHolder holder = findHolder(display);
		if (holder != null)
		{
			worksInCollege.setChecked(holder.isEmployee());
			loadStuden(getStudentData(holder));	
		}	
		
		MBPartner bpartner = MBPartner.get(Env.getCtx(), display);
		if(bpartner != null)
		{
			worksInCollege.setChecked(bpartner.isEmployee());
		}
	}

	private MCAHolder findHolder(String display) {
	
		StringBuilder whereClause = new 	StringBuilder();
		whereClause
		.append(I_CA_Holder.COLUMNNAME_Value).append("=? OR ")
		.append(I_CA_Holder.COLUMNNAME_Name).append(" LIKE '%").append(display).append("%' OR ")
		.append("EXISTS (SELECT 1 FROM C_BPartner p  WHERE p.C_BPartner_ID=CA_Holder.BPartner_Parent_ID AND p.Value=? OR p.TaxID=?)");
		return new Query(Env.getCtx() , I_CA_Holder.Table_Name , whereClause.toString(), null)
		.setClient_ID().setParameters(display, display , display)
		.first();
	}

	@Override
	public ADForm getForm() {
		return form;
	}
	
	/*private void loadBPartner()
	{	
		m_bpartner = new MBPartner(ctx, (Integer)fParent.getValue(),null);
		
		if(m_bpartner.get_ValueAsBoolean("IsStudent"))
		{
			worksInCollege.setChecked(false);
			loadStuden(m_bpartner,true);
		}
		else
		{
			//Verifica si es empleado
			X_HR_Employee employee = new Query(ctx, X_HR_Employee.Table_Name, "C_BPartner_ID=?", m_bpartner.get_TrxName()).setParameters(m_bpartner.get_ID()).first();
			worksInCollege.setChecked(employee!=null?true:false);
			loadStuden(m_bpartner, false);
		}
	}*/
	
	
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
		
		studentTable.setColumnClass(0, Boolean.class, false);       // 1- ID
		studentTable.setColumnClass(1, String.class, true);          // 2- Código
		studentTable.setColumnClass(2, String.class, true);          // 3- Nombre
		studentTable.setColumnClass(3, String.class, true);          // 4- Código seguro
		studentTable.setColumnClass(4, String.class, true);          // 5- Matricula
		studentTable.setColumnClass(5, String.class, true);          // 6- Codigo Bus
		studentTable.setColumnClass(6, Integer.class, true);		 
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
		paymentTable.setColumnClass(3, java.lang.Number.class, false);        
		paymentTable.setColumnClass(4, Boolean.class, false);         
		paymentTable.setColumnClass(5, String.class, false);          
		paymentTable.setColumnClass(6, String.class, false);         
		paymentTable.setColumnClass(7, java.lang.Number.class, false);
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
		loadPaymentTable();
	
		if(studentTable.getRowCount()>0)
		{
			((WListbox)studentTable).setSelectedIndex(0);
			//refresh();
		}
		
		fDocumentNo.setText(MSequence.getDocumentNo(Env.getContextAsInt(ctx, "@#AD_Client_ID@"), "CA_Payment", null) );
	}
	
}
