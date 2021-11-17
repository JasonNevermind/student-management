package com.example.controller;

import com.example.bean.Ap;
import com.example.bean.Student;
import com.example.mapper.ApMapper;
import com.example.mapper.StudentMapper;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class ApController {

    @Autowired
    StudentMapper studentMapper;

    @Autowired
    ApMapper apMapper;

    ////////////////////////奖惩信息列表////////////////////////////
    @GetMapping("/apList")
    public String classList(Model model) {
        model.addAttribute("apList", apMapper.selectApList());
        return "ap/list";
    }

    ////////////////////////添加奖惩信息////////////////////////////
    @GetMapping("/toInsertAp")
    public String toInsertAp(Model model) {
        List<Student> studentList = studentMapper.selectStudentList();
        model.addAttribute("firstStudent", studentList.get(0));
        studentList.remove(0);
        model.addAttribute("studentList", studentList);
        model.addAttribute("defaultApType", "奖励");
        model.addAttribute("apType", "惩罚");
        model.addAttribute("now", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        return "ap/insert";
    }

    @PostMapping("/insertAp")
    public String insertAp(Ap ap, Model model) {
        //奖惩信息不重复可直接添加
        List<Ap> apList = apMapper.selectApListForSome();
        List<Student> studentList = studentMapper.selectStudentList();
        Student firstStudent = studentMapper.selectStudentById(ap.getStudentId());
        studentList.remove(firstStudent);
        ArrayList<String> apType = new ArrayList<>(Arrays.asList("奖励", "惩罚"));
        apType.remove(ap.getApType());
        if (apList.contains(ap)) {
            model.addAttribute("firstStudent", firstStudent);
            model.addAttribute("studentList", studentList);
            model.addAttribute("defaultApType", ap.getApType());
            model.addAttribute("apType", apType);
            model.addAttribute("now", ap.getApTime());
            model.addAttribute("desc", ap.getApDesc());
            model.addAttribute("errorMsg", "该条信息已存在！");
            return "ap/insert";
        }
        apMapper.insertAp(ap);
        model.addAttribute("successMsg", "添加成功！");
        model.addAttribute("apList", apMapper.selectApList());
        return "ap/list";
    }

    ////////////////////////删除奖惩信息////////////////////////////
    @GetMapping("/deleteAp/{id}")
    public String deleteAp(@PathVariable("id") int id, Model model) {
        apMapper.deleteApById(id);
        model.addAttribute("successMsg", "删除成功！");
        model.addAttribute("apList", apMapper.selectApList());
        return "ap/list";
    }

    ////////////////////////导出奖惩信息////////////////////////////
    @GetMapping("/exportAp")
    public String exportAp(Model model) throws IOException {
        String fileName = "ap.xlsx";
        String[] titles = {"学号", "时间", "类型", "说明"};
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("奖惩信息表");
        XSSFRow titleRow = sheet.createRow(0);
        for (int i = 0; i < titles.length; i++) {
            XSSFCell cell = titleRow.createCell(i);
            cell.setCellValue(titles[i]);
        }
        List<Ap> apList = apMapper.selectApList();
        for (int i = 0; i < apList.size(); i++) {
            XSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(apList.get(i).getStudentId());
            row.createCell(1).setCellValue(apList.get(i).getStudentName());
            row.createCell(2).setCellValue(apList.get(i).getApTime());
            row.createCell(3).setCellValue(apList.get(i).getApType());
            row.createCell(4).setCellValue(apList.get(i).getApDesc());
        }
        FileOutputStream fos = new FileOutputStream(fileName);
        workbook.write(fos);
        workbook.close();
        fos.close();
        model.addAttribute("successMsg", "导出成功，导出的文件名为" + fileName + "！");
        model.addAttribute("apList", apMapper.selectApList());
        return "ap/list";
    }

    ////////////////////////导入奖惩信息////////////////////////////
    @GetMapping("/importAp")
    public String importAp(Model model) throws IOException {
        String fileName = "ap.xlsx";
        List<Ap> apListForSome = apMapper.selectApListForSome();
        FileInputStream fis = new FileInputStream(fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            if (row != null) {
                if (row.getCell(0) != null && row.getCell(2) != null && row.getCell(3) != null
                        && row.getCell(4) != null) {
                    String studentId = row.getCell(0).getStringCellValue();
                    String apTime = row.getCell(2).getStringCellValue();
                    String apType = row.getCell(3).getStringCellValue();
                    String apDesc = row.getCell(4).getStringCellValue();
                    Ap ap = new Ap(studentId, apTime, apType, apDesc);
                    if (!apListForSome.contains(ap)) {
                        apMapper.insertAp(ap);
                    }
                }
            }
        }
        fis.close();
        workbook.close();
        model.addAttribute("successMsg", "导入成功，导入的文件名为" + fileName + "！");
        model.addAttribute("apList", apMapper.selectApList());
        return "ap/list";
    }
}
