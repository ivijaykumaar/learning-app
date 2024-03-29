package com.learning_app.user.chathamkulam.Model.ModuleModel;

import java.util.List;

/**
 * Created by User on 6/1/2017.
 */

public class ModuleItems {

    private String module_name;
    private String sem_no, subject_id, sub_no, module_no, topic_no, topic_name, topic_duration, pauseDuration, totalDuration, count;

    private List<TopicItems> topic_details;

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }

    public List<TopicItems> getTopic_details() {
        return topic_details;
    }

    public void setTopic_details(List<TopicItems> topic_details) {
        this.topic_details = topic_details;
    }

    public String getSem_no() {
        return sem_no;
    }

    public void setSem_no(String sem_no) {
        this.sem_no = sem_no;
    }

    public String getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id;
    }

    public String getSub_no() {
        return sub_no;
    }

    public void setSub_no(String sub_no) {
        this.sub_no = sub_no;
    }

    public String getModule_no() {
        return module_no;
    }

    public void setModule_no(String module_no) {
        this.module_no = module_no;
    }

    public String getTopic_no() {
        return topic_no;
    }

    public void setTopic_no(String topic_no) {
        this.topic_no = topic_no;
    }

    public String getTopic_name() {
        return topic_name;
    }

    public void setTopic_name(String topic_name) {
        this.topic_name = topic_name;
    }

    public String getTopic_duration() {
        return topic_duration;
    }

    public void setTopic_duration(String topic_duration) {
        this.topic_duration = topic_duration;
    }

    public String getPauseDuration() {
        return pauseDuration;
    }

    public void setPauseDuration(String pauseDuration) {
        this.pauseDuration = pauseDuration;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
