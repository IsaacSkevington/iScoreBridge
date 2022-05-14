package com.OS3.iscorebridge

import java.util.*


const val MESSAGE_READ = 0
const val MESSAGE_WRITE = 1

const val MESSAGE_SEND_GAME = 2
const val MESSAGE_START = 3

const val STRING = 4
const val BYTEARRAY = 5


var clientNumber = 0


const val MESSAGE_READER_DISCONNECTED = 10
const val MESSAGE_WRITER_DISCONNECTED = 11
const val MESSAGE_CLIENT_CONNECTED = 12
const val  MESSAGECONNECTEDWRITER = 15
const val MESSAGE_CONNECTED_HOST = 16

const val SENDCONNECTIONINFO = 20

const val MESSAGE_CONNECTION_FAILED = 21
const val MESSAGE_DEVICE_ID_CHANGED = 22


const val MESSAGE_UPDATE_CLIENT = 25

const val CHECKCLIENTDETAILS = 26

const val PLAYERNOTFOUND = "Player Not Found"

const val  PLAYERLISTFILE = "playerlist.dat"

const val MESSAGE_SEND_DEAL = 29

const val MESSAGE_EDIT_GAME = 31

const val MATCHFINISHED = 32

const val NORTHSOUTH = 0
const val EASTWEST = 1

const val  CHECKSPECTATORDETAILS = 33


var gameInfo : GameInfo = GameInfo( ArrayList(), GAMEMODE_TEAMS, ArrayList(),
    Time(0), MovementSkeleton()
)

const val MESSAGE_CLIENT_DISCONNECTED = 34

const val SENDJOINCOMPLETE = 35
const val REQUESTAUTHORISATION = 36

const val MESSAGE_ROUND_COMPLETE = 37

const val MESSAGE_DIRECTOR_CALL = 40

const val CHANGEINFO = 41

const val HOSTIP = "192.168.49.1"


val programUUID: UUID = UUID.fromString("096e8f5d-2b31-410a-9cc3-c577003bbfdd")

const val HOSTPORT = 8888

