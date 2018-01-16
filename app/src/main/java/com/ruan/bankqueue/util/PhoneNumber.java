package com.ruan.bankqueue.util;

/**
 * 判断手机号码是否正确
 * @author by ken on 2017/9/23.
 */

public class PhoneNumber {
    private boolean pn=false;
    public  boolean phoneNumber(int phone){
        if( phone==130||phone==132||phone==133||phone==134||
            phone==135||phone==136||phone==137||phone==138||phone==139||
            phone==150||phone==151||phone==152||phone==153||phone==155||
            phone==156||phone==157||phone==158||phone==159||phone==147||
            phone==145||phone==180||phone == 181||phone==182||phone == 183||
            phone == 184 || phone==185||phone==186||phone==187|| phone==188||
                phone==189){
            pn=true;
        }else {
            pn=false;
        }
        return pn;
    }
}
