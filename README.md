# Issue Keeper
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/link.bek.tools/issue-keeper-junit/badge.svg)](https://maven-badges.herokuapp.com/maven-central/link.bek.tools/issue-keeper-junit)

Issue keeper is a tracking tool which makes the tests skipped in advance when they are blocked by the open issues.

  - Tracking systems - **Bugzilla, JIRA**
  - Custom **issue constraints** depending on the environment and current test suite (operating system, database, jdk, remote API, etc.)
  - Versioning - tests on **older versions** will be always skipped (though the issue is fixed in the new versions)
  - Multiple issues discoverred in one test
  - Skipping whole test classes
  - Design and runtime configurations
  - Integration with test frameworks - **JUnit**
  - Offline mode (cache for 24 hours - customizable)
  - Configurable issue tracking system states in projects (according to the unique workflows)

### Configuration

Add the following Maven dependency into your pom.xml:
```java
<dependency>
    <groupId>link.bek.tools</groupId>
    <artifactId>issue-keeper-junit</artifactId>
    <version>4.12.5</version> <!-- corresponds to junit version 4.12 -->
    <!--<version>4.11.2</version> corresponds to junit version 4.11 -->
</dependency>
```

Add the following rule into your test class or the super test base, :
```java
@Rule
public IKeeperJUnitConnector issueKeeper = new IKeeperJUnitConnector(new BugzillaClient("https://bugzilla.redhat.com"));
```

You are free to change the url of your tracking system.

### Usage

Just add @BZ or @JIRA annotation on top of a test method or class.
```java
@BZ("1155593")
@Test
public void runVerifiedIssueTest() {

@BZ({"1155593", "1203640"})
@Test
public void runMultipleVerifiedIssuesTest() {
```

### Optional Configuration

ikeeperConfiguration.properties:
  - Change of action
   - Skip - reported issue causes skipping the test
   - Fail - reported issue causes fail of the test
   - DoNothing - all the tests run, no matter the issues
   - Custom - you are free to decide whether to proceed testing or not
   - Example: action=org.jboss.qa.ikeeper.action.Fail
  - Set current testing version
   - testVersion=6.0.2
  - Set versions order
   - versions=6.0.1,6.0.2,6.0.3,6.1.0
  - Disable Issue Keeper
   - ikeeper.run=false
   - can be also configured as a system property -Dikeeper.run=false
  - Configure action states
   - JIRA@DEFAULT=NEW,OPEN,ASSIGNED,CODING IN PROGRESS,PULL REQUEST SENT,REOPENED
   - BZ@DEFAULT=NEW,ASSIGNED,POST,MODIFIED
   - JIRA@RHBPMS=NEW,OPEN,ASSIGNED,CODING IN PROGRESS,PULL REQUEST SENT,REOPENED,RESOLVED

ikeeperEnvironment.properties
  - Set the running environment properties for comparison with the issue constraints
  - Example:
```sh
os=RHEL6
jdk=OpenJDK7
db=PostgreSQL9.2
```
  - Alternatively in the code:
```java
issueKeeper.setEnvironmentProperties(envProps);
issueKeeper.setEnvironmentProperty("remoteAPI", controller.toString());
```

ikeeperConstraints.properties
  - Set the constraints for the reported issues (e.g. the specific issue does not run on PostgreSQL9.2 but it does elsewhere)
  - Example:
```sh
1107757-description=Test description of the issue 1107757.
1107757-os=RHEL6,Win2012
1107757-jdk=OpenJDK7
```
  - Support for regular expressions using tilde:
```sh
JBPM-3558-db~=Postgre.*
```
