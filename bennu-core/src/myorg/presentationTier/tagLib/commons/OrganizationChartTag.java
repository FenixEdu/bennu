/* 
* @(#)OrganizationChartTag.java 
* 
* Copyright 2009 Instituto Superior Tecnico 
* Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes 
*  
*      https://fenix-ashes.ist.utl.pt/ 
*  
*   This file is part of the Bennu Web Application Infrastructure. 
* 
*   The Bennu Web Application Infrastructure is free software: you can 
*   redistribute it and/or modify it under the terms of the GNU Lesser General 
*   Public License as published by the Free Software Foundation, either version  
*   3 of the License, or (at your option) any later version. 
* 
*   Bennu is distributed in the hope that it will be useful, 
*   but WITHOUT ANY WARRANTY; without even the implied warranty of 
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
*   GNU Lesser General Public License for more details. 
* 
*   You should have received a copy of the GNU Lesser General Public License 
*   along with Bennu. If not, see <http://www.gnu.org/licenses/>. 
*  
*/
package myorg.presentationTier.tagLib.commons;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import myorg.presentationTier.component.OrganizationChart;
import myorg.presentationTier.component.OrganizationChartRow;

import org.apache.struts.taglib.TagUtils;

/**
 * 
 * @author  Luis Cruz
 * 
*/
public class OrganizationChartTag extends BodyTagSupport {

    private static final String VERTICAL_LINE_HEIGHT = "height: 106px;";
    private static final String VERTICAL_LINE_WIDTH = "width: 5px;";
    private static final String VERTICAL_LINE_STYLE = VERTICAL_LINE_HEIGHT + " " + VERTICAL_LINE_WIDTH;
    private static final String HEIGHT_CONNECTOR = "height: 20px;";

    private static final String BORDER_TOP = "border-top: 1px solid #000;";
    private static final String BORDER_RIGHT = "border-right: 1px solid #000;";
    private static final String BORDER_TOP_RIGHT = BORDER_TOP + " " + BORDER_RIGHT;

    private static final String TABLE_OPEN_PREFIX = "<table style=\"padding: 0px; margin: 0px;\" cellpadding=\"0\" cellspacing=\"0\"";
    private static final String TABLE_OPEN_SUFFIX = ">";
    private static final String TABLE_OPEN = TABLE_OPEN_PREFIX + TABLE_OPEN_SUFFIX;
    private static final String TABLE_CLOSE = "</table>";

    private static final String ROW_OPEN = "\t<tr>";
    private static final String ROW_CLOSE = "</tr>";

    private static final String COLUMN_OPEN = "<td>";
    private static final String COLUMN_CLOSE = "</td>";

    private String name;
    private String id;
    private String type;

    private OrganizationChart<OrganizationChartRow<Object>> organizationChart;
    private int rowIndex = 0;
    private int columnIndex = 0;

    private StringBuilder stringBuilder = null;

    private void print(final String string) throws JspException {
	stringBuilder.append(string);
    }

    private void printBodyContent() throws JspException {
	print(bodyContent.getString());
	try {
	    bodyContent.clear();
	} catch (IOException e) {
	    throw new JspException(e);
	}
    }

    private void printToJsp() throws JspException {
	printToJsp(stringBuilder.toString());
    }

    private void printToJsp(final String string) throws JspException {
	try {
	    pageContext.getOut().print(string);
	} catch (IOException e) {
	    throw new JspException(e);
	}
    }

    @Override
    public int doStartTag() throws JspException {
	super.doStartTag();
	stringBuilder = new StringBuilder();
	organizationChart = (OrganizationChart) TagUtils.getInstance().lookup(pageContext, name, "request");

	print(TABLE_OPEN);
	
	return !organizationChart.isEmpty() && rowIndex < organizationChart.size() ? EVAL_BODY_TAG : SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
	print(TABLE_CLOSE);

	printToJsp();

	name = null;
	id = null;
	type = null;
	organizationChart = null;
	rowIndex = 0;
	columnIndex = 0;

	super.doEndTag();
	return EVAL_PAGE;
    }

    @Override
    public void doInitBody() throws JspException {
	super.doInitBody();

	if (columnIndex == 0) {
	    print(ROW_OPEN);
	    printRowBuffer();
	}

	final OrganizationChartRow row = organizationChart.get(rowIndex);
	final int rowSize = row.size();
	final int elementRowIndex = organizationChart.getElementRowIndex();
	if (rowIndex > elementRowIndex) {
	    if (elementRowIndex >= 0 && rowIndex < organizationChart.size() - 1) {
		if (columnIndex == row.getUnitsPerPart()) {
		    printVerticalLine();
		}
	    } else {
		if ((rowSize == 4 && columnIndex == 2) ||
			(rowSize == 6 && columnIndex == 3) ||
			(rowSize == 2 && columnIndex == 1)) {
		    printColumn(null, null, null);
		}
	    }
	}

	final int colspan = rowSize == 1  || (rowSize == 3 && columnIndex == 1)|| (rowSize == 5 && columnIndex == 2) ? 3 : 1;
	printColumnOpen(colspan, null, null, null);

	final Object object = row.get(columnIndex);
	pageContext.removeAttribute(id);
	pageContext.setAttribute(id, object);
    }

    @Override
    public int doAfterBody() throws JspException {
	super.doAfterBody();

	printBox();

	print(COLUMN_CLOSE);

	final OrganizationChartRow row = organizationChart.get(rowIndex);
	if (++columnIndex == row.size()) {
	    printRowBuffer();
	    print(ROW_CLOSE);
	    columnIndex = 0;
	    rowIndex++;
	}

	if (rowIndex == organizationChart.size()) {
	    return SKIP_BODY;
	}

	doInitBody();
	return EVAL_BODY_AGAIN;
    }

    @Override
    public void release() {
	organizationChart = null;
        super.release();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private void printRowBuffer() throws JspException {
	final int rowSize = organizationChart.get(rowIndex).size();
	final int padSize = (rowSize == 3 || rowSize == 4) ? 1 : (rowSize < 3 ? 2 : 0);
	for (int i = 0; i < padSize; i++) {
	    print(COLUMN_OPEN);
	    print(COLUMN_CLOSE);
	}
    }

    private void printVerticalLine() throws JspException {
	print(COLUMN_OPEN);
	{
	    print(TABLE_OPEN);
	    {
		printRowOpen(VERTICAL_LINE_STYLE);
		{
		    printColumn(null, BORDER_TOP_RIGHT, null);
		    printColumn(null, BORDER_TOP, null);
		}
		print(ROW_CLOSE);
	    }
	    print(TABLE_CLOSE);
	}
	print(COLUMN_CLOSE);
    }

    private void printAttribute(final String name, final String value) throws JspException {
	print(" ");
	print(name);
	print("=\"");
	print(value);
	print("\"");	
    }

    private void printTableOpen(final String width) throws JspException {
	print(TABLE_OPEN_PREFIX);
	if (width != null) {
	    printAttribute("width", width);
	}
	print(TABLE_OPEN_SUFFIX);
    }

    private void printRowOpen(final String style) throws JspException {
	print("<tr");
	if (style != null) {
	    printAttribute("style", style);
	}
	print(">");
    }

    private void printColumnOpen(final int colSpan, final String width, final String style, final String align) throws JspException {
	print("<td");
	if (colSpan > 1) {
	    printAttribute("colspan", Integer.toString(colSpan));
	}
	if (width != null) {
	    printAttribute("width", width);
	}
	if (style != null) {
	    printAttribute("style", style);
	}
	if (align != null) {
	    printAttribute("align", align);
	}
	print(">");
    }

    private void printColumn(final String width, final String style, final String align) throws JspException {
	printColumnOpen(1, width, style, align);
	print(COLUMN_CLOSE);
    }

    private String getBoxUpperLeftStyle() {
	return organizationChart.getElementRowIndex() == -1 || rowIndex == 0 ? null : (columnIndex == 0 ? BORDER_RIGHT : BORDER_TOP_RIGHT);
    }

    private String getBoxUpperRightStyle() {
	final int rowSize = organizationChart.get(rowIndex).size();
	return organizationChart.getElementRowIndex() == -1 || rowIndex == 0 ? null : (columnIndex == rowSize - 1 ? null : BORDER_TOP);
    }

    private String getBoxLowerLeftStyle() { 
	return organizationChart.getElementRowIndex() > -1 && rowIndex <= organizationChart.getElementRowIndex()
		&& rowIndex < organizationChart.size() - 1 ? BORDER_RIGHT : null;
    }

    private String getBoxLowerRightStyle() {
	return null;
    }

    private void printBox() throws JspException {
	printTableOpen("100%");
	{
	    printRowOpen(HEIGHT_CONNECTOR);
	    {
		printColumn("50%", getBoxUpperLeftStyle(), null);
		printColumn("50%", getBoxUpperRightStyle(), null);
	    }
	    print(ROW_CLOSE);
	    print(ROW_OPEN);
	    {
		printColumnOpen(2, null, null, "center");
		{
		    printBodyContent();
		}
		print(COLUMN_CLOSE);
	    }
	    print(ROW_CLOSE);
	    printRowOpen(HEIGHT_CONNECTOR);
	    {
		printColumn("50%", getBoxLowerLeftStyle(), null);
		printColumn("50%", getBoxLowerRightStyle(), null);
	    }
	    print(ROW_CLOSE);
	}
	print(TABLE_CLOSE);
    }

}
