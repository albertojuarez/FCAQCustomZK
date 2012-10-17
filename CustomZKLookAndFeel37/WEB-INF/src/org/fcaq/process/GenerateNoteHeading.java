package org.fcaq.process;

import java.util.List;

import org.compiere.model.MBPartner;
import org.compiere.model.MUser;
import org.compiere.model.Query;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.fcaq.model.X_CA_CourseDef;
import org.fcaq.model.X_CA_MatterAssignment;
import org.fcaq.model.X_CA_NoteHeading;
import org.fcaq.model.X_CA_Parcial;
import org.fcaq.model.X_CA_TeacherAssignment;
import org.fcaq.util.AcademicUtil;

public class GenerateNoteHeading extends SvrProcess{

	
	MBPartner currentBPartner = null;
	X_CA_Parcial parcial = null;
	int user_id = 0;
	
	@Override
	protected void prepare() {
		
		user_id = Env.getContextAsInt(this.getCtx(), "#AD_User_ID");
		MUser user = new MUser(this.getCtx(), user_id, this.get_TrxName());
		currentBPartner = (MBPartner)user.getC_BPartner();
		parcial = AcademicUtil.getCurrentParcial(getCtx());
		
	}

	@Override
	protected String doIt() throws Exception {
		
		
		String whereClause = X_CA_MatterAssignment.COLUMNNAME_CA_MatterAssignment_ID + 
				" IN ( SELECT " + X_CA_TeacherAssignment.COLUMNNAME_CA_MatterAssignment_ID + 
				" FROM " + X_CA_TeacherAssignment.Table_Name + 
				" WHERE " + X_CA_TeacherAssignment.COLUMNNAME_C_BPartner_ID + "=?)";
		
		List<X_CA_MatterAssignment> assignments = new Query(getCtx(), X_CA_MatterAssignment.Table_Name, whereClause, get_TrxName())
						.setOnlyActiveRecords(true)
						.setParameters(currentBPartner.get_ID())
						.list();
		
		for(X_CA_MatterAssignment assignment: assignments)
		{
			
			X_CA_CourseDef course = (X_CA_CourseDef)assignment.getCA_GroupAssignment().getCA_CourseDef();
			
			whereClause = X_CA_NoteHeading.COLUMNNAME_CA_NoteHeading_ID + 
					" IN (SELECT " + X_CA_NoteHeading.COLUMNNAME_CA_NoteHeading_ID +
					" FROM " + X_CA_NoteHeading.Table_Name + 
					" WHERE " + X_CA_NoteHeading.COLUMNNAME_CA_CourseDef_ID + "=? AND " +
					X_CA_NoteHeading.COLUMNNAME_CA_SubjectMatter_ID + "=? AND " +
					X_CA_NoteHeading.COLUMNNAME_CA_Parcial_ID + "=? AND " +
					X_CA_NoteHeading.COLUMNNAME_IsElective + "=?)";
			
			X_CA_NoteHeading heading = new Query(getCtx(), X_CA_NoteHeading.Table_Name, whereClause, get_TrxName())
					.setOnlyActiveRecords(true)
					.setParameters(course.get_ID(), 
								   course.isElective()?assignment.getElectiveSubject_ID():assignment.getCA_SubjectMatter_ID(),
								   parcial.get_ID(),
								   course.isElective())
					.first();
			
			if(heading==null)
			{
				heading = new X_CA_NoteHeading(this.getCtx(), 0, this.get_TrxName());
			}
			
			heading.setCA_CourseDef_ID(course.get_ID());
			heading.setCA_SubjectMatter_ID(course.isElective()?assignment.getElectiveSubject_ID():assignment.getCA_SubjectMatter_ID());
			heading.setCA_Parcial_ID(parcial.get_ID());
			heading.setIsElective(course.isElective());
			heading.set_ValueOfColumn("User1_ID", user_id);
			heading.saveEx();
		}
				
		
		
		return "Updated";
	}

}
