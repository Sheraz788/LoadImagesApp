package com.sheraz.loadimagesapp.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager


fun Context.hideKeyboard(){

    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

}