package com.wlp.utubed.util


/**
 * BROADCAST
 */

const val BROADCAST_LOGIN = "BROADCAST_9999"
const val BROADCAST_FIND_VIDEOS = "BROADCAST_9998"
const val BROADCAST_DOWNLOAD_VIDEO = "BROADCAST_9997"
const val BROADCAST_VOICE = "BROADCAST_9996"
const val BROADCAST_FOLDER = "BROADCAST_9995"
const val BROADCAST_LOGOUT = "BROADCAST_9994"

const val REQUEST_WRITE_PERMISSION = 9999

const val PAYLOAD_DOWNLOAD = "PAYLOAD_DOWNLOAD_9999"
const val PAYLOAD_VOICE = "PAYLOAD_VOICE_9999"

const val BROADCAST_RESERVATION_FOUND = "broadcast.reservation"
const val BROADCAST_RESERVATION_OK_1 = "broadcast.reservation.ok.1"

const val FILE_TYPE_MP3 = "mp3"
const val FILE_TYPE_VIDEO = "mp4"

const val LANGUAGE_IT = "it-IT"
const val LANGUAGE_EN = "en-EN"

/**
 * INTENT
 */

const val INTENT_ID_VIDEO = "INTENT_9999"
const val INTENT_TITLE = "INTENT_9998"
const val INTENT_FOLDER = "INTENT_9997"


/**
 * URI
 */

//######################BASE_URI####################################
const val BASE_URL : String = "https://reactive-utubed-api.herokuapp.com"
//-------------------------------------------------------------------


const val URI_LOGIN : String = "$BASE_URL/reactive/login"
const val URI_SIGNIN : String = "$BASE_URL/api/utubed/signin"
const val URI_FIND_BY_EMAIL : String = "$BASE_URL/reactive/profile"
const val URI_FIND_VIDEOS : String = "$BASE_URL/reactive/find"
const val URI_DOWNLOAD_VIDEO : String = "$BASE_URL/reactive/download"




