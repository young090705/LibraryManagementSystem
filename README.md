# 📚 图书管理系统 - 控制台演示
一个基于控制台的完整图书管理系统，使用**Java**构建，演示面向对象编程原则。
# 项目概述
本系统支持以下功能：
- 图书管理：添加、列出、搜索纸质书和电子书
- 成员管理：注册和查看图书馆成员
- 借阅归还：完整的借阅和归还流程
- 搜索排序：按标题查找图书，按标题或作者排序
- 统计功能：跟踪借阅历史，识别活跃成员
# 技术栈
面向对象概念：继承、多态、封装、抽象
集合框架：HashMap、ArrayList、Comparator、Streams
日期时间API：LocalDate用于归还日期管理
# 项目结构
```
LibraryManagementSystem/
├── src/
│   ├── Book.java (抽象类)
│   ├── PrintedBook.java
│   ├── EBook.java
│   ├── Member.java
│   ├── BorrowRecord.java
│   ├── Library.java (核心系统)
│   └── Main.java (控制台界面)
└── README.md
```
