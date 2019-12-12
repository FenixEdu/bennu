# Bennu Search

This module provides basic indexing and search of domain objects.
The purpose of this module is not to replace or serve as an alternative to general solutions like 
Elasticsearch but instead to provide a fast and simple alternative for simple scenarios that don't 
require such complex solutions.  

## Concepts

Currently this module provides a search solution for three cases:
 - index by a key string (KeyIndex)
 - index by year (YearIndex)
 - index by year and month (MonthIndex)

## Indexing your domain

To index your domain, first you need to connect the objects you want to index to one of the indexing
schemas: KeyIndex, YearIndex or MonthIndex. For example, suppose in our module we want to index objects of 
a domain class Person by both a string key and by year. The following DML snippet would result:

```dml
public class Person {
    int dateOfBirth;
    String name;
}

relation PersonYearIndex {
	Person playsRole person {
		multiplicity *;
	}
	.org.fenixedu.bennu.search.domain.YearIndex playsRole yearIndex {
		multiplicity 1..1;
	}
}

relation PersonYearIndex {
	Person playsRole person {
		multiplicity *;
	}
	.org.fenixedu.bennu.search.domain.KeyIndex playsRole keyIndex {
		multiplicity 1..1;
	}
}
```

It is the responsibility of each module to index each object. For example, in the constructor of the 
Person class, we might include the following code:

```java
public class Person {
    public Person(int dateOfBirth, String name) {
        setDateOfBirth(dateOfBirth);
        setName(name);
        DomainIndexSystem.getInstance().index(dateOfBirth, (index) -> index.getPersonSet(), this);
        DomainIndexSystem.getInstance().index(name, (index) -> index.getPersonSet(), this);
    }
}
```

Note that with this solution, if the date of birth or the name of a person changes, then result of a 
search will not be consistent. In this case it may be a better alternative to index each object overriding
the setters of the dateOfBirth and name fields. 

Take care to handle reindexing when necessary.

## Searching your domain

Once you have your domain indexed, this module provides a simple API for retrieving your objects.
For the above example, we could perform the following searches:

```java
Stream<Person> peopleBornIn1979 = DomainIndexSystem.getInstance().search(1979, (index) -> index.getPersonSet());
Stream<Person> peopleNamedJohnDoe = DomainIndexSystem.getInstance().search("John Doe", (index) -> index.getPersonSet());
```

Note that the search by string method has a key argument and not a text. This search only matches exact
strings. It does not perform a text search, but rather a key match. So if you have a person named 
"John W. Doe", the resulting stream will not contain that person.
