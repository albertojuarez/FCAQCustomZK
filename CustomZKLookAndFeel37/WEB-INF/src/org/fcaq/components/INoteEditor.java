package org.fcaq.components;

import org.compiere.model.MBPartner;
import org.fcaq.model.X_CA_Note;
import org.fcaq.model.X_CA_NoteHeadingLine;
import org.fcaq.model.X_CA_NoteLine;

public interface INoteEditor {

	
	
	public void setNote(X_CA_Note note);
	public void setNoteLine(X_CA_NoteLine  noteLine);
	public void setStudent(MBPartner student);
	public void setNoteHeading(X_CA_NoteHeadingLine noteHeading);
	
	public X_CA_Note getNote();
	public X_CA_NoteLine getNoteLine();
	public MBPartner getStudent();
	public X_CA_NoteHeadingLine getNoteHeading();
	
	public void saveEx();
	
}
