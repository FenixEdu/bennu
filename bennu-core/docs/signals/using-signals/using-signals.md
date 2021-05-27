# [Using Signals](using-signals.md)


**Package Renaming**
```
When Signals was integrated into Bennu Core, its package has been changed from org.fenixedu.bennu.signals to org.fenixedu.bennu.core.signals.
```

## Emitting Signals
You first need to create a class that represents the data about your event. This can be a simple bean. If you want to emit domain objects we provide a DomainObjectEvent that encapsulates an instance of a domain object. Next just invoke the Signal.emit with a key and the event object:

```javascript
User user = new User();
Signal.emit("bennu.user.created", new DomainObjectEvent<User>(user));
```

## Handling events
Now that your code is annotated you can start handling events. You can catch event within the same module or even between module, as long the final package contains both the emitter and the receiver. Using our previous example, we first need to create a handler for that event. As stated previously Bennu Signals uses the [Guava EventBus](https://code.google.com/p/guava-libraries/wiki/EventBusExplained) so all you need to do is create a class with a method annotated with Subscribe, as seen [here](https://code.google.com/p/guava-libraries/wiki/EventBusExplained#Example):

```javascript
class CreatePersonOnUser {
  @Subscribe public void doIt(DomainObjectEvent<User> e) {
      Person person = new Person(e.getInstance());
  }
}
```

To register this eventHandler, just simply use the Signal.register method:

```javascript
Signal.register("bennu.user.created", new CreatePersonOnUser());
```

Note that by default the events are captured within a transaction and ran only when it is about to finish, right after committing with success. This happens because in general signal handlers are expected to provide a decoupled extension to the code and not participating in the same computation (e.g. think "do this because that happen" instead of "if you do this do that"). However there are cases where you want to have some transactional assurances. If you want the event handler to run within the same transactional context that the event that calls it use register the handler with registerWithTransaction:
```javascript
Signal.registerWithTransaction("bennu.user.created", new CreatePersonOnUser());
```

This might be useful to ensure that only if both Person and User are created successfully that the transaction commits. If the handler code throws an exception, it will abort the entire transaction.


