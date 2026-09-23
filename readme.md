RP Gate Management System

Local Setup Guide — Windows (New Laptop)

This guide assumes the laptop is new and the required development tools are not installed.

1. Project structure

After extracting the ZIP, the project should look like:

RP_gate_management/
├── backend/
│   ├── pom.xml
│   └── src/
├── database/
│   └── rp_gate.db
└── frontend/
    ├── index.html
    ├── css/
    ├── js/
    ├── images/
    └── assets/

2. Required software

Install:

Git

Java JDK 17

Apache Maven 3.9.x

Visual Studio Code (recommended)

Google Chrome or another modern browser

VS Code Live Server extension

Node.js / npm

Node.js and npm are NOT required.

The frontend is plain HTML/CSS/JavaScript. Do not run:

npm install
npm run dev

There is no package.json in the frontend.

3. Install Git

Download Git for Windows:

https://git-scm.com/download/win

Install it using the normal/default options.

Verify:

git --version

You should see something similar to:

git version 2.x.x

If git is not recognized, close CMD, open a new CMD, and try again.

4. Install Java 17

Install Eclipse Temurin JDK 17.

Recommended: Windows Package Manager

Open Command Prompt as Administrator and run:

winget install EclipseAdoptium.Temurin.17.JDK

Accept any prompts if required.

Verify:

java --version

Also:

javac --version

Both should show Java 17.

If Java is not recognized, close CMD and open a new one. If necessary, restart Windows.

5. Install Apache Maven

Download Maven:

https://maven.apache.org/download.cgi

Download the Binary zip archive for Maven 3.9.x.

Extract it, for example, to:

C:\Program Files\apache-maven-3.9.x

The Maven bin directory should be:

C:\Program Files\apache-maven-3.9.x\bin

Add Maven to PATH

Open:

Windows Search → Environment Variables → Edit the system environment variables → Environment Variables

Under System variables:

Select Path

Click Edit

Click New

Add:

C:\Program Files\apache-maven-3.9.x\bin

Replace 3.9.x with the actual installed version.

Optional: create:

MAVEN_HOME

with value:

C:\Program Files\apache-maven-3.9.x

Click OK on all windows.

Open a new CMD and verify:

mvn --version

You should see Apache Maven 3.9.x and Java 17.

6. Install Visual Studio Code

Download:

https://code.visualstudio.com/

Install VS Code using the normal options.

7. Extract the project ZIP

Copy RP_gate_management.zip to the Desktop.

Right-click it and choose:

Extract All

The result should be:

Desktop
└── RP_gate_management
    ├── backend
    ├── database
    └── frontend

Do not accidentally create:

RP_gate_management
└── RP_gate_management
    ├── backend
    ├── database
    └── frontend

The backend, database, and frontend folders must be directly inside the main project folder.

8. Check the database

Open:

RP_gate_management\database

Make sure this file exists:

rp_gate.db

Do not delete or rename this file.

9. Check backend configuration

Open:

RP_gate_management\backend\src\main\resources\application.properties

For local Windows use, the important settings should be similar to:

spring.application.name=rp-gate-backend
server.address=127.0.0.1
server.port=8081

spring.datasource.url=jdbc:sqlite:${user.dir}/../database/rp_gate.db
rp.gate.database-path=${user.dir}/../database/rp_gate.db

spring.datasource.driver-class-name=org.sqlite.JDBC
spring.datasource.hikari.maximum-pool-size=1
spring.datasource.hikari.connection-init-sql=PRAGMA foreign_keys=ON

spring.sql.init.mode=never
spring.jackson.default-property-inclusion=non_null
server.error.include-message=never

For local running, keep:

server.address=127.0.0.1
server.port=8081

10. Start the Spring Boot backend

Open Command Prompt.

Go to the backend folder:

cd "C:\Users\<username>\Desktop\RP_gate_management\backend"

Replace <username> with the Windows username.

Example:

cd "C:\Users\John\Desktop\RP_gate_management\backend"

Run:

mvn spring-boot:run

The first run may take some time because Maven downloads dependencies.

Wait for:

Tomcat started on port 8081
Started RPGateApplication

Keep this CMD window open.

The backend must be running while the application is being used.

11. Test the backend

Open a second CMD.

Run:

curl http://localhost:8081/api/health

Expected:

{"status":"UP","mode":"offline"}

Test personnel:

curl http://localhost:8081/api/personnel

Test movements:

curl http://localhost:8081/api/movements

An empty database may correctly return:

[]

12. Install Live Server

Open VS Code and open the RP_gate_management folder.

Go to Extensions.

Search:

Live Server

Install:

Live Server — Ritwick Dey

13. Start the frontend

Open:

frontend/index.html

Right-click inside the file and choose:

Open with Live Server

The browser should open a URL similar to:

http://127.0.0.1:5500/frontend/index.html

The exact port may differ.

Do not double-click index.html

Do not use:

file:///C:/.../frontend/index.html

The frontend JavaScript needs to be served through HTTP.

14. Check the frontend API URL

Open:

frontend/js/api.js

For local use, it should contain:

const BASE_URL = 'http://127.0.0.1:8081/api';

If it contains port 8080, change it to 8081.

Save the file and refresh the browser.

15. Test personnel saving and searching

Open Personnel Master.

For testing, use fictional data:

Army No: TEST001
I-Card No: JC-TEST-001
Rank: Sepoy
Name: Arun Kumar

Save it.

Then run:

curl http://localhost:8081/api/personnel

The saved personnel should appear.

Then use the application's search function to search:

TEST001

or:

Arun Kumar

16. Test movement saving

Create a test movement.

Example:

Army No: TEST001
I-Card No: JC-TEST-001
Rank: Sepoy
Name: Arun Kumar
Movement Type: TD OUT

Fill all required fields and save.

Then run:

curl http://localhost:8081/api/movements

The saved movement should appear.

17. Verify SQLite directly (optional)

SQLite command-line access is optional for normal use.

If sqlite3 is installed:

cd "C:\Users\<username>\Desktop\RP_gate_management\database"
sqlite3 rp_gate.db

Inside SQLite:

.tables

You should see tables including:

leave_records
movement_records
night_pass_records
personnel
vehicle_records
outpass_records
td_posting_records

Check personnel:

SELECT * FROM personnel;

Check movements:

SELECT * FROM movement_records;

Exit:

.quit

SQLite is not required for normal application use because Spring Boot connects to the database automatically.

18. Normal startup after installation

After everything is installed, the normal startup is:

Terminal 1 — Backend

cd "C:\Users\<username>\Desktop\RP_gate_management\backend"
mvn spring-boot:run

Wait for:

Started RPGateApplication

Frontend

In VS Code:

frontend/index.html
→ Right-click
→ Open with Live Server

Then use the application in the browser.

19. Stopping the application

To stop Spring Boot, go to the backend CMD and press:

Ctrl + C

20. Port 8081 already in use

If Maven says:

Web server failed to start.
Port 8081 was already in use.

Check:

netstat -ano | findstr :8081

Find the PID at the end of the LISTENING line.

Then:

tasklist | findstr <PID>

If it is an old Java/Spring Boot process belonging to this application, stop it:

taskkill /PID <PID> /F

Then start again:

mvn spring-boot:run

Do not kill an unknown Windows process. Confirm that it is the old Java/Spring Boot process first.

21. Common problems

java is not recognized

Run:

java --version

If it fails:

Restart CMD.

Check that JDK 17 is installed.

Restart Windows if necessary.

mvn is not recognized

Run:

mvn --version

If it fails:

Check Maven installation.

Check Maven bin is in PATH.

Restart CMD.

Node.js/npm

Node.js is not required.

Do not run:

npm install
npm run dev

Buttons do not work

Make sure index.html was opened with:

Open with Live Server

and not:

file:///...

Also verify:

const BASE_URL = 'http://127.0.0.1:8081/api';

Frontend shows 500 Internal Server Error

Check:

curl http://localhost:8081/api/health

Expected:

{"status":"UP","mode":"offline"}

Then check the Spring Boot CMD for the actual error.

/api/personnel returns []

This can be normal when there are no personnel records yet.

/api/movements returns []

This can be normal when there are no movement records yet.

22. Database warning

The application uses:

database/rp_gate.db

Do not:

Delete it

Rename it

Move it

Change the database path without updating application.properties

The database already contains the required schema.

23. Final checklist

Before using the application:

Git installed

Java 17 installed

Maven 3.9.x installed

Project extracted correctly

database/rp_gate.db exists

Backend configuration uses port 8081

mvn spring-boot:run starts successfully

/api/health returns UP

frontend/js/api.js uses 127.0.0.1:8081

Live Server installed

index.html opened with Live Server

Frontend loads

Personnel can be saved

Personnel can be searched

Movement can be saved

Movement can be retrieved

Application architecture

Browser
   │
   ▼
Frontend
HTML / CSS / JavaScript
Live Server
   │
   │ HTTP API
   ▼
Spring Boot
Java 17
Port 8081
   │
   ▼
SQLite
database/rp_gate.db