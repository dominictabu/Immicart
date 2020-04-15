package com.andromeda.immicart.delivery.checkout

import android.app.Activity
import android.os.Bundle
import android.text.*
import android.text.style.RelativeSizeSpan
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import com.andromeda.immicart.R

import android.util.Log
import kotlinx.android.synthetic.main.content_drivers_tip.*
import android.content.Intent
import android.widget.*
import com.andromeda.immicart.delivery.pick_up.NavigateToStoreActivity


class DriversTipActivity : AppCompatActivity() {

    private val TAG = "DriversTipActivity"
    private val TIP_AMOUNT = "TIP_AMOUNT"
    private val CURRENT_TIP_AMOUNT = "CURRENT_TIP_AMOUNT"

    var tipAmount: Int = 50

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_drivers_tip)
//        setSupportActionBar(toolbar)

        if(intent.hasExtra(CURRENT_TIP_AMOUNT)) {
            val tip = intent.getIntExtra(CURRENT_TIP_AMOUNT, 50)

            if(tip == 0) {
                radioGroup?.check(R.id.tip_none)

            } else if(tip == 50) {
                radioGroup?.check(R.id.tip_50)

            } else if(tip == 100) {
                radioGroup?.check(R.id.tip_100)

            } else if(tip == 150) {
                radioGroup?.check(R.id.tip_150)

            } else if(tip == 200) {
                radioGroup?.check(R.id.tip_200)

            } else {
                radioGroup?.check(R.id.tip_other_amount)
                val radioButton = radioGroup?.findViewById<RadioButton>(R.id.tip_other_amount)
                radioButton?.text = "Other Amount ($tip)"

            }


        }




        go_back_button?.setOnClickListener {
            finish()
        }

        tip_other_amount?.setOnClickListener {
            showTipDialog()
        }
        radioGroup?.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                Log.d(TAG, "selected ID $checkedId")

                if(checkedId == R.id.tip_other_amount) {

                } else  if(checkedId == R.id.tip_none) {
                    tipAmount = 0

                } else  if(checkedId == R.id.tip_50) {
                    tipAmount = 50

                } else  if(checkedId == R.id.tip_100) {
                    tipAmount = 100

                }else  if(checkedId == R.id.tip_150) {
                    tipAmount = 150

                }else  if(checkedId == R.id.tip_200) {
                    tipAmount = 200

                }
            }
        })

        save_tip_btn?.setOnClickListener {
            val intent = Intent()
            intent.putExtra(TIP_AMOUNT, tipAmount)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

//        save_tip_btn?.setOnClickListener {
//            startActivity(Intent(this, NavigateToStoreActivity::class.java))
//        }

    }

    private var current = ""

    internal fun showTipDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        // ...Irrelevant code for customizing the buttons and title
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.tip_amount_dialog, null)
        dialogBuilder.setView(dialogView)

        val button = dialogView.findViewById<View>(R.id.submit_amount) as Button


        //        ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, 6, 0);// set color
        val amountet = dialogView.findViewById<View>(R.id.amont_edittext) as EditText
        val erroramountet = dialogView.findViewById<View>(R.id.error_txtview) as TextView


        val alertDialog = dialogBuilder.create()
        button.setOnClickListener {
            val amount = amountet.text
            if(TextUtils.isEmpty(amount)) {
                erroramountet.visibility = View.VISIBLE
            }  else {
                val amountInInt = amount.toString().toInt()
                tipAmount = amountInInt
                val radioButton = radioGroup?.findViewById<RadioButton>(R.id.tip_other_amount)
                radioButton?.text = "Other Amount ($amountInInt)"
                alertDialog.dismiss()

            }
        }

        alertDialog.show()
    }


    var  MYCURRENCIES : Map<String, String> = mapOf("KES" to "Ksh")


}
