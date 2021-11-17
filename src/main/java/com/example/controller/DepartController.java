package com.example.controller;

import com.example.mapper.DepartMapper;
import com.example.mapper.MajorMapper;
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
public class DepartController {

    @Autowired
    DepartMapper departMapper;

    @Autowired
    MajorMapper majorMapper;

    private static String tempDepartName;//保存院系名

    ////////////////////////院系列表////////////////////////////
    @GetMapping("/departList")
    public String departList(Model model) {
        model.addAttribute("allDepartName", departMapper.selectAllDepartName());
        return "depart/list";
    }

    ////////////////////////添加院系////////////////////////////
    @GetMapping("/toInsertDepart")
    public String toInsertDepart() {
        return "depart/insert";
    }

    @PostMapping("/insertDepart")
    public String insertDepart(String departName, Model model) {
        //取出已经存在的院系名，判断添加的院系名是否已经存在
        List<String> allDepartName = departMapper.selectAllDepartName();
        String errorMsg = "";
        //院系名不能重复
        if (!allDepartName.contains(departName)) {
            departMapper.insertDepart(departName);
            model.addAttribute("successMsg", "添加成功！");
            model.addAttribute("allDepartName", departMapper.selectAllDepartName());
            return "depart/list";
        } else {
            errorMsg = "院系名已经存在！";
            model.addAttribute("InputDepartName", departName);
            model.addAttribute("errorMsg", errorMsg);
            return "depart/insert";
        }
    }

    ////////////////////////修改院系////////////////////////////
    @GetMapping("/toUpdateDepart/{oldDepartName}")
    public String toUpdateDepart(@PathVariable("oldDepartName") String oldDepartName, Model model) {
        tempDepartName = oldDepartName;//查出原来的数据
        model.addAttribute("oldDepartName", oldDepartName);
        return "depart/update";
    }

    @PostMapping("/updateDepart")
    public String updateDepart(String newDepartName, Model model) {
        List<String> allDepartName = departMapper.selectAllDepartName();
        List<String> allDepartNameByMajor = majorMapper.selectAllDepartNameByMajor();//所有专业的院系名
        String errorMsg = "";
        if (tempDepartName.equals(newDepartName)) {
            errorMsg = "没做任何变化！";
        } else {
            //判断院系是否存在相关专业
            if (allDepartNameByMajor.contains(tempDepartName)) {
                errorMsg = "院系存在相关专业，不能修改院系名！";
            } else if (!allDepartName.contains(newDepartName)) {
                HashMap<String, String> map = new HashMap<>();
                map.put("oldDepartName", tempDepartName);
                map.put("newDepartName", newDepartName);
                departMapper.updateDepart(map);
                model.addAttribute("successMsg", "修改成功！");
                model.addAttribute("allDepartName", departMapper.selectAllDepartName());
                return "depart/list";
            } else {
                errorMsg = "院系名已经存在！";
            }
        }
        model.addAttribute("oldDepartName", newDepartName);
        model.addAttribute("errorMsg", errorMsg);
        return "depart/update";
    }

    ////////////////////////删除院系////////////////////////////
    @GetMapping("/deleteDepart/{departName}")
    public String deleteDepart(@PathVariable("departName") String departName, Model model) {
        //取出所有专业的院系名
        List<String> allDepartNameByMajor = majorMapper.selectAllDepartNameByMajor();
        //查看allDepartNameByMajor是否存在要删除的院系，如果存在就不能删除院系 <== 有专业就不能删除院系
        if (!allDepartNameByMajor.contains(departName)) {
            departMapper.deleteDepartByName(departName);
            model.addAttribute("successMsg", "删除成功！");
        } else {
            model.addAttribute("errorMsg", "错误！院系存在相关专业，不能删除院系！");
        }
        model.addAttribute("allDepartName", departMapper.selectAllDepartName());
        return "depart/list";
    }

    ////////////////////////导出院系////////////////////////////
    @GetMapping("/exportDepart")
    public String exportDepart(Model model) throws IOException {
        String fileName = "depart.xlsx";
        ///////////先写入标题栏数据//////////
        String[] titles = {"院系名"};
        //创建文件对象
        XSSFWorkbook workbook = new XSSFWorkbook();
        //创建表对象
        XSSFSheet sheet = workbook.createSheet("院系表");
        //创建标题栏（第一行）  参数为行下标  行下标从0开始
        XSSFRow titleRow = sheet.createRow(0);
        //写入标题栏数据
        for (int i = 0; i < titles.length; i++) {
            //创建单元格
            XSSFCell cell = titleRow.createCell(i);
            //单元格写入数据
            cell.setCellValue(titles[i]);
        }
        //////////写入院系数据/////////
        List<String> allDepartName = departMapper.selectAllDepartName();
        for (int i = 0; i < allDepartName.size(); i++) {
            //创建行 如果是数据的集合 需要遍历
            XSSFRow row = sheet.createRow(i + 1);
            //写入院系数据
            row.createCell(0).setCellValue(allDepartName.get(i));
        }
        //文件保存到本地 参数为要写出的位置
        FileOutputStream fos = new FileOutputStream(fileName);
        workbook.write(fos);
        workbook.close();
        fos.close();
        model.addAttribute("successMsg", "导出成功，导出的文件名为" + fileName + "！");
        model.addAttribute("allDepartName", departMapper.selectAllDepartName());
        return "depart/list";
    }

    ////////////////////////导入院系////////////////////////////
    @GetMapping("/importDepart")
    public String importDepart(Model model) throws IOException {
        String fileName = "depart.xlsx";
        //取出已有数据
        List<String> allDepartName = departMapper.selectAllDepartName();
        //通过流读取Excel文件
        FileInputStream fis = new FileInputStream(fileName);
        //通过poi解析流 HSSFWorkbook 处理流得到的对象中 就封装了Excel文件所有的数据
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        //从文件中获取表对象  getSheetAt通过下标获取
        XSSFSheet sheet = workbook.getSheetAt(0);
        //从表中获取到行数据  从第二行开始 到 最后一行  getLastRowNum() 获取最后一行的下标
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            //通过下标获取行
            XSSFRow row = sheet.getRow(i);
            //从行中获取数据   getNumericCellValue() 获取数字  getStringCellValue 获取String
            if (row != null && row.getCell(0) != null) {
                String name = row.getCell(0).getStringCellValue();
                //将name不重复的院系添加到院系表中
                if (!allDepartName.contains(name)) {
                    departMapper.insertDepart(name);
                }
            }
        }
        fis.close();
        workbook.close();
        model.addAttribute("successMsg", "导入成功，导入的文件名为" + fileName + "！");
        model.addAttribute("allDepartName", departMapper.selectAllDepartName());
        return "depart/list";
    }
}
