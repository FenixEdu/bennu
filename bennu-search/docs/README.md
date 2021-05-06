## [**Indexing and Search**](README.md)

**Deprecation Notice**

Bennu Search has been removed from the main distribution in Bennu 4.

Bennu provides a way to index and search domain entities, based on [Lucene](https://lucene.apache.org/). To make an indexable entity simply implement **Indexable** and **Searchable** interfaces. The **Indexable** is implemented by any entity that is able to provide index fields. The **Searchable** is implemented by any entity that when changed implies the recalculation of **Indexables**.

```java
public class User extends User_Base implements Indexable, Searchable {
    public static enum UserIndexableFields implements IndexableField {
        USERNAME, EMAIL;
        @Override
        public String getFieldName() {
            return name().toLowerCase();
        }
    }
    @Override
    public IndexDocument getDocumentToIndex() {
        IndexDocument index = new IndexDocument(this);
        index.indexString(UserIndexableFields.USERNAME, getUsername());
        if (getEmail() != null) {
            index.indexString(UserIndexableFields.EMAIL, getEmail());
        }
        return index;
    }
    @Override
    public Set<Indexable> getObjectsToIndex() {
        return Collections.<Indexable> singleton(this);
    }
}
```


To search start by creating a **Search** object, then using fluent methods, parametrize your search. Available parametrizations include:

* Tokenized input match
* Entire phrase input match
* Wildcard match
* Fuzzy matches
* Range queries
* 
Every type of parametrization can include a field, that if not specified the search is done on all fields. Also, the parametrizations have an Occurrence value, one of: Must, Should, Must Not.

An example Search parametrization could be:
```java
Search s = new Search().tokens(UserIndexableFields.USERNAME, "john", Occur.MUST).wildcard(UserIndexableFields.EMAIL, "*@gmail.com", Occur.MUST)
```
Meaning Users with username john with a gmail email.

Searches can also be sorted over several fields. To add a sort criteria continue using the fluent methods:
```java
s.sort(UserIndexableFields.USERNAME, false) // false if for reverse
```

Finally after all parametrizations invoke:
```java
List<User> matches = s.search(User.class);
```

