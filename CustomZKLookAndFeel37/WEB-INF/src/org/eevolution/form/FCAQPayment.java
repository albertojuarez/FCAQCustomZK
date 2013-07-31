package org.eevolution.form;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.fcaq.*;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.apps.form.Allocation;
import org.compiere.minigrid.IDColumn;
import org.compiere.minigrid.IMiniTable;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_S_TimeExpenseLine;
import org.compiere.model.MBPartner;
import org.compiere.model.MBank;
import org.compiere.model.MBankAccount;
import org.compiere.model.MDocType;
import org.compiere.model.MExpenseType;
import org.compiere.model.MInvoiceSchedule;
import org.compiere.model.MPayment;
import org.compiere.model.MProduct;
import org.compiere.model.MRefList;
import org.compiere.model.MSequence;
import org.compiere.model.MTimeExpenseLine;
import org.compiere.model.MUser;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.fcaq.model.MCAHolder;
import org.fcaq.model.MCAHolderRelation;
import org.fcaq.model.X_CA_Payment;

public abstract class FCAQPayment {

	public static CLogger log = CLogger.getCLogger(Allocation.class);

	public MBPartner m_bpartner = null;
	public LinkedHashMap<String, I_C_BPartner> studentList = new LinkedHashMap<String, I_C_BPartner>();
	public List<MTimeExpenseLine>  paymentSchedules = new ArrayList<MTimeExpenseLine>();
	public List<MDocType>  doctypes = new ArrayList<MDocType>();
	public List<MRefList> paymentTypes = new ArrayList<MRefList>();
	public List<MRefList> cardTypes = new ArrayList<MRefList>();

	public MBank bankbox = null;
	public MBankAccount cashbox = null;
	public MBank bankdiners = null;
	public MBank bankvisa = null;
	public List<MBankAccount> bankAccounts = new ArrayList<MBankAccount>();

	public X_CA_Payment caPayment = null;


	protected BigDecimal singleMonth = Env.ZERO;
	protected BigDecimal singleYear = Env.ZERO;
	protected BigDecimal singleTotal = Env.ZERO;
	protected BigDecimal consolidatedMonth =  Env.ZERO;
	protected BigDecimal consolidatedYear = Env.ZERO;
	protected BigDecimal consolidatedTotal = Env.ZERO;
	protected BigDecimal paymentValue = Env.ZERO;

	public String message_error = "";
	public MSequence sequence = null;
	public Properties ctx = Env.getCtx();
	public int INDEX_VALUE = 1; // Value
	public IMiniTable paymentTable;
	public IMiniTable studentTable;

	public Vector<String> getStudentColumnNames()
	{
		Vector<String> columnNames = new Vector<String>();
		columnNames.add(Msg.translate(Env.getCtx(), "IsPaid"));
		columnNames.add(Msg.translate(Env.getCtx(), "Value"));
		columnNames.add(Msg.translate(Env.getCtx(), "Name"));
		columnNames.add(Msg.translate(Env.getCtx(), "Grade"));
		columnNames.add(Msg.translate(Env.getCtx(), "Modality"));
		columnNames.add(Msg.translate(Env.getCtx(), "TransportCode"));
		columnNames.add(Msg.translate(Env.getCtx(), "ChildNo"));

		return columnNames;
	}

	public Vector<String> getPaymentColumnNames()
	{
		Vector<String> columnNames = new Vector<String>();
		columnNames.add(Msg.getMsg(Env.getCtx(), "PaymentType"));
		columnNames.add(Msg.getMsg(Env.getCtx(), "CardType"));
		columnNames.add(Msg.getMsg(Env.getCtx(), "NoQuotes"));
		columnNames.add(Msg.getMsg(Env.getCtx(), "HasInterests"));
		columnNames.add(Msg.getMsg(Env.getCtx(), "BankAccount"));
		columnNames.add(Msg.getMsg(Env.getCtx(), "ReferenceNo"));
		columnNames.add(Msg.getMsg(Env.getCtx(), "Amount"));

		return columnNames;
	}


	public  Vector<Vector<Object>> getStudentData(MCAHolder holder)
	{
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	
		for (MCAHolderRelation relation : holder.getHolderRelations())
		{	
			I_C_BPartner bpartner = relation.getC_BPartner();
			
			Vector<Object> line = new Vector<Object>();
			line.add(true);
			line.add(bpartner.getValue());
			line.add(bpartner.getName());
			line.add("0000"); // replace by SecureCode
			line.add("0000"); // replace by Enrollment
			line.add("0000"); // replace by Transport Code
			line.add(relation.getChildNo());
			data.add(line);
			studentList.put(bpartner.getValue() , bpartner);
			findPaymentSchedule(bpartner);
		}	
		return data;
	}

	public  Vector<Vector<Object>> getParentData(MBPartner bpartner)
	{
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> line = new Vector<Object>();



		List<MBPartner> students = new Query(ctx, MBPartner.Table_Name, "C_BPartner_ID <> ? AND C_BPartner_ID IN (Select C_BPartner_ID from AD_User where BPartner_Parent_ID=?)", null) // missing contact clauses
		.setParameters(bpartner.get_ID(),bpartner.get_ID()).list();

		for (MBPartner student : students)
		{

			studentList.put(student.getValue() , student);
			findPaymentSchedule(student);

			line.add(student.getValue());
			line.add(student.getName());
			line.add("0000"); // replace by SecureCode
			line.add("0000"); // replace by Enrollment
			line.add("0000"); // replace by Transport Code	

			String whereClause = "AD_User_ID IN (Select AD_User_ID from AD_User where BPartner_Parent_ID=? AND C_BPartner_ID=? AND ISPAYMENTRESPONSIBLE=? )";
			MUser contact = new Query(ctx, MUser.Table_Name, whereClause, null)
			.setParameters(bpartner.get_ID(), student.get_ID(), true)
			.first();

			line.add(contact!=null?true:false);


			data.add(line);
			line = new Vector<Object>();
		}

		return data;
	}

	public void findPaymentSchedule(I_C_BPartner student)
	{
		List<MTimeExpenseLine>  changes = new Query(ctx, MTimeExpenseLine.Table_Name,I_S_TimeExpenseLine.COLUMNNAME_C_BPartner_ID + "=?", null)
		.setParameters(student.getC_BPartner_ID())
		.list();

		if(paymentSchedules != null)
		{
			paymentSchedules.addAll(changes);
		}

	}



	public void calculateItems(String childValue)
	{
		singleMonth = Env.ZERO;;
		singleYear = Env.ZERO;;
		singleTotal = Env.ZERO;

		consolidatedMonth =  Env.ZERO;
		consolidatedYear = Env.ZERO;
		consolidatedTotal = Env.ZERO;
		paymentValue = Env.ZERO;
		

		I_C_BPartner student =   studentList.get(childValue);

		for(MTimeExpenseLine paymentSchedule : paymentSchedules)
		{
			if(paymentSchedule.getC_BPartner_ID() == student.getC_BPartner_ID())
			{
					if(!(paymentSchedule.get_ValueAsBoolean("IsPaid")))
					{
						I_M_Product concept =  paymentSchedule.getM_Product();
						MExpenseType conceptType = (MExpenseType) concept.getS_ExpenseType();

						String type = conceptType.get_ValueAsString(MInvoiceSchedule.COLUMNNAME_InvoiceFrequency);
						if(MInvoiceSchedule.INVOICEFREQUENCY_Monthly.equals(type))
							singleMonth = singleMonth.add(paymentSchedule.getApprovalAmt());
						else if("Y".equals(type))
							singleYear = singleYear.add(paymentSchedule.getApprovalAmt());

						singleTotal = singleTotal.add(paymentSchedule.getApprovalAmt());
					}
			}

			if(!(paymentSchedule.get_ValueAsBoolean("IsPaid")))
			{
				MProduct concept = (MProduct) paymentSchedule.getM_Product();
				MExpenseType conceptType = (MExpenseType) concept.getS_ExpenseType();
				String type = conceptType.get_ValueAsString(MInvoiceSchedule.COLUMNNAME_InvoiceFrequency);					
				if(MInvoiceSchedule.INVOICEFREQUENCY_Monthly.equals(type))
					consolidatedMonth = consolidatedMonth.add(paymentSchedule.getApprovalAmt());
				else if("Y".equals(type))
					consolidatedYear = consolidatedYear.add(paymentSchedule.getApprovalAmt());
				
				consolidatedTotal = consolidatedTotal.add(paymentSchedule.getApprovalAmt());
			}
		}
	}


	public void loadStartData()
	{
		String whereClause="DocBaseType = ? ";
		doctypes=new Query(ctx, MDocType.Table_Name,whereClause, null)
		.setParameters("APP").setClient_ID().setOnlyActiveRecords(true).list();

		sequence = new Query(ctx, MSequence.Table_Name, "Name = 'FCAQPayment'", null)
		.setClient_ID().setOnlyActiveRecords(true).first();

		paymentTypes = new Query(ctx, MRefList.Table_Name, "AD_Reference_ID=?", null)
		.setOnlyActiveRecords(true).setParameters(214).list();

		cardTypes = new Query(ctx, MRefList.Table_Name, "AD_Reference_ID=?", null)
		.setOnlyActiveRecords(true).setParameters(149).list();

		bankAccounts = new Query(ctx, MBankAccount.Table_Name, "", null)
		.setClient_ID().setOnlyActiveRecords(true).list();



		bankbox = new Query(ctx, MBank.Table_Name, "SwiftCode=?", null)
		.setOnlyActiveRecords(true).setParameters("C").first();

		bankdiners = new Query(ctx, MBank.Table_Name, "SwiftCode=?", null)
		.setOnlyActiveRecords(true).setParameters("D").first();

		bankvisa = new Query(ctx, MBank.Table_Name, "SwiftCode=?", null)
		.setOnlyActiveRecords(true).setParameters("V").first();

		if(bankbox!=null)
		{
			List<MBankAccount> cashboxes = new Query(ctx, MBankAccount.Table_Name, "C_Bank_ID=? AND SalesRep_ID=?", null)
			.setParameters(bankbox.get_ID(), Env.getContext(ctx, "#AD_User_ID"))
			.setOnlyActiveRecords(true).list();

			if(cashboxes.size()>1)
			{
				showErrorMessage("Existen mas de 2 cajas asignadas a este usuario");
			}
			else if(cashboxes.size()==0)
			{
				showErrorMessage("Este Usuario no tiene ninguna caja asignada");
			}
			else
			{
				cashbox = cashboxes.get(0);
			}

		}


	}



	public void clean()
	{
		m_bpartner = null;
		studentList = new LinkedHashMap<String, I_C_BPartner>();
		paymentSchedules = new ArrayList<MTimeExpenseLine>();
		caPayment= null;

		cleanComponents();
	}



	public abstract void cleanComponents();

	public abstract void showErrorMessage(String message);

	public abstract String getComboValue(int row, int column);

	public abstract void dinamycRefresh();



	public boolean completePayments(int docType_ID, String documentNo, Timestamp trxDate)
	{
		boolean success = false;

		message_error = "No se pudo completar los pagos";

		try{

			success = validatePayments();

			// create header


				caPayment = new X_CA_Payment(ctx, 0 ,null);
				caPayment.setC_BPartner_ID(m_bpartner.get_ID());
				caPayment.setDocumentNo(documentNo);
				caPayment.setC_DocType_ID(docType_ID);
				caPayment.setDateTrx(trxDate);
				caPayment.saveEx();
			

			// save lines 


			// buscar tabla de prioridades
			//List<X_CA_PaymentPreference> priorities = new Query(ctx, X_CA_PaymentPreference.Table_Name, "", null)
			//.setOnlyActiveRecords(true).setOrderBy("SeqNo").list();



			for(int tableRow=0; tableRow<=paymentTable.getRowCount()-1; tableRow++)
			{

				String type = getComboValue(tableRow, 0);

				if(!"AN".equals(type) && !"ME".equals(type))
					throw new AdempiereException("Indicar tipo de pago (Anual/Mensual)");

				String paymentType = getComboValue(tableRow, 1);
				String card = getComboValue(tableRow, 2);
				java.lang.Number quote = (java.lang.Number)paymentTable.getValueAt(tableRow, 3);
				Boolean hasinteres = (Boolean)paymentTable.getValueAt(tableRow, 4);
				String account = getComboValue(tableRow,5);
				String reference = (String)paymentTable.getValueAt(tableRow, 6);
				java.lang.Number amount = (java.lang.Number)paymentTable.getValueAt(tableRow, 7);

				for(int studentRow = 0; studentRow<=studentTable.getRowCount()-1; studentRow++)
				{

					String value = (String)studentTable.getValueAt(studentRow, INDEX_VALUE);
					I_C_BPartner student = studentList.get(value);
					Boolean doPay = (Boolean)studentTable.getValueAt(studentRow, 5);

					if(doPay)
					{
						MTimeExpenseLine currentSchedule=null;

						for(MTimeExpenseLine schedule : paymentSchedules)
						{
							if(schedule.getC_BPartner_ID()==student.getC_BPartner_ID())
							{
								currentSchedule=schedule;
							}
						}

						if(currentSchedule == null)
							throw new AdempiereException("No existe programa de Pago");

						// generar nuevo pago
						MPayment payment = new MPayment(ctx, 0, null);

						payment.set_ValueOfColumn("CA_Payment_ID", caPayment.get_ID());
						payment.setC_DocType_ID(1000008); // pago, que siempre no, que es cobro
						payment.setC_BankAccount_ID(Integer.parseInt(account));

						payment.set_ValueOfColumn("SalesRep_ID",Env.getContextAsInt(ctx, "#AD_User_ID"));

						payment.setTenderType(paymentType);

						if("K".equals(paymentType) || "Q".equals(paymentType)) // cheque nacional - internacional
						{
							payment.setCheckNo(reference);
						}
						else if("A".equals(paymentType)) // Depósito directo
						{
							payment.setAccountNo(reference);
						}
						else if("C".equals(paymentType)) // Tarjeta de Crédito
						{
							payment.setCreditCardType(card);
							payment.setCreditCardNumber(reference);
							payment.set_ValueOfColumn("NumberOfShares",new BigDecimal(String.valueOf(quote)));
							payment.set_ValueOfColumn("HasInterests", hasinteres);
						}


						payment.setC_BPartner_ID(student.getC_BPartner_ID());
					
						payment.setPayAmt(new BigDecimal(String.valueOf( getAmount(currentSchedule, type) )));
						payment.setC_Order_ID(currentSchedule.get_ID());
						payment.setC_Currency_ID(100); //USD

						if(payment.getPayAmt().compareTo(new BigDecimal("0"))>0)
						{
							payment.saveEx();
							payment.processIt(DocAction.ACTION_Complete);
						}
					}
				}

			}


			success = true;
		}
		catch(Exception e)
		{
			success = false;
			e.printStackTrace();
			message_error = e.getMessage();
		}

		return success;
	}



	private boolean validatePayments()
	{
		boolean valid = true;


		return valid;
	}



	private BigDecimal getAmount(MTimeExpenseLine schedule, String mode) {
		BigDecimal amount = Env.ZERO;

		if (!(schedule.get_ValueAsBoolean("IsPaid"))) {
			MProduct concept = (MProduct) schedule.getM_Product();
			MExpenseType conceptType = (MExpenseType) concept
					.getS_ExpenseType();
			String type = concept
					.get_ValueAsString(MInvoiceSchedule.COLUMNNAME_InvoiceFrequency);
			if (mode.equals(type)) {
				amount = amount.add(schedule.getApprovalAmt());
				schedule.set_ValueOfColumn("IsPaid", true);
				schedule.saveEx();
			}

		}

		return amount;
	}
}
