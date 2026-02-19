package com.example.fitpal3.utils.extensions

import com.google.firebase.Timestamp
import java.util.Date

val Long.toFirebaseTimestamp: Timestamp
    get() = Timestamp(Date(this))