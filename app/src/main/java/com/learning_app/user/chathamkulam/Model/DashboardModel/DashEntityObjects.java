package com.learning_app.user.chathamkulam.Model.DashboardModel;

import java.util.List;

/**
 * Created by User on 6/1/2017.
 */

public class DashEntityObjects {

    private String  country, university, course, semester;
    private List<DashSubjectEntity> subject_details;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public List<DashSubjectEntity> getSubject_details() {
        return subject_details;
    }

    public void setSubject_details(List<DashSubjectEntity> subject_details) {
        this.subject_details = subject_details;
    }
}
