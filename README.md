# RLLNP

#### 介绍

 **RLLNP软件主要获取RLL-Y2H测序数据中的蛋白互作信息。** 


#### 安装教程

1. 下载【RLLNP.jar】至本地，即可运行，无需安装；
2. 运行需要本地环境有Java开发工具包【JDK】或Java运行环境【JRE】（version >= 8）;
3. 使用JAVA虚拟机【JVM】中的【java -jar】命令进行运行即可；


#### 快速使用说明

```
// 1.查看用户文档
java -jar RLLNP.jar
java -jar RLLNP.jar -h
java -jar RLLNP.jar --help
```


```
// 2.查看软件版本
java -jar RLLNP.jar -v
java -jar RLLNP.jar --version
```

```
// 3.检查Prey文库和Bait文库信息
java -jar RLLNP.jar -a Prey.csv -b Bait.csv
```


```
// 4.利用半全局比对算法获取RLL-Y2H测序文件中的PPI信息
java -jar RLLNP.jar -a Prey.csv -b Bait.csv -f sequencing.fastq
java -jar RLLNP.jar -a Prey.csv -b Bait.csv -f sequencing.fastq.gz
java -jar RLLNP.jar -a Prey.csv -b Bait.csv -t 8 -f sequencing.fq  
java -jar RLLNP.jar -a Prey.csv -b Bait.csv -f sequencing.fq.gz -o PPI.csv
```


```
// 5.将多个PPI结果文件合并
java -jar RLLNP.jar -m merge.txt -o Total_PPI_result.csv
```

```
// 6.对比两个PPI结果文件之间的差异
java -jar RLLNP.jar -d PPI_result1.csv PPI_result2.csv
```



#### 实例教程

1. 准备Prey文库信息和Bait文库信息，需要CSV文件（注意尽量不要有中文字符），第一列应为基因名称，第二列应为基因序列 **(注意无需表头)** 。

> 下面以Prey文库文件【Prey.csv】为例：

| CCL11  | ATGAAAGTCTCTGCAGTGCTCCTGTGTCTGCTGTTCACAGCCGCCCTCTGCAGCATCCAGGTGTTGGCTCAGCCAGCTTCTATTCCAACCATCTGCTGCTTTAATGTGTCCAGAAAGAAGATCTCCGTTCAGCGACTGCAGAGCTACAGAAAAATCACGGGCAGCAAATGTCCTCAGAAAGCTGTGATATTCAACACCAAACAGAACAAGAAAATCTGTGTTGACCCCCAGGAGAAGTGGGTCCAGAATGCCATGGAGTACCTGAACCAAAAATTCCAAACTTTAAAGTCATAA |
|--------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| CCL20  | ATGATGTGCAGCAGCAAGAATTTGCTCCTGGCTGCTTTGATGTCAGTGCTCTTGCTCCACCTCTGCAGCAAGTCAGAAGCAGCAAGCAGCTTTGACTGCTGTCTCCGATACACAGAACGAATACTTCACCCCAGTGTTCTTGTGGGCTTCACACAGCAGTTGGCCAATGAAGCCTGTGACATCAATGCAGTCGTCTTTTACACCAGGAGAAAATTAGCTGTGTGTGCAGATCCAAAGAAGAAGTGGGTGAAACAAGCTGTGCACTTGCTCAGTCAAAGAGTCAAGAGGATGTAA |
| ...... | ......                                                                                                                                                                                                                                                                                               |
| S100A8 | ATGCTGACGGATCTGGAGAGTGCCATTAACTCCCTGATTGAAGTGTACCACAACTACTCCCTGCTGAAAGGGAATTACCACGCCGTCTACAGGGATGACTTGAAGAGACTGTTAGAGACAGAGTGTCCTAAGTTTTTGAAGAAAAAGGATGCAGACACTTGGTTCAAAGAGTTGGACATCAATCAGGATGGTGGAATTAACTTCGAGGAGTTCCTCGTGCTGGTGATAAAGGTGGGCCTGGCAGCCCATGAAGACATTCACAAAGAATAG                         |


2.利用RLLNP软件检查Prey文库文件和Bait文库文件中是否存在
                                                                                                                                                                                                                                                                                               
``
java -jar RLLNP.jar -a Prey.csv - b Bait.csv
``


#### 参数说明

![输入图片说明](https://images.gitee.com/uploads/images/2021/0514/163951_48580ddc_7810647.png "屏幕截图.png")

#### 注意事项





