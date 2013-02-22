package pt.ist.bennu.portal.domain;


public class MenuItem extends MenuItem_Base {

    public MenuItem() {
        super();
        setOrd(0);
    }

    @Override
    public void addChild(MenuItem child) {
        child.setOrd(getNextOrder());
        super.addChild(child);
    }

    private Integer getNextOrder() {
        return getChild().size() + 1;
    }

}
