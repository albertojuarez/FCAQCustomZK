package org.fcaq.process;

import java.util.List;

import org.compiere.model.MBPartner;
import org.compiere.model.MUser;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.fcaq.model.X_CA_NoteCategory;
import org.fcaq.model.X_CA_NoteType;

public class CopyCategoryTemplete extends SvrProcess{


	MBPartner bpartnerfrom = null;
	MBPartner bpartnerto = null;
	MUser userfrom = null;
	MUser userto = null;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("C_BPartner_ID"))
				bpartnerfrom  = new MBPartner(this.getCtx(), para[i].getParameterAsInt(), this.get_TrxName());
			else if(name.equals("BPartner_Parent_ID"))
				bpartnerto  = new MBPartner(this.getCtx(), para[i].getParameterAsInt(), this.get_TrxName());

		}
	}

	@Override
	protected String doIt() throws Exception {


		userfrom = new Query(this.getCtx(), MUser.Table_Name, MUser.COLUMNNAME_C_BPartner_ID + "=?", this.get_TrxName())
		.setOnlyActiveRecords(true)
		.setParameters(bpartnerfrom.get_ID())
		.first();

		userto = new Query(this.getCtx(), MUser.Table_Name, MUser.COLUMNNAME_C_BPartner_ID + "=?", this.get_TrxName())
		.setOnlyActiveRecords(true)
		.setParameters(bpartnerto.get_ID())
		.first();
		
		
		// Copying category


		List<X_CA_NoteCategory> categories  = new Query(this.getCtx(), X_CA_NoteCategory.Table_Name, "User1_ID=?", this.get_TrxName())
		.setOnlyActiveRecords(true)
		.setParameters(userfrom.get_ID())
		.list();

		for(X_CA_NoteCategory category : categories)
		{
			X_CA_NoteCategory categoryTo = new X_CA_NoteCategory(this.getCtx(), 0, this.get_TrxName());

			categoryTo.setName(category.getName());
			categoryTo.set_ValueOfColumn("SeqNo", category.get_Value("SeqNo"));
			categoryTo.set_ValueOfColumn("User1_ID", userto.get_ID());
			categoryTo.saveEx();
		}
		
		//Copying types

		List<X_CA_NoteType> types = new Query(this.getCtx(), X_CA_NoteType.Table_Name, "User1_ID=?", this.get_TrxName())
		.setOnlyActiveRecords(true)
		.setParameters(userfrom.get_ID())
		.list();

		for(X_CA_NoteType type : types)
		{
			X_CA_NoteType typeTo = new X_CA_NoteType(this.getCtx(), 0, this.get_TrxName());

			typeTo.setName(type.getName());
			typeTo.set_ValueOfColumn("SeqNo", type.get_Value("SeqNo"));
			typeTo.set_ValueOfColumn("User1_ID", userto.get_ID());
			typeTo.saveEx();
		}


		return "Created Templete";
	}

}
