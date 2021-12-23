package models

object db_test extends App{
  JBDCBlockingCalls.createUser("user","password")
  JBDCBlockingCalls.validateUser("user","password")
}
