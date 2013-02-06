#This Makefile is a temporary alternative to the aggregating pom file
all: bennu-maven-plugin bennu-project bennu-service bennu-core bennu-dispatch

bennu-maven-plugin:
	@echo Installing Bennu Maven Plugin...
	@cd maven/bennu-maven-plugin/
	@mvn clean install -q
	@cd ../../

bennu-project:
	@echo Installing Bennu Project...
	@cd bennu-project
	@mvn clean install -q
	@cd ..

bennu-service: bennu-project
	@echo Installing Bennu Service Annotation...
	@cd bennu-service
	@mvn clean install -q
	@cd ..

bennu-core: bennu-project bennu-service
	@echo Installing Bennu Core...
	@cd bennu-core
	@mvn clean install -q
	@cd ..

bennu-dispatch: bennu-project bennu-core bennu-service
	@echo Installing Bennu Dispatch...
	@cd bennu-dispatch
	@mvn clean install -q
	@cd ..
