## [**Transactional IO**](./transactional-io.md)
Due to the heavily transaction environment provided by Bennu and the Fenix Framework, dealing with side-effects is far from trivial. As transactional file systems are not commonplace, dealing with files in a transactional environment is a challenge.

To help cope with such a commonplace need, Bennu provides an abstraction to deal with Transactional IO (i.e. file upload and download).

## Core Concepts
Transactional IO support is provided by creating immutable GenericFile instances, which are transparently stored in a FileStorage, who is responsible for actually storing the file in a transactional-safe manner. You may have multiple File Storages configured at any given moment in your system. This allows you to store different types of files in a different manner (by storing the most critical files in a more reliable and mayhap more expensive storage medium, for instance).

## Using Bennu IO
### Generic File API

The basic API for Bennu IO is the GenericFile API. A GenericFile is the representation of an immutable file (i.e. it does not provide you with an edit method), which allows you to transactionally access the file's contents.

To create a new GenericFile, you simply create the object, and the infrastructure will ensure that its contents are properly accessible.

To retrieve the file's contents programmatically, you simply need to use the getContent() and getStream() methods, which will return a byte[] and InputStream respectively. When showing the file directly to a user, you should consider using the [File Download Servlet](https://confluence.fenixedu.org/display/BENNU/Transactional+IO#TransactionalIO-file-download-serv).

### Choosing what Generic File to use
As GenericFile is an abstract API, you need to choose a specific implementation of it when creating a file. Bennu IO provides GroupBasedFile, an implementation that delegates access control to a [Group](./../access-groups/access-groups.md) instance. When your access logic is simple, you should use GroupBasedFile instead of rolling your own subclass. However, when the logic is complex, or you would be creating a Group only to use in the file, your should extend GenericFile. Instructions on doing so are presented below.

### File Download Servlet

Bennu IO provides a standard endpoint for users to download files. This endpoint will take the file's permissions into account, showing an unauthorized message if the user is not allowed to access the file. The endpoint will also take several common optimizations into account: File ranges, Not Modified Headers, Cache headers, etc.

By invoking the FileDownloadServlet.getDownloadUrl(GenericFile) method, you can retrieve the URL in which the user can download the file, using the File Download Servlet.

If you wish to implement your own logic to determine the file to be downloaded, instead of just following the strategy implemented by this servlet, while retaining the implementation of the optimizations, you may want to use the multiple downloadFile methods in the DownloadUtil class.

## Extending

### Creating new file types
Whereas Bennu's GroupBasedFile should cover the needs of most applications with simple file access requirements, there are cases where it is either too complex to configure, or does not provide enough flexibility.

If you require further customization, you can create a subclass of GenericFile. This will allow you to:

+ Provide custom access logic (if you would create a Group subclass just for use with GroupBasedFile, you should take this approach instead).
+ Provide a custom filename/display name/content type.
+ Provide a way to determine the File Storage to be used (the default is to use the configuration for the file type).
+ Add custom on-creation and on-deletion logic.

When subclassing GenericFile, you are only required to provide an implementation for the isAccessible(User) method. This method will be invoked when a user tries to download a file, and is used to determine if the user is given access.

As most file instances will already be connected to their context, many implementations may simply choose to traverse this relation to determine whether a user can access the file or not (for instance, in the workflow module, a user can access one of the process's files, if she is part of the process).

Due to the limitations imposed by the Fenix Framework's code generation process, GenericFile only provides an empty constructor and two protected init methods (one with a byte[] and another with a File). Subclasses should create sensible constructors that receive all the required arguments (for instance, the context, filename, display name, file contents), and take care of invoking the init method. The user of your API should never have to manually invoke them!

### Creating new file storages

Bennu's File Storage offering only allows you to store files in a local file system (that for production environments must be network-shared). If you require another backing storage, you need to provide your own implementation of FileStorage.

In a nutshell, a file storage must simply provide a way to store and retrieve files. There are three required methods to implement, and two optional ones:

+ String store(GenericFile, byte[]) - **Required** - Stores the given byte array, associated with the given file. Must return an identifier string to be used later in returning the file's contents. If the contents array is null, the storage should delete the underlying file if it exists.
+ String store(GenericFile, File) - **Optional/Recommended** - The same as the method above, but taking a java.io.File instead. Whereas implementation of this method is not required, it is highly recommended. By taking a file instead of a byte array, this does not require the whole file to be loaded into memory, and allows for storing files larger than 2GB (the maximum size of a Java array).
+ byte[] read(GenericFile) - **Required** - Retrieves the contents of the given file. Due to the limitations of Java arrays (shown above), usage of this method will be limited.
+ InputStream readAsInputStream(InputStream) - **Required** - Returns an InputStream containing the contents of the given file.
+ boolean tryLowLevelDownload(GenericFile, HttpServletRequest, HttpServletResponse, long start, long end) - **Optional** - If your storage is able to provide a better way to download files than just writing an InputStream into the response's output buffer, you should implement this method. This allows you to use lower level, backend-specific ways to download, such as sendfile support for file-based systems.

#### **Transactional environment**
Such as everything else in a transactional environment, a call to one of the store methods is not final, as the transaction that call the method may restart or even abort.

As such, when implementing a File Storage, you must take special precautions, as to avoid using operations with side effects within the transaction's body, and leave the 'commit' step of storing the file to the commit phase of the transaction. Usage of Transaction listeners is recommended.

## Configuring
[Bennu Admin](./../bennu-admin/bennu-admin.md) provides user interfaces to configure Bennu IO. In particular, it allows the creation of new Local File System and Domain storage configurations, and allows defining the store to be used for each file type. Refer to the linked documentation page for more details.
