<h1>Audition Post</h1>

[//]: # (# Audition Post)

This project is a **Spring Boot** application designed to expose four distinct REST APIs to retrieve Audition Post
information, primarily utilizing **Spring Boot, Java, and Gradle**. It incorporates static analysis tools such as *
*SpotBugs, Checkstyle, and PMD** for code quality verification and generates code coverage reports. Additionally, the
project includes a Postman collection for testing and validating the exposed APIs.

<h2>Installation</h2>

[//]: # (## Installation)

### Prerequisites

Make sure you have the following software installed:

- Any Springboot/Java IDE. Ideally IntelliJIdea.
- Java 17
- Gradle 8
- Postman Desktop Client or Postman Web Client (Optional)

### Step-by-Step Guide

1. Unzip file audition-api.zip
2. Open InteliJIdea. Go to File > Open > select audition-api project > click OK
3. Run configurations are exported at '.idea\runConfigurations', So it will be available to use.
4. Run audition-api-build to build project from Run configuration.
5. Run audition-api-bootRun to run project from Run configuration.

Once you see line `Tomcat started on port(s): 8080`, server is up and running.

### Execution

1. Open Postman client.
2. Click on import and select [Postman Collection](src/test/resources/AuditionPost.postman_collection.json) file from
   path `src/test/resources/AuditionPost.postman_collection.json`
3. Now you have all the API endpoints available for the project in your Postman.
4. Call the APIs to see the results.

OR

Alternatively, open Command Prompt and call the APIs using `curl` if you don't want to use Postman.

```
curl -u admin:adminPassword http://localhost:8080/posts?title=provident%20occaecati%20excepturi

curl -u admin:adminPassword http://localhost:8080/posts/11

curl -u admin:adminPassword http://localhost:8080/posts/11/comments

curl -u admin:adminPassword http://localhost:8080/comments?postId=11

curl -u admin:adminPassword http://localhost:8080/actuator/info

curl -u admin:adminPassword http://localhost:8080/actuator/health
```

<h2>API Documentation</h2>

- Once the project is up and running, the Swagger documentation can be accessed
  through [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html
  )
- Click on the top right green 'Authorize' button.
- Provide the <b>username:</b> `admin` and <b>password:</b> `adminPassword` to accesss all the listed APIs.

<h2>Highlights of Implementation</h2>

- Basic authentication has been implemented in Spring. It is added in postman collection with below credentials:
    - Username: `admin`
    - Password: `adminPassword`
- Added OpenTelemetry traceId and spanId in response header.
- Spring Actuator endpoints for info and health are enabled and active.
- Standard naming conventions have been followed throughout the code.
- Added property `logging.interceptor.enabled` to enable/disable logging through "src/main/resources/application.yml"
  file.
- All TODO tasks have been completed.

<h2>Code Quality Reports</h2>

[//]: # (# Code Quality Reports)

### Test Coverage Report

- **Jacoco**: [View Jacoco Test Coverage Report](build/reports/jacoco/test/html/index.html)

[//]: # (  The Jacoco report provides insights into the code coverage of your unit tests.)

### Code Style Reports

- **CheckStyle (Main)**: [View CheckStyle Main Report](build/reports/checkstyle/main.html)

[//]: # (  This report shows the CheckStyle analysis for the main codebase, highlighting code style violations and suggestions.)

- **CheckStyle (Test)**: [View CheckStyle Test Report](build/reports/checkstyle/test.html)

[//]: # (  This report shows the CheckStyle analysis for test code, ensuring it adheres to the defined coding standards.)

### Static Analysis Reports

- **PMD (Main)**: [View PMD Main Report](build/reports/pmd/main.html)

[//]: # (  PMD static analysis report for the main codebase, which checks for common code issues like unused variables and)

[//]: # (  complex code.)

- **PMD (Test)**: [View PMD Test Report](build/reports/pmd/test.html)

[//]: # (  PMD static analysis report for test code, similar to the main code analysis but focused on test-related issues.)

- **SpotBugs**: [View SpotBugs Report](build/reports/spotbugs/spotbugs.html)

[//]: # (  SpotBugs report analyzes your code for potential bugs, such as null pointer exceptions or concurrency issues.)


