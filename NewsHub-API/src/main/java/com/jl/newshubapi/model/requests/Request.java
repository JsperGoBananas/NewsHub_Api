package com.jl.newshubapi.model.requests;

import com.jl.newshubapi.model.entity.Tuple;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Request object

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    private  String url;
    private  Tuple<String, String>[] headers;
    private  String body;

}
