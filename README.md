# accountService


Integration test AccountControllerIntegTest should be run on an environment with Docker:
mvn -Dtest=AccountControllerIntegTest test

mvn clean deploy

Specify connection properties to mysql database:
spring.datasource.url,spring.datasource.username,spring.datasource.password,spring.datasource.driver-class-name
