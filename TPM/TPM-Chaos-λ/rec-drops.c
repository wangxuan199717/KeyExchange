#include <wiringPi.h>
#include <stdio.h>
#include <stdlib.h>

typedef unsigned char uint8;
typedef unsigned int  uint16;
typedef unsigned long uint32;

#define HIGH_TIME 32

int pinNumber = 0;
 
int main(void)
{
    printf("PIN:%d\n", pinNumber);

    if (-1 == wiringPiSetup()) {
        printf("Setup wiringPi failed!");
        return 1;
    }
 
    pinMode(pinNumber, INPUT); 

    printf("Starting...\n");
    int i=5;
    while (i--) 
    {
	delay(100);
        pinMode(pinNumber, INPUT); // set mode to output
        digitalWrite(pinNumber, 1); // output a high level 
        if (!digitalRead(pinNumber))
        {
            printf("The Weather is Rainning!\n");
        }
        else
        {
            printf("The Weather is Sunny\n");
           
        }
    }
    return 0;
}
