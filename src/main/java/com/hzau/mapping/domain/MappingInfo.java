package com.hzau.mapping.domain;

import lombok.Data;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class MappingInfo {

    /*
        MmeI-ATTL-MmeI 标记序列（66bp）
        MMEI_ATTL_MMEI_5_3是5'-3'端的序列，正向序列；
        MMEI_ATTL_MMEI_3_5是3'-5'端的序列，反向序列；
     */
    public static final String MMEI_ATTL_MMEI_5_3 =
            "GTTGGATAGCGTGCGGGTGCCAGGGCGTGCCCTTGAGTTCTCTCAGTTGGGGGCGTTGACTCCAAC";
    public static final String MMEI_ATTL_MMEI_3_5 =
            "GTTGGAGTCAACGCCCCCAACTGAGAGAACTCAAGGGCACGCCCTGGCACCCGCACGCTATCCAAC";

    // fq的测序文件中reads
    private List<Object[]> fastq_seq;

    // 文件总reads数量
    private int fqFileReadsCounts;

    // 统计能够根据半全局比对(通过阈值)来比对到MmeI-Attl-MmeI序列的。
    private AtomicInteger alignToMarkSeqCounts;

    // 统计fq文件的输入所消耗的时间
    private Long totalTime;

    public MappingInfo() {
        fastq_seq = new LinkedList<>();
        fqFileReadsCounts = 0;
        alignToMarkSeqCounts = new AtomicInteger(0);
        totalTime = 0L;
    }
}

