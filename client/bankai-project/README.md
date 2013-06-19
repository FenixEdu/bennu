Bankai Project
=============

This project is used as parent pom for bankai projects.

Description
=============

When using this project as parent pom it will enable javascript minification for your application.
Please check bankai project for more information

Usage
=============

Install this project
-------------
    mvn -Dr.js.skip=true clean package

Use as parent pom
-------------
    <parent>
        <groupId>pt.ist</groupId>
        <artifactId>bankai-project</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
