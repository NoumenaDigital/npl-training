NPL_VERSION=v2.4
MAVEN_CLI_OPTS?=--no-transfer-progress

.PHONY: zip
zip:
	@if [ "$(NPL_VERSION)" = "" ]; then echo "NPL_VERSION not set"; exit 1; fi
	@mkdir -p 05_migrating/src/exercise/main/kotlin-script && mkdir -p target && cd target && mkdir -p src && cd src && \
		cp -r ../../05_migrating/src/exercise/main/npl-* . && cp -r ../../05_migrating/src/exercise/main/yaml . && cp -r ../../05_migrating/src/exercise/main/kotlin-script . && \
		zip -r ../npl-integrations-$(NPL_VERSION).zip *

.PHONY: mvn-integration-test
mvn-integration-test:
	mvn $(MAVEN_CLI_OPTS) verify -pl 05_migrating -P integration-test

.PHONY:	mvn-clean
mvn-clean:
	mvn $(MAVEN_CLI_OPTS) clean

.PHONY: mvn-install
mvn-install:
	mvn $(MAVEN_CLI_OPTS) install

.PHONY: integration-test
integration-test: mvn-clean mvn-install mvn-integration-test
