package com.learning_app.user.chathamkulam.Model.StoreModel;

/**
 * Created by Naren on 29-05-2017.
 */
public class StoreSubjectEntity {
    private String subject_name, amount, price_type, free_validity, paid_validity, file,id,sub_no;

    public StoreSubjectEntity(){}

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSub_no() {
        return sub_no;
    }

    public void setSub_no(String sub_no) {
        this.sub_no = sub_no;
    }
}
