package org.fenixedu.bennu.io.domain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;

public final class GroupBasedFile extends GroupBasedFile_Base {
    public GroupBasedFile(final String displayName, final String filename, final byte[] content,
                          final Group accessGroup) {
        init(displayName, filename, content);
        setAccessGroup(accessGroup);
    }

    /**
     * Creates a new {@link GroupBasedFile} with the contents of the given file and the provided name and access group.
     * 
     * @param displayName
     *            The pretty name for this file
     * @param filename
     *            The low-level filename for this file
     * @param file
     *            The file from which the contents of the newly created file are based upon
     * @param accessGroup
     *            The access group for this file
     * @throws IOException
     *             If an error occurs while reading the input file or storing it in the underlying storage
     */
    public GroupBasedFile(final String displayName, final String filename, final File file, final Group accessGroup)
            throws IOException {
        init(displayName, filename, file);
        setAccessGroup(accessGroup);
    }
    
    public GroupBasedFile(final String displayName, final String filename, final InputStream stream,
                          final Group accessGroup) throws IOException {
        init(displayName, filename, stream);
        setAccessGroup(accessGroup);
    }

    public Group getAccessGroup() {
        return getGroup().toGroup();
    }

    public void setAccessGroup(final Group accessGroup) {
        setGroup(accessGroup.toPersistentGroup());
    }

    @Override
    public boolean isAccessible(final User user) {
        return getGroup().isMember(user);
    }

    @Override
    public void delete() {
        setGroup(null);
        super.delete();
    }
}
