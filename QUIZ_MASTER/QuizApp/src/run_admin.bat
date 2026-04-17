@echo off
set "LIB_DIR=..\lib"
set "BIN_DIR=..\bin"

start java -cp "%LIB_DIR%\*;%BIN_DIR%" com.quizapp.client.AdminClient
