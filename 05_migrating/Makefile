# Default values when running outside gitlab CI
SHELL := /bin/bash
MAVEN_LOGGING="-Dorg.slf4j.simpleLogger.defaultLogLevel=info -Dorg.slf4j.simpleLogger.showDateTime=true -Dorg.slf4j.simpleLogger.dateTimeFormat=HH:mm:ss,SSS"
NPL_MAVEN_CLI_OPTS=--no-transfer-progress --batch-mode
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

NPL_VERSION=v1

.PHONY: zip
zip:
	@if [ "$(NPL_VERSION)" = "" ]; then echo "NPL_VERSION not set"; exit 1; fi
	@mkdir -p src/exercise/main/kotlin-script && mkdir -p target && cd target && mkdir -p npl-src && cd npl-src && \
		cp -r ../../src/exercise/main/npl-* . && cp -r ../../src/exercise/main/yaml . && cp -r ../../src/exercise/main/kotlin-script . && \
		zip -r ../npl-integrations-$(NPL_VERSION).zip *

