package com.ruan.bankqueue.util;

/**
 * 判断手机号码是否正确
 * Created by Administrator on 2017/9/23.
 */

public class PhoneNumber {
    boolean pn=false;
    public  boolean PhoneNumber(int phone_3){
        if( phone_3==130||phone_3==132||phone_3==133||phone_3==134||
            phone_3==135||phone_3==136||phone_3==137||phone_3==138||phone_3==139||
            phone_3==150||phone_3==151||phone_3==152||phone_3==153||phone_3==155||
            phone_3==156||phone_3==157||phone_3==158||phone_3==159||phone_3==147||
            phone_3==145||phone_3==180||phone_3==185||phone_3==186||phone_3==187||
            phone_3==188||phone_3==189){
            pn=true;
        }else {
            pn=false;
        }
        return pn;
    }
}
