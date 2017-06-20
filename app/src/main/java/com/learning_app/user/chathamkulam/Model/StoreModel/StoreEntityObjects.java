package com.learning_app.user.chathamkulam.Model.StoreModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by Naren on 29-05-2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class StoreEntityObjects {

    private String country, university, course, sem_no, id;
    private List<StoreSubjectEntity> subject_details;

    public StoreEntityObjects() {
    }

    public List<StoreSubjectEntity> getSubject_details() {
        return subject_details;
    }

    public void setSubject_details(List<StoreSubjectEntity> subject_details) {
        this.subject_details = subject_details;
    }

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

    public String getSem_no() {
        return sem_no;
    }

    public void setSem_no(String sem_no) {
        this.sem_no = sem_no;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}