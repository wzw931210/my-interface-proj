package cn.jinjing.plat.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class CreateKey {

	 /** 
     * 获得key
     * @param len 密码长度 
     * @return 
     */  
    public static String createKey(int len){  
        int random = createRandomInt();  
        return createKey(random, len);  
    }  
      
    public static String createKey(int random,int len){  
        Random rd = new Random(random);  
        final int  maxNum = 62;  
        StringBuffer sb = new StringBuffer();  
        int rdGet;//取得随机数  
        char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',  
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',  
                'x', 'y', 'z', 'A','B','C','D','E','F','G','H','I','J','K',  
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',  
                'X', 'Y' ,'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };  
          
        int count=0;  
        while(count < len){  
            rdGet = Math.abs(rd.nextInt(maxNum));//生成的数最大为62-1  
            if (rdGet >= 0 && rdGet < str.length) {  
                sb.append(str[rdGet]);  
                count ++;  
            }  
        }  
        return sb.toString();  
    }  

    public static int createRandomInt(){  
        //得到0.0到1.0之间的数字，并扩大100000倍  
        double temp = Math.random()*100000;  
        //如果数据等于100000，则减少1  
        if(temp>=100000){  
            temp = 99999;  
        }  
        int tempint = (int)Math.ceil(temp);  
        return tempint;  
    }

    //可打印字符
    public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };


    //    本算法利用62个可打印字符，通过随机生成32位UUID，由于UUID都为十六进制，所以将UUID分成8组，每4个为一组，然后通过模62操作，结果作为索引取出字符，
    public static String generateShortUuid8() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();
    }

    public static void main(String[] args){
        //12000000 120w次数内不允许有重复
        int length=8;
        int num =1200000;
        String[] keys = new String[num];
        Set<String> keylist=  new HashSet<>();
        for (int i=0;i<num;i++){
//            String reg=createKey(8);
            String reg=generateShortUuid8();
            keylist.add(reg);
        }
//        System.out.println(keys.length);
        System.out.println(keylist.size());

    }

}
