package models

object RoomsDataModel {
  private var rooms = JBDCBlockingCallsModel.getRooms
  def addRoom(roomName:String): Unit = {
    if(JBDCBlockingCallsModel.addRoom(roomName))
      rooms = rooms + (roomName->Set.empty[String])
  }
  def Rooms: Map[String, Set[String]] = rooms
}
