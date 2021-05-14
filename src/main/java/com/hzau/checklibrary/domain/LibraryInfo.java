package com.hzau.checklibrary.domain;

import lombok.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class LibraryInfo {

    // BD库的基因名称
    private List<String> baitLibraryName;
    // BD库的基因序列
    private List<String> baitLibrarySeq;
    // AD库的基因名称
    private List<String> preyLibraryName;
    // AD库的基因序列
    private List<String> preyLibrarySeq;

    // AD库的正向基因序列-AD库的基因名称
    private HashMap<String,String> preyLibrarySeqAndName;
    // AD库的基因反向序列-AD库的基因名称
    private HashMap<String,String> preyLibraryReverseSeqAndName;
    // BD库的正向基因序列-BD库的基因名称
    private HashMap<String,String> baitLibrarySeqAndName;
    // BD库的基因反向序列-BD库的基因名称
    private HashMap<String,String> baitLibraryReverseSeqAndName;

    // 无数字输入指定集合大小，默认每个集合都为2000size
    public LibraryInfo() {
        baitLibraryName = new ArrayList<>(2000);
        baitLibrarySeq = new ArrayList<>(2000);
        preyLibraryName = new ArrayList<>(2000);
        preyLibrarySeq = new ArrayList<>(2000);

        preyLibrarySeqAndName = new HashMap<>(2000);
        preyLibraryReverseSeqAndName = new HashMap<>(2000);
        baitLibrarySeqAndName = new HashMap<>(2000);
        baitLibraryReverseSeqAndName = new HashMap<>(2000);
    }
}
