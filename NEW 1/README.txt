DietPlanner - Java Swing + MySQL (JDBC) - with Login/Signup
----------------------------------------------------------

Included files:
- dietdb.sql          -> SQL script: creates database, tables and a sample user (username: test, password: test123)
- DBConnection.java   -> JDBC connection helper (edit USER/PASS as needed)
- User.java           -> User model class
- UserDAO.java        -> User DAO (find/create)
- DietPlan.java       -> Diet plan model class
- DietPlanDAO.java    -> DAO for diet plans (CRUD)
- LoginApp.java       -> Login window (entry point)
- SignupDialog.java   -> Signup dialog used by LoginApp
- MainApp.java        -> Java Swing dashboard (login required)

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
     java -cp .;mysql-connector-java-8.0.xx.jar LoginApp
   - On macOS/Linux:
     java -cp .:mysql-connector-java-8.0.xx.jar LoginApp

Default demo account: username='test', password='test123'

Notes:
- Passwords are stored in plaintext in this demo. For production use, hash passwords (BCrypt, Argon2).
- If you get a JDBC driver error, ensure the connector jar path is correct and included in the classpath.
- If you want an executable JAR, ask me and I'll package instructions and manifest for you.

Enjoy! 🎯
