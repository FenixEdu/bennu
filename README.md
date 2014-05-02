# Bennu - Modular Web App Development for Java

Bennu is the foundation for building modular Java web applications based on the [fenix-framework](http://fenix-framework.github.io/).

Bennu covers the following core features:
* Runtime installation configuration
* Runtime application menu configuration
* User authentication
* Access groups
* Rest server infrastructure
* Transactional IO
* Scheduling system (crontab like configuration)
* Indexing and search
* Theming
* Internationalization

## Create your own web application in 5 minutes

### Basic setup

* __step 1__ Create a mysql database for your project

* __step 2__ Create a web-app project:
```
mvn archetype:generate \
    -DarchetypeGroupId=org.fenixedu \
    -DarchetypeArtifactId=bennu-webapp-archetype \
    -DarchetypeVersion=3.0.0 \
    -DarchetypeRepository=https://fenix-ashes.ist.utl.pt/nexus/content/groups/fenix-ashes-maven-repository
```
suplying the database information as requested

* __step 3__ Run ```mvn package jetty:run``` on the newly created project and it's up and running

* __step 4__ Access [http://localhost:8080/web-app-artifactId](http://localhost:8080/)

* __step 5__ Login a user without supplying any password, this will be your manager account for now.

* __step 6__ Access Admin>Configuration link and set the name and other details of your App

### Creating an hello world module

* __step 7__ Create a library project using
```
mvn archetype:generate \
    -DarchetypeGroupId=org.fenixedu \
    -DarchetypeArtifactId=bennu-project-archetype \
    -DarchetypeVersion=3.0.0 \
    -DarchetypeRepository=https://fenix-ashes.ist.utl.pt/nexus/content/groups/fenix-ashes-maven-repository
```

* __step 8__ Open the file ```src/main/webapp/hello-world/index.html``` and set it's contents to:
```
<h1>Hello World</h1>
```

* __step 9__ Open the file ```src/main/resources/hello-world/apps.json``` and set it's contents to:
```
{"apps" :
    [{
        "title" :  {"en-GB" : "Hello World", "pt-PT": "Olá Mundo"},
        "description" : {"en-GB" : "Greets people warmly", "pt-PT": "Saúda pessoas afectivamente"},
        "accessExpression" : "#anyone",
        "path" : "hello-world",
    }]
}
```

* __step 10__ Access Admin>Menu Configuration and add the hello world functionality in any place of the menu, then access it and get greeted

## What's next?

From here you can start developing your application modules. Further documentation (both developer and user) can be found in the project's wiki

## Contribute

Bennu is open source LGPL 3. We welcome interest in the project and are open to your contributions
