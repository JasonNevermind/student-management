package com.example.controller;


import com.example.bean.Course;
import com.example.bean.Sc;
import com.example.bean.ScInfo;
import com.example.bean.Student;
import com.example.mapper.CourseMapper;
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
public class ScController {

    @Autowired
    ScMapper scMapper;

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    StudentMapper studentMapper;

    private static String tempStudentId;
    private static String tempCourseId;
    private static Integer tempGrade;

    ////////////////////////成绩查询////////////////////////////
    @GetMapping("/toQueryGrade")
    public String toQueryGrade() {
        return "sc/query";
    }

    @PostMapping("/queryGrade")
    public String queryGrade(String studentId, Model model) {
        List<String> allStudentId = studentMapper.selectAllStudentId();
        List<String> allScStudentId = scMapper.selectAllStudentId();
        if (!allStudentId.contains(studentId)) {
            model.addAttribute("errorMsg", "学生不存在！");
        } else if (!allScStudentId.contains(studentId)) {
            model.addAttribute("errorMsg", "该学生没有选课！");
        } else {
            model.addAttribute("gradeList", scMapper.selectScInfoById(studentId));
        }
        return "sc/query";
    }

    ////////////////////////选课列表////////////////////////////
    @GetMapping("/scList")
    public String scList(Model model) {
        model.addAttribute("scList", scMapper.selectScList());
        return "sc/list";
    }

    ////////////////////////添加选课////////////////////////////
    @GetMapping("/toInsertSc")
    public String toInsertSc(Model model) {
        List<Student> studentList = studentMapper.selectStudentList();
        model.addAttribute("firstStudent", studentList.get(0));
        studentList.remove(0);
        model.addAttribute("studentList", studentList);
        List<Course> courseList = courseMapper.selectCourseList();
        model.addAttribute("firstCourse", courseList.get(0));
        courseList.remove(0);
        model.addAttribute("courseList", courseList);
        return "sc/insert";
    }

    @PostMapping("/insertSc")
    public String insertSc(Sc sc, Model model) {
        System.out.println(sc);
        List<Student> studentList = studentMapper.selectStudentList();
        List<Course> courseList = courseMapper.selectCourseList();
        List<String> courseIds = scMapper.selectCourseIdById(sc.getStudentId());//该生选过的课程
        Student firstStudent = studentMapper.selectStudentById(sc.getStudentId());
        Course firstCourse = courseMapper.selectCourseById(sc.getCourseId());
        studentList.remove(firstStudent);
        courseList.remove(firstCourse);
        String errorMsg = "";
        if (courseIds.contains(sc.getCourseId())) {
            //学号和课程号这个组合不能重复
            errorMsg = "该条成绩信息已存在！";
        } else if (sc.getGrade() != null && (sc.getGrade() < 0 || sc.getGrade() > 100)) {
            errorMsg = "成绩在0-100之间，请输入正确的成绩！";
        } else {
            scMapper.insertSc(sc);
            model.addAttribute("successMsg", "添加成功！");
            model.addAttribute("scList", scMapper.selectScList());
            return "sc/list";
        }
        model.addAttribute("firstStudent", firstStudent);
        model.addAttribute("studentList", studentList);
        model.addAttribute("courseList", courseList);
        model.addAttribute("firstCourse", firstCourse);
        model.addAttribute("InputGrade", sc.getGrade());
        model.addAttribute("errorMsg", errorMsg);
        return "sc/insert";
    }

    ////////////////////////修改选课////////////////////////////
    @GetMapping("/toUpdateSc/{studentId}/{courseId}")
    public String toUpdateSc(@PathVariable("studentId") String studentId, @PathVariable("courseId") String courseId, Model model) {
        Sc sc = scMapper.selectScById(studentId, courseId);
        tempStudentId = studentId;
        tempCourseId = courseId;
        tempGrade = sc.getGrade();
        List<Student> studentList = studentMapper.selectStudentList();
        List<Course> courseList = courseMapper.selectCourseList();
        Student oldStudent = studentMapper.selectStudentById(studentId);
        Course oldCourse = courseMapper.selectCourseById(courseId);
        studentList.remove(oldStudent);
        courseList.remove(oldCourse);
        model.addAttribute("oldStudent", oldStudent);
        model.addAttribute("oldCourse", oldCourse);
        model.addAttribute("studentList", studentList);
        model.addAttribute("courseList", courseList);
        model.addAttribute("newGrade", tempGrade);
        return "sc/update";
    }

    @PostMapping("/updateSc")
    public String updateSc(Sc sc, Model model) {
        List<String> allStudentId = scMapper.selectAllStudentId();//所有选了课的学生
        allStudentId.remove(sc.getStudentId());
        List<String> allCourseId = scMapper.selectAllCourseId();
        allCourseId.remove(sc.getCourseId());
        List<Student> studentList = studentMapper.selectStudentList();
        List<Course> courseList = courseMapper.selectCourseList();
        Student oldStudent = studentMapper.selectStudentById(sc.getStudentId());
        Course oldCourse = courseMapper.selectCourseById(sc.getCourseId());
        studentList.remove(oldStudent);
        courseList.remove(oldCourse);
        String errorMsg = "";
        if (tempStudentId.equals(sc.getStudentId()) && tempCourseId.equals(sc.getCourseId()) &&
                tempGrade.equals(sc.getGrade())) {
            errorMsg = "没做任何变化！";
        } else {
            if (sc.getGrade() < 0 || sc.getGrade() > 100) {
                errorMsg = "成绩不合法！";
            } else if (allCourseId.contains(sc.getCourseId()) && allStudentId.contains(sc.getStudentId())) {
                errorMsg = "该条成绩信息已存在！";
            } else {
                HashMap<String, Object> map = new HashMap<>();
                map.put("oldStudentId", tempStudentId);
                map.put("oldCourseId", tempCourseId);
                map.put("newStudentId", sc.getStudentId());
                map.put("newCourseId", sc.getCourseId());
                map.put("newGrade", sc.getGrade());
                scMapper.updateSc(map);
                model.addAttribute("successMsg", "修改成功！");
                model.addAttribute("scList", scMapper.selectScList());
                return "sc/list";
            }
        }
        model.addAttribute("oldStudent", oldStudent);
        model.addAttribute("oldCourse", oldCourse);
        model.addAttribute("studentList", studentList);
        model.addAttribute("courseList", courseList);
        model.addAttribute("newGrade", sc.getGrade());
        model.addAttribute("errorMsg", errorMsg);
        return "sc/update";
    }

    ////////////////////////删除选课////////////////////////////
    @GetMapping("/deleteSc/{studentId}/{courseId}")
    public String deleteSc(@PathVariable("studentId") String studentId, @PathVariable("courseId") String courseId
            , Model model) {
        scMapper.deleteScById(studentId, courseId);
        model.addAttribute("successMsg", "删除成功！");
        model.addAttribute("scList", scMapper.selectScList());
        return "sc/list";
    }

    ////////////////////////导出选课////////////////////////////
    @GetMapping("/exportSc")
    public String exportMajor(Model model) throws IOException {
        String fileName = "sc.xlsx";
        String[] titles = {"学号", "姓名", "课程号", "课程名", "学分", "成绩"};
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("选课表");
        XSSFRow titleRow = sheet.createRow(0);
        for (int i = 0; i < titles.length; i++) {
            XSSFCell cell = titleRow.createCell(i);
            cell.setCellValue(titles[i]);
        }
        List<ScInfo> scList = scMapper.selectScList();
        for (int i = 0; i < scList.size(); i++) {
            XSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(scList.get(i).getStudentId());
            row.createCell(1).setCellValue(scList.get(i).getStudentName());
            row.createCell(2).setCellValue(scList.get(i).getCourseId());
            row.createCell(3).setCellValue(scList.get(i).getCourseName());
            row.createCell(4).setCellValue(scList.get(i).getCredit());
            row.createCell(5).setCellValue(scList.get(i).getGrade() + "");//成绩用字符串存储
        }
        FileOutputStream fos = new FileOutputStream(fileName);
        workbook.write(fos);
        workbook.close();
        fos.close();
        model.addAttribute("successMsg", "导出成功，导出的文件名为" + fileName + "！");
        model.addAttribute("scList", scMapper.selectScList());
        return "sc/list";
    }

    ////////////////////////导入选课////////////////////////////
    @GetMapping("/importSc")
    public String importMajor(Model model) throws IOException {
        //导入时只需考虑学号、课程号和成绩
        String fileName = "sc.xlsx";
        FileInputStream fis = new FileInputStream(fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            if (row != null) {
                if (row.getCell(0) != null && row.getCell(2) != null &&
                        row.getCell(5) != null) {
                    String studentId = row.getCell(0).getStringCellValue();
                    String courseId = row.getCell(2).getStringCellValue();
                    Integer grade = Integer.parseInt(row.getCell(5).getStringCellValue());
                    List<String> courseIds = scMapper.selectCourseIdById(studentId);
                    if (!courseIds.contains(courseId)) {
                        scMapper.insertSc(new Sc(studentId, courseId, grade));
                    }
                }
            }
        }
        fis.close();
        workbook.close();
        model.addAttribute("successMsg", "导入成功，导入的文件名为" + fileName + "！");
        model.addAttribute("scList", scMapper.selectScList());
        return "sc/list";
    }
}
