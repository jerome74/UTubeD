package com.wlp.utubed.util


/**
 * BROADCAST
 */

const val BROADCAST_LOGIN = "BROADCAST_9999"
const val BROADCAST_FIND_VIDEOS = "BROADCAST_9998"
const val BROADCAST_DOWNLOAD_VIDEO = "BROADCAST_9997"

const val BROADCAST_RESERVATION_FOUND = "broadcast.reservation"
const val BROADCAST_RESERVATION_OK_1 = "broadcast.reservation.ok.1"


/**
 * URI
 */

//######################BASE_URI####################################
const val BASE_URL : String = "https://utubed.herokuapp.com"
//-------------------------------------------------------------------


const val URI_LOGIN : String = "$BASE_URL/login"
const val URI_FIND_BY_EMAIL : String = "$BASE_URL/api/utubed/profile"
const val URI_FIND_VIDEOS : String = "$BASE_URL/api/utubed/find"
const val URI_DOWNLOAD_VIDEO : String = "$BASE_URL/api/utubed/download"




