@echo off
setlocal
set CP=out;LIBRARY\mysql-connector-j-9.4.0.jar
if not exist out mkdir out
if not exist LIBRARY mkdir LIBRARY
javac -d out src\*.java || goto :eof
java -cp "%CP%" Main
