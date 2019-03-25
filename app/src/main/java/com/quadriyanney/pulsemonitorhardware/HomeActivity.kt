package com.quadriyanney.pulsemonitorhardware

import android.app.Activity
import android.os.Bundle
import com.google.android.things.contrib.driver.adc.mcp300x.Mcp300x
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*

private const val SPI_DEVICE_NAME = "SPI3.0"
private const val MEASUREMENTS = "measurements"
private const val KEY = "key"
private const val MEASURE = "measure"

class HomeActivity : Activity() {

    private lateinit var driver: Mcp300x
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        databaseReference = FirebaseDatabase.getInstance().reference

        driver = Mcp300x(SPI_DEVICE_NAME, Mcp300x.Configuration.MCP3008)

        databaseReference.child(MEASURE).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value == true) {
                    val key = databaseReference.child(MEASUREMENTS).push().key
                    val pulse = driver.readSingleEndedInput(0) / 6.5

                    databaseReference.child(MEASUREMENTS).child(key.toString())
                        .setValue(Measurement(pulse.toInt(), System.currentTimeMillis()))
                    databaseReference.child(KEY).setValue(key.toString())
                    databaseReference.child(MEASURE).setValue(false)
                }
            }

            override fun onCancelled(p0: DatabaseError) { }
        })
    }

}
