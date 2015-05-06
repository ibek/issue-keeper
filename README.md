# Issue Keeper

Issue keeper is a tracking tool which makes the tests skipped in advance when they are blocked by the open issues.

  - Tracking systems - **Bugzilla, JIRA**
  - Custom **issue constraints** depending on the environment and current test suite (operating system, database, jdk, remote API, etc.)
  - Versioning - tests on **older versions** will be always skipped (though the issue is fixed in the new versions)
  - Multiple issues discoverred in one test
  - Skipping whole test classes
  - Design and runtime configurations
  - Integration with test frameworks - **JUnit**

### Configuration

Add the following Maven dependency into your pom.xml:
```java
<dependency>
    <groupId>link.bek.tools</groupId>
    <artifactId>issue-keeper-junit</artifactId>
    <version>4.11.1</version> <!-- corresponds to junit version 4.11 -->
</dependency>
```

Add the following rule into your test class or the super test base, :
```java
@Rule
public TestRule issueKeeper = new IKeeperJUnitConnector(new BugzillaClient("https://bugzilla.redhat.com"));
```

You are free to change the url of your tracking system.

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

ikeeperEnvironment.properties
  - Set the running environment properties for comparison with the issue constraints
  - Example:
```sh
os=RHEL6
jdk=OpenJDK7
db=PostgreSQL9.2
```

ikeeperConstraints.properties
  - Set the constraints for the reported issues (e.g. the specific issue does not run on PostgreSQL9.2 but it does elsewhere)
  - Example:
```sh
1107757-description=Test description of the issue 1107757.
1107757-os=RHEL6,Win2012
1107757-jdk=OpenJDK7
```

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

