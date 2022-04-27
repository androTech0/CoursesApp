package com.helmy.coursesapp.Classes

import java.util.HashMap

data class UserData(

    var Email: String = "",
    var Name: String = "",
    var Phone: String = "",
    var Password: String = "",
    var Birthdate: String = "",
    var Image: String = "",
    var Kind: String = "",
    var Courses:HashMap<String,HashMap<String,*>> = hashMapOf()

)