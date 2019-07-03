package com.mdev.revit.utils

import androidx.fragment.app.Fragment

interface NavigationHost{

    fun navigateTo(fragment: Fragment,addToBackStack: Boolean)

}