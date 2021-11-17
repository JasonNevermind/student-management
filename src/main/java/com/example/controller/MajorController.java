package com.example.controller;

import com.example.bean.Major;
import com.example.mapper.ClassMapper;
import com.example.mapper.DepartMapper;
import com.example.mapper.MajorMapper;
import org.apache.ibatis.annotations.Param;
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
import java.util.HashMap;
import java.util.List;

@Controller
public class MajorController {

    @Autowired
    DepartMapper departMapper;

    @Autowired
    MajorMapper majorMapper;

    @Autowired
    ClassMapper classMapper;

    private static String tempMajorName;
    private static String tempDepartName;

    ////////////////////////专业列表////////////////////////////
    @GetMapping("/majorList")
    public String majorList(Model model) {
        model.addAttribute("majorList", majorMapper.selectMajorList());
        return "major/list";
    }

    ////////////////////////添加专业////////////////////////////
    @GetMapping("/toInsertMajor")
    public String toInsertMajor(Model model) {
        //查出所有院系信息
        List<String> allDepartName = departMapper.selectAllDepartName();
        model.addAttribute("selectDepartName", allDepartName.get(0));
        allDepartName.remove(0);
        model.addAttribute("allDepartName", allDepartName);
        return "major/insert";
    }

    @PostMapping("/insertMajor")
    public String insertMajor(Major major, Model model) {
        //取出已经存在的专业，判断添加的专业是否已经存在
        List<String> allMajorName = majorMapper.selectAllMajorName();
        List<String> allDepartName = departMapper.selectAllDepartName();
        String errorMsg = "";
        //专业名不能重复
        if (!allMajorName.contains(major.getMajorName())) {
            majorMapper.insertMajor(major);
            model.addAttribute("successMsg", "添加成功！");
            model.addAttribute("majorList", majorMapper.selectMajorList());
            return "major/list";
        } else {
            errorMsg = "专业名已经存在！";
            model.addAttribute("InputMajorName", major.getMajorName());
        }
        model.addAttribute("selectDepartName", major.getDepartName());
        allDepartName.remove(major.getDepartName());
        model.addAttribute("allDepartName", allDepartName);
        model.addAttribute("errorMsg", errorMsg);
        return "major/insert";
    }

    ////////////////////////修改专业////////////////////////////
    @GetMapping("/toUpdateMajor/{oldMajorName}")
    public String toUpdateMajor(@PathVariable("oldMajorName") String oldMajorName, Model model) {
        tempMajorName = oldMajorName;//原来的专业名
        List<String> allDepartName = departMapper.selectAllDepartName();
        tempDepartName = majorMapper.selectMajorByName(oldMajorName).getDepartName();//原来的院系名
        model.addAttribute("selectDepartName", tempDepartName);
        allDepartName.remove(tempDepartName);
        model.addAttribute("allDepartName", allDepartName);
        return "major/update";
    }

    @PostMapping("/updateMajor")
    public String updateMajor(Major major, Model model) {
        List<String> allMajorName = majorMapper.selectAllMajorName();
        List<String> allDepartName = departMapper.selectAllDepartName();
        List<String> allMajorNameByClass = classMapper.selectAllMajorNameByClass();
        allMajorName.remove(major.getMajorName());
        String errorMsg = "";
        if (tempMajorName.equals(major.getMajorName()) && tempDepartName.equals(major.getDepartName())) {
            errorMsg = "没做任何变化！";
        } else {
            if (allMajorNameByClass.contains(tempMajorName) && !tempMajorName.equals(major.getMajorName())) {
                errorMsg = "专业存在相关班级，不能修改专业名！";
            } else if (allMajorName.contains(major.getMajorName())) {
                errorMsg = "专业名已经存在！";
            } else {
                HashMap<String, Object> map = new HashMap<>();
                map.put("oldMajorName", tempMajorName);
                map.put("newMajorName", major.getMajorName());
                map.put("newDepartName", major.getDepartName());
                majorMapper.updateMajor(map);
                model.addAttribute("successMsg", "修改成功！");
                model.addAttribute("majorList", majorMapper.selectMajorList());
                return "major/list";
            }
        }
        model.addAttribute("oldMajorName", major.getMajorName());
        model.addAttribute("selectDepartName", major.getDepartName());
        allDepartName.remove(major.getDepartName());
        model.addAttribute("allDepartName", allDepartName);
        model.addAttribute("errorMsg", errorMsg);
        return "major/update";
    }

    ////////////////////////删除专业////////////////////////////
    @GetMapping("/deleteMajor/{majorName}")
    public String deleteDepart(@PathVariable("majorName") String majorName, Model model) {
        //班级表写好后可判断专业是否存在班级，来决定能否删除专业
        List<String> allMajorNameByClass = classMapper.selectAllMajorNameByClass();
        if (!allMajorNameByClass.contains(majorName)) {
            majorMapper.deleteMajorByName(majorName);
            model.addAttribute("successMsg", "删除成功！");
        } else {
            model.addAttribute("errorMsg", "错误！专业存在相关班级，不能删除专业！");
        }
        model.addAttribute("majorList", majorMapper.selectMajorList());
        return "major/list";
    }

    ////////////////////////导出专业////////////////////////////
    @GetMapping("/exportMajor")
    public String exportMajor(Model model) throws IOException {
        String fileName = "major.xlsx";
        String[] titles = {"专业名", "院系名"};
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("专业表");
        XSSFRow titleRow = sheet.createRow(0);
        for (int i = 0; i < titles.length; i++) {
            XSSFCell cell = titleRow.createCell(i);
            cell.setCellValue(titles[i]);
        }
        List<Major> majorList = majorMapper.selectMajorList();
        for (int i = 0; i < majorList.size(); i++) {
            XSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(majorList.get(i).getMajorName());
            row.createCell(1).setCellValue(majorList.get(i).getDepartName());
        }
        FileOutputStream fos = new FileOutputStream(fileName);
        workbook.write(fos);
        workbook.close();
        fos.close();
        model.addAttribute("successMsg", "导出成功，导出的文件名为" + fileName + "！");
        model.addAttribute("majorList", majorMapper.selectMajorList());
        return "major/list";
    }

    ////////////////////////导入专业////////////////////////////
    @GetMapping("/importMajor")
    public String importMajor(Model model) throws IOException {
        String fileName = "major.xlsx";
        List<String> allMajorName = majorMapper.selectAllMajorName();
        List<String> allDepartName = departMapper.selectAllDepartName();
        FileInputStream fis = new FileInputStream(fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            if (row != null) {
                if (row.getCell(0) != null && row.getCell(1) != null) {
                    String majorName = row.getCell(0).getStringCellValue();
                    String departName = row.getCell(1).getStringCellValue();
                    if (!allMajorName.contains(majorName) && allDepartName.contains(departName)) {
                        majorMapper.insertMajor(new Major(majorName, departName));
                    }
                }
            }
        }
        fis.close();
        workbook.close();
        model.addAttribute("successMsg", "导入成功，导入的文件名为" + fileName + "！");
        model.addAttribute("majorList", majorMapper.selectMajorList());
        return "major/list";
    }
}
