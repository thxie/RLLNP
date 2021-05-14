package com.hzau.mapping.methods;


import com.hzau.checklibrary.domain.LibraryInfo;
import com.hzau.mapping.domain.MappingInfo;
import com.hzau.options.domain.Arguments;
import com.hzau.output.domain.PPIResultInfo;
import com.hzau.output.methods.SumAndOutputPPIResultMethods;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AlignmentAndGetPPIResultThead extends Thread{

    private List<Object[]>[] subSeqList;
    private int i;
    private MappingInfo mappingInfo;
    private Arguments arguments;
    private LibraryInfo libraryInfo;
    private PPIResultInfo ppiResultInfo;

    // 通过构造方法，把需要的信息传入
    public AlignmentAndGetPPIResultThead(List<Object[]>[] subSeqList,int i,MappingInfo mappingInfo,
                                         Arguments arguments, LibraryInfo libraryInfo,PPIResultInfo ppiResultInfo){
        this.subSeqList = subSeqList;
        this.i = i;
        this.mappingInfo = mappingInfo;
        this.arguments = arguments;
        this.libraryInfo = libraryInfo;
        this.ppiResultInfo = ppiResultInfo;
    }

    @Override
    public void run() {
        handleAlignmentAndGetPPIResult(subSeqList,i,mappingInfo,arguments,libraryInfo,ppiResultInfo);
    }

    /**
     * 将保存测序reads的集合分解成几个集合，并利用多线程和半全局比对算法来匹配MmeI-ATTL-MmeI序列，
     * 将两端截取为 CutLength bp的两个子字符串；
     */
    private static void handleAlignmentAndGetPPIResult (List<Object[]>[] subSeqList,int i,MappingInfo mappingInfo,
                                                       Arguments arguments,LibraryInfo libraryInfo,PPIResultInfo ppiResultInfo) {
        // 分别对输入进来的子序列进行两次匹配(5_3和3_5)：
        List<String[]> subList_53 = AlignmentAndGetPPIResultMethods.alignmentAndCut(subSeqList[i],arguments,mappingInfo,MappingInfo.MMEI_ATTL_MMEI_5_3);
        List<String[]> subList_35 = AlignmentAndGetPPIResultMethods.alignmentAndCut(subSeqList[i],arguments,mappingInfo,MappingInfo.MMEI_ATTL_MMEI_3_5);

        // 放空待半全局比对集合
        subSeqList[i] = null;

        // 获取最终的蛋白互作网络的结果
        Map<String,Integer> sub_PPI_result = AlignmentAndGetPPIResultMethods.getPPIMappingResult(subList_53,subList_35,libraryInfo,ppiResultInfo);

        // 放空比对后的集合
        subList_53 = null;
        subList_35 = null;

        // 将PPIResult子集输入至汇总的PPIResult中
        SumAndOutputPPIResultMethods.sumSubToTotalPPIResult(sub_PPI_result,ppiResultInfo);

        // 放空PPIResult子集的内存
        sub_PPI_result = null;
    }
}

//        System.out.println(sub_PPI_result);

//    public static void handleAlignmentAndGetPPIResult (List<Object[]> SeqList,MappingInfo mappingInfo,
//                                                       Arguments arguments,LibraryInfo libraryInfo,PPIResultInfo ppiResultInfo) {
//        List<String[]> subList_53 = AlignmentAndGetPPIResultMethods.alignmentAndCut(SeqList,arguments,MappingInfo.MMEI_ATTL_MMEI_5_3);
//        List<String[]> subList_35 = AlignmentAndGetPPIResultMethods.alignmentAndCut(SeqList,arguments,MappingInfo.MMEI_ATTL_MMEI_3_5);
//        mappingInfo.setFastq_seq(CollUtil.sub(mappingInfo.getFastq_seq(),0,0));
//
//        Utils.plusAtomicIntegerInfo(mappingInfo.getAlignToMarkSeqCounts(),subList_53.size() + subList_35.size());
//
//        Map<String,Integer> sub_PPI_result = AlignmentAndGetPPIResultMethods.getPPIMappingResult(subList_53,subList_35,libraryInfo,ppiResultInfo);
//
//        subList_53 = null;
//        subList_35 = null;
//
//        SumAndOutputPPIResultMethods.sumSubToTotalPPIResult(sub_PPI_result,ppiResultInfo);
//
//        // 放空PPIResult子集的内存
//        sub_PPI_result = null;
//    }