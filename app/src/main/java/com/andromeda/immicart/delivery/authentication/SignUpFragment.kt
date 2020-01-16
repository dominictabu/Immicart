package com.andromeda.immicart.delivery.authentication


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.ProductsPageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_sign_up.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SignUpFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadinglayout.visibility = View.GONE
        create_account_btn.visibility = View.VISIBLE

        auth = FirebaseAuth.getInstance()

        create_account_btn.setOnClickListener {
            val email = email_edittxt.text.toString()
            val password = password.text.toString()
            val name = first_name_edittxt.text.toString()

            if (TextUtils.isEmpty(name) && !isEmailValid(email = email) && !isPasswordValid(password)) {
                error_name_edittext.visibility = View.VISIBLE
                error_email_edittext.visibility = View.VISIBLE
                error_password_edittext.visibility = View.VISIBLE

            } else if (TextUtils.isEmpty(name) && !isEmailValid(email = email)) {
                error_name_edittext.visibility = View.VISIBLE
                error_email_edittext.visibility = View.VISIBLE
            } else if (TextUtils.isEmpty(name) && !isPasswordValid(password)) {
                error_name_edittext.visibility = View.VISIBLE
                error_password_edittext.visibility = View.VISIBLE
            } else if (TextUtils.isEmpty(name)) {
                error_name_edittext.visibility = View.VISIBLE

            } else if(!isEmailValid(email = email)) {
                error_email_edittext.visibility = View.VISIBLE


            } else if (!isPasswordValid(password)) {
                error_password_edittext.visibility = View.VISIBLE


            } else {
                error_name_edittext.visibility = View.GONE
                error_email_edittext.visibility = View.GONE
                error_password_edittext.visibility = View.GONE

                signIn(email, password)
            }

        }

    }


    fun signIn(email: String, password: String) {
        loadinglayout.visibility = View.VISIBLE
        create_account_btn.visibility = View.GONE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    val intent : Intent = Intent(activity!!, ProductsPageActivity::class.java)
                    startActivity(intent)

                } else {

                }
            }

    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        return if (TextUtils.isEmpty(password)) {
            false
        } else
            password.length >= 6

    }

    fun createProfile(name: String, email: String, photoURL: String) {
        val db = FirebaseFirestore.getInstance();
        val userUID = FirebaseAuth.getInstance().uid
        val documentPath = "users/$userUID"

        val user = HashMap<String, Any>()
        user.put("name", name)
        user.put("email", email)
        user.put("imageUrl", photoURL)

        db.document(documentPath).set(user).addOnSuccessListener {

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignUpFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
