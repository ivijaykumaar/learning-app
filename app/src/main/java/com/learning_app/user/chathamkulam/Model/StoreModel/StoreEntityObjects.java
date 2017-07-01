package com.learning_app.user.chathamkulam.Model.StoreModel;

import java.util.List;

/**
 * Created by Naren on 29-05-2017.
 */

public class StoreEntityObjects {

    private String country, university, course, sem_no, id;
    private List<StoreSubjectEntity> subject_details;

    private String subject_name, amount, price_type, free_validity, paid_validity,
            file,sub_no,paid_validity_date,free_validity_date,size,qa_count,file_count,video_count;


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

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPrice_type() {
        return price_type;
    }

    public void setPrice_type(String price_type) {
        this.price_type = price_type;
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

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getSub_no() {
        return sub_no;
    }

    public void setSub_no(String sub_no) {
        this.sub_no = sub_no;
    }

    public String getPaid_validity_date() {
        return paid_validity_date;
    }

    public void setPaid_validity_date(String paid_validity_date) {
        this.paid_validity_date = paid_validity_date;
    }

    public String getFree_validity_date() {
        return free_validity_date;
    }

    public void setFree_validity_date(String free_validity_date) {
        this.free_validity_date = free_validity_date;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getQa_count() {
        return qa_count;
    }

    public void setQa_count(String qa_count) {
        this.qa_count = qa_count;
    }

    public String getFile_count() {
        return file_count;
    }

    public void setFile_count(String file_count) {
        this.file_count = file_count;
    }

    public String getVideo_count() {
        return video_count;
    }

    public void setVideo_count(String video_count) {
        this.video_count = video_count;
    }
}