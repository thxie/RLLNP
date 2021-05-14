package com.hzau.mapping.methods;

import cn.hutool.core.collection.CollUtil;
import com.hzau.Utils;
import com.hzau.options.domain.Arguments;
import com.hzau.checklibrary.domain.LibraryInfo;
import com.hzau.mapping.domain.MappingInfo;
import com.hzau.output.domain.PPIResultInfo;
import java.util.List;

public class MulThreadsForGetPPIResults {

    public static void mulThreads(MappingInfo mappingInfo, Arguments arguments, LibraryInfo libraryInfo, PPIResultInfo ppiResultInfo){
        // 将大的集合拆分成小的集合数组
        List<Object[]>[] subFqSeqArray = Utils.subList(mappingInfo.getFastq_seq(),arguments.getThreadNum());

        // 放空 reads 集合的内存
        mappingInfo.setFastq_seq(CollUtil.sub(mappingInfo.getFastq_seq(),0,0));

        // 创建线程组，并且在线程组中分别创建 AlignmentAndGetPPIResultThead 类；
        Thread[] threads = new Thread[arguments.getThreadNum()];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new AlignmentAndGetPPIResultThead(subFqSeqArray,i,mappingInfo,arguments,libraryInfo,ppiResultInfo);
        }

        // 启动线程组
        for (Thread thread : threads) thread.start();

        // 等待线程组运行完毕
        try {
            for (Thread thread : threads) thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
