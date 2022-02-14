package com.example.iscorebridge

import java.util.*
import kotlin.collections.ArrayList


val MESSAGE_READ = 0
val MESSAGE_WRITE = 1

val SENDGAME = 2
val SENDSTART = 3

val STRING = 4
val BYTEARRAY = 5




const val REQUEST_ENABLE_BT = 6
const val BLUETOOTH_PERMISSIONS_GRANTED = 7
const val MESSAGE_CONNECT = 8

const val MESSAGE_CONNECTDEVICE = 9
val MESSAGE_READER_DISCONNECTED = 10
val MESSAGE_WRITER_DISCONNECTED = 11
val MESSAGE_CLIENT_CONNECTED = 12

public var gameInfo : GameInfo = GameInfo(2, GAMEMODE_TEAMS, 10, MOVEMENT_NONE, ArrayList<String>())





val programUUID: UUID = UUID.fromString("096e8f5d-2b31-410a-9cc3-c577003bbfdd")