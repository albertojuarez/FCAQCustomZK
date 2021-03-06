package org.fcaq.components;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.MBPartner;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Trx;
import org.compiere.util.TrxRunnable;
import org.eevolution.form.AcademicNote;
import org.eevolution.form.DisciplineNotes;
import org.fcaq.model.X_CA_CourseDef;
import org.fcaq.model.X_CA_DiscCalc;
import org.fcaq.model.X_CA_DisciplineConfig;
import org.fcaq.model.X_CA_MatterAssignment;
import org.fcaq.model.X_CA_Note;
import org.fcaq.model.X_CA_NoteHeadingLine;
import org.fcaq.model.MCANoteLine;
import org.fcaq.model.X_CA_NoteLine;
import org.fcaq.model.X_CA_NoteRule;
import org.fcaq.model.X_CA_Parcial;
import org.fcaq.model.X_CA_SchoolYearConfig;
import org.fcaq.util.AcademicUtil;
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


	private MBPartner student = null;
	private X_CA_NoteHeadingLine noteHeadingLine = null;

	private X_CA_SchoolYearConfig yearConfig = null;
	private X_CA_NoteRule noteRule = null;
	private X_CA_DisciplineConfig discConfig = null;

	private boolean isforced = false;
	private boolean isfinal=false;
	private boolean isdiscipline=false;
	private boolean isaverange=false;
	private String dccriteria = "";
	private String sportCriteria="";

	private int noteLine_id = 0;
	private int note_id=0;


	private Decimalbox decimalBox = new Decimalbox();
	private BigDecimal oldValue = new BigDecimal("0");

	private AcademicNote academicNote;
	private DisciplineNotes disciplineNotes;
	private String DTString = "";


	public WNoteEditor()
	{
		super();
		decimalBox.addEventListener(Events.ON_BLUR, new EventListener(){
			@Override
			public void onEvent(Event event){
				if(decimalBox.getValue()!=null)
				{
					MCANoteLine noteline = new MCANoteLine(Env.getCtx(), noteLine_id, null);
					if("O".equals(noteline.getDocStatus()))
						decimalBox.setValue(AcademicUtil.applyRound(noteline.getAmount(), decimalBox.getValue(), noteline.getDocStatus()));
					saveEx();
				}	
				else
				{
					try
					{



						MCANoteLine noteline = new MCANoteLine(Env.getCtx(), noteLine_id, null);
						AcademicUtil.setNeedRecalculated((X_CA_Parcial)noteline.getCA_Note().getCA_Parcial(), (MBPartner)noteline.getC_BPartner());
						noteline.deleteEx(true);
						noteline=null;
						noteLine_id=0;

						if(!isfinal && !isdiscipline)
						{
							academicNote.refreshFinalNote(student);
						}
						else if((isdiscipline && !isfinal) || (isdiscipline && isfinal && isaverange) )
						{
							if(discConfig.isAverageCriteria() || (!discConfig.isAverageCriteria()  && isaverange ))
							{
								disciplineNotes.refreshDisciplineNote(student);
							}
						}

					}
					catch(Exception e)
					{
						//System.out.println("Nothing to do, just ignore");
					}
				}
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
		this.note_id = note.get_ID();

		try{
			
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

			//Date parcialEnd  = note.getCA_Parcial().getDateTo();
			Date parcialEnd  = note.getCA_Parcial().getDateStart(); // Inicio de Fecha de Juntas

			String dateString = df.format(parcialEnd);
			
			dateString  = dateString +  " 23:59:59"; 
		     
		    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
		    parcialEnd = dateFormat.parse(dateString); 
			
			
			long currentTime = System.currentTimeMillis();

			if(!(note.getCA_Parcial().getDateFrom().getTime()<= currentTime &&
					parcialEnd.getTime()>=(currentTime)))
			{
				decimalBox.setReadonly(true);
			}}
		catch(Exception e)
		{
			//Nothing to do, just ignore
		}

	}

	@Override
	public void setNoteLine(X_CA_NoteLine noteLine) {
		if(noteLine!=null)
		{
			this.noteLine_id = noteLine.get_ID();
			decimalBox.setReadonly(!"O".equals((noteLine.getDocStatus())));

			if(isdiscipline)
			{
				if(noteLine.getDcCriteria()!=null )
				{
					if(noteLine.getDcCriteria().length()>0)
					{
						if(discConfig.isAverageCriteria())
						{
							if(noteLine.get_Value("Qty")==null)
								decimalBox.setValue(new BigDecimal("0"));
							else
								decimalBox.setValue((BigDecimal)noteLine.get_Value("Qty"));
						}		
						else
						{
							decimalBox.setValue(noteLine.getAmount());
						}

					}
					else
					{
						decimalBox.setValue(noteLine.getAmount());
					}
				}
				else
				{
					decimalBox.setValue(noteLine.getAmount());
				}
			}
			else
			{
				decimalBox.setValue(noteLine.getAmount());
			}

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
		return new X_CA_Note(Env.getCtx(), note_id, null);
	}

	@Override
	public MCANoteLine getNoteLine() {

		if(noteLine_id>0)
		{
			return new MCANoteLine(Env.getCtx(), noteLine_id, null);
		}

		return null;
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
		//decimalBox.setFormat(yearConfig.getFormatPattern());
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
		this.dccriteria = criteria;
	}

	@Override
	public String getCriteria() {
		return this.dccriteria;
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
			else if((isdiscipline && !isfinal) || (isdiscipline && isfinal && isaverange) )
			{
				if(discConfig.isAverageCriteria() || (!discConfig.isAverageCriteria()  && isaverange ))
				{
					disciplineNotes.refreshDisciplineNote(student);
				}
			}

		}
	}




	private void saveAcademicNote(String trxName)
	{
		if(decimalBox.getValue()==null)
		{
			decimalBox.setValue(new BigDecimal("0"));
		}

		if(decimalBox.getValue().compareTo(new BigDecimal(yearConfig.getNoteScale()))>0 || decimalBox.getValue().compareTo(new BigDecimal("0"))<0)
		{
			decimalBox.setValue(new BigDecimal("0"));
		}

		X_CA_Note note = getNote();

		if(note!=null)
		{
			X_CA_CourseDef course = (X_CA_CourseDef)note.getCA_CourseDef();
			if(course!=null)
			{
				String section = course.getSection();
				if(section!=null)
				{
					if(Integer.parseInt(section)<=3 )
					{
						if(decimalBox.getValue().compareTo(new BigDecimal("5"))>0 || decimalBox.getValue().compareTo(new BigDecimal("0"))<0)
						{
							decimalBox.setValue(new BigDecimal("0"));
						}
					}
				}
			}
		}


		MCANoteLine noteline = null;

		if(noteLine_id==0)
		{
			noteline = new MCANoteLine(Env.getCtx(), 0, trxName);
			noteline.setCA_Note_ID(note.get_ID());
			noteline.setC_BPartner_ID(student.get_ID());
			if(noteHeadingLine!=null)
				noteline.set_CustomColumn("CA_NoteHeadingLine_ID", noteHeadingLine.get_ID());
			noteline.setSportCriteria(sportCriteria);
			noteline.setDtString(DTString);
			noteline.setDocStatus("O");
		}
		else
		{
			noteline = new MCANoteLine(Env.getCtx(), noteLine_id, trxName);
		}

		if(noteline.getDocStatus().equals("O"))
		{
			noteline.setAmount(decimalBox.getValue());
			oldValue = decimalBox.getValue();
			noteline.setIsFinal(false);
			noteline.saveEx();

			setNeedRecalculated();
		}

		noteLine_id = noteline.get_ID();
	}

	private void setNeedRecalculated() {
		
		X_CA_CourseDef course = (X_CA_CourseDef) AcademicUtil.getGroupCourse(student.getCtx(), student, student.get_TrxName()).getCA_CourseDef();
		
		X_CA_Parcial realparcial = AcademicUtil.getRealParcial(course, 
				getNote().getCA_Parcial().getCA_EvaluationPeriod().getSeqNo().toString(),  
				getNote().getCA_Parcial().getSeqNo().toString());

		String whereClause = X_CA_DiscCalc.COLUMNNAME_C_BPartner_ID + "=? AND " + X_CA_DiscCalc.COLUMNNAME_CA_Parcial_ID + "=?";

		X_CA_DiscCalc calc = new Query(student.getCtx(), X_CA_DiscCalc.Table_Name, whereClause, student.get_TrxName())
		.setOnlyActiveRecords(true)
		.setParameters(student.get_ID(), realparcial.getCA_Parcial_ID())
		.first();

		if(calc==null)
		{
			calc = new X_CA_DiscCalc(student.getCtx(), 0, student.get_TrxName());
		}

		calc.setC_BPartner_ID(student.get_ID());
		calc.setCA_Parcial_ID(realparcial.getCA_Parcial_ID());
		calc.setAverange1(new BigDecimal("0"));
		calc.setAverange2(new BigDecimal("0"));
		calc.setIsNeed(true);

		calc.saveEx();
	}



	public void saveDisciplineNote(String trxName)
	{
		if(decimalBox.getValue()==null)
		{
			decimalBox.setValue(new BigDecimal("0"));
		}

		if(decimalBox.getValue().compareTo(new BigDecimal(yearConfig.getNoteScale()))>0 || decimalBox.getValue().compareTo(new BigDecimal("0"))<0)
		{
			decimalBox.setValue(new BigDecimal("0"));
		}

		if(discConfig.isAverageCriteria() 
				&& X_CA_CourseDef.SECTION_Kinder.equals(getNote().getCA_CourseDef().getSection())    
				&& X_CA_CourseDef.MODALITY_Nacional.equals(getNote().getCA_CourseDef().getModality())  
				&& decimalBox.getValue().compareTo(new BigDecimal("5"))>0)
		{

			decimalBox.setValue(new BigDecimal("0"));
		}

		if(!discConfig.isAverageCriteria() && decimalBox.getValue().compareTo(new BigDecimal("4"))>0 && !isfinal)
		{
			decimalBox.setValue(new BigDecimal("0"));
		}


		MCANoteLine noteline = null;


		String whereClause = MCANoteLine.COLUMNNAME_CA_Note_ID + "=? AND " +
				MCANoteLine.COLUMNNAME_C_BPartner_ID + "=? AND " + 
				MCANoteLine.COLUMNNAME_IsDiscipline + "=? AND " + 
				MCANoteLine.COLUMNNAME_IsFinal + "=? AND " + 
				MCANoteLine.COLUMNNAME_IsAverage + "=? ";

		List<Object> parameters = new ArrayList<Object>();

		parameters.add(getNote().get_ID());
		parameters.add(student.get_ID());
		parameters.add(true);
		parameters.add(isfinal);
		parameters.add(isaverange);



		if(!isfinal)
		{
			whereClause += " AND " + MCANoteLine.COLUMNNAME_DcCriteria + " =? ";
			parameters.add(dccriteria);
		}

		noteline = new Query(Env.getCtx(), MCANoteLine.Table_Name, whereClause, trxName)
		.setOnlyActiveRecords(true)
		.setParameters(parameters)
		.first();

		if(noteline==null)
			//if(noteLine_id==0)
		{
			noteline = new MCANoteLine(Env.getCtx(), 0, trxName);
			noteline.setCA_Note_ID(getNote().get_ID());
			noteline.setC_BPartner_ID(student.get_ID());
			noteline.setIsDiscipline(true);
			noteline.setIsFinal(isfinal);
			noteline.setIsAverage(isaverange);
			noteline.setDcCriteria(dccriteria);
			noteline.setDocStatus("O");
			//}
			//else
			//{
			//	noteline = new X_CA_NoteLine(Env.getCtx(), noteLine_id, trxName);
		}
		
		BigDecimal rValue = AcademicUtil.applyRound(noteline.getAmount(), decimalBox.getValue()!=null?decimalBox.getValue():BigDecimal.ZERO	, noteline.getDocStatus());

		
		
		if(noteline.getDocStatus().equals("O"))
		{
			decimalBox.setValue(rValue);

			if(!discConfig.isAverageCriteria() || isaverange)
			{
				noteline.setAmount(decimalBox.getValue());
				noteline.set_ValueOfColumn("Qty", decimalBox.getValue());
			}
			else
			{
				double tmp = decimalBox.getValue().doubleValue();
				if(tmp>=0 && tmp<=60)
				{
					noteline.setAmount(new BigDecimal("1"));
				}
				else if(tmp>60 && tmp<=70)
				{
					noteline.setAmount(new BigDecimal("2"));
				}
				else if(tmp>70 && tmp<=90)
				{
					noteline.setAmount(new BigDecimal("3"));
				}
				else if(tmp>90 && tmp<=100)
				{
					noteline.setAmount(new BigDecimal("4"));
				}

				noteline.set_ValueOfColumn("Qty", decimalBox.getValue());
			}


			oldValue = decimalBox.getValue();


			noteline.saveEx();
			setNeedRecalculated();
		}
		else
		{
			//decimalBox.setValue(noteline.getAmount());
			oldValue = decimalBox.getValue();
		}

		disciplineNotes.copyToAlternateNotes(student, getNote(), noteline, trxName);

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

	}



	@Override
	public X_CA_MatterAssignment getMatterAssignment() {
		return null;
	}



	@Override
	public void setSportCriteria(String sportCriteria) {
		this.sportCriteria=sportCriteria;
	}



	@Override
	public String getSportCriteria() {
		return sportCriteria;
	}



	@Override
	public void setDTString(String dtstring) {
		this.DTString = dtstring;
	}



	@Override
	public String getDTString() {
		return DTString;
	}





}
