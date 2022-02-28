package com.OS3.iscorebridge

import java.util.*
import kotlin.collections.ArrayList


val MESSAGE_READ = 0
val MESSAGE_WRITE = 1

val SENDGAME = 2
val SENDSTART = 3

val STRING = 4
val BYTEARRAY = 5


var clientNumber = 0

const val REQUEST_ENABLE_BT = 6
const val WIFI_PERMISSIONS_GRANTED = 7
const val MESSAGE_CONNECT = 8

const val MESSAGE_CONNECTDEVICE = 9
val MESSAGE_READER_DISCONNECTED = 10
val MESSAGE_WRITER_DISCONNECTED = 11
val MESSAGE_CLIENT_CONNECTED = 12
val MESSAGE_ONETIMEREADER_DATAAVAILABLE = 13
const val MESSAGECONNECTED = 14
const val  MESSAGECONNECTEDWRITER = 15
const val MESSAGECONNECTEDREADER = 16
const val MESSAGE_CLIENT_CHANGE = 17
const val MESSAGE_START = 18

public var gameInfo : GameInfo = GameInfo(2, GAMEMODE_TEAMS, 10, MOVEMENT_NONE, ArrayList<String>())



@Volatile var deviceID : String = UUID.randomUUID().toString()

val programUUID: UUID = UUID.fromString("096e8f5d-2b31-410a-9cc3-c577003bbfdd")