# 基于树型奇偶机的密钥交换系统和算法

##改进算法
###本地输入序列
####LFSR输入序列的生成
####混沌序列的生成

###基于滑动窗口的学习规则

###高效输入序列的生成

##系统
开发环境介绍
开发环境	类型
操作系统	Ubuntu 12.0
语言	Java 12.0
开发工具	IDEA 2019
CPU	酷睿I5-4210M
内存	DDR3 8G
显卡	GTX950M

部署环境
部署环境	类型
操作系统	Ubuntu for Raspberry 4B
语言	Java 12.0
硬件主控设备	Raspberry 4B *2
传感器1：温度传感器	DS18B20
传感器2：湿度传感器	DHT11
传感器3：雨滴传感器	Drops
传感器4：摄像头模块	Pi Cam
通信设备：通信模块	BCM43438 WiFi /串口

##运行
1，编译
2，java -jar TPM.jar //服务器端

3,java - jar TPM-Client.jar // 客户端

4,命令
服务器端支持的命令
命令	说明
Weather	雨滴传感器，返回是否在下雨
Temp	当前环境温度
Ham	当前环境湿度
File+文件名	获取服务器文件
ListFile+目录	显示当前目录文件名
Camera	使用摄像设备拍照并传送照片
其他字符串	向服务器直接发送消息

5，总体硬件
![1616144006102](https://user-images.githubusercontent.com/31534048/111755183-cb95c800-88d3-11eb-8632-cc0702a4a2f7.jpg)
![IMG_20210319_165302](https://user-images.githubusercontent.com/31534048/111755188-cc2e5e80-88d3-11eb-8f09-e0cc10c814ce.jpg)
主要包括：
树莓派4b
温度，湿度，天气，摄像头模块，作为通信信息的载体

加密：
密钥交换---树型奇偶机

加密方式AES加密方式


6，总体耗时
10000位密钥交换+网络延迟+密钥转换+打印输出≈1200ms
设备算力较低和网络延迟是主要耗时点


研究背景：
1，密码学有受到威胁，研究新的密码体制，神经网络发展迅速。引出Gans以及其他网络密码。
2，Gans基于对抗的思想，符合密码学的攻击者和防御者的模式，引出可以通过合作的方式（互学习）树型奇偶机就是互学习的方式。
3，简单介绍树型奇偶机的工作流程。

创新点：
介绍改进目的：实用化，高效密钥交换，安全性，为通信系统做准备
1，本地输入向量生成
(1)来由：输入向量较大，串口传输慢
(2)生成方案：混沌，LFSR生成方式，对比两种方案优势
(3)最终得出结论
2，滑动窗口协议加入
(1)来由：训练速度慢，学习效率低
(2)方案：使用滑动窗口动态调节学习效率
(3)可行性缘由：窗口的正确率能够代表当前同步程度，原方案忽略了错误信息
(4)实验结果：提高了学习效率
3，高效输入向量的生成
(1)来由：输入向量完全随机，不能够利用当前的同步信息，使得每次的学习信息量较低。
(2)方案：根据公式xx可知，调节输入向量-1和1的比例使得输出值合适，以此生成高效输入向量。
(3)结果：同步程度迅速上升，的确使得每次学习到的信息量增加了。

最终结论：按照不同场合使用场景，选择使用1和2结合，或者2和3结合的方式提高密钥交换的效率。


通信系统（二维码链接到我的github）：

1，介绍系统结构：
(1)密钥交换流程：上图
(2)系统结构：树莓派+传感器+密钥交换+加密方式AES256
(3)多客户端：介绍两种密钥管理交换方式（星型和树型），维护通用密钥k1
(4)可以简单演示系统工作流程。

2，提出下一步可以将协议嵌入到tcp协议中，或者应用层和软件调用之间层。

总结展望：
1，提出本文的研究成果
2，下一步的研究方向
