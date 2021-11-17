package com.example.controller;

import com.example.bean.Course;
import com.example.mapper.CourseMapper;
import com.example.mapper.ScMapper;
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
public class CourseController {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    ScMapper scMapper;

    private static String tempCourseId;
    private static String tempCourseName;
    private static Integer tempCourseCredit;

    ////////////////////////课程列表////////////////////////////
    @GetMapping("/courseList")
    public String courseList(Model model) {
        model.addAttribute("courseList", courseMapper.selectCourseList());
        return "course/list";
    }

    ////////////////////////添加课程////////////////////////////
    @GetMapping("/toInsertCourse")
    public String toInsertCourse() {
        return "course/insert";
    }

    @PostMapping("/insertCourse")
    public String insertCourse(Course course, Model model) {
        List<String> allCourseId = courseMapper.selectAllCourseId();
        String errorMsg = "";
        //学分大于等于0
        if (course.getCredit() < 0) {
            errorMsg = "学分要大于等于0，请输入正确的学分";
        } else if (!allCourseId.contains(course.getCourseId())) {
            //课程号不能重复
            courseMapper.insertCourse(course);
            model.addAttribute("successMsg", "添加成功！");
            model.addAttribute("courseList", courseMapper.selectCourseList());
            return "course/list";
        } else {
            errorMsg = "课程号已存在！";
        }
        model.addAttribute("InputCourseId", course.getCourseId());
        model.addAttribute("InputCourseName", course.getCourseName());
        model.addAttribute("InputCourseCredit", course.getCredit());
        model.addAttribute("errorMsg", errorMsg);
        return "course/insert";
    }

    ////////////////////////修改课程////////////////////////////
    @GetMapping("/toUpdateCourse/{oldCourseId}")
    public String toUpdateCourseList(@PathVariable("oldCourseId") String oldCourseId, Model model) {
        Course course = courseMapper.selectCourseById(oldCourseId);
        tempCourseId = oldCourseId;
        tempCourseName = course.getCourseName();
        tempCourseCredit = course.getCredit();
        model.addAttribute("oldCourseId", tempCourseId);
        model.addAttribute("oldCourseName", tempCourseName);
        model.addAttribute("oldCourseCredit", tempCourseCredit);
        return "course/update";
    }

    @PostMapping("/updateCourse")
    public String updateCourse(Course course, Model model) {
        List<String> allCourseId = courseMapper.selectAllCourseId();
        List<String> allScCourseId = scMapper.selectAllCourseId();//所有被选的课程号
        allCourseId.remove(tempCourseId);
        String errorMsg = "";
        if (tempCourseName.equals(course.getCourseName()) && tempCourseCredit.equals(course.getCredit()) &&
                tempCourseId.equals(course.getCourseId())) {
            errorMsg = "没做任何变化！";
        } else {
            //学分大于等于0
            if (course.getCredit() < 0) {
                errorMsg = "学分要大于等于0，请输入正确的学分";
            } else if (allScCourseId.contains(tempCourseId) && !tempCourseId.equals(course.getCourseId())) {
                errorMsg = "有学生选了这门课，不能修改课程号！";
            } else if (allCourseId.contains(course.getCourseId())) {
                errorMsg = "课程号已经存在！";
            } else {
                HashMap<String, Object> map = new HashMap<>();
                map.put("oldCourseId", tempCourseId);
                map.put("newCourseId", course.getCourseId());
                map.put("newCourseName", course.getCourseName());
                map.put("newCourseCredit", course.getCredit());
                courseMapper.updateCourse(map);
                model.addAttribute("successMsg", "修改成功！");
                model.addAttribute("courseList", courseMapper.selectCourseList());
                return "course/list";
            }
        }
        model.addAttribute("oldCourseId", course.getCourseId());
        model.addAttribute("oldCourseName", course.getCourseName());
        model.addAttribute("oldCourseCredit", course.getCredit());
        model.addAttribute("errorMsg", errorMsg);
        return "course/update";
    }

    ////////////////////////删除课程////////////////////////////
    @GetMapping("/deleteCourse/{id}")
    public String deleteCourse(@PathVariable("id") String id, Model model) {
        //选课表有人选的话删不掉
        List<String> allCourseId = scMapper.selectAllCourseId();
        if (allCourseId.contains(id)) {
            model.addAttribute("errorMsg", "有学生选了这门课，不能删除！");
        } else {
            courseMapper.deleteCourseById(id);
            model.addAttribute("successMsg", "删除成功！");
        }
        model.addAttribute("courseList", courseMapper.selectCourseList());
        return "course/list";
    }

    ////////////////////////导出课程////////////////////////////
    @GetMapping("/exportCourse")
    public String exportMajor(Model model) throws IOException {
        String fileName = "course.xlsx";
        String[] titles = {"课程号", "课程名", "学分"};
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("课程表");
        XSSFRow titleRow = sheet.createRow(0);
        for (int i = 0; i < titles.length; i++) {
            XSSFCell cell = titleRow.createCell(i);
            cell.setCellValue(titles[i]);
        }
        List<Course> courseList = courseMapper.selectCourseList();
        for (int i = 0; i < courseList.size(); i++) {
            XSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(courseList.get(i).getCourseId());
            row.createCell(1).setCellValue(courseList.get(i).getCourseName());
            row.createCell(2).setCellValue(courseList.get(i).getCredit() + "");//用字符串存储
        }
        FileOutputStream fos = new FileOutputStream(fileName);
        workbook.write(fos);
        workbook.close();
        fos.close();
        model.addAttribute("successMsg", "导出成功，导出的文件名为" + fileName + "！");
        model.addAttribute("courseList", courseMapper.selectCourseList());
        return "course/list";
    }

    ////////////////////////导入课程////////////////////////////
    @GetMapping("/importCourse")
    public String importMajor(Model model) throws IOException {
        String fileName = "course.xlsx";
        List<String> allCourseId = courseMapper.selectAllCourseId();
        FileInputStream fis = new FileInputStream(fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            if (row != null) {
                if (row.getCell(0) != null && row.getCell(1) != null &&
                        row.getCell(2) != null) {
                    String courseId = row.getCell(0).getStringCellValue();
                    String courseName = row.getCell(1).getStringCellValue();
                    int credit = Integer.parseInt(row.getCell(2).getStringCellValue());
                    if (!allCourseId.contains(courseId)) {
                        courseMapper.insertCourse(new Course(courseId, courseName, credit));
                    }
                }
            }
        }
        fis.close();
        workbook.close();
        model.addAttribute("successMsg", "导入成功，导入的文件名为" + fileName + "！");
        model.addAttribute("courseList", courseMapper.selectCourseList());
        return "course/list";
    }
}
