## 1.项目描述

项目名：基础服务— 路由中心

git仓库地址：

​	公司git地址：https://git.schtwz.cn

​	ssh:git@git.schtwz.cn:base/gateway-center.git

​	http:https://git.schtwz.cn/base/gateway-center.git


## 2. 如何运行


> 服务端运行：
>
> ```bash
> #(日志输出在 log.path 配置目录)
> nohup  java -jar xx.jar   >/dev/null 2>&1 &
> ```
>
> ```bash
> #docker 启动 
> docker run  -p 83:8080 -d -i  test:1.0-SNAPSHOT
> ```
>
> 开发运行：请参考IntelliJ IDEA + springboot  使用说明（https://blog.csdn.net/y12nre/article/details/60869829）

### 2.1 开发环境配置

> java:   jdk1.8 
>
> Springboot:1.5.9
>
> Mysql: 5.7+
>
> linux: Centos 7.2+ 


### 2.3 发布流程

```bash
#1. 确定发布环境（dev | test | produce ），在父pom中设置profile
#2. 项目打包--docker
mvn clean package docker:build
#3. 项目打包--jar包
mvn clean install -Pproduce

```


