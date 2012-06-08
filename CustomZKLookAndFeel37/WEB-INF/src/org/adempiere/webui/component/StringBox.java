
package org.adempiere.webui.component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.apps.AEnv;
import org.compiere.model.Obscure;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.zkoss.zhtml.Table;
import org.zkoss.zhtml.Td;
import org.zkoss.zhtml.Tr;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Vbox;


public class StringBox extends Div implements org.zkoss.zul.api.Textbox
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 7089099079981906933L;

	private Textbox textbox = new Textbox();
	
	private Obscure	m_obscure = null;

    
	public boolean isReadonly()
	{
		return textbox.isReadonly();
	}
        /**
     * 
     * @param integral
     */
    public StringBox()
    {
        super();
        init();
    }
    
    private void init()
    {
		textbox = new Textbox();
		String style = AEnv.isFirefox2() ? "display: inline" : "display: inline-block"; 
        style = style + ";white-space:nowrap";
        this.setStyle(style);	     
        this.appendChild(textbox);
    }
    
    
    
    
    
    /**
     * 
     * @param value
     */
    public void setValue(Object value)
    {
    	if (value == null)
    		textbox.setValue(null);
    	else
    		textbox.setValue(value.toString());
    }
    
    /**
     * 
     * @return BigDecimal
     */
    public String getValue()
    {
    	return textbox.getValue();
    }
    
	/**
	 * 
	 * @return boolean
	 */
	public boolean isEnabled()
	{
		 return textbox.isReadonly();
	}
	
	/**
     * method to ease porting of swing form
     * @param listener
     */
	public void addFocusListener(EventListener listener) {
		textbox.addEventListener(Events.ON_FOCUS, listener);
		textbox.addEventListener(Events.ON_BLUR, listener);
	}
	
	@Override
	public boolean addEventListener(String evtnm, EventListener listener)
	{
	     
	         return textbox.addEventListener(evtnm, listener);
	     
	}
	
	@Override
	public void focus()
	{
		textbox.focus();
	}
	
	public Textbox getTextBox()
	{
		return textbox;
	}
	@Override
	public boolean isDisabled() {
		return textbox.isDisabled();
	}
	@Override
	public void setDisabled(boolean disabled) {
		textbox.setDisabled(disabled);
	}
	@Override
	public void setReadonly(boolean readonly) {
		textbox.setReadonly(readonly);
	}
	@Override
	public String getName() {
		return textbox.getName();
	}
	@Override
	public void setName(String name) {
		textbox.setName(name);
	}
	@Override
	public String getErrorMessage() {
		return textbox.getErrorMessage();
	}
	@Override
	public void clearErrorMessage(boolean revalidateRequired) {
		textbox.clearErrorMessage(revalidateRequired);
	}
	@Override
	public void clearErrorMessage() {
		textbox.clearErrorMessage();
	}
	@Override
	public String getText() throws WrongValueException {
		return textbox.getText();
	}
	@Override
	public void setText(String value) throws WrongValueException {
		textbox.setText(value);
	}
	@Override
	public int getMaxlength() {
		return textbox.getMaxlength();
	}
	@Override
	public void setMaxlength(int maxlength) {
		textbox.setMaxlength(maxlength);
	}
	@Override
	public int getCols() {
		return textbox.getCols();
	}
	@Override
	public void setCols(int cols) throws WrongValueException {
		textbox.setCols(cols);
	}
	@Override
	public int getTabindex() {
		return textbox.getTabindex();
	}
	@Override
	public void setTabindex(int tabindex) throws WrongValueException {
		textbox.setTabindex(tabindex);
	}
	@Override
	public boolean isMultiline() {
		return textbox.isMultiline();
	}
	@Override
	public String getType() {
		return textbox.getType();
	}
	@Override
	public void select() {
		textbox.select();
	}
	@Override
	public void setConstraint(String constr) {
		textbox.setConstraint(constr);
	}
	@Override
	public Object getRawValue() {
		return textbox.getRawValue();
	}
	@Override
	public String getRawText() {
		return textbox.getRawText();
	}
	@Override
	public void setRawValue(Object value) {
		textbox.setRawValue(value);
	}
	@Override
	public boolean isValid() {
		return textbox.isValid();
	}
	@Override
	public void setSelectedText(int start, int end, String newtxt,
			boolean isHighLight) {
		textbox.setSelectedText(start, end, newtxt, isHighLight);
	}
	@Override
	public void setSelectionRange(int start, int end) {
		textbox.setSelectionRange(start, end);
	}
	@Override
	public String getAreaText() {
		return textbox.getAreaText();
	}
	@Override
	public void setConstraint(Constraint constr) {
		textbox.setConstraint(constr);
	}
	@Override
	public Constraint getConstraint() {
		return textbox.getConstraint();
	}
	@Override
	public void setValue(String value) throws WrongValueException {
		textbox.setValue(value);
	}
	@Override
	public void setType(String type) throws WrongValueException {
		textbox.setType(type);
	}
	@Override
	public int getRows() {
		return textbox.getRows();
	}
	@Override
	public void setRows(int rows) throws WrongValueException {
		textbox.setRows(rows);
	}
	@Override
	public void setMultiline(boolean multiline) {
		textbox.setMultiline(multiline);
	}
	
	public void setObscureType(String obscureType)
    {
    	if (obscureType != null && obscureType.length() > 0)
		{
			m_obscure = new Obscure ("", obscureType);
		}
    	else
    	{
    		m_obscure = null;
    	}
    	setValue(getValue());
    }
	
	public void setWidth(String width)
	{
		super.setWidth(width);
		textbox.setWidth(width);
	}
	
	public void setHeight(String height)
	{
		super.setHeight(height);
		textbox.setHeight("95%");
	}
}
