package com.andromeda.immicart.delivery.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.andromeda.immicart.R
import kotlinx.android.synthetic.main.activity_sort.*
import android.widget.RadioButton




class SortActivity : AppCompatActivity() {


    val TAG : String = "SortActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sort)

        val sortViewModel : SortViewModel = ViewModelProviders.of(this).get(SortViewModel::class.java)


        sortViewModel.currentSortingOption.observe(this, Observer {
            Log.d(TAG, "currentSortingOption ID $it")
            radioGroup?.check(it)

        })

        if (radioGroup.getCheckedRadioButtonId() == -1)
            {
                // no radio buttons are checked
                (radioGroup.getChildAt(0) as RadioButton).isChecked = true
            }
            else {
            // one of the radio buttons is checked

        }


        radioGroup?.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                Log.d(TAG, "selected ID $checkedId")

                sortViewModel.setCurrentSortingOption(checkedId)
            }

        })


        cancel_action?.setOnClickListener {
            finish()
        }

        reset_action?.setOnClickListener {

        }
    }
}
