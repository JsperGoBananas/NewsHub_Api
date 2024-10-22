package com.jl.newshubapi.model.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class RequestModel {
    private String model;
    private List<Message> messages;
    private String type;

}
