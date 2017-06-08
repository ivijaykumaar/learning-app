package com.learning_app.user.chathamkulam.Model.ModuleModel;

import java.util.List;

/**
 * Created by User on 6/1/2017.
 */

public class ModuleItems {

    private String moduleName;
    private List<TopicItems> topicItems;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public List<TopicItems> getTopicItems() {
        return topicItems;
    }

    public void setTopicItems(List<TopicItems> topicItems) {
        this.topicItems = topicItems;
    }
}
