# Bennu - Modular Web App Development for Java [![Build Status](https://travis-ci.org/FenixEdu/bennu.svg?branch=develop)](https://travis-ci.org/FenixEdu/bennu)

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

* __Step 1__ Create a mysql database for your project

* __Step 2__ Create a web-app project:
```
mvn archetype:generate \
    -DarchetypeGroupId=org.fenixedu \
    -DarchetypeArtifactId=bennu-webapp-archetype \
    -DarchetypeVersion=3.3.0 \
    -DarchetypeRepository=https://fenix-ashes.ist.utl.pt/nexus/content/groups/fenix-ashes-maven-repository
```
Make sure to supply the information required by the archetype.

> Due to a limitation with Maven, you cannot select an empty database password.
> If your password is empty, choose any, and edit the 'src/main/resources/fenix-framework.properties' file.

* __Step 3__ Run ```mvn tomcat7:run``` on the newly created project and it's up and running

* __Step 4__ Open your application at [http://localhost:8080/](http://localhost:8080/)

* __Step 5__ Follow the on-screen bootstrap guide, which will ask your for some initial system configurations.

* __Step 6__ Once you hit finish, you should see the initial screen of your application. To login, simply
             click the 'Login' button on the upper right corner, and enter the username you chose in the
             previous step.

* __Step 7__ Click on the 'System Management' and you are now able to select which functionalities your
             application provides.

### Creating an hello world module

* __Step 1__ Create a library project using
```
mvn archetype:generate \
    -DarchetypeGroupId=org.fenixedu \
    -DarchetypeArtifactId=bennu-project-archetype \
    -DarchetypeVersion=3.3.0 \
    -DarchetypeRepository=https://fenix-ashes.ist.utl.pt/nexus/content/groups/fenix-ashes-maven-repository
```

> The following steps assume you named your application 'hello-world'. If that is not the case,
> replace 'hello-world' with the name you chose.

* __Step 2__ Open the file ```src/main/webapp/hello-world/index.jsp``` and set it's contents to:
```
<h1>Hello World</h1>
```

* __Step 3__ Open the file ```src/main/resources/apps.json``` and set it's contents to:
```
{"apps": [{
    "title": { "en-GB" : "Hello World", "pt-PT": "Olá Mundo" },
    "description": { "pt-PT": "O meu primeiro módulo Bennu", "en-GB": "My First Bennu Module" },
    "accessExpression": "anyone",
    "path": "hello-world",
    "functionalities": [{
        "title" :  {"en-GB" : "Hello World", "pt-PT": "Olá Mundo"},
        "description" : {"en-GB" : "Greets people warmly", "pt-PT": "Saúda pessoas afectivamente"},
        "accessExpression": "anyone",
        "path": "#"
    }]
}]}
```

* __Step 4__ Add your newly created module to your application, and install it using the System Management tool.
             Your app should now be available in the application's menu.

## What's next?

From here you can start developing your application modules. Further documentation (both developer and user) can be found in the project's wiki

## Contribute

Bennu is open source LGPL 3. We welcome interest in the project and are open to your contributions
