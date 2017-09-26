package com.learning_app.user.chathamkulam.Model.StoreModel;

import java.util.List;

/**
 * Created by Naren on 29-05-2017.
 */

public class StoreEntityObjects {

    private String country, university, course, sem_no, id;
    private List<StoreSubjectEntity> subject_details;

    private String subject_name, sub_no, sub_cost, trial, file, size, file_count, qa_count, video_count, url,expiry_date;

    public StoreEntityObjects() {
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

    public List<StoreSubjectEntity> getSubject_details() {
        return subject_details;
    }

    public void setSubject_details(List<StoreSubjectEntity> subject_details) {
        this.subject_details = subject_details;
    }


    //    Search getters and setters
    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getSub_no() {
        return sub_no;
    }

    public void setSub_no(String sub_no) {
        this.sub_no = sub_no;
    }

    public String getSub_cost() {
        return sub_cost;
    }

    public void setSub_cost(String sub_cost) {
        this.sub_cost = sub_cost;
    }

    public String getTrial() {
        return trial;
    }

    public void setTrial(String trial) {
        this.trial = trial;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFile_count() {
        return file_count;
    }

    public void setFile_count(String file_count) {
        this.file_count = file_count;
    }

    public String getQa_count() {
        return qa_count;
    }

    public void setQa_count(String qa_count) {
        this.qa_count = qa_count;
    }

    public String getVideo_count() {
        return video_count;
    }

    public void setVideo_count(String video_count) {
        this.video_count = video_count;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }
}