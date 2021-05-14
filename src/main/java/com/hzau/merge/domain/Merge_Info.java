package com.hzau.merge.domain;

import lombok.Data;
import java.util.Map;
import java.util.TreeMap;

@Data
public class Merge_Info {

    private Map<String,Integer> mergePPIResults;

    public Merge_Info(){
        mergePPIResults = new TreeMap<>();
    }
}
