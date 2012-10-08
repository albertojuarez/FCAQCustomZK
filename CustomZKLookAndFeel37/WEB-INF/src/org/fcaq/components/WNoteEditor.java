package org.fcaq.components;

import java.math.BigDecimal;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.MBPartner;
import org.compiere.util.Env;
import org.compiere.util.Trx;
import org.compiere.util.TrxRunnable;
import org.eevolution.form.AcademicNote;
import org.eevolution.form.DisciplineNotes;
import org.fcaq.model.X_CA_DisciplineConfig;
import org.fcaq.model.X_CA_MatterAssignment;
import org.fcaq.model.X_CA_Note;
import org.fcaq.model.X_CA_NoteHeadingLine;
import org.fcaq.model.X_CA_NoteLine;
import org.fcaq.model.X_CA_NoteRule;
import org.fcaq.model.X_CA_SchoolYearConfig;
import org.zkoss.zhtml.Table;
import org.zkoss.zhtml.Td;
import org.zkoss.zhtml.Tr;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;


public class WNoteEditor extends Div  implements INoteEditor{

	private static final long serialVersionUID = 299456427772228471L;
	private X_CA_Note note = null;
	private X_CA_NoteLine noteline = null;
	private MBPartner student = null;
	private X_CA_NoteHeadingLine noteHeadingLine = null;
	private int noteLine_id = 0;
	private X_CA_SchoolYearConfig yearConfig = null;
	private X_CA_NoteRule noteRule = null;
	private X_CA_DisciplineConfig discConfig = null;
	private X_CA_MatterAssignment assignment = null;
	
	private boolean isforced = false;
	private boolean isfinal=false;
	private boolean isdiscipline=false;
	private boolean isaverange=false;
	private String criteria = "";
	


	private Decimalbox decimalBox = new Decimalbox();
	private BigDecimal oldValue = new BigDecimal("0");

	private AcademicNote academicNote;
	private DisciplineNotes disciplineNotes;


	public WNoteEditor()
	{
		super();
		decimalBox.addEventListener(Events.ON_BLUR, new EventListener(){
			@Override
			public void onEvent(Event event){
					saveEx();
			}
		});
		init();
	}



	private void init()
	{
		Table grid = new Table();
		appendChild(grid);
		grid.setStyle("border: none; padding: 0px; margin: 0px;");
		grid.setDynamicProperty("border", "0");
		grid.setDynamicProperty("cellpadding", "0");
		grid.setDynamicProperty("cellspacing", "0");

		Tr tr = new Tr();
		grid.appendChild(tr);
		tr.setStyle("border: none; padding: 0px; margin: 0px; white-space:nowrap; ");

		Td td = new Td();
		tr.appendChild(td);
		td.setStyle("border: none; padding: 0px; margin: 0px;");
		decimalBox.setScale(Decimalbox.AUTO);

		decimalBox.setStyle("display: inline;");
		td.appendChild(decimalBox);


		String style = AEnv.isFirefox2() ? "display: inline" : "display: inline-block"; 
		style = style + ";white-space:nowrap";
		this.setStyle(style);	     
	}

	public BigDecimal getValue()
	{
		return decimalBox.getValue();
	}

	public void setValue(BigDecimal value)
	{
		this.decimalBox.setValue(value);
	}

	@Override
	public void setNote(X_CA_Note note) {
		this.note = note;
	}

	@Override
	public void setNoteLine(X_CA_NoteLine noteLine) {
		if(noteLine!=null)
		{
			this.noteLine_id = noteLine.get_ID();
			decimalBox.setValue(noteLine.getAmount());
			
		}
		else
		{
			this.noteLine_id=0;
		}
	}

	@Override
	public void setStudent(MBPartner student) {
		this.student = student;
	}

	@Override
	public X_CA_Note getNote() {
		return note;
	}

	@Override
	public X_CA_NoteLine getNoteLine() {
		
		if(noteLine_id>0)
		{
			noteline = new X_CA_NoteLine(Env.getCtx(), noteLine_id, null);
		}
		
		return noteline;
	}

	@Override
	public MBPartner getStudent() {
		return student;
	}

	@Override
	public void setNoteHeading(X_CA_NoteHeadingLine noteHeading) {
		this.noteHeadingLine = noteHeading;
	}

	@Override
	public X_CA_NoteHeadingLine getNoteHeading() {
		return noteHeadingLine;
	}

	@Override
	public void setAcademicNoteInstance(AcademicNote academicNote) {
		this.academicNote = academicNote;
	}

	@Override
	public AcademicNote getAcademicNote() {
		return academicNote;
	}

	@Override
	public void setSchoolYearConfig(X_CA_SchoolYearConfig yearConfig) {
		this.yearConfig = yearConfig;
		decimalBox.setFormat(yearConfig.getFormatPattern());
	}

	@Override
	public void setNoteRule(X_CA_NoteRule noteRule) {
		this.noteRule = noteRule;
	}

	@Override
	public X_CA_SchoolYearConfig getSchoolYearConfig() {
		return yearConfig;
	}

	@Override
	public X_CA_NoteRule getNoteRule() {
		return noteRule;
	}

	@Override
	public void setIsFinal(boolean isfinal) {
		this.isfinal = isfinal;
		decimalBox.setReadonly(isfinal);
	}
	
	@Override
	public void forceEditable()
	{
		isforced = true;
		decimalBox.setReadonly(false);
	}
	
	public boolean isforced()
	{
		return isforced;
	}

	@Override
	public boolean isFinal() {
		return isfinal;
	}

	


	@Override
	public void setFValue(BigDecimal value) {
		decimalBox.setValue(value);
	}



	@Override
	public int getNoteLine_ID() {
		return noteLine_id;
	}



	@Override
	public void setIsDiscipline(boolean isdiscipline) {
		this.isdiscipline = isdiscipline;
	}



	@Override
	public boolean isDiscipline() {
		return this.isdiscipline;
	}


	@Override
	public void setDisciplineNoteInstance(DisciplineNotes disciplineNotes) {
		this.disciplineNotes = disciplineNotes;
	}

	@Override
	public DisciplineNotes getDisciplineNoteInstance() {
		return this.disciplineNotes;
	}
	
	@Override
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	@Override
	public String getCriteria() {
		return this.criteria;
	}
	
	@Override
	public void setDisciplineConfig(X_CA_DisciplineConfig disciplineConfig) {
		this.discConfig  = disciplineConfig;
	}

	@Override
	public X_CA_DisciplineConfig getDisciplineConfig() {
		return this.discConfig;
	}

	@Override
	public void saveEx() {
		try{
			Trx.run(new TrxRunnable() 
			{
				public void run(String trxName)
				{
					if(!isfinal && !isdiscipline)
					{
						saveAcademicNote(trxName);
					}
					else if((isdiscipline && !isfinal) || (isdiscipline && isfinal && isaverange) )
					{
						saveDisciplineNote(trxName);
					}
				}
			});
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(!isfinal && !isdiscipline)
			{
				academicNote.refreshFinalNote(student);
			}
			else if(isdiscipline)
			{
				disciplineNotes.refreshDisciplineNote(student);
			}
		}
	}


	private void saveAcademicNote(String trxName)
	{
		if(decimalBox.getValue().compareTo(new BigDecimal(yearConfig.getNoteScale()))>0)
		{
			decimalBox.setValue(new BigDecimal(yearConfig.getNoteScale()));
		}
			
		
		if(noteLine_id==0)
		{
			noteline = new X_CA_NoteLine(Env.getCtx(), 0, trxName);
			noteline.setCA_Note_ID(note.get_ID());
			noteline.setC_BPartner_ID(student.get_ID());
			noteline.set_CustomColumn("CA_NoteHeadingLine_ID", noteHeadingLine.get_ID());
		}
		else
		{
			noteline = new X_CA_NoteLine(Env.getCtx(), noteLine_id, trxName);
		}
		noteline.setAmount(decimalBox.getValue());
		oldValue = decimalBox.getValue();
		noteline.setIsFinal(false);
		noteline.saveEx();
		
		noteLine_id = noteline.get_ID();
	}

	private void saveDisciplineNote(String trxName)
	{
		if(decimalBox.getValue()==null)
		{
			decimalBox.setValue(new BigDecimal("0"));
		}
		
		if(decimalBox.getValue().compareTo(new BigDecimal(yearConfig.getNoteScale()))>0)
		{
			decimalBox.setValue(new BigDecimal(yearConfig.getNoteScale()));
		}
		
		if(noteLine_id==0)
		{
			noteline = new X_CA_NoteLine(Env.getCtx(), 0, trxName);
			noteline.setCA_Note_ID(note.get_ID());
			noteline.setC_BPartner_ID(student.get_ID());
			noteline.setIsDiscipline(true);
			noteline.setIsFinal(isfinal);
			noteline.setIsAverage(isaverange);
			noteline.setDcCriteria(criteria);
		}
		else
		{
			noteline = new X_CA_NoteLine(Env.getCtx(), noteLine_id, trxName);
		}
		noteline.setAmount(decimalBox.getValue());
		oldValue = decimalBox.getValue();
		noteline.saveEx();
		
		noteLine_id = noteline.get_ID();
		
	}



	@Override
	public void setIsAverange(boolean averange) {
		this.isaverange = averange;
	}

	@Override
	public boolean isAverange() {
		return this.isaverange;
	}



	@Override
	public boolean haveChanged() {

		if(decimalBox.getValue()==null)
			decimalBox.setValue(new BigDecimal("0"));
		if(oldValue==null)
			return true;
		if(oldValue.compareTo(decimalBox.getValue())!=0)
			return true;
		
		return false;
	}



	@Override
	public void setMatterAssignment(X_CA_MatterAssignment assignment) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public X_CA_MatterAssignment getMatterAssignment() {
		// TODO Auto-generated method stub
		return null;
	}

}
