package com.accionmfb.omnix.core.util.excel;

import com.accionmfb.omnix.core.annotation.ServiceOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ExcelUtil {

    @ServiceOperation(description = "Reads all records from a perfect excel workbook")
    public static <T> List<T> readAllRecords(File excelFile){
        return new LinkedList<>();
    }
}
