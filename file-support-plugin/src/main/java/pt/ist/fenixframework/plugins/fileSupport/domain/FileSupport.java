package pt.ist.fenixframework.plugins.fileSupport.domain;

import pt.ist.fenixframework.pstm.PersistentRoot;

public class FileSupport extends FileSupport_Base {

    private static FileSupport instance = null;

    private FileSupport() {
        super();
        FileSupport root = PersistentRoot.getRoot(FileSupport.class.getName());
        if (root != null && root != this) {
            throw new Error("Trying to create a 2nd instance of LuceneSearchPluginRoot! That, my friend, is not allowed!");
        }

    }

    public static FileSupport getInstance() {
        if (instance == null) {
            instance = PersistentRoot.getRoot(FileSupport.class.getName());
            if (instance == null) {
                instance = new FileSupport();
                PersistentRoot.addRoot(FileSupport.class.getName(), instance);
            }
        }
        return instance;
    }

}
