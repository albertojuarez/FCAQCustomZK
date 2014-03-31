/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/

/**
 * 2007, Modified by Posterita Ltd.
 */

package org.adempiere.webui.window;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListItem;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.Window;
import org.compiere.model.MCountry;
import org.compiere.model.MLocation;
import org.compiere.model.MRefList;
import org.compiere.model.MRegion;
import org.compiere.model.MSysConfig;
import org.compiere.util.CLogger;
import org.compiere.util.DefaultContextProvider;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.ValueNamePair;
import org.fcaq.model.MCACanton;
import org.fcaq.model.MCAParish;
import org.fcaq.model.MCAProvince;
import org.fcaq.model.MCASector;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hbox;

/**
 * @author Sendy Yagambrum
 * @date July 16, 2007
 * Location Dialog Box
 * This class is based upon VLocationDialog, written by Jorg Janke
 * @author Cristina Ghita, www.arhipac.ro
 * 			<li>FR [ 2794312 ] Location AutoComplete
 * @author Teo Sarca, teo.sarca@gmail.com
 * 			<li>BF [ 2995212 ] NPE on Location dialog
 * 				https://sourceforge.net/tracker/?func=detail&aid=2995212&group_id=176962&atid=955896
 * @author victor.perez@e-evolution.com, www.e-evolution.com
 * 			<li>BF [ 3294610] The location should allow open a google map
 * 				https://sourceforge.net/tracker/?func=detail&atid=879335&aid=3294610&group_id=176962
 * 
 * @TODO: Implement fOnline button present in swing client
 * 
 **/
public class WLocationDialog extends Window implements EventListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6892969005447776082L;
	private static final String LABEL_STYLE = "white-space: nowrap;";
	/** Logger          */
	private static CLogger log = CLogger.getCLogger(WLocationDialog.class);
	private Label lblAddress1;
	private Label lblAddress2;
	private Label lblAddress3;
	private Label lblAddress4;
	private Label lblCity;
	private Label lblZip;
	private Label lblRegion;
	private Label lblPostal;
	private Label lblPostalAdd;
	private Label lblCountry;
	//Begin Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
	private Label lblSector;
	private Label lblProvince;
	private Label lblCanton;
	private Label lblParish;
	private Label lblAddressType;
	//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
	
	private Textbox txtAddress1;
	private Textbox txtAddress2;
	private Textbox txtAddress3;
	private Textbox txtAddress4;
	private WAutoCompleterCity txtCity;
	private Textbox txtPostal;
	private Textbox txtPostalAdd;
	private Listbox lstRegion;
	private Listbox lstCountry;
	//Begin Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
	private Listbox lstSector;
	private Listbox lstProvince;
	private Listbox lstCanton;
	private Listbox lstParish;
	private Listbox	lstAddressType;
	//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
	private Button btnUrl;

	private Button btnOk;
	private Button btnCancel;
	private Grid mainPanel;

	private boolean     m_change = false;
	private MLocation   m_location;
	private int         m_origCountry_ID;
	private int         s_oldCountry_ID = 0;
	//Begin Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
	private int 		s_oldProvince_ID = 0;
	private int 		s_oldCanton_ID = 0;
	//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
	
	private int m_WindowNo = 0;

	private boolean isCityMandatory = false;
	private boolean isRegionMandatory = false;
	private boolean isAddress1Mandatory = false;
	private boolean isAddress2Mandatory = false;
	private boolean isAddress3Mandatory = false;
	private boolean isAddress4Mandatory = false;
	private boolean isPostalMandatory = false;
	private boolean isPostalAddMandatory = false;
	//Begin Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
	private boolean isSectorMandatory = false;
	private boolean isProvinceMandatory = false;
	private boolean isCantonMandatory = false;
	private boolean isParishMandatory = false;
	private boolean isAddressTypeMandatory = false;
	
	private boolean inProvinceAction;
	private boolean inCantonAction;
	//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013

	private boolean inCountryAction;
	private boolean inOKAction;

	public WLocationDialog(String title, MLocation location)
	{
		m_location = location;
		if (m_location == null)
			m_location = new MLocation (Env.getCtx(), 0, null);
		//  Overwrite title 
		if (m_location.getC_Location_ID() == 0)
			setTitle(Msg.getMsg(Env.getCtx(), "LocationNew"));
		else
			setTitle(Msg.getMsg(Env.getCtx(), "LocationUpdate"));    
		//
		// Reset TAB_INFO context
		Env.setContext(Env.getCtx(), m_WindowNo, Env.TAB_INFO, "C_Region_ID", null);
		Env.setContext(Env.getCtx(), m_WindowNo, Env.TAB_INFO, "C_Country_ID", null);
		//
		initComponents();
		init();
		//      Current Country
		for (MCountry country:MCountry.getCountries(Env.getCtx()))
		{
			lstCountry.appendItem(country.toString(), country);
		}
		setCountry();
		lstCountry.addEventListener(Events.ON_SELECT,this);
		lstRegion.addEventListener(Events.ON_SELECT,this);
		m_origCountry_ID = m_location.getC_Country_ID();
		//  Current Region
		lstRegion.appendItem("", null);
		for (MRegion region : MRegion.getRegions(Env.getCtx(), m_origCountry_ID))
		{
			lstRegion.appendItem(region.getName(),region);
		}
		if (m_location.getCountry().isHasRegion()) {
			if (m_location.getCountry().get_Translation(MCountry.COLUMNNAME_RegionName) != null
					&& m_location.getCountry().get_Translation(MCountry.COLUMNNAME_RegionName).trim().length() > 0)
				lblRegion.setValue(m_location.getCountry().get_Translation(MCountry.COLUMNNAME_RegionName));
			else
				lblRegion.setValue(Msg.getMsg(Env.getCtx(), "Region"));
		}

		setRegion();
		
		//Begin Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		for (MCASector sector : MCASector.getSectors(Env.getCtx(), null)) 
		{	
			lstSector.appendItem(sector.getName(), sector);
		}
		
		for (MCAProvince province : MCAProvince.getProvinces(Env.getCtx(), m_origCountry_ID, null)) 
		{
			lstProvince.appendItem(province.getName(), province);	
		}
		for (ValueNamePair valueNamePair : MRefList.getList(Env.getCtx(), MLocation.ADDRESSTYPE_AD_Reference_ID, false))
		{
			lstAddressType.appendItem(valueNamePair.getName(), valueNamePair.getValue());
		}
		
		lstSector.addEventListener(Events.ON_SELECT, this);
		lstProvince.addEventListener(Events.ON_SELECT, this);
		lstCanton.addEventListener(Events.ON_SELECT, this);
		lstParish.addEventListener(Events.ON_SELECT, this);
		lstAddressType.addEventListener(Events.ON_SELECT, this);
		//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		
		initLocation();
		//               
		this.setWidth("390px");
		this.setClosable(true);
		this.setBorder("normal");
		this.setAttribute("mode","modal");
	}

	private void initComponents()
	{
		lblAddress1     = new Label(Msg.getElement(Env.getCtx(), "Address1"));
		lblAddress1.setStyle(LABEL_STYLE);
		lblAddress2     = new Label(Msg.getElement(Env.getCtx(), "Address2"));
		lblAddress2.setStyle(LABEL_STYLE);
		lblAddress3     = new Label(Msg.getElement(Env.getCtx(), "Address3"));
		lblAddress3.setStyle(LABEL_STYLE);
		lblAddress4     = new Label(Msg.getElement(Env.getCtx(), "Address4"));
		lblAddress4.setStyle(LABEL_STYLE);
		lblCity         = new Label(Msg.getMsg(Env.getCtx(), "City"));
		lblCity.setStyle(LABEL_STYLE);
		lblZip          = new Label(Msg.getMsg(Env.getCtx(), "Postal"));
		lblZip.setStyle(LABEL_STYLE);
		lblRegion       = new Label(Msg.getMsg(Env.getCtx(), "Region"));
		lblRegion.setStyle(LABEL_STYLE);
		lblPostal       = new Label(Msg.getMsg(Env.getCtx(), "Postal"));
		lblPostal.setStyle(LABEL_STYLE);
		lblPostalAdd    = new Label(Msg.getMsg(Env.getCtx(), "PostalAdd"));
		lblPostalAdd.setStyle(LABEL_STYLE);
		lblCountry      = new Label(Msg.getMsg(Env.getCtx(), "Country"));
		lblCountry.setStyle(LABEL_STYLE);
		//Begin Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		lblSector		= new Label(Msg.getElement(Env.getCtx(), MCASector.COLUMNNAME_CA_Sector_ID));
		lblSector.setStyle(LABEL_STYLE);
		lblProvince		= new Label(Msg.getElement(Env.getCtx(), MCAProvince.COLUMNNAME_CA_Province_ID));
		lblProvince.setStyle(LABEL_STYLE);
		lblCanton		= new Label(Msg.getElement(Env.getCtx(), MCACanton.COLUMNNAME_CA_Canton_ID));
		lblCanton.setStyle(LABEL_STYLE);
		lblParish		= new Label(Msg.getElement(Env.getCtx(), MCAParish.COLUMNNAME_CA_Parish_ID));
		lblParish.setStyle(LABEL_STYLE);
		lblAddressType	= new Label(Msg.getElement(Env.getCtx(), MLocation.COLUMNNAME_AddressType));
		lblAddressType.setStyle(LABEL_STYLE);
		//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013

		txtAddress1 = new Textbox();
		txtAddress1.setCols(30);
		txtAddress2 = new Textbox();
		txtAddress2.setCols(30);
		txtAddress3 = new Textbox();
		txtAddress3.setCols(30);
		txtAddress4 = new Textbox();
		txtAddress4.setMultiline(true);
		((HtmlBasedComponent)txtAddress4).setStyle("height:60px");	
		txtAddress4.setCols(30);

		//autocomplete City
		txtCity = new WAutoCompleterCity(m_WindowNo);
		txtCity.setCols(30);
		txtCity.setAutodrop(true);
		txtCity.setAutocomplete(true);
		txtCity.addEventListener(Events.ON_CHANGING, this);
		//txtCity

		txtPostal = new Textbox();
		txtPostal.setCols(30);
		txtPostalAdd = new Textbox();
		txtPostalAdd.setCols(30);

		lstRegion    = new Listbox();
		lstRegion.setMold("select");
		lstRegion.setWidth("204px");
		lstRegion.setRows(0);

		lstCountry  = new Listbox();
		lstCountry.setMold("select");
		lstCountry.setWidth("204px");
		lstCountry.setRows(0);
		
		//Begin Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		lstSector	= new Listbox();
		lstSector.setMold("select");
		lstSector.setWidth("204px");
		lstSector.setRows(0);
		
		lstProvince	= new Listbox();
		lstProvince.setMold("select");
		lstProvince.setWidth("204px");
		lstProvince.setRows(0);
		
		lstCanton	= new Listbox();
		lstCanton.setMold("select");
		lstCanton.setWidth("204px");
		lstCanton.setRows(0);
		
		lstParish	= new Listbox();
		lstParish.setMold("select");
		lstParish.setWidth("204px");
		lstParish.setRows(0);
		
		lstAddressType	= new Listbox();
		lstAddressType.setMold("select");
		lstAddressType.setWidth("204px");
		lstAddressType.setRows(0);
		//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		
		btnUrl =  new Button();
		btnUrl.setImage("/images/Online10.png");
		btnUrl.addEventListener(Events.ON_CLICK,this);
		
		btnOk = new Button();
		btnOk.setImage("/images/Ok16.png");
		btnOk.addEventListener(Events.ON_CLICK,this);
		btnCancel = new Button();
		btnCancel.setImage("/images/Cancel16.png");
		btnCancel.addEventListener(Events.ON_CLICK,this);

		mainPanel = GridFactory.newGridLayout();
		mainPanel.setStyle("padding:5px");
	}

	private void init()
	{
		Row pnlAddress1 = new Row();
		pnlAddress1.appendChild(lblAddress1.rightAlign());
		pnlAddress1.appendChild(txtAddress1);        

		Row pnlAddress2 = new Row();
		pnlAddress2.appendChild(lblAddress2.rightAlign());
		pnlAddress2.appendChild(txtAddress2);

		Row pnlAddress3 = new Row();
		pnlAddress3.appendChild(lblAddress3.rightAlign());
		pnlAddress3.appendChild(txtAddress3);

		Row pnlAddress4 = new Row();
		pnlAddress4.appendChild(lblAddress4.rightAlign());
		pnlAddress4.appendChild(txtAddress4);

		Row pnlCity     = new Row();
		pnlCity.appendChild(lblCity.rightAlign());
		pnlCity.appendChild(txtCity);

		Row pnlPostal   = new Row();
		pnlPostal.appendChild(lblPostal.rightAlign());
		pnlPostal.appendChild(txtPostal);

		Row pnlPostalAdd = new Row();
		pnlPostalAdd.appendChild(lblPostalAdd.rightAlign());
		pnlPostalAdd.appendChild(txtPostalAdd);

		Row pnlRegion    = new Row();
		pnlRegion.appendChild(lblRegion.rightAlign());
		pnlRegion.appendChild(lstRegion);

		Row pnlCountry  = new Row();
		pnlCountry.appendChild(lblCountry.rightAlign());
		pnlCountry.appendChild(lstCountry);

		//Begin Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		Row pnlSector  = new Row();
		pnlSector.appendChild(lblSector.rightAlign());
		pnlSector.appendChild(lstSector);

		Row pnlProvince = new Row();
		pnlProvince.appendChild(lblProvince.rightAlign());
		pnlProvince.appendChild(lstProvince);

		Row pnlCanton    = new Row();
		pnlCanton.appendChild(lblCanton.rightAlign());
		pnlCanton.appendChild(lstCanton);

		Row pnlParish  = new Row();
		pnlParish.appendChild(lblParish.rightAlign());
		pnlParish.appendChild(lstParish);
		
		org.zkoss.zul.Row pnlLocationType = new Row();
		pnlLocationType.appendChild(lblAddressType.rightAlign());
		pnlLocationType.appendChild(lstAddressType);
		//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		
		Panel pnlButtonLeft = new Panel();
	    pnlButtonLeft.appendChild(btnUrl);
	    pnlButtonLeft.setAlign("left");
	        
		Panel pnlButtonRight   = new Panel();
		pnlButtonRight.appendChild(btnOk);
		pnlButtonRight.appendChild(btnCancel);
		pnlButtonRight.setWidth("100%");
		pnlButtonRight.setStyle("text-align:right");

		Hbox hboxButton = new Hbox();
	    hboxButton.appendChild(pnlButtonLeft);
	    hboxButton.appendChild(pnlButtonRight);
	    hboxButton.setWidth("100%");
	        
		this.appendChild(mainPanel);
		this.appendChild(hboxButton);

	}
	/**
	 * Dynamically add fields to the Location dialog box
	 * @param panel panel to add
	 *
	 */
	private void addComponents(Row row)
	{
		if (mainPanel.getRows() != null)
			mainPanel.getRows().appendChild(row);
		else
			mainPanel.newRows().appendChild(row);
	}

	private void initLocation()
	{
		if (mainPanel.getRows() != null)
			mainPanel.getRows().getChildren().clear();

		MCountry country = m_location.getCountry();
		log.fine(country.getName() + ", Region=" + country.isHasRegion() + " " + country.getCaptureSequence()
				+ ", C_Location_ID=" + m_location.getC_Location_ID());
		//  new Country
		if (m_location.getC_Country_ID() != s_oldCountry_ID)
		{
			lstRegion.getChildren().clear();
			if (country.isHasRegion()) {
				lstRegion.appendItem("", null);
				for (MRegion region : MRegion.getRegions(Env.getCtx(), country.getC_Country_ID()))
				{
					lstRegion.appendItem(region.getName(),region);
				}
				if (m_location.getCountry().get_Translation(MCountry.COLUMNNAME_RegionName) != null
						&& m_location.getCountry().get_Translation(MCountry.COLUMNNAME_RegionName).trim().length() > 0)
					lblRegion.setValue(m_location.getCountry().get_Translation(MCountry.COLUMNNAME_RegionName));
				else
					lblRegion.setValue(Msg.getMsg(Env.getCtx(), "Region"));
			}
			
			//Begin Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
			lstProvince.getChildren().clear();
			lstProvince.appendItem("", null);
			for (MCAProvince province : MCAProvince.getProvinces(Env.getCtx(), m_location.getC_Country_ID(), null))
			{
				lstProvince.appendItem(province.getName(), province);
			}
			
			Iterator<?> iter = lstProvince.getChildren().iterator();
			String provinceDefault = MSysConfig.getValue("FCAQProvinceDefault");
			
			while (iter.hasNext())
			{
				ListItem listitem = (ListItem)iter.next();
				
				if (listitem.getValue() != null 
						&& provinceDefault.equals(((MCAProvince) listitem.getValue()).getValue())) {
					
					lstProvince.setSelectedItem(listitem);
					m_location.setCA_Province_ID(((MCAProvince) listitem.getValue()).get_ID());
					
					break;
				}
			}
			//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
			
			s_oldCountry_ID = m_location.getC_Country_ID();
		}
		
		if (m_location.getC_Region_ID() > 0 && m_location.getC_Region().getC_Country_ID() == country.getC_Country_ID()) {
			setRegion();
		} else {
			lstRegion.setSelectedItem(null);
			m_location.setC_Region_ID(0);
		}

		if (country.isHasRegion() && m_location.getC_Region_ID() > 0)
		{
			Env.setContext(Env.getCtx(), m_WindowNo, Env.TAB_INFO, "C_Region_ID", String.valueOf(m_location.getC_Region_ID()));
		} else {
			Env.setContext(Env.getCtx(), m_WindowNo, Env.TAB_INFO, "C_Region_ID", "0");
		}
		Env.setContext(Env.getCtx(), m_WindowNo, Env.TAB_INFO, "C_Country_ID", String.valueOf(country.get_ID()));
		
		txtCity.fillList();
		
		//Begin Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		if (m_location.getCA_Province_ID() != s_oldProvince_ID)
		{
			lstCanton.getChildren().clear();
			lstCanton.appendItem("", null);
			for (MCACanton canton : MCACanton.getCanton(Env.getCtx(), m_location.getCA_Province_ID(), null))
			{
				lstCanton.appendItem(canton.getName(), canton);
			}
			s_oldProvince_ID = m_location.getCA_Province_ID();
		}
		
		if (m_location.getCA_Canton_ID() != s_oldCanton_ID)
		{
			lstParish.getChildren().clear();
			lstParish.appendItem("", null);
			for (MCAParish parish : MCAParish.getParishes(Env.getCtx(), m_location.getCA_Canton_ID(), null))
			{
				lstParish.appendItem(parish.getName(), parish);
			}
			s_oldCanton_ID = m_location.getCA_Canton_ID();
		}
		
		MCAProvince province = new MCAProvince(Env.getCtx(), m_location.getCA_Province_ID(), null);
		
		if (province.get_ID() > 0 && province.getC_Country_ID() == m_location.getC_Country_ID()) {
			setProvince();
		}
		else {
			lstProvince.setSelectedItem(null);
			m_location.setCA_Province_ID(0);
		}
		
		MCACanton canton = new MCACanton(Env.getCtx(), m_location.getCA_Canton_ID(), null);
		
		if (canton.get_ID() > 0 && canton.getCA_Province_ID() == m_location.getCA_Province_ID()) {
			setCanton();
		}
		else {
			lstCanton.setSelectedItem(null);
			m_location.setCA_Canton_ID(0);
		}
		
		MCAParish parish = new MCAParish(Env.getCtx(), m_location.getCA_Parish_ID(), null);
		
		if (parish.get_ID() > 0 && parish.getCA_Canton_ID() == m_location.getCA_Canton_ID()) {
			setParish();
		}
		else {
			lstParish.setSelectedItem(null);
			m_location.setCA_Parish_ID(0);
		}
		//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		
		//      sequence of City Postal Region - @P@ @C@ - @C@, @R@ @P@
		String ds = country.getCaptureSequence();
		if (ds == null || ds.length() == 0)
		{
			log.log(Level.SEVERE, "CaptureSequence empty - " + country);
			ds = "";    //  @C@,  @P@
		}
		isCityMandatory = false;
		isRegionMandatory = false;
		isAddress1Mandatory = false;
		isAddress2Mandatory = false;
		isAddress3Mandatory = false;
		isAddress4Mandatory = false;
		isPostalMandatory = false;
		isPostalAddMandatory = false;
		//Begin Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		isSectorMandatory = false;
		isProvinceMandatory = false;
		isCantonMandatory = false;
		isParishMandatory = false;
		isAddressTypeMandatory = false;
		//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		
		StringTokenizer st = new StringTokenizer(ds, "@", false);
		while (st.hasMoreTokens())
		{
			String s = st.nextToken();
			if (s.startsWith("CO")) {
				//  Country Last
				addComponents((Row)lstCountry.getParent());
				// TODO: Add Online
				// if (m_location.getCountry().isPostcodeLookup()) {
					// addLine(line++, lOnline, fOnline);
				// }
			} 
			//Begin Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
			else if (s.startsWith("SE")) {
				addComponents((Row)lstSector.getParent());
				isSectorMandatory = s.endsWith("!");
			} else if (s.startsWith("PR")) {
				addComponents((Row)lstProvince.getParent());
				isProvinceMandatory = s.endsWith("!");
			} else if (s.startsWith("CA")) {
				addComponents((Row)lstCanton.getParent());
				isCantonMandatory = s.endsWith("!");
			} else if (s.startsWith("PA")) {
				addComponents((Row)lstParish.getParent());
				isParishMandatory = s.endsWith("!");
			} else if (s.startsWith("AT")) {
				addComponents((Row)lstAddressType.getParent());
				isAddressTypeMandatory = s.endsWith("!");
			}
			//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
			else if (s.startsWith("A1")) {
				addComponents((Row)txtAddress1.getParent());
				isAddress1Mandatory = s.endsWith("!");
			} else if (s.startsWith("A2")) {
				addComponents((Row)txtAddress2.getParent());
				isAddress2Mandatory = s.endsWith("!");
			} else if (s.startsWith("A3")) {
				addComponents((Row)txtAddress3.getParent());
				isAddress3Mandatory = s.endsWith("!");
			} else if (s.startsWith("A4")) {
				addComponents((Row)txtAddress4.getParent());
				isAddress4Mandatory = s.endsWith("!");
			} else if (s.startsWith("C")) {
				addComponents((Row)txtCity.getParent());
				isCityMandatory = s.endsWith("!");
			} else if (s.startsWith("P")) {
				addComponents((Row)txtPostal.getParent());
				isPostalMandatory = s.endsWith("!");
			} else if (s.startsWith("A")) {
				addComponents((Row)txtPostalAdd.getParent());
				isPostalAddMandatory = s.endsWith("!");
			} else if (s.startsWith("R") && m_location.getCountry().isHasRegion()) {
				addComponents((Row)lstRegion.getParent());
				isRegionMandatory = s.endsWith("!");
			}
		}

		//      Fill it
		if (m_location.getC_Location_ID() != 0)
		{
			txtAddress1.setText(m_location.getAddress1());
			txtAddress2.setText(m_location.getAddress2());
			txtAddress3.setText(m_location.getAddress3());
			txtAddress4.setText(m_location.getAddress4());
			txtCity.setText(m_location.getCity());
			txtPostal.setText(m_location.getPostal());
			txtPostalAdd.setText(m_location.getPostal_Add());
			if (m_location.getCountry().isHasRegion())
			{
				if (m_location.getCountry().get_Translation(MCountry.COLUMNNAME_RegionName) != null
						&& m_location.getCountry().get_Translation(MCountry.COLUMNNAME_RegionName).trim().length() > 0)
					lblRegion.setValue(m_location.getCountry().get_Translation(MCountry.COLUMNNAME_RegionName));
				else
					lblRegion.setValue(Msg.getMsg(Env.getCtx(), "Region"));

				setRegion();                
			}
			setCountry();
			//Begin Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
			setSector();
			setProvince();
			setCanton();
			setParish();
			setAddressType();
			//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		}
	}
	
	private void setCountry()
	{
		List<?> listCountry = lstCountry.getChildren();
		Iterator<?> iter = listCountry.iterator();
		while (iter.hasNext())
		{
			ListItem listitem = (ListItem)iter.next();
			if (m_location.getCountry().equals(listitem.getValue()))
			{
				lstCountry.setSelectedItem(listitem);
			}
		}
	}

	private void setRegion()
	{
		if (m_location.getRegion() != null) 
		{
			List<?> listState = lstRegion.getChildren();
			Iterator<?> iter = listState.iterator();
			while (iter.hasNext())
			{
				ListItem listitem = (ListItem)iter.next();
				if (m_location.getRegion().equals(listitem.getValue()))
				{
					lstRegion.setSelectedItem(listitem);
				}
			}
		}
		else
		{
			lstRegion.setSelectedItem(null);
		}        
	}
	
	//Begin Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
	private void setSector()
	{
		MCASector sector = new MCASector(Env.getCtx(), m_location.getCA_Sector_ID(), null);
		
		if (sector.get_ID() > 0) 
		{
			List<?> listState = lstSector.getChildren();
			Iterator<?> iter = listState.iterator();
			while (iter.hasNext())
			{
				ListItem listitem = (ListItem)iter.next();
				if (sector.equals(listitem.getValue()))
				{
					lstSector.setSelectedItem(listitem);
				}
			}
		}
		else
		{
			lstSector.setSelectedItem(null);
		}        
	}
	
	private void setProvince()
	{
		MCAProvince province = new MCAProvince(Env.getCtx(), m_location.getCA_Province_ID(), null);
		
		if (province.get_ID() > 0) 
		{
			List<?> listState = lstProvince.getChildren();
			Iterator<?> iter = listState.iterator();
			while (iter.hasNext())
			{
				ListItem listitem = (ListItem)iter.next();
				if (province.equals(listitem.getValue()))
				{
					lstProvince.setSelectedItem(listitem);
				}
			}
		}
		else
		{
			lstProvince.setSelectedItem(null);
		}        
	}
	
	private void setCanton()
	{	
		MCACanton canton = new MCACanton(Env.getCtx(), m_location.getCA_Canton_ID(), null);
		
		if (canton.get_ID() > 0) 
		{
			List<?> listState = lstCanton.getChildren();
			Iterator<?> iter = listState.iterator();
			while (iter.hasNext())
			{
				ListItem listitem = (ListItem)iter.next();
				if (canton.equals(listitem.getValue()))
				{
					lstCanton.setSelectedItem(listitem);
				}
			}
		}
		else
		{
			lstCanton.setSelectedItem(null);
		}        
	}
	
	private void setParish()
	{	
		MCAParish parish = new MCAParish(Env.getCtx(), m_location.getCA_Parish_ID(), null);
		
		if (parish.get_ID() > 0) 
		{
			List<?> listState = lstParish.getChildren();
			Iterator<?> iter = listState.iterator();
			while (iter.hasNext())
			{
				ListItem listitem = (ListItem)iter.next();
				if (parish.equals(listitem.getValue()))
				{
					lstParish.setSelectedItem(listitem);
				}
			}
		}
		else
		{
			lstParish.setSelectedItem(null);
		}        
	}
	
	private void setAddressType()
	{
		String locationType = m_location.getAddressType();
		
		if (locationType != null)
		{
			List<?> listState = lstAddressType.getChildren();
			Iterator<?> iter = listState.iterator();
			while (iter.hasNext())
			{
				ListItem listitem = (ListItem)iter.next();
				if (locationType.equals(listitem.getValue()))
				{
					lstAddressType.setSelectedItem(listitem);
				}
			}
		}
		else
		{
			lstAddressType.setSelectedItem(null);
		}
	}
	//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
	
	/**
	 *  Get result
	 *  @return true, if changed
	 */
	public boolean isChanged()
	{
		return m_change;
	}   //  getChange
	/**
	 *  Get edited Value (MLocation)
	 *  @return location
	 */
	public MLocation getValue()
	{
		return m_location;
	}   

	public void onEvent(Event event) throws Exception
	{
		if (btnOk.equals(event.getTarget()))
		{
			inOKAction = true;
			
			if (m_location.getCountry().isHasRegion() && lstRegion.getSelectedItem() == null) {
				if (txtCity.getC_Region_ID() > 0 && txtCity.getC_Region_ID() != m_location.getC_Region_ID()) {
					m_location.setRegion(MRegion.get(Env.getCtx(), txtCity.getC_Region_ID()));
					setRegion();
				}
			}
			
			String msg = validate_OK();
			if (msg != null) {
				FDialog.error(0, this, "FillMandatory", Msg.parseTranslation(Env.getCtx(), msg));
				inOKAction = false;
				return;
			}
			
			if(action_OK())
			{
				m_change = true;
				inOKAction = false;
				this.dispose();
			}
			else
			{
				FDialog.error(0, this, "CityNotFound");
			}
			inOKAction = false;
		}
		else if (btnCancel.equals(event.getTarget()))
		{
			m_change = false;
			this.dispose();
		}
		//  Country Changed - display in new Format
		else if (lstCountry.equals(event.getTarget()))
		{
			inCountryAction = true;
			MCountry c = (MCountry)lstCountry.getSelectedItem().getValue();
			m_location.setCountry(c);
			m_location.setC_City_ID(0);
			m_location.setCity(null);
			//  refresh
			initLocation();
			inCountryAction = false;
		}
		//  Region Changed 
		else if (lstRegion.equals(event.getTarget()))
		{
			if (inCountryAction || inOKAction)
				return;
			MRegion r = (MRegion)lstRegion.getSelectedItem().getValue();
			m_location.setRegion(r);
			m_location.setC_City_ID(0);
			m_location.setCity(null);
			//  refresh
			initLocation();
		}
		//Begin Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		//  Sector Changed
		else if (lstSector.equals(event.getTarget()))
		{
			if (lstSector.getSelectedItem() != null)
			{
				MCASector sector = (MCASector)lstSector.getSelectedItem().getValue();
				if (sector != null)
					m_location.setCA_Sector_ID(sector.get_ID());
			}
		}
		//	Province Changed
		else if (lstProvince.equals(event.getTarget()))
		{
			if (inCountryAction)
				return;
			
			if (lstProvince.getSelectedItem() != null)
			{
				inProvinceAction = true;
				MCAProvince province = (MCAProvince)lstProvince.getSelectedItem().getValue();
				if (province != null)
					m_location.setCA_Province_ID(province.get_ID());
				//	refresh
				initLocation();
				inProvinceAction = false;
			}
		}
		//	Canton Changed
		else if (lstCanton.equals(event.getTarget()))
		{
			if (inCountryAction || inProvinceAction)
				return;
			
			if (lstCanton.getSelectedItem() != null)
			{
				inCantonAction = true;
				MCACanton canton = (MCACanton)lstCanton.getSelectedItem().getValue();
				if (canton != null)
					m_location.setCA_Canton_ID(canton.get_ID());
				//	refresh
				initLocation();
				inCantonAction = false;
			}
		}
		//	Parish Changed
		else if (lstParish.equals(event.getTarget()))
		{
			if (inCountryAction || inProvinceAction || inCantonAction)
				return;
			if (lstParish.getSelectedItem() != null)
			{
				MCAParish parish = (MCAParish)lstParish.getSelectedItem().getValue();
				if (parish != null)
					m_location.setCA_Parish_ID(parish.get_ID());
				//	refresh
				initLocation();
			}
		}
		//	AddressType Changed
		else if (lstAddressType.equals(event.getTarget()))
		{
			if (lstAddressType.getSelectedItem() != null)
			{
				m_location.setAddressType((String)lstAddressType.getSelectedItem().getValue());
			}
		}
		//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		else if (btnUrl.equals(event.getTarget()))
		{
			Env.startBrowser(DefaultContextProvider.GOOGLE_MAPS_URL_PREFIX + getCurrentLocation());
			m_change = false;
			this.dispose();
		}
	}

	
	// LCO - address 1, region and city required
	private String validate_OK() {
		String fields = "";
		if (isAddress1Mandatory && txtAddress1.getText().trim().length() == 0) {
			fields = fields + " " + "@Address1@, ";
		}
		if (isAddress2Mandatory && txtAddress2.getText().trim().length() == 0) {
			fields = fields + " " + "@Address2@, ";
		}
		if (isAddress3Mandatory && txtAddress3.getText().trim().length() == 0) {
			fields = fields + " " + "@Address3@, ";
		}
		if (isAddress4Mandatory && txtAddress4.getText().trim().length() == 0) {
			fields = fields + " " + "@Address4@, ";
		}
		if (isCityMandatory && txtCity.getValue().trim().length() == 0) {
			fields = fields + " " + "@C_City_ID@, ";
		}
		if (isRegionMandatory && lstRegion.getSelectedItem() == null) {
			fields = fields + " " + "@C_Region_ID@, ";
		}
		if (isPostalMandatory && txtPostal.getText().trim().length() == 0) {
			fields = fields + " " + "@Postal@, ";
		}
		if (isPostalAddMandatory && txtPostalAdd.getText().trim().length() == 0) {
			fields = fields + " " + "@PostalAdd@, ";
		}
		//Begin Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		if (isSectorMandatory && lstSector.getSelectedItem() == null) {
			fields = fields + " " + "@CA_Sector_ID@, ";
		}
		if (isProvinceMandatory && lstProvince.getSelectedItem() == null) {
			fields = fields + " " + "@CA_Province_ID@, ";
		}
		if (isCantonMandatory && lstCanton.getSelectedItem() == null) {
			fields = fields + " " + "@CA_Canton_ID@, ";
		}
		if (isParishMandatory && lstParish.getSelectedItem() == null) {
			fields = fields + " " + "@CA_Parish_ID@, ";
		}
		if (isAddressTypeMandatory && lstAddressType.getSelectedItems() == null) {
			fields = fields + " " + "@AddressType@";
		}
		//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		
		if (fields.trim().length() > 0)
			return fields.substring(0, fields.length() -2);

		return null;
	}

	/**
	 *  OK - check for changes (save them) & Exit
	 */
	private boolean action_OK()
	{
		m_location.setAddress1(txtAddress1.getValue());
		m_location.setAddress2(txtAddress2.getValue());
		m_location.setAddress3(txtAddress3.getValue());
		m_location.setAddress4(txtAddress4.getValue());
		m_location.setC_City_ID(txtCity.getC_City_ID()); 
		m_location.setCity(txtCity.getValue());
		m_location.setPostal(txtPostal.getValue());
		//  Country/Region
		MCountry country = (MCountry)lstCountry.getSelectedItem().getValue();
		m_location.setCountry(country);
		if (country.isHasRegion() && lstRegion.getSelectedItem() != null)
		{
			MRegion r = (MRegion)lstRegion.getSelectedItem().getValue();
			m_location.setRegion(r);
		}
		else
		{
			m_location.setC_Region_ID(0);
		}
		//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		if (lstSector.getSelectedItem() != null) 
		{
			MCASector sector = (MCASector)lstSector.getSelectedItem().getValue();
			if (sector != null)
				m_location.setCA_Sector_ID(sector.get_ID());
		}
		if (lstProvince.getSelectedItem() != null)
		{
			MCAProvince province = (MCAProvince)lstProvince.getSelectedItem().getValue();
			if (province != null)
				m_location.setCA_Province_ID(province.get_ID());
		}
		if (lstCanton.getSelectedItem() != null)
		{
			MCACanton canton = (MCACanton)lstCanton.getSelectedItem().getValue();
			if (canton != null)
				m_location.setCA_Canton_ID(canton.get_ID());
		}
		if (lstParish.getSelectedItem() != null)
		{
			MCAParish parish = (MCAParish)lstParish.getSelectedItem().getValue();
			if (parish != null)
				m_location.setCA_Parish_ID(parish.get_ID());
		}
		if (lstAddressType.getSelectedItem() != null)
		{
			if (lstAddressType.getSelectedItem().getValue() != null)
				m_location.setAddressType((String)lstAddressType.getSelectedItem().getValue());
		}
		//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		
		//  Save changes
		if(m_location.save())
		{
			return true;
		}
		else
		{
			return false;
		}
	}   //  actionOK

	@Override
	public void dispose()
	{
		if (!m_change && m_location != null && !m_location.is_new())
		{
			m_location = new MLocation(m_location.getCtx(), m_location.get_ID(), null);
		}	
		super.dispose();
	}
	
	/**
	 * 	Get edited Value (MLocation)
	 *	@return location
	 */
	private String getCurrentLocation() {
		m_location.setAddress1(txtAddress1.getText());
		m_location.setAddress2(txtAddress2.getText());
		m_location.setAddress3(txtAddress3.getText());
		m_location.setAddress4(txtAddress4.getText());
		m_location.setCity(txtCity.getText());
		m_location.setPostal(txtPostal.getText());
		m_location.setPostal_Add(txtPostalAdd.getText());
		//  Country/Region
		MCountry c = (MCountry)lstCountry.getSelectedItem().getValue();
		m_location.setCountry(c);
		if (m_location.getCountry().isHasRegion())
		{
			MRegion r = (MRegion)lstRegion.getSelectedItem().getValue();
			m_location.setRegion(r);
		}
		else
			m_location.setC_Region_ID(0);
		
		//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		if (lstSector.getSelectedItem() != null)
		{
			MCASector sector = (MCASector)lstSector.getSelectedItem().getValue();
			if (sector != null)
				m_location.setCA_Sector_ID(sector.get_ID());
		}
		if (lstProvince.getSelectedItem() != null)
		{
			MCAProvince province = (MCAProvince)lstProvince.getSelectedItem().getValue();
			if (province != null)
				m_location.setCA_Province_ID(province.get_ID());
		}
		if (lstCanton.getSelectedItem() != null)
		{
			MCACanton canton = (MCACanton)lstCanton.getSelectedItem().getValue();
			if (canton != null)
				m_location.setCA_Canton_ID(canton.get_ID());
		}
		if (lstParish.getSelectedItem() != null)
		{
			MCAParish parish = (MCAParish)lstParish.getSelectedItem().getValue();
			if (parish != null)
				m_location.setCA_Parish_ID(parish.get_ID());
		}
		if (lstAddressType.getSelectedItem() != null)
		{
			if (lstAddressType.getSelectedItem().getValue() != null)
				m_location.setAddressType((String)lstAddressType.getSelectedItem().getValue());
		}
		//End Add FCAQ Fields, Josias Vargas, e-Evolution, 12-06-2013
		
		//return m_location.toString().replace(", ", "%");
		return m_location.toStringFCAQ().replace(", ", "%");
	}
}
