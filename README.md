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

