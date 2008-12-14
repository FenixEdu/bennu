package myorg.presentationTier;

import org.apache.struts.action.ActionForward;


public class LayoutContext extends Context {

    private String layout = "/defaultLayout.jsp";

    private String title = "";
    private String head = "/head.jsp";
    private String pageHeader = "/pageHeader.jsp";
    private String sideBarLeft = "/sideBarLeft.jsp";
    private String sideBar = "/sideBar.jsp";
    private String pageOperations = "/blank.jsp";
    private String breadCrumbs = "/breadCrumbs.jsp";
    private String body = "/blank.jsp";
    private String footer = "/footer.jsp";

    public LayoutContext() {
	super();
    }

    public LayoutContext(final String path) {
	super(path);
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getPageHeader() {
        return pageHeader;
    }

    public void setPageHeader(String pageHeader) {
        this.pageHeader = pageHeader;
    }

    public String getSideBarLeft() {
        return sideBarLeft;
    }

    public void setSideBarLeft(String sideBarLeft) {
        this.sideBarLeft = sideBarLeft;
    }

    public String getSideBar() {
        return sideBar;
    }

    public void setSideBar(String sideBar) {
        this.sideBar = sideBar;
    }

    public String getPageOperations() {
        return pageOperations;
    }

    public void setPageOperations(String pageOperations) {
        this.pageOperations = pageOperations;
    }

    public String getBreadCrumbs() {
        return breadCrumbs;
    }

    public void setBreadCrumbs(String breadCrumbs) {
        this.breadCrumbs = breadCrumbs;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    @Override
    public ActionForward forward(final String body) {
	setBody(body);
	return new ActionForward(getLayout());
    }

}
