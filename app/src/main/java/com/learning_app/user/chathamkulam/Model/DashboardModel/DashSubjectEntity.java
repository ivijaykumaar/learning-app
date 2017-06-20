package com.learning_app.user.chathamkulam.Model.DashboardModel;

/**
 * Created by User on 6/1/2017.
 */

public class DashSubjectEntity {

    private String subject_name, subject_id,subject_no,free_validity,paid_validity,duration,video_count,notes_count,qbank_count;

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getVideo_count() {
        return video_count;
    }

    public void setVideo_count(String video_count) {
        this.video_count = video_count;
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

    public String getFree_validity() {
        return free_validity;
    }

    public void setFree_validity(String free_validity) {
        this.free_validity = free_validity;
    }

    public String getPaid_validity() {
        return paid_validity;
    }

    public void setPaid_validity(String paid_validity) {
        this.paid_validity = paid_validity;
    }
}
