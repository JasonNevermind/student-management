package com.example.controller;

import com.example.bean.Class;
import com.example.mapper.ClassMapper;
import com.example.mapper.MajorMapper;
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
import java.util.HashMap;
import java.util.List;

@Controller
public class ClassController {

    @Autowired
    MajorMapper majorMapper;

    @Autowired
    ClassMapper classMapper;

    @Autowired
    StudentMapper studentMapper;

    private static String tempClassName;
    private static String tempMajorName;

    ////////////////////////班级列表////////////////////////////
    @GetMapping("/classList")
    public String classList(Model model) {
        model.addAttribute("classList", classMapper.selectClassList());
        return "class/list";
    }

    ////////////////////////添加班级////////////////////////////
    @GetMapping("/toInsertClass")
    public String toInsertClass(Model model) {
        List<String> allMajorName = majorMapper.selectAllMajorName();
        model.addAttribute("selectMajorName", allMajorName.get(0));
        allMajorName.remove(0);
        model.addAttribute("allMajorName", allMajorName);
        return "class/insert";
    }

    @PostMapping("/insertClass")
    public String insertClass(Class cla, Model model) {
        //取出已经存在的班级，判断添加的班级是否已经存在
        List<String> allMajorName = majorMapper.selectAllMajorName();
        List<String> allClassName = classMapper.selectAllClassName();
        String errorMsg = "";
        //班级名不能重复
        if (!allClassName.contains(cla.getClassName())) {
            classMapper.insertClass(cla);
            model.addAttribute("successMsg", "添加成功！");
            model.addAttribute("classList", classMapper.selectClassList());
            return "class/list";
        } else {
            errorMsg = "班级名已经存在！";
            model.addAttribute("InputClassName", cla.getClassName());
            model.addAttribute("selectMajorName", cla.getMajorName());
            allMajorName.remove(cla.getMajorName());
            model.addAttribute("allMajorName", allMajorName);
            model.addAttribute("errorMsg", errorMsg);
            return "class/insert";
        }
    }

    ////////////////////////修改班级////////////////////////////
    @GetMapping("/toUpdateClass/{oldClassName}")
    public String toUpdateCLass(@PathVariable("oldClassName") String oldClassName, Model model) {
        tempClassName = oldClassName;
        List<String> allMajorName = majorMapper.selectAllMajorName();
        tempMajorName = classMapper.selectClassByName(oldClassName).getMajorName();
        model.addAttribute("oldClassName", tempClassName);
        model.addAttribute("selectMajorName", tempMajorName);
        allMajorName.remove(tempMajorName);
        model.addAttribute("allMajorName", allMajorName);
        return "class/update";
    }

    @PostMapping("/updateClass")
    public String updateClass(Class cla, Model model) {
        List<String> allMajorName = majorMapper.selectAllMajorName();
        List<String> allClassName = classMapper.selectAllClassName();
        Class classByName = classMapper.selectClassByName(tempClassName);//原来的班级
        allClassName.remove(tempClassName);//去掉原来所在的班级名
        String errorMsg = "";
        if (tempClassName.equals(cla.getClassName()) && tempMajorName.equals(cla.getMajorName())) {
            errorMsg = "没做任何变化！";
        } else {
            if (classByName.getClassNum() > 0 && !tempClassName.equals(cla.getClassName())) {
                errorMsg = "班级存在学生，不能修改班级名！";
            } else if (allClassName.contains(cla.getClassName())) {
                errorMsg = "班级名已经存在！";
            } else {
                HashMap<String, Object> map = new HashMap<>();
                map.put("oldClassName", tempClassName);
                map.put("newClassName", cla.getClassName());
                map.put("newMajorName", cla.getMajorName());
                classMapper.updateClass(map);
                model.addAttribute("successMsg", "修改成功！");
                model.addAttribute("classList", classMapper.selectClassList());
                return "class/list";
            }
        }
        model.addAttribute("oldClassName", cla.getClassName());
        model.addAttribute("selectMajorName", cla.getMajorName());
        allMajorName.remove(cla.getMajorName());
        model.addAttribute("allMajorName", allMajorName);
        model.addAttribute("errorMsg", errorMsg);
        return "class/update";
    }

    ////////////////////////删除班级////////////////////////////
    @GetMapping("/deleteClass/{className}")
    public String deleteClass(@PathVariable("className") String className, Model model) {
        //判断班级人数是否为0来判断能否删除班级
        Class classByName = classMapper.selectClassByName(className);
        if (classByName.getClassNum() > 0) {
            model.addAttribute("errorMsg", "错误！班级存在学生，不能删除班级！");
        } else {
            classMapper.deleteClassByName(className);
            model.addAttribute("successMsg", "删除成功！");
        }
        model.addAttribute("classList", classMapper.selectClassList());
        return "class/list";
    }

    ////////////////////////导出班级////////////////////////////
    @GetMapping("/exportClass")
    public String exportClass(Model model) throws IOException {
        String fileName = "class.xlsx";
        String[] titles = {"班级名", "专业名", "班级人数"};
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("班级表");
        XSSFRow titleRow = sheet.createRow(0);
        for (int i = 0; i < titles.length; i++) {
            XSSFCell cell = titleRow.createCell(i);
            cell.setCellValue(titles[i]);
        }
        List<Class> classList = classMapper.selectClassList();
        for (int i = 0; i < classList.size(); i++) {
            XSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(classList.get(i).getClassName());
            row.createCell(1).setCellValue(classList.get(i).getMajorName());
            row.createCell(2).setCellValue(classList.get(i).getClassNum());
        }
        FileOutputStream fos = new FileOutputStream(fileName);
        workbook.write(fos);
        workbook.close();
        fos.close();
        model.addAttribute("successMsg", "导出成功，导出的文件名为" + fileName + "！");
        model.addAttribute("classList", classMapper.selectClassList());
        return "class/list";
    }

    ////////////////////////导入班级////////////////////////////
    @GetMapping("/importClass")
    public String importClass(Model model) throws IOException {
        String fileName = "class.xlsx";
        List<String> allClassName = classMapper.selectAllClassName();
        List<String> allMajorName = majorMapper.selectAllMajorName();
        FileInputStream fis = new FileInputStream(fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            if (row != null) {
                //没有考虑班级人数，因为新班级肯定是没有学生的，都为0
                if (row.getCell(0) != null && row.getCell(1) != null) {
                    String className = row.getCell(0).getStringCellValue();
                    String majorName = row.getCell(1).getStringCellValue();
                    if (!allClassName.contains(className) && allMajorName.contains(majorName)) {
                        classMapper.insertClass(new Class(className, majorName));
                    }
                }
            }
        }
        fis.close();
        workbook.close();
        model.addAttribute("successMsg", "导入成功，导入的文件名为" + fileName + "！");
        model.addAttribute("classList", classMapper.selectClassList());
        return "class/list";
    }
}
