package org.eevolution.form;

import java.sql.Timestamp;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Combobox;
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
import org.adempiere.webui.window.FDialog;
import org.compiere.model.MAttributeInstance;
import org.compiere.model.MBPartner;
import org.compiere.model.MBankAccount;
import org.compiere.model.MDocType;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MRefList;
import org.compiere.model.MSequence;
import org.compiere.model.Query;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.eevolution.model.X_HR_Employee;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Html;
import org.zkoss.zul.Space;

public class WFCAQPayment extends FCAQPayment implements IFormController, EventListener, WTableModelListener, ValueChangeListener{

	private CustomForm form = new CustomForm();
	private Borderlayout mainLayout = new Borderlayout();
	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();
	
	private Button bRefresh = new Button();
	private Panel southPanel = new Panel();
	private StatusBarPanel statusBar = new StatusBarPanel();
	
	
	private Panel paymentPanel = new Panel();
	private Borderlayout borderPaymentLayout = new Borderlayout();
	private Panel paymentSummaryPanel = new Panel();
	private Grid paymentSummaryLayout = GridFactory.newGridLayout();
	
	private Panel paymentSouthPanel = new Panel();
	private Grid paymentSouthLayout = GridFactory.newGridLayout();
	
	private Label lGeneralData = new Label();
	private Label lParent = new Label();
	private WSearchEditor fParent = null;
	private Checkbox worksInCollege = new Checkbox();
	private Label lStudentData = new Label();
	
	//Payment fields
	
	private Label lChildNo = new Label();
	private Textbox fChildNo = new Textbox();
	private Label lScholarship = new Label();
	private Textbox fScholarship = new Textbox();
	private Label lSingleMonth = new Label();
	private Textbox fSingleMonth = new Textbox();
	private Label lSingleYear = new Label();
	private Textbox fSingleYear = new Textbox();
	private Label lSingleTotal = new Label();
	private Textbox fSingleTotal = new Textbox();
	private Label lConsolidated = new Label();
	private Label lConMonth = new Label();
	private Textbox fConMonth = new Textbox();
	private Label lConYear = new Label();
	private Textbox fConYear = new Textbox();
	private Label lConTotal = new Label();
	private Textbox fConTotal = new Textbox();
	private Label lPaymentModality = new Label();
	private Combobox fPaymentMode = new Combobox();
	
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
	private Textbox fValue = new Textbox();
	private Textbox fValueDiff = new Textbox();
	
	private Label lPaymentMode = new Label();
	
	private Button bNewPayment = new Button(Msg.translate(ctx, "NewPayment"));
	private Button bSavePayment = new Button(Msg.translate(ctx, "CompletePayments"));

	private Label currentBox = new Label();
	
	
	public WFCAQPayment()
	{
		try
		{
			
			loadStartData();
			dynInit();
			zkInit();
			loadStudentTable();
			loadPaymentTable();
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
		Rows rows = null;
		Row row = null;
		parameterLayout.setWidth("800px");
		rows = parameterLayout.newRows();
		row = rows.newRow();
		lGeneralData.setStyle("font-weight: bold");
		row.appendChild(lGeneralData);
		row = rows.newRow();
		row.appendChild(lParent.rightAlign());
		row.appendChild(fParent.getComponent());
		row.appendChild(new Space());
		row.appendChild(worksInCollege);
		row.appendChild(new Space());
		row.appendChild(currentBox.rightAlign());
		row = rows.newRow();
		row = rows.newRow();
		row.appendChild(new Space());
		row = rows.newRow();
		row.appendChild(new Html("<hr>"));
		row = rows.newRow();
		lStudentData.setStyle("font-weight: bold");
		row.appendChild(lStudentData);
		
		
		
		
		Center center = new Center();
		center.setFlex(true);
		mainLayout.appendChild(center);
		
		center.appendChild((WListbox)studentTable);
		center.setStyle("height: 25%");
		((WListbox)studentTable).setWidth("99%");
		((WListbox)studentTable).setHeight("100%");
		center.setStyle("border: none");
		
		
		South south = new South();
		south.setStyle("border: none");
		south.setStyle("height: 75%");

		mainLayout.appendChild(south);
		south.appendChild(borderPaymentLayout);

		// Payment 
		

		North paynorth = new North();
		paynorth.setStyle("border: none");
		paynorth.appendChild(paymentSummaryPanel);
		paymentSummaryPanel.appendChild(paymentSummaryLayout);

		
		borderPaymentLayout.appendChild(paynorth);
		borderPaymentLayout.setWidth("800px");
		borderPaymentLayout.setHeight("100%");
		
		rows = paymentSummaryLayout.newRows();
		row = rows.newRow();
		row.appendChild(lChildNo.rightAlign());
		row.appendChild(fChildNo);
		row.appendChild(lScholarship.rightAlign());
		row.appendChild(fScholarship);
		
		row = rows.newRow();
		row.appendChild(lSingleMonth.rightAlign());
		row.appendChild(fSingleMonth);
		row.appendChild(lSingleYear.rightAlign());
		row.appendChild(fSingleYear);
		row.appendChild(lSingleTotal.rightAlign());
		row.appendChild(fSingleTotal);
		row = rows.newRow();

		row = rows.newRow();
		row.appendChild(new Space());
		row = rows.newRow();
		row.appendChild(new Html("<hr>"));
		row = rows.newRow();
		lConsolidated.setStyle("font-weight: bold");
		row.appendChild(lConsolidated);
		
		row = rows.newRow();
		row.appendChild(lConMonth.rightAlign());
		row.appendChild(fConMonth);
		row.appendChild(lConYear.rightAlign());
		row.appendChild(fConYear);
		row.appendChild(lConTotal.rightAlign());
		row.appendChild(fConTotal);
		row.appendChild(lPaymentModality.rightAlign());
		row.appendChild(fPaymentMode);
		row = rows.newRow();

		row = rows.newRow();
		row.appendChild(new Space());
		row = rows.newRow();
		row.appendChild(new Html("<hr>"));
		row = rows.newRow();
		lPaymentReceipt.setStyle("font-weight: bold");
		row.appendChild(lPaymentReceipt);
		
		row = rows.newRow();
		row.appendChild(lDocumentType.rightAlign());
		row.appendChild(fDocumentType);
		
		row = rows.newRow();
		row.appendChild(lDocumentNo.rightAlign());
		row.appendChild(fDocumentNo);
		row.appendChild(lDate.rightAlign());
		row.appendChild(fDate.getComponent());
		
		row = rows.newRow();
		row.appendChild(lConcept.rightAlign());
		row.appendChild(fConcept);
		row.appendChild(lValue.rightAlign());
		row.appendChild(fValue);
		row.appendChild(fValueDiff);
		row = rows.newRow();

		row = rows.newRow();
		row.appendChild(new Space());
		row = rows.newRow();
		row.appendChild(new Html("<hr>"));
		row = rows.newRow();
		lPaymentMode.setStyle("font-weight: bold");
		row.appendChild(lPaymentMode);
		row = rows.newRow();
		row.appendChild(bNewPayment);
		row.appendChild(bSavePayment);

		 center = new Center();
		center.setFlex(true);
		borderPaymentLayout.appendChild(center);
		
		center.appendChild((WListbox)paymentTable);
		center.setStyle("height: 25%");
		((WListbox)paymentTable).setWidth("99%");
		((WListbox)paymentTable).setHeight("100%");
		center.setStyle("border: none");
		
	}

	

	private void dynInit() {
		
		paymentTable = ListboxFactory.newDataTable();
		studentTable = ListboxFactory.newDataTable();
		
		lGeneralData.setText(Msg.translate(ctx, "GeneralData"));
		lParent.setText(Msg.translate(ctx, "Parent"));
		
		int AD_Column_ID = 2893;        //  C_BPartner.C_BPartner_ID
		MLookup lookupBP = MLookupFactory.get (ctx, form.getWindowNo(), 0, AD_Column_ID, DisplayType.Search);
		fParent = new WSearchEditor("C_BPartner_ID", true, false, true, lookupBP);
		
		
		worksInCollege.setLabel(Msg.translate(ctx, "WorksInCollege"));
		lStudentData.setText(Msg.translate(ctx, "StudentData"));
		lChildNo.setText(Msg.translate(ctx,"ChildNo"));
		lScholarship.setText(Msg.translate(ctx, "Scholarship"));
		lSingleMonth.setText(Msg.translate(ctx, "SingleMonth"));
		lSingleYear.setText(Msg.translate(ctx, "SingleYear"));
		lSingleTotal.setText(Msg.translate(ctx, "SingleTotal"));
				
		lConsolidated.setText(Msg.translate(ctx,"FamilyConsolidated"));
		lConMonth.setText(Msg.translate(ctx, "ConsolidatedMonth"));
		lConYear.setText(Msg.translate(ctx, "ConsolidatedYear"));
		lConTotal.setText(Msg.translate(ctx, "ConsolidatedTotal"));
		lPaymentModality.setText(Msg.translate(ctx, "PaymentModality"));
		
		lPaymentReceipt.setText(Msg.translate(ctx, "PaymentReceipt"));
		lDocumentType.setText(Msg.translate(ctx, "DocumentType"));
		lDocumentNo.setText(Msg.translate(ctx, "DocumentNo"));
		lDate.setText(Msg.translate(ctx, "Date"));
		fDate.setValue(new Timestamp(System.currentTimeMillis()));
		lConcept.setText(Msg.translate(ctx, "Concept"));
		lValue.setText(Msg.translate(ctx, "PaymentValue"));
		lPaymentMode.setText(Msg.translate(ctx, "PaymentMode"));
		
		fPaymentMode.appendItem("Diners", "DI");
		fPaymentMode.appendItem("Pago Anticipado", "AP");
		fPaymentMode.appendItem("Rol de Pagos", "PR");
		fPaymentMode.appendItem("Pago Mesual", "MP");
		fPaymentMode.appendItem("Pago Web", "WP");
		
		currentBox.setText("Caja Actual: " + cashbox.getAccountNo());
		
		for(MDocType doctype : doctypes)
		{
			fDocumentType.appendItem(doctype.getName(), doctype.get_ID());
			fDocumentType.setSelectedIndex(0);
		}
		
		
		fDocumentNo.setText(MSequence.getDocumentNo(Env.getContextAsInt(ctx, "@#AD_Client_ID@"), "CA_Payment", null) );
		
		
		fParent.addValueChangeListener(this);
		((WListbox)studentTable).addActionListener(this);
		bNewPayment.addActionListener(this);
		bSavePayment.addActionListener(this);
		
	}
	
	private void loadStudentTable() {
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>(); //new getCandidateData(fEvaluator.getValue(), fDate.getValue(), fGroup.getValue());
		Vector<String> columnNames = getStudentColumnNames();
		
		((WListbox)studentTable).clear();
		((WListbox)studentTable).getModel().removeTableModelListener(this);
		
		ListModelTable modelP = new ListModelTable(data);
		modelP.addTableModelListener(this);
		((WListbox)studentTable).setData(modelP, columnNames);
		
		studentTable.setColumnClass(0, String.class, false);        //  1- Código
		studentTable.setColumnClass(1, String.class, false);          // 2- Nombre
		studentTable.setColumnClass(2, String.class, false);          // 3- Código seguro
		studentTable.setColumnClass(3, String.class, false);          // 4- Matricula
		studentTable.setColumnClass(4, String.class, false);          // 5- Codigo Bus
		studentTable.setColumnClass(5, Boolean.class, false);

	}
	
	private void loadPaymentTable() {
		Vector<Vector<Object>> data = new Vector<Vector<Object>>(); //new getCandidateData(fEvaluator.getValue(), fDate.getValue(), fGroup.getValue());
		Vector<String> columnNames = getPaymentColumnNames();
		
		((WListbox)paymentTable).clear();
		((WListbox)paymentTable).getModel().removeTableModelListener(this);
		
		ListModelTable modelP = new ListModelTable(data);
		modelP.addTableModelListener(this);
		((WListbox)paymentTable).setData(modelP, columnNames);
		
		
		paymentTable.setColumnClass(0, Combobox.class, false); 
		paymentTable.setColumnClass(1, Combobox.class, false);          
		paymentTable.setColumnClass(2, Combobox.class, false);       
		paymentTable.setColumnClass(3, java.lang.Number.class, false);        
		paymentTable.setColumnClass(4, Boolean.class, false);         
		paymentTable.setColumnClass(5, String.class, false);          
		paymentTable.setColumnClass(6, String.class, false);         
		paymentTable.setColumnClass(7, java.lang.Number.class, false);          

		
		

		
		
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
			fParent.setValue(value);
			loadBPartner();
		}
		
	}


	@Override
	public void tableChanged(WTableModelEvent event) {

	}


	@Override
	public void onEvent(Event event) throws Exception {

		if(event.getTarget().equals(studentTable))
		{
			if(studentTable.getSelectedRow()>=0)
			{
				refresh();
			}
		}
		else if(event.getTarget().equals(bNewPayment))
		{
			addPaymentRow();
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


	@Override
	public ADForm getForm() {
		return form;
	}
	

	
	private void loadBPartner()
	{	
		
		
		m_bpartner = new MBPartner(ctx, (Integer)fParent.getValue(),null);
		
		if(m_bpartner.get_ValueAsBoolean("IsStudent"))
		{
			worksInCollege.setChecked(false);
			loadBPartner(m_bpartner,true);
		}
		else
		{
			//Verifica si es empleado
			X_HR_Employee employee = new Query(ctx, X_HR_Employee.Table_Name, "C_BPartner_ID=?", m_bpartner.get_TrxName()).setParameters(m_bpartner.get_ID()).first();
			worksInCollege.setChecked(employee!=null?true:false);
			loadBPartner(m_bpartner, false);
		}
	}
	
	private void loadBPartner(MBPartner bpartner, boolean student)
	{
		Vector<Vector<Object>> data; 
		
		if(student)
			data = getStudentData(m_bpartner);
		else
			data = getParentData(m_bpartner);
		
		
		Vector<String> columnNames = getStudentColumnNames();
		
		((WListbox)studentTable).clear();
		((WListbox)studentTable).getModel().removeTableModelListener(this);
		
		ListModelTable modelP = new ListModelTable(data);
		modelP.addTableModelListener(this);
		((WListbox)studentTable).setData(modelP, columnNames);
		

		studentTable.setColumnClass(0, String.class, true);        //  1- Código
		studentTable.setColumnClass(1, String.class, true);          // 2- Nombre
		studentTable.setColumnClass(2, String.class, true);          // 3- Código seguro
		studentTable.setColumnClass(3, String.class, true);          // 4- Matricula
		studentTable.setColumnClass(4, String.class, true);          // 5- Codigo Bus
		studentTable.setColumnClass(5, Boolean.class, false);
		
		if(studentTable.getRowCount()>0)
		{
			((WListbox)studentTable).setSelectedIndex(0);
			refresh();
		}

	}
	
	
	private void refresh()
	{
		int row = studentTable.getSelectedRow();
		String value = (String)studentTable.getValueAt(row, INDEX_VALUE);
		fChildNo.setValue(getChildNo(value));
		calculateItems(value);
		
		fSingleMonth.setText(String.valueOf(singleMonth));
		fSingleYear.setText(String.valueOf(singleYear));
		fSingleTotal.setText(String.valueOf(singleTotal));
		
		fConMonth.setText(String.valueOf(consolidatedMonth));
		fConYear.setText(String.valueOf(consolidatedYear));
		fConTotal.setText(String.valueOf(consolidatedTotal));
		
		String pm = getDefaultPayMode(value);
		
		for(int x = 0 ; x<=fPaymentMode.getItemCount()-1;x++)
		{
			if(fPaymentMode.getItemAtIndex(x).getValue().equals(pm))
			{
				fPaymentMode.setSelectedIndex(x);
			}
			
		}
			
		
	}
	
	
	private void addPaymentRow()
	{		
		
		Vector<Object> data = new Vector<Object>();
		
		
		Combobox type = new Combobox();
		type.appendItem("Anual", "AN");
		type.appendItem("Mensual", "ME");
		data.add(type);

		Combobox paymentType = new Combobox();
		for( MRefList payT :  paymentTypes)
			paymentType.appendItem(payT.getName(), payT.getValue());
		paymentType.addEventListener("onChange", this);
		paymentType.setSelectedIndex(0);
		data.add(paymentType);
		
		Combobox cardType = new Combobox();
		cardType.addEventListener("onChange", this);
		data.add(cardType);
		data.add(1);

		data.add(new Boolean(false));
		
		Combobox bankAccount = new Combobox();

		data.add(bankAccount);
		
		data.add("");
		
		data.add(0);
		
		((WListbox)paymentTable).getModel().add(data);
		
		refreshSelectedRow(paymentType);
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
		
		//---
		
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
		fChildNo.setValue("");
		
		fSingleMonth.setText("");
		fSingleYear.setText("");
		fSingleTotal.setText("");
		
		fConMonth.setText("");
		fConYear.setText("");
		fConTotal.setText("");
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
			refresh();
		}
		
		fDocumentNo.setText(MSequence.getDocumentNo(Env.getContextAsInt(ctx, "@#AD_Client_ID@"), "CA_Payment", null) );
	}
	
}
