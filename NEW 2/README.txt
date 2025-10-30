DietPlanner - Java Swing + MySQL (JDBC)
--------------------------------------

Included files:
- dietdb.sql          -> SQL script: creates database, tables and a sample user (username: test, password: test123)
- DBConnection.java   -> JDBC connection helper (edit USER/PASS as needed)
- DietPlan.java       -> Model class
- DietPlanDAO.java    -> Data access object (CRUD)
- MainApp.java        -> Java Swing frontend (connects to DB)

Requirements:
- Java JDK 11+ installed.
- MySQL server installed and running.
- MySQL Connector/J (mysql-connector-java-8.0.xx.jar) downloaded.

Setup & Run:
1. Start your MySQL server (XAMPP, mysql service, etc).
2. Open your MySQL client and run 'dietdb.sql' to create DB and sample user.
3. Edit DBConnection.java: set USER and PASS to your MySQL credentials.
4. Place 'mysql-connector-java-8.0.xx.jar' into this project folder.
5. Compile:
   - On Windows:
     javac -cp .;mysql-connector-java-8.0.xx.jar *.java
   - On macOS/Linux:
     javac -cp .:mysql-connector-java-8.0.xx.jar *.java
6. Run:
   - On Windows:
     java -cp .;mysql-connector-java-8.0.xx.jar MainApp
   - On macOS/Linux:
     java -cp .:mysql-connector-java-8.0.xx.jar MainApp

Notes:
- The app uses a demo user with id=1 (username 'test') by default. You can create additional users in the DB.
- Passwords in the SQL are plaintext for demo only. Use hashing for production.
- If you get a JDBC driver error, ensure the connector jar path is correct and included in the classpath.

Troubleshooting:
- "Communications link failure" -> check DB URL, server running, and port 3306.
- "Access denied" -> check username/password and privileges.
- If you want a login screen, ask me and I'll add it.

Enjoy! 🎯
