simpleSpider
============

一个用于挖掘网站页面数据的工具集

Ebook中实现了一个用于挖掘某电子书网站信息并存进数据库的脚本代码

实现了一个不熟悉SQL语言，也能使用的数据库的数据库模块类

重新封装了Jsoup，实现了文件的下载和文件上传，还有处理JSON的网络连接类，能够自动保存Cookie和使用Useragent简单避免一些服务器封锁

类的介绍

Model 用于数据库数据处理，自动生成SQL代码

DabaBase 用于数据库连接，数据库设置在里头

C 网络连接的工具包装类

T 用于多线程并发控制

ThreadRun 多线程程序继承类
