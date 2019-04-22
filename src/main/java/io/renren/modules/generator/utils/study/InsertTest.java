package io.renren.modules.generator.utils.study;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InsertTest {
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
//    public static void main(String[] args) {
//        try{
//            Class.forName("com.mysql.jdbc.Driver");
//            System.out.println("开始连接数据库");
//            Connection conn=DriverManager.getConnection(
//                    "jdbc:mysql://localhost:3306/maolocal"
//                    ,"root","root");
//            Statement stmt =conn.createStatement();
//            long start =System.currentTimeMillis();
//            CountDownLatch countDownLatch = new CountDownLatch(10);
//            ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
//            for (int i = 0; i < 10; i++) {
//                cachedThreadPool.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            for (int i=0;i<10000;i++){
//                                String s=getRandomString(5);
//                                boolean rs = stmt.execute("insert  into  tb_string (name) values "+"('"+s+"')");
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        } finally {
//                            countDownLatch.countDown();
//                        }
//                    }
//                });
//            }
//            countDownLatch.await();
//            long end=System.currentTimeMillis();
//            long time=end -start;
//            System.out.println("使用十个线程同时插入10000数据,总100000数据,耗时："+time);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
}