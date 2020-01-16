package com.andromeda.immicart.delivery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import com.andromeda.immicart.R
import kotlinx.android.synthetic.main.activity_review.*

class ReviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        val reviewHeader = findViewById<TextView>(R.id.reviewHeader)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)


//TODO I didnt find where the supermarkets are stored
        reviewHeader.text = "How was your experience at "+ "Supermarket name"

//        The value gotten from rating....to be stored in db
        val ratingValue = ratingBar.rating.toString()


    }

    fun saveReview(){
        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener{view ->
//            Use this review text and store in db?
            val reviewText = findViewById<EditText>(R.id.reviewText)



        }

    }
}
