package org.eevolution.form;

import java.sql.Timestamp;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
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
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Separator;

public class WCandidateAssignment extends CandidateAssignment 
	implements IFormController, EventListener, WTableModelListener, ValueChangeListener
{
	
	// Layout components
	private CustomForm form = new CustomForm();
	private Borderlayout mainLayout = new Borderlayout();
	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();
	private Panel southPanel = new Panel();
	private StatusBarPanel statusBar = new StatusBarPanel();
	private WListbox candidateTable = ListboxFactory.newDataTable();
	private Button bAssign = new Button();
	private Button bFindCandidates = new Button();
	
	// Form Components
	
	private Label lEvaluator = null;
	private Label lDate = null;
	private Label lGroup = null;
	
	private WSearchEditor fEvaluator = null;
	private WDateEditor fDate = null;
	private Combobox fGroup = null;


	public WCandidateAssignment()
	{
		try
		{
			dynInit();
			zkInit();
			loadCandidateTable();
			southPanel.appendChild(new Separator());
			southPanel.appendChild(statusBar);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "", e);
		}
	}
	


	private void loadCandidateTable() {
		Vector<Vector<Object>> data = getCandidateData(fEvaluator.getValue(), fDate.getValue(), fGroup.getValue());
		Vector<String> columnNames = getColumnNames();
		
		candidateTable.clear();
		candidateTable.getModel().removeTableModelListener(this);
		
		ListModelTable modelP = new ListModelTable(data);
		modelP.addTableModelListener(this);
		candidateTable.setData(modelP, columnNames);
		
		candidateTable.setColumnClass(0, String.class, true);         //  0-Name
		candidateTable.setColumnClass(1, Timestamp.class, true);        //  1-Birthday
		candidateTable.setColumnClass(2, String.class, true);          // Group
				
	}


	// Init Components
	private void zkInit() {
		
		
		form.appendChild(mainLayout);
		mainLayout.setWidth("99%");
		mainLayout.setHeight("100%");
		
		parameterPanel.appendChild(parameterLayout);

		
		
		lEvaluator = new Label();
		lEvaluator.setText(Msg.getMsg(Env.getCtx(), "Evaluator"));

		lDate = new Label();
		lDate.setText(Msg.getMsg(Env.getCtx(), "EvaluationDate"));
		fDate = new WDateEditor();
		
		lGroup = new Label();
		lGroup.setText(Msg.getMsg(Env.getCtx(), "AcademicGroup"));
		
		bFindCandidates.setLabel(Msg.getMsg(Env.getCtx(), "FindCandidates"));
		
		bAssign.setLabel(Msg.getMsg(Env.getCtx(), "Assign"));
		
		
		
		North north = new North();
		north.setStyle("border: none");
		mainLayout.appendChild(north);
		north.appendChild(parameterPanel);
		Rows rows = null;
		Row row = null;
		parameterLayout.setWidth("800px");
		rows = parameterLayout.newRows();
		row = rows.newRow();
		
		row.appendChild(lEvaluator);
		row.appendChild(fEvaluator.getComponent());
		row.appendChild(lDate);
		row.appendChild(fDate.getComponent());
		row = rows.newRow();
		row.appendChild(lGroup);
		row.appendChild(fGroup);
		row = rows.newRow();
		row.appendChild(bFindCandidates);
		
		Center center = new Center();
		center.setFlex(true);
		mainLayout.appendChild(center);
		
		center.appendChild(candidateTable);
		candidateTable.setWidth("99%");
		candidateTable.setHeight("99%");
		center.setStyle("border: none");
		
		
		South south = new South();
		south.setStyle("border: none");
		Panel southPanel = new Panel();
		Grid southLayout= GridFactory.newGridLayout();
		southPanel.appendChild(southLayout);
		south.appendChild(southPanel);
		rows = new Rows();
		southLayout.appendChild(rows);
		row = rows.newRow();
		row.appendChild(bAssign);
		mainLayout.appendChild(south);
		
	}

	// Init Search Editors
	private void dynInit() {
		
			int AD_Column_ID = 2893;        //  C_BPartner.C_BPartner_ID
			MLookup lookupBP = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, AD_Column_ID, DisplayType.Search);
			fEvaluator = new WSearchEditor("C_BPartner_ID", true, false, true, lookupBP);
			fEvaluator.addValueChangeListener(this);
			
			fGroup = new Combobox();
			fGroup.appendItem("I", "1");
			fGroup.appendItem("II", "2");
			fGroup.appendItem("II", "3");
			fGroup.appendItem("IV", "4");

	}




	@Override
	public void valueChange(ValueChangeEvent evt) {

	}

	@Override
	public void tableChanged(WTableModelEvent event) {

	}

	@Override
	public void onEvent(Event event) throws Exception {

	}

	@Override
	public ADForm getForm() {
		return form;
	}

}
