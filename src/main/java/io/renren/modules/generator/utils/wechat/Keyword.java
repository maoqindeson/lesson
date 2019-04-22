package io.renren.modules.generator.utils.wechat;

import lombok.Data;

@Data
public class Keyword {
    private String value;
    public Keyword(String value){
        super();
        this.value=value;
    }
    public Keyword(){
    }

//    public static void main(String[] args) {
//        String a = URLDecoder.decode("pages/index/index?redirect=%2Fpages%2Fucenter%2FcouponCenter%2FcouponCenter");
//        System.out.println(a);
//    }
}
