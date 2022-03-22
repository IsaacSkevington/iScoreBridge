package com.OS3.iscorebridge

import java.util.*


const val MESSAGE_READ = 0
const val MESSAGE_WRITE = 1

const val SENDGAME = 2
const val SENDSTART = 3

const val STRING = 4
const val BYTEARRAY = 5


var clientNumber = 0

const val REQUEST_ENABLE_BT = 6
const val WIFI_PERMISSIONS_GRANTED = 7
const val MESSAGE_CONNECT = 8

const val MESSAGE_CONNECTDEVICE = 9
const val MESSAGE_READER_DISCONNECTED = 10
const val MESSAGE_WRITER_DISCONNECTED = 11
const val MESSAGE_CLIENT_CONNECTED = 12
const val MESSAGE_ONETIMEREADER_DATAAVAILABLE = 13
const val  MESSAGECONNECTEDWRITER = 15
const val MESSAGECONNECTEDHOST = 16
const val MESSAGE_START = 18


const val CREATE_FILE = 19
const val SENDCONNECTIONINFO = 20

const val MESSAGE_CONNECTION_FAILED = 21
const val MESSAGE_DEVICE_ID_CHANGED = 22

public var gameInfo : GameInfo = GameInfo(2, GAMEMODE_TEAMS, 10, MOVEMENT_NONE, ArrayList<String>())

const val HOSTIP = "192.168.49.1"

@Volatile var deviceID : String = ""

val programUUID: UUID = UUID.fromString("096e8f5d-2b31-410a-9cc3-c577003bbfdd")

public const val hostport = 8888
public const val readwriteport1 = 8889
public const val readwriteport2 = 8890
