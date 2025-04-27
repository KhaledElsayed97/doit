package dev.khaled.doit.util

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun String.isValidEmail() =
    isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun View.hide(){
    visibility = View.GONE
}

fun Fragment.snackbar(msg: String){
    Snackbar.make(requireView(),msg, Snackbar.LENGTH_LONG).show()
}

fun View.show(){
    visibility = View.VISIBLE
}