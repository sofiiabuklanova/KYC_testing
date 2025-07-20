To run tests, you'll need:
* Maven 3.14.0
* IntelliJ IDEA 2024.3
* TestNG 3.5.3
* JDK 23

Install all recommended software, open project in IDEA, run server mockup on localhost:3000 and run RegistrationTests and KYCTests. 

Maven command to run all tests: mvn clean test -DsuiteXmlFile=testng.xml

Possible issues: 
* IDEA might use wrong shortening of command line, to fix it - edit configuration template and use JAR manifest shortening. 
* Frontend mockup has issue in package.json, recommended to replace line 41 (with proxy information) to

  "options": {
    "allowedHosts": ["localhost", ".localhost"],
    "proxy": "https://localhost:3000/"
  },
