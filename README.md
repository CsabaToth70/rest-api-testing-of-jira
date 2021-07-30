# rest-api-testing-of-jira
Testing of Jira functions through Jira REST API, Jira Cloud platform of the Atlassian.


## Applied technologies

Project SDK: java 11 version 11.09<br>
Project language level: 16 Records, patterns<br>
Applied framework of testing: REST Assured<br>
Authentication: Base64, data source: app.properties

![](Rest-assured.jpg)
![](java.png)


## Technical information and set up

For Jira page testing, the user need to have to registered username and password for Codecool meta stage of Jira:
https://jira-auto.codecool.metastage.net/

Username and password for Jira: should be placed 'app.properties' named file property file saved into main project
directory in this format:

        username={username}
        password={password}

## Tasks of the project:

1. Create an issue via the Jira REST API and validate that the response code is 201 OK<br>
2. Get an issue by Key and validate the value of the summary field
3. Create a new comment to an existing Issue and validate it on the issue endpoint
4. Update an existing comment on an Issue and validate it on the issue endpoint
5. Delete an existing comment from an Issue and validate that it is not available on the issue endpoint anymore


