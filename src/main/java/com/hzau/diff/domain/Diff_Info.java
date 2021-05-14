package com.hzau.diff.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Diff_Info {
    private Map<String,Integer> file1_PPIResults;
    private Map<String,Integer> file2_PPIResults;

    public Diff_Info(){
        file1_PPIResults = new HashMap<>(2000);
        file2_PPIResults = new HashMap<>(2000);
    }
}
