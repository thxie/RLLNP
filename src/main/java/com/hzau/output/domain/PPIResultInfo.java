package com.hzau.output.domain;

import lombok.Data;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class PPIResultInfo {

    // 最终需要输出到CSV文件中的互作频率对，使用TreeMap集合，是因为想要
    private Map<String,Integer> TotalPPIResult;

    // 输出匹配到最终 CutLength bp 的 数量
    private AtomicLong mapPPIResultCounts;

    public PPIResultInfo(){
        TotalPPIResult = new TreeMap<>();
        mapPPIResultCounts = new AtomicLong(0L);
    }
}
