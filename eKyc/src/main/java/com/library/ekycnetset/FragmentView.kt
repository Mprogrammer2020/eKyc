package com.library.ekycnetset

import androidx.fragment.app.Fragment

interface FragmentView {

     fun getCurrentFragment(): Fragment

     fun setTitle(): String

}