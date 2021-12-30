package models

object db_test extends App{
  JBDCBlockingCallsModel.createUser("user","password")
  JBDCBlockingCallsModel.validateUser("user","password")
}
