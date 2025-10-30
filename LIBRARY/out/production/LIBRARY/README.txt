Library Management System (Java Swing + MySQL)
----------------------------------------------
Contents:
- src/                : Java source files
- MySQL/librarydb.sql : SQL script to create database and tables

Setup steps:
1. Install Java (JDK 8+), MySQL, and a Java IDE (IntelliJ/Eclipse/NetBeans).
2. Create the database and tables:
   - Open MySQL shell or Workbench and run: MySQL/librarydb.sql
3. Update DBConnection.java:
   - Replace YOUR_PASSWORD_HERE with your MySQL root password (or create a DB user).
4. Add MySQL Connector/J (mysql-connector-java-8.x.x.jar) to your project's classpath.
5. Import/open the src/ folder in your IDE and run LoginPage.java.
6. First create an account using Signup, then login.

Notes:
- Passwords are stored in plain text for simplicity. For production, hash passwords.
- Feel free to modify and extend BookForm, StudentForm, Issue/Return forms.
