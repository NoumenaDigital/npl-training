MAVEN_CLI_OPTS?=--no-transfer-progress

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
