package org.eevolution.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Window;
import org.compiere.minigrid.IMiniTable;
import org.compiere.model.MBPartner;
import org.compiere.model.MSysConfig;
import org.compiere.model.MUser;
import org.compiere.util.Env;
import org.fcaq.model.X_CA_Note;
import org.fcaq.model.X_CA_NoteHeadingLine;
import org.fcaq.model.X_CA_NoteLine;
import org.fcaq.util.AcademicUtil;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Html;


public class WGradeViewer extends Window{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	X_CA_Note note = null;
	boolean isdiscipline = false;
	List<MBPartner> students = null;
	List<X_CA_NoteHeadingLine> headingLines = null;
	MUser user = null;
	
	public WGradeViewer (List<String> description, IMiniTable table)
	{
		super();

		Html html = new Html();
		
		String htmlString = buildHTMLString(description, table);
		html.setContent(htmlString);
		
		saveForPrinting(htmlString);
		
		
		Borderlayout mainCLayout = new Borderlayout();
		Panel parameterCPanel = new Panel();
		mainCLayout.setWidth("99%");
		mainCLayout.setHeight("99%");
		
		Center centerC = new Center();
		mainCLayout.appendChild(centerC);
		
		centerC.appendChild(html);
		html.setWidth("99%");
		html.setHeight("99%");
		centerC.setStyle("border: none");
		
		appendChild(mainCLayout);
	}
	
	private String buildHTMLString(List<String> description, IMiniTable table) {

		
		user = new MUser(Env.getCtx(), Env.getContextAsInt(Env.getCtx(), "#AD_User_ID"), null);
		
		String ip = MSysConfig.getValue("FCAQ_MainIP");
		
		String html = "<a href=" + ip + "/reports/tmpgrade/" + user.getName()+ ".html target=_blank>Print</a><font FACE='arial' SIZE=1 >";
		html += "<p>" + user.getName() + "<br>";
		
		for(String item : description)
		{
			html+= item + "<br>";
		}
		html += "<br>";
				
		html+= "<table border=1>\n";
		
		for(int row=0;row<=table.getRowCount()-1;row++)

		{
			html +="<tr>";
			for(int col=0;col<=table.getColumnCount()-1; col++)

			{
				Object value = table.getValueAt(row, col);
				if(value!=null)
					html += "<td>" + value.toString() + "</td>";
				else
					html += "<td></td>";
			}
			html +="</tr>";
		}
		
		html += "</table></font>\n";
		
		return html;
		
	}

	public WGradeViewer (X_CA_Note note, List<MBPartner>students , List<X_CA_NoteHeadingLine> headingLines, boolean isdiscipline )
	{
		super();
		
		this.note = note;
		this.isdiscipline = isdiscipline;
		this.students = students;
		this.headingLines = headingLines;
		
		
		Html html = new Html();
		
		String htmlString = buildHTMLString();
		html.setContent(htmlString);
		
		saveForPrinting(htmlString);
		
		
		Borderlayout mainCLayout = new Borderlayout();
		Panel parameterCPanel = new Panel();
		mainCLayout.setWidth("99%");
		mainCLayout.setHeight("99%");
		
		Center centerC = new Center();
		mainCLayout.appendChild(centerC);
		
		centerC.appendChild(html);
		html.setWidth("99%");
		html.setHeight("99%");
		centerC.setStyle("border: none");
		
		appendChild(mainCLayout);
		
	}
	
	private String buildHTMLString()
	{
		
		user = new MUser(Env.getCtx(), Env.getContextAsInt(Env.getCtx(), "#AD_User_ID"), null);
		
		String ip = MSysConfig.getValue("FCAQ_MainIP");
		
		String html = "<a href=" + ip + "/reports/tmpgrade/" + user.getName()+ ".html target=_blank>Print</a><font FACE='arial' SIZE=1 >";
				
		html+= "<table border=1>\n";
	
		html += "<tr>\n" +
					"<td>Nombre</td>\n";
		
		if(!isdiscipline)
		{
			for(int x=0; x<=headingLines.size()-1; x++)
			{
				html += "<td>" + headingLines.get(x).getName() + "</td>\n" ;
			}	
		}
		else
		{
			    html += "<td>Criterion A</td>\n" ;
			    html += "<td>Criterion B</td>\n" ;
			    html += "<td>Criterion C</td>\n" ;
			    html += "<td>Disciplinary Average</td>\n" ;
		}
		html += "<td>Promedio</td>\n" ;
		html += "</tr>\n";
		
		for(MBPartner student : students)
		{
			
			String name = (student.get_ValueAsString("LastName1")!=null?student.get_ValueAsString("LastName1"):"") + " " +
					(student.get_ValueAsString("LastName2")!=null?student.get_ValueAsString("LastName2"):"") + " " +
					(student.get_ValueAsString("FirstName1")!=null?student.get_ValueAsString("FirstName1"):"") + " " +
					(student.get_ValueAsString("FirstName2")!=null?student.get_ValueAsString("FirstName2"):"") ;
			
			html += "<tr>\n";
			html += "<td>" + name + "</td>\n";
			
			List<X_CA_NoteLine> noteDetail = AcademicUtil.getNoteLinesByStudent(student, note, isdiscipline);

			System.out.println(student.get_ID() + ",");
			
			if(!isdiscipline)
			{
				for(X_CA_NoteHeadingLine headingLine : headingLines)
				{
					boolean enc = false;
					for(X_CA_NoteLine line : noteDetail)
					{
						
						if(headingLine.getCA_NoteHeadingLine_ID() == line.getCA_NoteHeadingLine_ID())
						{
							enc=true;
							html += "<td>" + line.getAmount().toString() + "</td>";
						}
					}
					if(!enc)
					{
						html += "<td></td>";
					}
				}
				
				for(X_CA_NoteLine line : noteDetail)
				{
					if(line.isFinal())
					{
						html += "<td>" + line.getAmount().toString() + "</td>";
					}
				}
				
				
			}
			
			else
			{
				List<String> criterias = new ArrayList<String>();
				criterias.add("A");
				criterias.add("B");
				criterias.add("C");
				
				for (String criteria : criterias)
				{
					for(X_CA_NoteLine line : noteDetail)
					{
						if(criteria.equals(line.getDcCriteria()))
						{
							html += "<td>" + line.getQty().toString() + "</td>";	
						}
					}
				}
				
				for(X_CA_NoteLine line : noteDetail)
				{
					if(line.isFinal() && line.isAverage())
					{
						html += "<td>" + line.getAmount().toString() + "</td>";
					}
				}
				
				for(X_CA_NoteLine line : noteDetail)
				{
					if(line.isFinal() && !line.isAverage())
					{
						html += "<td>" + line.getAmount().toString() + "</td>";
					}
				}

			}
			
			
			html += "</tr>\n";
			
		}
		
		html +="</tr>\n";
		html += "</table></font>\n";
		
		return html;
	}
	

	private void saveForPrinting(String html)
	{
		html = "<html><header></header><body onload='window.print()'>" + html + "</body></html>";
		
		try {
			File pFile = new File("/u01/app/Adempiere/jboss/server/adempiere/deploy/reports.war/tmpgrade/" + user.getName() + ".html");
			FileOutputStream pBytes = new FileOutputStream(pFile);
			pBytes.write(html.getBytes());
			pBytes.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
