package com.tom.spotifygamev3.Utils

object Constants {
    const val BASE_URL = "https://api.spotify.com/v1/"
    const val LASTFM_BASE_URL = "https://ws.audioscrobbler.com/2.0/"
    const val LASTFM_API_KEY = "f504a46e92218226ee4517f47f04d7db"

    const val TEST_PLAYLIST_URI = "1ocd7l0Q4L97N3JHNdMUfD"
    const val TRACKS_URL_PARAMS = "fields=href,next,items(track(album(id,images,name,uri,album_type),artists(id,name,uri),id,name,preview_url,uri))&market=IE"
    const val BICEP_URI = "73A3bLnfnz5BoQjb4gNCga"
    const val ALBUM_GAME_NUM_QUESTIONS = 10
    const val SMALL_IMAGE_SIZE = 60

    const val TOP_50_IRL = "37i9dQZEVXbKM896FDX8L1"
    const val TODAYS_TOP_HITS = "37i9dQZF1DXcBWIGoYBM5M"
    const val GLOBAL_TOP_50 = "37i9dQZEVXbMDoHDwVN2tF"
    const val RAP_CAVIAR = "37i9dQZF1DX0XUsuxWHRQd"
    const val SONGS_CAR = "37i9dQZF1DWWMOmoXKqHTD"
    const val ALL_OUT_00S = "37i9dQZF1DX4o1oenSJRJd"
    const val ROCK_CLASSICS = "37i9dQZF1DWXRqgorJj26U"

    val COMMON_PLAYLISTS = listOf(
        TOP_50_IRL,
        TODAYS_TOP_HITS,
        GLOBAL_TOP_50,
        RAP_CAVIAR,
        SONGS_CAR,
        ALL_OUT_00S,
        ROCK_CLASSICS
    )

    const val ALBUM_GAME_TYPE = "album"
    const val HIGH_LOW_GAME_TYPE = "highlow"

    const val USER_PLAYLISTS_URL_PARAMS = "limit=30"
    const val SIMPLE_PLAYLIST_PARAMS = "fields=id,images,owner,name"
    const val FULL_PLAYLISTS_PARAMS = "fields=id,images,owner,name,tracks"

    val ALPHANUM_REGEX = "[^A-Za-z0-9 ]".toRegex()
}