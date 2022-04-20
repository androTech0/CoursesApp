package com.helmy.coursesapp.LecturerFragments.Course

data class CourseData(
    var CourseId: String = "",
    var CourseName: String = "",
    var CourseImage: String = "",
    var NumberOfVideos:Long = 0,
    var NumberOfStudents:Long = 0,
    var StudentsIDs:List<String> = listOf()
)