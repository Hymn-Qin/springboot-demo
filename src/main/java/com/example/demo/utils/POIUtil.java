package com.example.demo.utils;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixiaoxin on 2017/12/13.
 */
public class POIUtil {

    public static List<ArrayList<String>> getFileDatas(InputStream is, String fileName, Integer maxRows) throws Exception {
        Workbook wb = (new POIExcelUtil()).validateExcel(fileName, is, maxRows);
        POIExcelUtil poiExcelUtil = new POIExcelUtil();
        List<ArrayList<String>> datas = poiExcelUtil.read(wb);
        is.close();
        return datas;
    }

    public static List<ArrayList<String>> getFileDatas(InputStream is, String fileName, Integer maxRows,int index) throws Exception {
        Workbook wb = (new POIExcelUtil()).validateExcel(fileName, is, maxRows);
        POIExcelUtil poiExcelUtil = new POIExcelUtil();
        List<ArrayList<String>> datas = poiExcelUtil.read2(wb,index);
        is.close();
        return datas;
    }

    public static List<ArrayList<String>> getFileDatas(InputStream is, String fileName) throws Exception {
        Workbook wb = (new POIExcelUtil()).validateExcel(fileName, is, Integer.valueOf(1000));
        POIExcelUtil poiExcelUtil = new POIExcelUtil();
        List<ArrayList<String>> datas = poiExcelUtil.read(wb);
        is.close();
        return datas;
    }
}
