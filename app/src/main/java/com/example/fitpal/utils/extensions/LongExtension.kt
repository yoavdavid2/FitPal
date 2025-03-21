package com.example.fitpal.utils.extensions

import com.google.firebase.Timestamp
import java.util.Date

val Long.toFirebaseTimestamp: Timestamp
    get() = Timestamp(Date(this))