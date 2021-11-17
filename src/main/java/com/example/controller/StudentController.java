package com.example.controller;


import com.example.bean.Student;
import com.example.mapper.ApMapper;
import com.example.mapper.ClassMapper;
import com.example.mapper.ScMapper;
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
import java.util.*;

@Controller
public class StudentController {

    @Autowired
    StudentMapper studentMapper;

    @Autowired
    ClassMapper classMapper;

    @Autowired
    ScMapper scMapper;

    @Autowired
    ApMapper apMapper;

    private static String tempStudentId;
    private static String tempStudentName;
    private static String tempStudentGender;
    private static Integer tempStudentAge;
    private static String tempClassName;

    //学生信息视图
    @GetMapping("/studentInfoList")
    public String studentInfoList(Model model){
        model.addAttribute("studentInfoList",studentMapper.getStudentInfoView());
        return "student/studentInfoList";
    }

    ////////////////////////学生列表////////////////////////////
    @GetMapping("/studentList")
    public String stuList(Model model) {
        model.addAttribute("studentList", studentMapper.selectStudentList());
        return "student/list";
    }

    ////////////////////////添加学生////////////////////////////
    @GetMapping("/toInsertStudent")
    public String toInsertStudent(Model model) {
        List<String> allClassName = classMapper.selectAllClassName();
        model.addAttribute("selectClassName", allClassName.get(0));
        allClassName.remove(0);
        model.addAttribute("allClassName", allClassName);
        model.addAttribute("InputStudentGender", "男");
        model.addAttribute("genderList", "女");
        return "student/insert";
    }

    @PostMapping("/insertStudent")
    public String insertStudent(Student student, Model model) {
        List<String> allStudentId = studentMapper.selectAllStudentId();
        List<String> allClassName = classMapper.selectAllClassName();
        List<String> genderList = new ArrayList<>(Arrays.asList("男", "女"));
        allClassName.remove(student.getClassName());
        String errorMsg = "";
        //年龄在0-120之间
        if (!(student.getStudentAge() > 0 && student.getStudentAge() < 120)) {
            errorMsg = "年龄范围为0-120，请输入正确的年龄";
        } else if (!allStudentId.contains(student.getStudentId())) {
            //学号不能重复
            studentMapper.insertStudent(student);
            model.addAttribute("successMsg", "添加成功！" + student.getClassName() + "人数加1");
            model.addAttribute("studentList", studentMapper.selectStudentList());
            return "student/list";
        } else {
            errorMsg = "学号已存在！";
        }
        model.addAttribute("InputStudentId", student.getStudentId());
        model.addAttribute("InputStudentName", student.getStudentName());
        model.addAttribute("InputStudentAge", student.getStudentAge());
        model.addAttribute("genderList", genderList);
        genderList.remove(student.getStudentGender());
        model.addAttribute("InputStudentGender", student.getStudentGender());
        model.addAttribute("selectClassName", student.getClassName());
        model.addAttribute("allClassName", allClassName);
        model.addAttribute("errorMsg", errorMsg);
        return "student/insert";
    }

    ////////////////////////修改学生////////////////////////////
    @GetMapping("/toUpdateStudent/{oldStudentId}")
    public String toUpdateStudent(@PathVariable("oldStudentId") String oldStudentId, Model model) {
        Student student = studentMapper.selectStudentById(oldStudentId);
        List<String> genderList = new ArrayList<>(Arrays.asList("男", "女"));
        List<String> allClassName = classMapper.selectAllClassName();
        tempStudentId = oldStudentId;
        tempStudentName = student.getStudentName();
        tempStudentGender = student.getStudentGender();
        tempStudentAge = student.getStudentAge();
        tempClassName = student.getClassName();
        allClassName.remove(student.getClassName());
        model.addAttribute("oldStudentId", tempStudentId);
        model.addAttribute("oldStudentName", tempStudentName);
        model.addAttribute("oldStudentGender", tempStudentGender);
        genderList.remove(tempStudentGender);
        model.addAttribute("oldStudentAge", tempStudentAge);
        model.addAttribute("genderList", genderList);
        model.addAttribute("selectClassName", tempClassName);
        model.addAttribute("allClassName", allClassName);
        return "student/update";
    }

    @PostMapping("/updateStudent")
    public String updateStudent(Student student, Model model) {
        List<String> allClassName = classMapper.selectAllClassName();
        List<String> allStudentId = studentMapper.selectAllStudentId();//所有学生学号
        List<String> allScStudentId = scMapper.selectAllStudentId();//所有选了课的学生学号
        List<String> allApStudentId = apMapper.selectAllApStudentId();//所有有奖惩信息的学生学号
        List<String> genderList = new ArrayList<>(Arrays.asList("男", "女"));
        allStudentId.remove(tempStudentId);
        String errorMsg = "";
        if (tempStudentId.equals(student.getStudentId()) && tempStudentName.equals(student.getStudentName())
                && tempStudentGender.equals(student.getStudentGender()) && tempClassName.equals(student.getClassName())
                && tempStudentAge.equals(student.getStudentAge())) {
            errorMsg = "没做任何变化！";
        } else {
            if (!(student.getStudentAge() > 0 && student.getStudentAge() < 120)) {
                errorMsg = "年龄范围为0-120，请输入正确的年龄";
            } else if (allScStudentId.contains(tempStudentId) && !tempStudentId.equals(student.getStudentId())) {
                errorMsg = "该学生存在选课信息，不能修改学生学号！";
            } else if (allApStudentId.contains(tempStudentId) && !tempStudentId.equals(student.getStudentId())) {
                errorMsg = "该学生存在奖惩信息，不能修改学生学号！";
            } else if (allStudentId.contains(student.getStudentId())) {
                errorMsg = "学号已存在";
            } else {
                HashMap<String, Object> map = new HashMap<>();
                map.put("oldStudentId", tempStudentId);
                map.put("newStudentId", student.getStudentId());
                map.put("newStudentName", student.getStudentName());
                map.put("newStudentGender", student.getStudentGender());
                map.put("newStudentAge", student.getStudentAge());
                map.put("newClassName", student.getClassName());
                studentMapper.updateStudent(map);
                model.addAttribute("successMsg", "修改成功！" + tempClassName + "人数减1，" + student.getClassName() + "人数加1");
                model.addAttribute("studentList", studentMapper.selectStudentList());
                return "student/list";
            }
        }
        model.addAttribute("oldStudentId", student.getStudentId());
        model.addAttribute("oldStudentName", student.getStudentName());
        model.addAttribute("oldStudentGender", student.getStudentGender());
        genderList.remove(student.getStudentGender());
        model.addAttribute("genderList", genderList);
        model.addAttribute("oldStudentAge", student.getStudentAge());
        model.addAttribute("selectClassName", student.getClassName());
        allClassName.remove(student.getClassName());
        model.addAttribute("allClassName", allClassName);
        model.addAttribute("errorMsg", errorMsg);
        return "student/update";
    }

    ////////////////////////删除学生////////////////////////////
    @GetMapping("/deleteStudent/{id}")
    public String deleteStudent(@PathVariable("id") String id, Model model) {
        List<String> allScStudentId = scMapper.selectAllStudentId();//所有选了课的学生学号
        List<String> allApStudentId = apMapper.selectAllApStudentId();//所有有奖惩信息的学生学号
        String errorMsg = "";
        if (allScStudentId.contains(id)) {
            errorMsg = "该学生存在选课信息，不能删除学生！";
        } else if (allApStudentId.contains(id)) {
            errorMsg = "该学生存在奖惩信息，不能删除学生！";
        } else {
            model.addAttribute("successMsg", "删除成功！" + studentMapper.selectStudentById(id).getClassName() + "人数减1");
            studentMapper.deleteStudentById(id);
        }
        model.addAttribute("errorMsg", errorMsg);
        model.addAttribute("studentList", studentMapper.selectStudentList());
        return "student/list";
    }

    ////////////////////////导出学生////////////////////////////
    @GetMapping("/exportStudent")
    public String exportMajor(Model model) throws IOException {
        String fileName = "student.xlsx";
        String[] titles = {"学号", "姓名", "性别", "年龄", "班级名"};
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("学生表");
        XSSFRow titleRow = sheet.createRow(0);
        for (int i = 0; i < titles.length; i++) {
            XSSFCell cell = titleRow.createCell(i);
            cell.setCellValue(titles[i]);
        }
        List<Student> studentList = studentMapper.selectStudentList();
        for (int i = 0; i < studentList.size(); i++) {
            XSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(studentList.get(i).getStudentId());
            row.createCell(1).setCellValue(studentList.get(i).getStudentName());
            row.createCell(2).setCellValue(studentList.get(i).getStudentGender());
            row.createCell(3).setCellValue(studentList.get(i).getStudentAge() + "");//用字符串存储
            row.createCell(4).setCellValue(studentList.get(i).getClassName());
        }
        FileOutputStream fos = new FileOutputStream(fileName);
        workbook.write(fos);
        workbook.close();
        fos.close();
        model.addAttribute("successMsg", "导出成功，导出的文件名为" + fileName + "！");
        model.addAttribute("studentList", studentMapper.selectStudentList());
        return "student/list";
    }

    ////////////////////////导入学生////////////////////////////
    @GetMapping("/importStudent")
    public String importMajor(Model model) throws IOException {
        String fileName = "student.xlsx";
        List<String> allStudentId = studentMapper.selectAllStudentId();//所有的学号
        List<String> allClassName = classMapper.selectAllClassName();//所有的班级名
        FileInputStream fis = new FileInputStream(fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            if (row != null) {
                if (row.getCell(0) != null && row.getCell(1) != null &&
                        row.getCell(2) != null && row.getCell(3) != null &&
                        row.getCell(4) != null) {
                    String id = row.getCell(0).getStringCellValue();
                    String name = row.getCell(1).getStringCellValue();
                    String gender = row.getCell(2).getStringCellValue();
                    int age = Integer.parseInt(row.getCell(3).getStringCellValue());
                    String className = row.getCell(4).getStringCellValue();
                    if (!allStudentId.contains(id) && allClassName.contains(className)) {
                        studentMapper.insertStudent(new Student(id, name, gender, age, className));
                    }
                }
            }
        }
        fis.close();
        workbook.close();
        model.addAttribute("successMsg", "导入成功，导入的文件名为" + fileName + "！");
        model.addAttribute("studentList", studentMapper.selectStudentList());
        return "student/list";
    }
}
