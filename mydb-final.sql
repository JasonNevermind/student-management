CREATE DATABASE IF NOT EXISTS `mydb` /*!40100 DEFAULT CHARACTER SET utf8 */; 
USE `mydb`;
-- 院系表
CREATE TABLE `depart` (
  `depart_name` varchar(30) NOT NULL COMMENT '院系名',
  PRIMARY KEY (`depart_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='院系表';

-- 专业表
CREATE TABLE `major` (
  `major_name` varchar(30) NOT NULL COMMENT '专业名',
  `depart_name` varchar(30) NOT NULL COMMENT '院系名',
  PRIMARY KEY (`major_name`),
  FOREIGN KEY (`depart_name`) REFERENCES `depart` (`depart_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='专业表';

-- 班级表
CREATE TABLE `class` (
  `class_name` varchar(30) NOT NULL COMMENT '班级名',
  `major_name` varchar(30) NOT NULL COMMENT '专业名',
  `class_num` int(11) DEFAULT 0 COMMENT '班级人数',
  PRIMARY KEY (`class_name`),
  FOREIGN KEY (`major_name`) REFERENCES `major` (`major_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='班级表';

-- 学生表
CREATE TABLE `student` (
  `stu_id` CHAR(10) NOT NULL COMMENT '学号',
  `stu_name` VARCHAR(30) NOT NULL COMMENT '姓名',
  `stu_gender` CHAR(1) NOT NULL COMMENT '性别' CHECK(`stu_gender`='男' OR `stu_gender`='女'),
  `stu_age` INT(11) NOT NULL COMMENT '年龄',
  `class_name` VARCHAR(30) DEFAULT NULL COMMENT '班级名',
  PRIMARY KEY (`stu_id`),
  FOREIGN KEY (`class_name`) REFERENCES `class` (`class_name`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='学生表';

-- 课程表
CREATE TABLE `course` (
  `course_id` varchar(30) NOT NULL COMMENT '课程号',
  `course_name` varchar(30) NOT NULL COMMENT '课程名',
  `credit` int(11) NOT NULL COMMENT '学分',
  PRIMARY KEY (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='课程表';

-- 选课表
CREATE TABLE `sc` (
  `stu_id` char(10) NOT NULL COMMENT '学号',
  `course_id` varchar(30) NOT NULL COMMENT '课程号',
  `grade` int(11) DEFAULT NULL COMMENT '成绩',
  PRIMARY KEY (`stu_id`,`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='选课表';

-- 奖惩表
CREATE TABLE `ap` (
  `ap_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `stu_id` char(10) NOT NULL COMMENT '学号',
  `ap_time` date NOT NULL COMMENT '时间',
  `ap_type` char(2) NOT NULL COMMENT '类别(奖励或处罚)' CHECK(`ap_type`='奖励' OR `ap_type`='处罚'),
  `ap_desc` varchar(100) NOT NULL COMMENT '获奖说明或处罚说明',
  PRIMARY KEY (`ap_id`),
  FOREIGN KEY (`stu_id`) REFERENCES `student` (`stu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='奖惩表';

insert into depart(depart_name) values ('计算机与信息系'),('机械工程系'),('能源化工系');

insert into major(major_name,depart_name) values
('物联网工程','计算机与信息系'),
('计算机科学与技术','计算机与信息系'),
('自动化','机械工程系'),
('应用化学','能源化工系');

insert into class(class_name,major_name) values
('物联网18-2班','物联网工程'),
('物联网19-2班','物联网工程'),
('计算机18-1班','计算机科学与技术'),
('计算机20-1班','计算机科学与技术'),
('自动化18-1班','自动化'),
('应化18-1班','应用化学'),
('应化19-1班','应用化学');

insert into student(stu_id,stu_name,stu_gender,stu_age,class_name) values
('2018001219','习龙梅','女',20,'物联网18-2班'),
('2018225469','林宾','男',18,'计算机18-1班'),
('2018547095','林俊明','男',21,'物联网18-2班'),
('2019552526','罗永','男',20,'物联网19-2班'),
('2018654144','冯玄雅','女',18,'应化18-1班'),
('2020024100','陈沁','女',19,'计算机20-1班'),
('2018025494','王飞宇','男',20,'自动化18-1班'),
('2019224408','郁宏盛','男',22,'应化19-1班'),
('2020569894','孙盈','女',21,'计算机20-1班'),
('2018922397','陈芝兰','女',19,'自动化18-1班'),
('2018777892','许菡','女',20,'物联网18-2班');

insert into course(course_id,course_name,credit) values
('123456','数学分析 (一)',4),
('654321','计算机图形学基础',2),
('123654','机械设计基础',3),
('321456','化学基础（整合科学）',4),
('456789','计算机网络概论',3),
('987654','分析化学',3),
('123789','计算数学',3);

insert into sc(stu_id,course_id,grade) values
('2018001219','456789',81),
('2018777892','123456',59),
('2020024100','654321',74),
('2018922397','123654',65),
('2020569894','456789',60),
('2018654144','123789',90);

insert into ap(stu_id,ap_time,ap_type,ap_desc) values
('2020569894','2021-05-12','奖励','一等奖学金'),
('2018654144','2020-12-20','处罚','夜不归宿'),
('2019224408','2021-09-13','奖励','优秀三好学生'),
('2018001219','2019-10-11','奖励','优秀共青团员'),
('2018777892','2021-03-21','处罚','考试作弊');

update class set class_num=3 where class_name='物联网18-2班';
update class set class_num=1 where class_name='物联网19-2班';
update class set class_num=1 where class_name='应化18-1班';
update class set class_num=1 where class_name='应化19-1班';
update class set class_num=2 where class_name='自动化18-1班';
update class set class_num=1 where class_name='计算机18-1班';
update class set class_num=2 where class_name='计算机20-1班';

-- 创建视图查询各个学生的学号、姓名、班级、专业、院系
create view stu_info
as
SELECT stu_id, stu_name, c.class_name, m.major_name, d.depart_name
FROM student s,
     class c,
     major m,
     depart d
WHERE s.class_name = c.class_name
  and c.major_name = m.major_name
  and m.depart_name = d.depart_name;
-- 查询视图
-- select * from stu_info  

-- 创建存储过程查询指定学生的成绩单等
-- 默认情况下，创建存储过程会报错，这是因为默认的分隔符是;，而select里已经有了一个分隔符，换一下分隔符就不会报错了
delimiter //
create procedure stu_grade(in id char(10))
begin
    SELECT s.stu_id, stu_name, sc.course_id, course_name, credit, grade
    FROM student s,
         course c,
         sc
    WHERE s.stu_id = id
      and s.stu_id = sc.stu_id
      and c.course_id = sc.course_id;
end//
DELIMITER ;
-- 调用存储过程
-- call stu_grade('2018001219')

-- 创建触发器当增加、删除学生和修改学生班级信息时自动修改相应班级学生人数
create trigger tri_insert
after insert
on student
for each row
update class
set class_num=class_num + 1
WHERE new.class_name = class_name;
-- 测试：添加⼀个学⽣信息，触发器执⾏了⼀次
-- INSERT INTO student(stu_id, stu_name, stu_gender, stu_age, class_name)
-- VALUES ('2018122319', '白双儿', '女', 19, '物联网19-2班')

create trigger tri_delete
after delete
on student
for each row
update class
set class_num=class_num - 1
WHERE old.class_name = class_name;
-- 测试：删除⼀个学⽣信息，触发器执⾏了⼀次
-- delete
-- from student
-- WHERE stu_id = '2018025494';

DELIMITER //
CREATE TRIGGER tri_update
    AFTER UPDATE
    ON student
    FOR EACH ROW
BEGIN
    UPDATE class SET class_num=class_num - 1 WHERE old.class_name = class_name;
    UPDATE class SET class_num=class_num + 1 WHERE new.class_name = class_name;
END//
DELIMITER ;
-- 测试：修改⼀个学⽣信息的班级，触发器执⾏了⼀次
-- update student
-- set class_name='计算机18-1班'
-- WHERE stu_id = '2018001219';