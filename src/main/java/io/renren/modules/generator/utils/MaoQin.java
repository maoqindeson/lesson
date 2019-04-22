//package io.renren.modules.generator.utils;
//
//import javax.servlet.ServletOutputStream;
//import java.util.Scanner;
//
//public class MaoQin {
//    public static void main(String[] args) {
//        //猜数字游戏
//        int  a  = 29;
//        Scanner input = new Scanner(System.in);//创建一个键盘扫描类对象
//        System.out.print("输入你猜的答案,提示范围在0~100:");
//        int contents = input.nextInt(); //输入整型
//        if (contents>100){
//            System.out.println("您输入的数字超出范围");
//        }
//        if (contents == a) {
//            System.out.println("恭喜你答对了");
//        } else if(a>contents&&contents<a){
//            System.out.println("再试一次,");
//        }
//    }
//
//}
