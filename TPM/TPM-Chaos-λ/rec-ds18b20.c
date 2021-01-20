//ds18b20.c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

int Open_send(char *base){//打开发送数据
    int fd, size;
    char buffer[1024];
    fd = open(base,O_RDONLY);
    lseek(fd,69,SEEK_SET);
    size = read(fd,buffer,sizeof(buffer));
    close(fd);
    printf("temp ℃ = %f\n",(float)atoi(buffer)/1000.0);
    return 0;
}

int readFileList(char *basePath){//文件查找
    DIR *dir;
    struct dirent *ptr;
    char base[1024];

    if ((dir=opendir(basePath)) == NULL){
        perror("Open dir error...");
        exit(1);
    }
    while ((ptr=readdir(dir)) != NULL)
    {
        if(strcmp(ptr->d_name,".")==0 || strcmp(ptr->d_name,"..")==0){//current dir OR parrent dir
            continue;
        } else if(ptr->d_type == 10){
            memset(base,'\0',sizeof(base));
            sprintf(base,"%s",ptr->d_name);
            if((strcmp("27",base)<0)&&(strcmp("29",base)>0)){
                sprintf(base,"%s/%s/w1_slave",basePath,ptr->d_name);
                //printf("%s\n",base);
                while(1)
                Open_send(base);
            }
        }
    }
    closedir(dir);
    return 1;
}

int main(void){
    DIR *dir;
    char basePath[1024];
    memset(basePath,'\0',sizeof(basePath));
    strcpy(basePath,"/sys/bus/w1/devices");
    readFileList(basePath);
    return 0;
}
