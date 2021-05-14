package com.hzau;

public class Constant {

    public static final String VERSION_INFO =
                  "\n\t---------------------------------------\n" +
                    "\t|      Welcome to RLLNP software      |\n" +
                    "\t---------------------------------------\n" +
                    "\t|           Version = 1.0.0           |\n" +
                    "\t---------------------------------------\n";

    public static final String USAGE_INFO =
            "\n" +
            "   ------------------------------------------------\n"+
            "   |  The options for RLLNP software as follows:  |\n" +
            "   ------------------------------------------------\n" + "\n";

    public static final String WELCOME_INFO =
        "\n" +
                "\t██████╗ ██╗     ██╗     ███╗   ██╗██████╗ \n" +
                "\t██╔══██╗██║     ██║     ████╗  ██║██╔══██╗\n" +
                "\t██████╔╝██║     ██║     ██╔██╗ ██║██████╔╝\n" +
                "\t██╔══██╗██║     ██║     ██║╚██╗██║██╔═══╝ \n" +
                "\t██║  ██║███████╗███████╗██║ ╚████║██║     \n" +
                "\t╚═╝  ╚═╝╚══════╝╚══════╝╚═╝  ╚═══╝╚═╝     \n";

    public static final String MERGE_INFO =
            "\n--------------------- Merge Information ---------------------\n";

    public static final String MERGE_OUTPUT_PATH = "Output_File_Path:{}\n";



    public static final String DIFF_INFO =
            "\n---------------------- Diff Information ---------------------\n";

    public static final String DIFF_RIGHT =
            "【These CSV files are totally same!】\n";

    public static final String DIFF_ERROR =
            "【Error】: There some different PPIs in your double CSV files:\n{}\n";



    public static final String LIBRARY_CHECK_INFO =
            "\n------------------ Library Check Information ----------------\n";

    public static final String LIBRARY_ERROR_INFO =
            "【Error】: The prey and bait library must be together for the option -f/--fastq!\n";



    public static final String ARGUMENTS_INFO =
            "-------------------- Options Information --------------------\n" +
                    "\nPrey_Library_File_Path: {} " +
                    "\nBait_Library_File_Path: {} " +
                    "\nFastq_File_Path: {} " +
                    "\nOutput_File_Path: {}" +
                    "\nThread_Nums: {} " +
                    "\nNot_Match_Nums: {} " +
                    "\nCut_length_Nums: {}";



    public static final String  ALIGNMENT_INFO =
            "\n------------------- Alignment Information ------------------\n";

    public static final String ALIGNMENT_PROCESS_INFO =
            "Alignment has been solved in {}% !";


    public static final String STATISTICS_INFO =
            "\n------------------- Statistics Information -------------------\n" +
                    "\nTotal_Time: {}" +
                    "\nTotal_Reads_Counts: {}" +
                    "\nReads_Align_To_MmeI-ATTL-MmeI_Counts: {}"+
                    "\nReads_Unique_Mapping_To_Library_Counts: {}\n";

                    public static final String FINAL =
            "-------------------------------------------------------------\n";
}

//    public static final String  PREY_LIBRARY_OUTPUT_53 =
//            "\nPrey（5‘-3’）[size:{}]\n{}";
//
//    public static final String  PREY_LIBRARY_OUTPUT_35 =
//            "\nPrey（3‘-5’）[size:{}]\n{}";
//
//    public static final String  BAIT_LIBRARY_OUTPUT_53 =
//            "\nBait（5‘-3’）[size:{}]\n{}";
//
//    public static final String  BAIT_LIBRARY_OUTPUT_35 =
//            "\nBait（3‘-5’）[size:{}]\n{}";


//    public static final String  ALIGNMENT_HAS_MATCHED =
//            "3'-5':{} ,this seq has been matched in 5'-3' pattern!";
//
//    public static final String  ALIGNMENT_SCORES_NOT_ENOUGH =
//            "{}{}--[Scores：{}],score is not enough！";
//
//    public static final String  ALIGNMENT_RIGHT =
//            "{}{}--{}[Scores：{}]";
//
//    public static final String  ALIGNMENT_MARK_SEQ_TOO_SHORT =
//            "{}{}--[Scores：{}],the mark seq is too short！";


//    public static final String  PPI_INFO =
//            "\n----------------------- PPI Information ----------------------\n";

//    public static final String PPI_RIGHT_53 =
//            "5'-3':{}--{},[BD:{},AD:{}]";
//
//    public static final String PPI_RIGHT_35 =
//            "3'-5':{}--{},[AD:{},BD:{}]";
//
//    public static final String PPI_NOT_MATCH_53 =
//            "5'-3':{}--{},unique matching failed；";
//
//    public static final String PPI_NOT_MATCH_35 =
//            "3'-5':{}--{},unique matching failed；";



//                    "\nAlignment_Cut_Time: {} ms" +
//                    "\nGet_PPI_Result_Time: {} ms" +