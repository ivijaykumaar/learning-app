package com.learning_app.user.chathamkulam.Model.DashboardModel;

import java.util.List;

/**
 * Created by User on 6/1/2017.
 */

public class DashEntityObjects {

    //    Normal Items
    private String country, university, course, semester;
    private List<DashSubjectEntity> subject_details;

    //    Filter Items
    private String subject_id, subject_no, subject_name, subject_cost, trial, duration,
            notes_count, qbank_count, video_count, zip_url, validityTill, progress, status,download_id;

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

    public String getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id;
    }

    public String getSubject_no() {
        return subject_no;
    }

    public void setSubject_no(String subject_no) {
        this.subject_no = subject_no;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getSubject_cost() {
        return subject_cost;
    }

    public void setSubject_cost(String subject_cost) {
        this.subject_cost = subject_cost;
    }

    public String getTrial() {
        return trial;
    }

    public void setTrial(String trial) {
        this.trial = trial;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getNotes_count() {
        return notes_count;
    }

    public void setNotes_count(String notes_count) {
        this.notes_count = notes_count;
    }

    public String getQbank_count() {
        return qbank_count;
    }

    public void setQbank_count(String qbank_count) {
        this.qbank_count = qbank_count;
    }

    public String getVideo_count() {
        return video_count;
    }

    public void setVideo_count(String video_count) {
        this.video_count = video_count;
    }

    public String getZip_url() {
        return zip_url;
    }

    public void setZip_url(String zip_url) {
        this.zip_url = zip_url;
    }

    public String getValidityTill() {
        return validityTill;
    }

    public void setValidityTill(String validityTill) {
        this.validityTill = validityTill;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDownload_id() {
        return download_id;
    }

    public void setDownload_id(String download_id) {
        this.download_id = download_id;
    }
}
