@author Mishika Vora
----------------------
Program Description
----------------------

Relevant Files:
Record.java;WorkRecord.java;Bug.java
SalesForceClient.java;RecordGen.java;WorkerThread.java

This program uses Force.com RESTApi to CRUD your Salesforce app.
SalesForceClient.java establishes an https connection to the url you want to
hit with post/get requests (Content Type: JSON). 
Each Record Object takes in a SalesForceClient object to HTTPPOST/HTTPGET
wrapped in methods like queryRecords, postRecord. Other wrapper methods include
generateRecord which calls createRecord (creates a JSONObject of a template of
a default Record with fields filled in) and postRecord (to HTTPPOST the JSON
Object), and validatePost which will try the Post x number of times if it fails.

WorkerThread is used in RecordGen to concurrently hit the url with post requests
to split up the task of generating x amount of records.

(You may want to override properties of concurrent REST Api calls, number of REST Api
calls which you can do in qa/hoseMyOrg.jsp)

WorkRecord.java extends Record.java
Bug.java extends WorkRecord.java (so one can create a Bug record and not
just a generic Record).
                         
Jars Needed:
log4j-1.2.17.jar
javax-ssl-1_1.jar
httpasyncclient-cache-4.0-beta4.jar
httpasyncclient-4.0-beta4.jar
httpmime-4.2.5.jar
httpcore-4.2.4.jar
httpclient-cache-4.2.5.jar
httpclient-4.2.5.jar
fluent-hc-4.2.5.jar
commons-logging-1.1.1.jar
commons-codec-1.6.jar
----------------------
Running The Program
----------------------

Example: java -jar workload-test.jar [your .properties file]
or build path to RecordGen.java in Eclipse

By default it will use config.properties which you will need. Config.properties expects
Client ID, Client Secret, username and password. More info below.

-----------------------------------
Oauth Authentication for Salesforce
-----------------------------------

Make sure your environment in .properties file is https://...etc

Please refer to: 
http://www.salesforce.com/us/developer/docs/api_rest/

Log in as admin user.
Setup --> Develop (left hand column) --> Remote Access (under Develop).
You may be redirected to Apps Page.
Create a Connected App (see below)
(Check - Enable Oauth Settings)
(Select all available Oauth scopes).
After you have saved.
Click on your Connected App.
CLIENT_ID = Consumer Key
CLIENT_SECRET = Consumer Secret

Next Steps:
Getting Security Token

Go back to the top right hand side. 
Click on Admin User.
My Settings.
Personal.
Reset My Security Token.
After you get that, then your security token should be emailed to you
(hopefully you associated your email with admin user).

you will need this for ouath as
USERNAME = your login username
PASSWORD = your login password (appended to) security token

-------------------------
Design Decisions & Issues
-------------------------

o WorkTrigger needs to be disabled in your app (it seems it cannot
push records without this being disabled) -- which was for my specific case in create the custom object ADM_Work__c
for copy of Salesforce's custom app gus.salesforce.com.
o Some defaults had to be made in Record.java (will create an Account
record by default) and similarly with WorkRecord (default is an empty obj).
o A lot of hardcoded Strings for logging messages and Record template
o Each Record takes in a SalesForceClient to use so you can use multiple clients
and connect to various apps easily.
o It may seem you need multiple 'Record' objects but you can use one 'Record'
object and call createRecord multiple times

-------------------------
Class Hierachy
-------------------------		

	WorkerThread.java has-a Record.java
	Record.java has-a SalesforceClient.java
	WorkRecord.java is-a Record.java
	Bug.java is-a WorkRecord.java


  Contacts
  --------

     o Please contact me (Mishika Vora) at mvora@salesforce.com or alternatively at mish.vora@gmail.com


