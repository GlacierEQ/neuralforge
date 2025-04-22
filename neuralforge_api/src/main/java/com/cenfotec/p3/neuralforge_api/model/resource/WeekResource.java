package com.cenfotec.p3.neuralforge_api.model.resource;

import lombok.Data;

import java.util.List;

@Data
public class WeekResource {
    private String id;
    private String topic;
    private Boolean visible;
    private List<String> contentIds;
}
