package com.mullaihub.hiper.http.utils

import com.mullaihub.hiper.http.data.Mixer


fun mix(vararg args: Pair<String, Any>): HashMap<String, Any> {
    return Mixer(*args)
}


