package com.cenfotec.p3.neuralforge_api.model.resource;

import lombok.Data;

import java.util.List;

@Data
public class VirtualClassResource {
    private String id;
    private String ownerId;
    private String title;
    private String description;
    private List<WeekResource> weeks;
    private List<VirtualStudentResource> students;
}
