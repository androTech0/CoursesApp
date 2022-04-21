package com.helmy.coursesapp

import java.util.HashMap

data class UserData(
    var first_name: String = "",
    var middle_name: String = "",
    var last_name: String = "",
    var Birthday: String = "",
    var location: String = "",
    var email: String = "",
    var Phone_number: String = "",
    var password: String = "",
    var kind_of_account: String = "",
    var Courses:HashMap<String,HashMap<String,*>> = hashMapOf()

    /*
    hashMapOf("course_id" to hashMapOf("course_progress" to 1,"course_done" to false))

    */
)