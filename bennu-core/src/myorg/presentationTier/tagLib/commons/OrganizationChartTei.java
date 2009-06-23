package myorg.presentationTier.tagLib.commons;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public class OrganizationChartTei extends TagExtraInfo {

    public VariableInfo[] getVariableInfo(TagData data) {

        String type = (String) data.getAttribute("type");
        if (type == null) {
            type = "java.lang.Object";
        }

        return new VariableInfo[] {
        	new VariableInfo(data.getAttributeString("id"), type, true, VariableInfo.NESTED )
        };

    }

}
