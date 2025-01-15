# Default values when running outside gitlab CI
SHELL := /bin/bash
MAVEN_LOGGING="-Dorg.slf4j.simpleLogger.defaultLogLevel=info -Dorg.slf4j.simpleLogger.showDateTime=true -Dorg.slf4j.simpleLogger.dateTimeFormat=HH:mm:ss,SSS"
NPL_MAVEN_CLI_OPTS=-s ../.m2/settings.xml --batch-mode
NPL_VERSION=$(shell mvn $(NPL_MAVEN_CLI_OPTS) help:evaluate -Dexpression=project.version -q -DforceStdout)

.PHONY: build-npl
build-npl:
	mvn $(NPL_MAVEN_CLI_OPTS) clean compile

.PHONY: build
build:
	docker-compose up -d
	mvn $(NPL_MAVEN_CLI_OPTS) clean verify

.PHONY: local-install
local-install:
	mvn $(NPL_MAVEN_CLI_OPTS) $(MAVEN_LOGGING) clean install -DskipTests -DskipITs -Dbuild.number=$(CI_JOB_ID) -Dversion.full=$(NPL_VERSION) $(MAVEN_LOGGING)
	docker build --build-arg JAR_VERSION="$(NPL_VERSION)" --build-arg GIT_REV="$(CI_COMMIT_SHORT_SHA)" --build-arg BUILD_DATE="$(shell date)" .

.PHONY: cleanup
cleanup:
	docker-compose down --remove-orphans --volumes
